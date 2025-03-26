package com.domin.sndt.info

data class ActiveConnection(
    val connectionType: String?,
    val externalIp: String?,
    val externalIpv6: String?,
    val httpProxy: String?
)

data class ConnectionInfo(
    val ipv4Address: String?,
    val subnetMask: String?,
    val gatewayIpv4: String?,
    val dnsServerIpv4: String?,
    val ipv6Address: String?,
    val gatewayIpv6: String?,
    val dnsServerIpv6: String?
)

data class WifiDetails(
    val wifiEnabled: Boolean = false,
    val connectionState: String?,
    val dhcpLeaseTime: Int?,
    val ssid: String?,
    val bssid: String?,
    val channel: String?,
    val speed: String?,
    val signalStrength: String?
)

data class CellDetails(
    val dataState: Int?,
    val dataActivity: Int?,
    val roaming: Boolean = false,
    val simState: Int?,
    val simName: String?,
    val simMccMnc: String?,
    val operatorName: String?,
    val networkType: Int?,
    val phoneType: Int?
)