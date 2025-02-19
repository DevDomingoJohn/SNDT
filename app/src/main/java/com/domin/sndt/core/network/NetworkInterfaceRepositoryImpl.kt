package com.domin.sndt.core.network

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import com.domin.sndt.core.domain.NetworkInterfaceRepository
import com.domin.sndt.scan.Device
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.net.Inet4Address
import java.net.InetAddress
import java.net.InterfaceAddress
import java.net.NetworkInterface
import java.util.HexFormat

class NetworkInterfaceRepositoryImpl: NetworkInterfaceRepository {

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
            Log.d("SubnetDebug", "Interface: ${networkInterface.displayName}, Name: ${networkInterface.name}, isUp: ${networkInterface.isUp}")

            if (networkInterface.displayName == "wlan0") {
                val interfaceAddresses = networkInterface.interfaceAddresses
                for (interfaceAddress in interfaceAddresses) {
                    val address = interfaceAddress.address
                    Log.d("SubnetDebug", "  Address: ${address.hostAddress}, Type: ${address::class.java.simpleName}")

                    if (address is Inet4Address) {
                        val prefixLength = interfaceAddress.networkPrefixLength
                        Log.d("SubnetDebug", "  Prefix Length: $prefixLength")
                        val subnetMask = prefixLengthToSubnetMask(prefixLength)
                        Log.d("SubnetDebug", "  Subnet Mask: $subnetMask")
                        return subnetMask
                    }
                }
            }
        }

        return null
    }


    @SuppressLint("NewApi")
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
                    reader.forEachLine { Log.i("getMacAddress",it) }
                }
            }
        }
        return mac
//        val netInterface = NetworkInterface.getByInetAddress(inetAddress) ?: return null
//        val macAddressBytes = netInterface.hardwareAddress
//
//        val macAddressHex = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            HexFormat.ofDelimiter(":").formatHex(macAddressBytes)
//        } else {
//            macAddressBytes.joinToString(":") { String.format("%02X", it) }
//        }
//        Log.i("Mac",macAddressHex)
//        return macAddressHex
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

    override suspend fun scanNetwork(deviceReached: (Device) -> Unit): List<Device> {
        val localIp = getLocalIp()!!
        val subnetMask = getSubnet()!!

        val networkAddressInt = calculateNetworkAddress(localIp,subnetMask)
        val broadcastAddressInt = calculateBroadcastAddress(networkAddressInt,subnetMask)



        val deviceList = mutableListOf<Device>()
        for (ipInt in networkAddressInt + 1 .. broadcastAddressInt - 1) {
            val address = integerToInetAddress(ipInt)

            if (withContext(Dispatchers.IO) {
                    address.isReachable(1000)
                }) {
                val hostAddress = address.hostAddress!!
//                val macAddress: String? = getMacAddress(address)
                val hostName: String? = if (address.hostName != hostAddress) address.hostName else null

                val macAddress = getMacAddress(hostAddress)
                deviceReached(Device(hostName,hostAddress,macAddress))
                deviceList.add(Device(hostName,hostAddress,macAddress))
            }
        }

        return deviceList
    }
}