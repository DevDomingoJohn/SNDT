package com.domin.sndt.core.domain

interface NetworkInterfaceRepository {
    suspend fun getLocalIp(): String?
    suspend fun getSubnet(): String?
    suspend fun calculateNetworkAddress(localIp: String, subnetMask: String): Int
    suspend fun integerToInetAddress(ipInt: Int)
    suspend fun calculateBroadcastAddress(networkAddressInt: Int, subnetMask: String): Int
}