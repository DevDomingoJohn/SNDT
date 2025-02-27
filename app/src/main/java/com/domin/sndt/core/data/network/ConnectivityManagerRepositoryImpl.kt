package com.domin.sndt.core.data.network

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import com.domin.sndt.core.data.IpifyRepositoryImpl
import com.domin.sndt.core.domain.NetworkInterfaceRepository
import com.domin.sndt.core.domain.ConnectivityManagerRepository
import com.domin.sndt.info.ActiveConnection
import com.domin.sndt.info.ConnectionDetails
import java.net.Inet4Address
import java.net.Inet6Address

class ConnectivityManagerRepositoryImpl(
    private val wifiManager: WifiManager,
    private val connectivityManager: ConnectivityManager,
    private val networkInterfaceRepository: NetworkInterfaceRepository,
    private val ipifyRepositoryImpl: IpifyRepositoryImpl
): ConnectivityManagerRepository {
    override suspend fun getConnectionInfo(): ConnectionDetails {
        var httpProxy = "N/A"
        var connectionType = "None"
        var ssid = "N/A"
        var bssid = "N/A"
        var channel = "N/A"
        var speed = "N/A"
        var signalStrength = "N/A"

        var ipv4Address = "N/A"
        var subnetMask = "N/A"
        var gatewayIpv4 = "N/A"
        var dnsServerIpv4 = "N/A"
        var ipv6Address = "N/A"
        var gatewayIpv6 = "N/A"
        var dnsServerIpv6 = "N/A"

        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)

        networkCapabilities?.let {
            if (it.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                connectionType = "Wi-Fi"
                val linkProperties = connectivityManager.getLinkProperties(network)
                if (linkProperties!= null) {
                    httpProxy = linkProperties.httpProxy.toString()

                    for (route in linkProperties.routes) {
                        if (route.isDefaultRoute && route.gateway is Inet4Address) {
                            gatewayIpv4 = route.gateway?.hostAddress ?: "N/A"
                        } else if (route.isDefaultRoute && route.gateway is Inet6Address) {
                            gatewayIpv4 = route.gateway?.hostAddress ?: "N/A"
                        }
                    }

                    for (address in linkProperties.dnsServers) {
                        if (address.hostAddress?.contains(".") == true) {
                            dnsServerIpv4 = address.hostAddress ?: "N/A"
                        } else if (address.hostAddress?.contains(":") == true) {
                            dnsServerIpv6 = address.hostAddress ?: "N/A"
                        }
                    }
                }

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

                ipv4Address = networkInterfaceRepository.getLocalIp() ?: "N/A"
                subnetMask = networkInterfaceRepository.getSubnet() ?: "N/A"
                ipv6Address = networkInterfaceRepository.getIpv6() ?: "N/A"
            }
        }

        val isWifiEnabled = wifiManager.isWifiEnabled

        val connectionState = wifiManager.wifiState.let {
            if (it == WifiManager.WIFI_STATE_ENABLED) "Connected" else "Disconnected"
        }

        val dhcpLease = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S)
            wifiManager.dhcpInfo.leaseDuration.toString() else null

        val publicIpv4 = ipifyRepositoryImpl.getPublicIpv4() ?: "N/A"
        val publicIpv6 = ipifyRepositoryImpl.getPublicIpv6() ?: "N/A"

        return ConnectionDetails(
            connectionType = connectionType,
            externalIp = publicIpv4,
            externalIpv6 = publicIpv6,
            httpProxy = httpProxy,

            ipv4Address = ipv4Address,
            subnetMask = subnetMask,
            gatewayIpv4 = gatewayIpv4,
            dnsServerIpv4 = dnsServerIpv4,
            ipv6Address = ipv6Address,
            gatewayIpv6 = gatewayIpv6,
            dnsServerIpv6 = dnsServerIpv6,

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

    override suspend fun getConnectionType(): ActiveConnection? {
        val publicIpv4 = ipifyRepositoryImpl.getPublicIpv4()
        val publicIpv6 = ipifyRepositoryImpl.getPublicIpv6()

        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)

        networkCapabilities?.let {
            val linkProperties = connectivityManager.getLinkProperties(network)
            val httpProxy = linkProperties?.httpProxy?.host

            if (it.hasTransport(NetworkCapabilities.TRANSPORT_WIFI))
                return ActiveConnection("Wi-Fi",publicIpv4,publicIpv6,httpProxy)

            if (it.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
                return ActiveConnection("Cell",publicIpv4,publicIpv6,httpProxy)

            if (it.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET))
                return ActiveConnection("Ethernet",publicIpv4,publicIpv6,httpProxy)
        }

        return null
    }
}