package com.domin.sndt

interface ConnectivityRepository {
    suspend fun getLocalIp()
}