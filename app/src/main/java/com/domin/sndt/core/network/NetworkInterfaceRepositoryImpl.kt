package com.domin.sndt.core.network

import com.domin.sndt.core.domain.NetworkInterfaceRepository
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
}