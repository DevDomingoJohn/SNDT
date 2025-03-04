package com.domin.sndt.core.data.api

import com.skydoves.sandwich.onSuccess

class IpifyRepositoryImpl(
    private val ipifyApi: IpifyApi
) {
    suspend fun getPublicIpv4(): String? {
        var result: String? = null
        ipifyApi.getPublicIpv4().onSuccess {
            result = data
        }
        return result
    }
    suspend fun getPublicIpv6(): String? {
        var result: String? = null
        ipifyApi.getPublicIpv6().onSuccess {
            result = data
        }
        return result
    }
}