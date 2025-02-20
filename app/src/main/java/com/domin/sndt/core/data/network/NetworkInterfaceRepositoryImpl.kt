package com.domin.sndt.core.data.network

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import com.domin.sndt.core.domain.MacVendorsRepository
import com.domin.sndt.core.domain.NetworkInterfaceRepository
import com.domin.sndt.scan.Device
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.net.Inet4Address
import java.net.InetAddress
import java.net.NetworkInterface

class NetworkInterfaceRepositoryImpl(
    private val macVendorsRepository: MacVendorsRepository
): NetworkInterfaceRepository {

    override suspend fun getLocalIp(): String? {
        val networkInterfaces = NetworkInterface.getNetworkInterfaces()

        while (networkInterfaces.hasMoreElements()) {
            val networkInterface = networkInterfaces.nextElement()
            val addresses = networkInterface.inetAddresses

            while (addresses.hasMoreElements()) {
                val address = addresses.nextElement()
                if (!address.isLoopbackAddress && address.hostAddress?.contains('.') == true) { // Check for IPv4 and non-loopback
                    return address.hostAddress
                }
            }
        }

        return null
    }

    override suspend fun getSubnet(): String? {
        val networkInterfaces = NetworkInterface.getNetworkInterfaces()

        while (networkInterfaces.hasMoreElements()) {
            val networkInterface = networkInterfaces.nextElement()

            if (networkInterface.displayName == "wlan0") {
                val interfaceAddresses = networkInterface.interfaceAddresses
                for (interfaceAddress in interfaceAddresses) {
                    val address = interfaceAddress.address

                    if (address is Inet4Address) {
                        val prefixLength = interfaceAddress.networkPrefixLength
                        val subnetMask = prefixLengthToSubnetMask(prefixLength)
                        return subnetMask
                    }
                }
            }
        }

        return null
    }

    private fun getCurrentDeviceMac(inetAddress: InetAddress): String? {
        val netInterface = NetworkInterface.getByInetAddress(inetAddress) ?: return null
        val macAddressBytes = netInterface.hardwareAddress

        val macAddressHex = macAddressBytes.joinToString(":") { String.format("%02X", it) }

        return macAddressHex.lowercase()
    }

    private suspend fun getMacAddress(ipAddress: String): String? {
        var mac: String? = null
        withContext(Dispatchers.IO) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                try {
                    val execution = Runtime.getRuntime().exec("ip neigh")
                    execution.waitFor()
                    execution.inputStream.bufferedReader().use { reader ->
                        reader.forEachLine {
                            val line = it.split(Regex("\\s+"))

                            if (line[0] == ipAddress) {
                                mac = line[4]
                                return@forEachLine
                            }
                        }
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            } else {
                val file = File("/proc/net/arp")
                if (!file.exists() || !file.canRead()) {
                    Log.i("getMacAddress","Filepath doesn't exists or cannot be read.")
                }
                file.bufferedReader().use { reader ->
                    reader.forEachLine {
                        val line = it.split(Regex("\\s+"))

                        if (line[0] == ipAddress) {
                            mac = line[3]
                            return@forEachLine
                        }
                    }
                }
            }
        }

        return mac
    }

    private fun calculateNetworkAddress(localIp: String, subnetMask: String): Int {
        val ipAddress = InetAddress.getByName(localIp)
        val subnetMaskAddress = InetAddress.getByName(subnetMask)

        val ipBytes = ipAddress.address
        val maskBytes = subnetMaskAddress.address

        var networkAddress = 0
        for (i in 0..3) {
            networkAddress = networkAddress or ((ipBytes[i].toInt() and maskBytes[i].toInt() and 0xFF) shl (8 * (3 - i)))
        }
        return networkAddress
    }

    private fun calculateBroadcastAddress(networkAddressInt: Int, subnetMask: String): Int {
        val subnetMaskAddress = InetAddress.getByName(subnetMask)

        val maskBytes = subnetMaskAddress.address
        var invertedMaskInt = 0
        for (i in 0..3) {
            invertedMaskInt = invertedMaskInt or ((maskBytes[i].toInt().inv() and 0xFF) shl (8 * (3 - i)))
        }
        return networkAddressInt or invertedMaskInt
    }

    @SuppressLint("DefaultLocale")
    private fun prefixLengthToSubnetMask(prefixLength: Short): String {
        val mask = 0xFFFFFFFFL shl (32 - prefixLength)
        return String.format(
            "%d.%d.%d.%d",
            (mask shr 24) and 0xFF,
            (mask shr 16) and 0xFF,
            (mask shr 8) and 0xFF,
            mask and 0xFF
        )
    }

    private fun integerToInetAddress(ipInt: Int): InetAddress {
        val ipBytes = ByteArray(4)
        for (i in 0..3) {
            ipBytes[i] = (ipInt shr (8 * (3 - i))).toByte()
        }
        return InetAddress.getByAddress(ipBytes)
    }

    override suspend fun scanNetwork(deviceReached: (Device) -> Unit) {
        val localIp = getLocalIp()!!
        val subnetMask = getSubnet()!!

        val networkAddressInt = calculateNetworkAddress(localIp,subnetMask)
        val broadcastAddressInt = calculateBroadcastAddress(networkAddressInt,subnetMask)

        for (ipInt in networkAddressInt + 1 .. broadcastAddressInt - 1) {
            val address = integerToInetAddress(ipInt)
            if (address.isReachable(1000)) {
                val hostAddress = address.hostAddress!!
                val label = if (hostAddress == localIp) "This Device" else null
                val macAddress = if (hostAddress == localIp) getCurrentDeviceMac(address) else getMacAddress(hostAddress)
                val vendor = macVendorsRepository.getVendorByMac(macAddress)

                deviceReached(Device(label,hostAddress,macAddress,vendor))
            }
        }
    }
}