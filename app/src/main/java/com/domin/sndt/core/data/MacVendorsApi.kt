package com.domin.sndt.core.data

import com.skydoves.sandwich.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface MacVendorsApi {
    @GET("{mac}")
    suspend fun getVendorByMac(@Path("mac") mac: String): ApiResponse<String?>

    companion object {
        const val baseUrl = "https://api.macvendors.com/"
    }
}