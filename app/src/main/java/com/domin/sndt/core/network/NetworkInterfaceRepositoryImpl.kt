package com.domin.sndt.core.network

import android.util.Log
import com.domin.sndt.core.domain.NetworkInterfaceRepository
import java.net.InetAddress
import java.net.NetworkInterface

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
            val interfaceAddresses = networkInterface.interfaceAddresses

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

    override suspend fun calculateNetworkAddress(localIp: String, subnetMask: String): Int {
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

    override suspend fun calculateBroadcastAddress(networkAddressInt: Int, subnetMask: String): Int {
        val subnetMaskAddress = InetAddress.getByName(subnetMask)

        val maskBytes = subnetMaskAddress.address
        var invertedMaskInt = 0
        for (i in 0..3) {
            invertedMaskInt = invertedMaskInt or ((maskBytes[i].toInt().inv() and 0xFF) shl (8 * (3 - i)))
        }
        return networkAddressInt or invertedMaskInt
    }

    fun prefixLengthToSubnetMask(prefixLength: Short): String {
        val mask = 0xFFFFFFFFL shl (32 - prefixLength)
        return String.format(
            "%d.%d.%d.%d",
            (mask shr 24) and 0xFF,
            (mask shr 16) and 0xFF,
            (mask shr 8) and 0xFF,
            mask and 0xFF
        )
    }

    override suspend fun integerToInetAddress(ipInt: Int) {
        val ipBytes = ByteArray(4)
        for (i in 0..3) {
            ipBytes[i] = (ipInt shr (8 * (3 - i))).toByte()
        }
        val address = InetAddress.getByAddress(ipBytes)
        val ip = address.hostAddress!!
        Log.i("RepositoryImpl",ip)
    }
}