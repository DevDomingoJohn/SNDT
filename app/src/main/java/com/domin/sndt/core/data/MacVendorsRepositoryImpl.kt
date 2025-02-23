package com.domin.sndt.core.data

import com.domin.sndt.core.domain.MacVendorsRepository
import com.skydoves.sandwich.onSuccess

class MacVendorsRepositoryImpl(
    private val macVendorsApi: MacVendorsApi
):MacVendorsRepository {
    override suspend fun getVendorByMac(mac: String?): String? {
        var result: String? = null
        if (mac != null) {
            macVendorsApi.getVendorByMac(mac).onSuccess {
                result = data
            }
        }
        return result
    }
}