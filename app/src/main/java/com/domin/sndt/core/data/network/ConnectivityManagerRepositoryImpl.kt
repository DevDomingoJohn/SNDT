package com.domin.sndt.core.data.network

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import com.domin.sndt.core.data.IpifyRepositoryImpl
import com.domin.sndt.core.domain.NetworkInterfaceRepository
import com.domin.sndt.core.domain.ConnectivityManagerRepository
import com.domin.sndt.info.ActiveConnection
import com.domin.sndt.info.ConnectionInfo
import com.domin.sndt.info.WifiDetails
import java.net.Inet4Address
import java.net.Inet6Address

class ConnectivityManagerRepositoryImpl(
    private val wifiManager: WifiManager,
    private val connectivityManager: ConnectivityManager,
    private val networkInterfaceRepository: NetworkInterfaceRepository,
    private val ipifyRepositoryImpl: IpifyRepositoryImpl
): ConnectivityManagerRepository {
    override suspend fun getWifiDetails(): WifiDetails? {
        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return null

        val isWifiEnabled = wifiManager.isWifiEnabled
        val connectionState = "Connected"
        var ssid: String?
        var bssid: String?
        var channel: String?
        var speed: String?
        var signalStrength: String?
        var dhcpLease: Int? = null

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S){
            dhcpLease = wifiManager.dhcpInfo.leaseDuration
            ssid = wifiManager.connectionInfo.ssid
            bssid = wifiManager.connectionInfo.bssid
            speed = wifiManager.connectionInfo.linkSpeed.toString()
            signalStrength = wifiManager.connectionInfo.rssi.toString()
            channel = wifiManager.connectionInfo.frequency.toString()
        } else {
            val wifiInfo = networkCapabilities.transportInfo as (WifiInfo)
            ssid = wifiInfo.ssid
            bssid = wifiInfo.bssid
            speed = wifiInfo.linkSpeed.toString()
            signalStrength = wifiInfo.rssi.toString()
            channel = wifiInfo.frequency.toString()
        }

        return WifiDetails(isWifiEnabled,connectionState,dhcpLease,ssid,bssid,channel,speed,signalStrength)
    }

    private suspend fun getConnectionInfo(network: Network): ConnectionInfo {
        val linkProperties = connectivityManager.getLinkProperties(network)!!
        var gatewayIpv4: String? = null
        var gatewayIpv6: String? = null
        var dnsIpv4: String? = null
        var dnsIpv6: String? = null

        for (route in linkProperties.routes) {
            val gateway = route.gateway
            if (gateway != null && route.isDefaultRoute && !gateway.isLoopbackAddress) {
                if (gateway is Inet4Address)
                    gatewayIpv4 = route.gateway?.hostAddress
                if (gateway is Inet6Address)
                    gatewayIpv6 = route.gateway?.hostAddress
            }
        }

        for (dns in linkProperties.dnsServers) {
            if (dns is Inet4Address && !dns.isLoopbackAddress)
                dnsIpv4 = dns.hostAddress
            if (dns is Inet6Address && !dns.isLoopbackAddress)
                dnsIpv6 = dns.hostAddress
        }

        val ipv4Address = networkInterfaceRepository.getLocalIp() ?: "N/A"
        val subnetMask = networkInterfaceRepository.getSubnet() ?: "N/A"
        val ipv6Address = networkInterfaceRepository.getIpv6() ?: "N/A"

        return ConnectionInfo(ipv4Address,subnetMask,gatewayIpv4,dnsIpv4,ipv6Address,gatewayIpv6,dnsIpv6)
    }

    override suspend fun getConnectionDetails(): Pair<ActiveConnection,ConnectionInfo>? {
        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)

        networkCapabilities?.let {
            val publicIpv4 = ipifyRepositoryImpl.getPublicIpv4()
            val publicIpv6 = ipifyRepositoryImpl.getPublicIpv6()

            val linkProperties = connectivityManager.getLinkProperties(network)
            val httpProxy = linkProperties?.httpProxy?.host

            if (it.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                val activeConnection = ActiveConnection("Wi-Fi",publicIpv4,publicIpv6,httpProxy)
                val connectionInfo = getConnectionInfo(network!!)
                return Pair(activeConnection,connectionInfo)
            }

            if (it.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                val activeConnection = ActiveConnection("Cell",publicIpv4,publicIpv6,httpProxy)
                val connectionInfo = getConnectionInfo(network!!)
                return Pair(activeConnection,connectionInfo)
            }

            if (it.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                val activeConnection = ActiveConnection("Ethernet",publicIpv4,publicIpv6,httpProxy)
                val connectionInfo = getConnectionInfo(network!!)
                return Pair(activeConnection,connectionInfo)
            }
        }

        return null
    }
}