package com.domin.sndt.core.domain

interface NetworkInterfaceRepository {
    suspend fun getLocalIp(): String?
    suspend fun getSubnet(): String?
}