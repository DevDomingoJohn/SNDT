package com.domin.sndt.core.domain.repo

import android.net.Network
import com.domin.sndt.info.ActiveConnection
import com.domin.sndt.info.CellDetails
import com.domin.sndt.info.ConnectionInfo
import com.domin.sndt.info.WifiDetails
import kotlinx.coroutines.flow.Flow

interface ConnectivityManagerRepository {
    suspend fun networkCallback(callback: (Network?) -> Unit)
    suspend fun getWifiDetails(): WifiDetails
    suspend fun getCellDetails(): CellDetails
    suspend fun getCellSignalStrength(callback: (Int?) -> Unit)
    suspend fun getCellDataState(): Flow<Int>
    suspend fun getConnectionDetails(network: Network): Pair<ActiveConnection, ConnectionInfo>?
}