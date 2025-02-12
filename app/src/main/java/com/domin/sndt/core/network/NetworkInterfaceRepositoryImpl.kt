package com.domin.sndt.core.network

import android.net.Network
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.domin.sndt.core.domain.NetworkInterfaceRepository
import com.domin.sndt.scan.Device
import java.net.InetAddress
import java.net.NetworkInterface
import java.util.HexFormat

class NetworkInterfaceRepositoryImpl: NetworkInterfaceRepository {
    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override suspend fun getLocalIp(): String? {
        val networkInterfaces = NetworkInterface.getNetworkInterfaces()

        while (networkInterfaces.hasMoreElements()) {
            val networkInterface = networkInterfaces.nextElement()
            val addresses = networkInterface.inetAddresses

            while (addresses.hasMoreElements()) {
                val address = addresses.nextElement()
                if (!address.isLoopbackAddress && address.hostAddress?.contains('.') == true) { // Check for IPv4 and non-loopback
                    Log.i("Hostname",address.hostName)
                    val netInterface = NetworkInterface.getByInetAddress(address)
                    val macAddressBytes = netInterface.hardwareAddress
                    val macAddressHex = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        HexFormat.ofDelimiter(":").formatHex(macAddressBytes)
                    } else {
                        macAddressBytes.joinToString(":") { String.format("%02X", it) }
                    }
                    Log.i("Mac",macAddressHex)
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

    fun integerToAddress(ipInt: Int): InetAddress {
        val ipBytes = ByteArray(4)
        for (i in 0..3) {
            ipBytes[i] = (ipInt shr (8 * (3 - i))).toByte()
        }
        return InetAddress.getByAddress(ipBytes)
    }

    override suspend fun getDevices(networkAddressInt: Int, broadcastAddressInt: Int): List<Device> {
        val deviceList = mutableListOf<Device>()
        Log.i("RepositoryImpl","Network Scanning...")
        for (ipInt in networkAddressInt + 1 .. broadcastAddressInt - 1) {
            val address = integerToAddress(ipInt)
            Log.i("InetAddress","Checking ${address.hostAddress!!}!")
            if (address.isReachable(50)) {
                Log.i("RepositoryImpl","${address.hostAddress!!} is reachable!")
                deviceList.add(Device(address.hostName,address.hostAddress!!,"IDK"))
            }
        }

        return deviceList
    }
}