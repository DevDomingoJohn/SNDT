package com.domin.sndt.core.data

import com.skydoves.sandwich.onSuccess

class MacLookupRepositoryImpl(
    private val macLookupApi: MacLookupApi
) {
    suspend fun getVendorByMac(mac: String?): String? {
        var result: String? = null
        if (mac != null) {
            macLookupApi.getVendorByMac(mac).onSuccess {
                if (data != "*NO COMPANY*") result = data
            }
        }
        return result
    }
}