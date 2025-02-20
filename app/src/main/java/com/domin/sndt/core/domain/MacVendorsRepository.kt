package com.domin.sndt.core.domain

interface MacVendorsRepository {
    suspend fun getVendorByMac(mac: String?): String?
}