package com.domin.sndt.core.domain

import com.domin.sndt.info.ActiveConnection
import com.domin.sndt.info.CellDetails
import com.domin.sndt.info.ConnectionInfo
import com.domin.sndt.info.WifiDetails

interface ConnectivityManagerRepository {
    suspend fun getWifiDetails(): WifiDetails
    suspend fun getCellDetails(): CellDetails
    suspend fun getCellSignalStrength(callback: (Int?) -> Unit)
    suspend fun getConnectionDetails(): Pair<ActiveConnection, ConnectionInfo>?
}