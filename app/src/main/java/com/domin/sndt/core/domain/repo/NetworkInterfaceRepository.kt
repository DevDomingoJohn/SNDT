package com.domin.sndt.core.domain.repo

import com.domin.sndt.scan.Device

interface NetworkInterfaceRepository {
    suspend fun getLocalIp(): String?
    suspend fun getIpv6(): String?
    suspend fun getSubnet(): String?
    suspend fun scanNetwork(deviceReached: (Device) -> Unit, isScanning: (Boolean) -> Unit)
}