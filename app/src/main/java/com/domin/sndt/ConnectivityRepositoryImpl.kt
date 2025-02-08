package com.domin.sndt

import android.net.ConnectivityManager
import android.util.Log

class ConnectivityRepositoryImpl(
    private val connectivityManager: ConnectivityManager
): ConnectivityRepository {
    override suspend fun getLocalIp() {
        Log.i("IMPLEMENTATION","Working!")
    }
}