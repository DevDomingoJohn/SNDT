package com.domin.sndt.core.domain

interface WifiManagerRepository {
    suspend fun getWifiDetails()
}