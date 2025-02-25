package com.domin.sndt.core.data.network

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import com.domin.sndt.core.domain.WifiManagerRepository
import com.domin.sndt.info.ConnectionDetails

class WifiManagerRepositoryImpl(
    private val wifiManager: WifiManager,
    private val connectivityManager: ConnectivityManager
): WifiManagerRepository {
    override suspend fun getWifiDetails(): ConnectionDetails {
        var ssid = "N/A"
        var bssid = "N/A"
        var channel = "N/A"
        var speed = "N/A"
        var signalStrength = "N/A"

        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)

        networkCapabilities?.let {
            if (it.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                val linkProperties = connectivityManager.getLinkProperties(network)
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S){
                    ssid = wifiManager.connectionInfo.ssid
                    bssid = wifiManager.connectionInfo.bssid
                    speed = wifiManager.connectionInfo.linkSpeed.toString()
                    signalStrength = wifiManager.connectionInfo.rssi.toString()
                    channel = wifiManager.connectionInfo.frequency.toString()
                } else {
                    val wifiInfo = it.transportInfo as (WifiInfo)
                    ssid = wifiInfo.ssid
                    bssid = wifiInfo.bssid
                    speed = wifiInfo.linkSpeed.toString()
                    signalStrength = wifiInfo.rssi.toString()
                    channel = wifiInfo.frequency.toString()
                }
            }

        }

        val isWifiEnabled = wifiManager.isWifiEnabled

        val connectionState = wifiManager.wifiState.let {
            if (it == WifiManager.WIFI_STATE_ENABLED) "Connected" else "Disconnected"
        }

        val dhcpLease = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S)
            wifiManager.dhcpInfo.leaseDuration.toString() else null

        return ConnectionDetails(
            wifiEnabled = isWifiEnabled,
            connectionState = connectionState,
            dhcpLeaseTime = dhcpLease,
            ssid = ssid,
            bssid = bssid,
            channel = channel,
            speed = speed,
            signalStrength = signalStrength
        )
    }
}