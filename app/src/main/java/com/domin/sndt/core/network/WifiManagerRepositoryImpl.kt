package com.domin.sndt.core.network

import android.net.wifi.WifiManager
import com.domin.sndt.core.domain.WifiManagerRepository

class WifiManagerRepositoryImpl(
    private val wifiManager: WifiManager
): WifiManagerRepository {
    override suspend fun getWifiDetails() {
        TODO("Not yet implemented")
    }
}