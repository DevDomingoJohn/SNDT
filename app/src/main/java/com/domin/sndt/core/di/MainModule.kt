package com.domin.sndt.core.di

import android.app.Application
import android.content.Context
import android.net.wifi.WifiManager
import com.domin.sndt.core.data.MacLookupApi
import com.domin.sndt.core.data.MacLookupRepositoryImpl
import com.domin.sndt.core.domain.NetworkInterfaceRepository
import com.domin.sndt.core.domain.WifiManagerRepository
import com.domin.sndt.core.data.network.NetworkInterfaceRepositoryImpl
import com.domin.sndt.core.data.network.WifiManagerRepositoryImpl
import com.domin.sndt.core.domain.MacLookupRepository
import com.skydoves.sandwich.retrofit.adapters.ApiResponseCallAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MainModule {

    @Provides
    @Singleton
    fun provideWifiManager(context: Application): WifiManager {
        return context.getSystemService(Context.WIFI_SERVICE) as WifiManager
    }

    @Provides
    @Singleton
    fun provideWifiManagerRepository(wifiManager: WifiManager): WifiManagerRepository {
        return WifiManagerRepositoryImpl(wifiManager)
    }

    @Provides
    @Singleton
    fun provideMacLookupApi(): MacLookupApi =
        Retrofit.Builder()
            .baseUrl(MacLookupApi.BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addCallAdapterFactory(ApiResponseCallAdapterFactory.create())
            .build()
            .create(MacLookupApi::class.java)

    @Provides
    @Singleton
    fun provideMacLookupRepository(macLookupApi: MacLookupApi): MacLookupRepository =
        MacLookupRepositoryImpl(macLookupApi)

    @Provides
    @Singleton
    fun provideNetworkInterfaceRepository(macLookupRepository: MacLookupRepository): NetworkInterfaceRepository {
        return NetworkInterfaceRepositoryImpl(macLookupRepository)
    }
}