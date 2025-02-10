package com.domin.sndt.info

data class UIState(
    val connectionType: String = "None",
    val externalIp: String = "N/A",
    val externalIpv6: String = "N/A",
    val httpProxy: String = "N/A",

    val wifiEnabled: Boolean = false,
    val connectionState: String = "Disconnected",
    val dhcpLeaseTime: String = "N/A",
    val ssid: String = "N/A",
    val vendor: String = "N/A",
    val channel: String = "N/A",
    val speed: String = "N/A",
    val signalStrength: String = "N/A"
)
