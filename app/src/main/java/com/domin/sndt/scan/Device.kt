package com.domin.sndt.scan

data class Device(
    val label: String? = null,
    val ipAddress: String,
    val macAddress: String? = null,
    val vendor: String? = null
)