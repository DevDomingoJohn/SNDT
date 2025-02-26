package com.domin.sndt.core.data

import com.skydoves.sandwich.ApiResponse
import retrofit2.http.GET

interface IpifyApi {
    @GET(BASE_URL)
    suspend fun getPublicIpv4(): ApiResponse<String>

    @GET("https://api6.ipify.org")
    suspend fun getPublicIpv6(): ApiResponse<String>

    companion object {
        const val BASE_URL = "https://api.ipify.org"
    }
}