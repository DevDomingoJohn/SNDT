package com.domin.sndt.core.data

import com.domin.sndt.core.domain.MacLookupRepository
import com.skydoves.sandwich.onSuccess

class MacLookupRepositoryImpl(
    private val macLookupApi: MacLookupApi
):MacLookupRepository {
    override suspend fun getVendorByMac(mac: String?): String? {
        var result: String? = null
        if (mac != null) {
            macLookupApi.getVendorByMac(mac).onSuccess {
                if (data != "*NO COMPANY*") result = data
            }
        }
        return result
    }
}