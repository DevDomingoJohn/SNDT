package com.domin.sndt.scan

data class Device(
    val hostname: String? = null,
    val ipAddress: String,
    val macAddress: String? = null
)