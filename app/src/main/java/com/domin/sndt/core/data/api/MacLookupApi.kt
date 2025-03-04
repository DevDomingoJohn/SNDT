package com.domin.sndt.core.data.api

import com.domin.sndt.BuildConfig
import com.skydoves.sandwich.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MacLookupApi {
    @GET("{mac}/company/name")
    suspend fun getVendorByMac(@Path("mac") mac: String, @Query("apiKey") apiKey: String = BuildConfig.API_KEY): ApiResponse<String>

    companion object {
        const val BASE_URL = "https://api.maclookup.app/v2/macs/"
    }
}