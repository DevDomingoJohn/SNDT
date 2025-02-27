package com.domin.sndt.core.domain

import com.domin.sndt.info.ActiveConnection
import com.domin.sndt.info.ConnectionDetails

interface ConnectivityManagerRepository {
    suspend fun getConnectionInfo(): ConnectionDetails
    suspend fun getConnectionType(): ActiveConnection?
}