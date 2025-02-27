package com.domin.sndt.info

data class ActiveConnectionState(
    val connectionType: String = "None",
    val externalIp: String = "N/A",
    val externalIpv6: String = "N/A",
    val httpProxy: String = "N/A",
)

data class WifiConnectionState(
    val ipv4Address: String = "N/A",
    val subnetMask: String = "N/A",
    val gatewayIpv4: String = "N/A",
    val dnsServerIpv4: String = "N/A",
    val ipv6Address: String = "N/A",
    val gatewayIpv6: String = "N/A",
    val dnsServerIpv6: String = "N/A",
)

data class WifiDetailsState(
    val wifiEnabled: String = "No",
    val connectionState: String = "Disconnected",
    val dhcpLeaseTime: String? = null,
    val ssid: String = "N/A",
    val bssid: String = "N/A",
    val channel: String = "N/A",
    val speed: String = "N/A",
    val signalStrength: String = "N/A"
)