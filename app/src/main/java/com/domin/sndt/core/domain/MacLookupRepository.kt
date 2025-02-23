package com.domin.sndt.core.domain

interface MacLookupRepository {
    suspend fun getVendorByMac(mac: String?): String?
}