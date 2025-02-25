package com.domin.sndt.core.domain

import com.domin.sndt.info.ConnectionDetails

interface WifiManagerRepository {
    suspend fun getWifiDetails(): ConnectionDetails
}