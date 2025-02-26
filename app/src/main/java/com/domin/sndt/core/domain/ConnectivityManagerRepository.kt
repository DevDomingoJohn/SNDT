package com.domin.sndt.core.domain

import com.domin.sndt.info.ConnectionDetails

interface ConnectivityManagerRepository {
    suspend fun getWifiDetails(): ConnectionDetails
}