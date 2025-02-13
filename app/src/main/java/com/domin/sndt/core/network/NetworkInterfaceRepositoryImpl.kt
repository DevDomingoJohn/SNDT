package com.domin.sndt.core.network

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.domin.sndt.core.domain.NetworkInterfaceRepository
import com.domin.sndt.scan.Device
import java.net.InetAddress
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
                    Log.i("Hostname",address.hostName)
                    return address.hostAddress
                }
            }
        }

        return null
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    private fun getMacAddress(inetAddress: InetAddress): String? {
        val netInterface = NetworkInterface.getByInetAddress(inetAddress) ?: return null
        val macAddressBytes = netInterface.hardwareAddress

        val macAddressHex = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            HexFormat.ofDelimiter(":").formatHex(macAddressBytes)
        } else {
            macAddressBytes.joinToString(":") { String.format("%02X", it) }
        }
        Log.i("Mac",macAddressHex)
        return macAddressHex
    }

    override suspend fun getSubnet(): String? {
        val networkInterfaces = NetworkInterface.getNetworkInterfaces()

        while (networkInterfaces.hasMoreElements()) {
            val networkInterface = networkInterfaces.nextElement()
            val interfaceAddresses = networkInterface.interfaceAddresses
            networkInterface.hardwareAddress

            for (interfaceAddress in interfaceAddresses) {
                val address = interfaceAddress.address
                val prefixLength = interfaceAddress.networkPrefixLength

                if (address.hostAddress?.contains('.') == true) {
                    val subnetMask = prefixLengthToSubnetMask(prefixLength)
                    return subnetMask
                }
            }
        }

        return null
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

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override suspend fun scanNetwork(deviceReached: (Device) -> Unit): List<Device> {
        val localIp = getLocalIp()!!
        val subnetMask = getSubnet()!!

        val networkAddressInt = calculateNetworkAddress(localIp,subnetMask)
        val broadcastAddressInt = calculateBroadcastAddress(networkAddressInt,subnetMask)

        val deviceList = mutableListOf<Device>()
        for (ipInt in networkAddressInt + 1 .. broadcastAddressInt - 1) {
            val address = integerToInetAddress(ipInt)
            if (address.isReachable(50)) {
                val hostAddress = address.hostAddress!!
                val macAddress: String? = getMacAddress(address)
                val hostName: String? = if (address.hostName != hostAddress) address.hostName else null

                deviceReached(Device(hostName,hostAddress,macAddress))
                deviceList.add(Device(hostName,hostAddress,macAddress))
            }
        }

        return deviceList
    }
}