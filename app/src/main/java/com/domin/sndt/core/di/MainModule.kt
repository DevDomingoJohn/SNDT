package com.domin.sndt.core.di

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.telephony.TelephonyManager
import com.domin.sndt.core.data.api.IpifyApi
import com.domin.sndt.core.data.api.IpifyRepositoryImpl
import com.domin.sndt.core.data.api.MacLookupApi
import com.domin.sndt.core.data.api.MacLookupRepositoryImpl
import com.domin.sndt.core.domain.NetworkInterfaceRepository
import com.domin.sndt.core.domain.ConnectivityManagerRepository
import com.domin.sndt.core.data.network.NetworkInterfaceRepositoryImpl
import com.domin.sndt.core.data.network.ConnectivityManagerRepositoryImpl
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
    fun provideConnectivityManager(context: Application): ConnectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    @Provides
    @Singleton
    fun provideWifiManager(context: Application): WifiManager =
        context.getSystemService(Context.WIFI_SERVICE) as WifiManager

    @Provides
    @Singleton
    fun provideTelephonyManager(context: Application): TelephonyManager =
        context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

    @Provides
    @Singleton
    fun provideConnectivityManagerRepository(
        wifiManager: WifiManager,
        connectivityManager: ConnectivityManager,
        telephonyManager: TelephonyManager,
        networkInterfaceRepository: NetworkInterfaceRepository,
        ipifyRepositoryImpl: IpifyRepositoryImpl
    ): ConnectivityManagerRepository =
        ConnectivityManagerRepositoryImpl(wifiManager, connectivityManager, telephonyManager, networkInterfaceRepository,ipifyRepositoryImpl)

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
    fun provideIpifyApi(): IpifyApi =
        Retrofit.Builder()
            .baseUrl(IpifyApi.BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addCallAdapterFactory(ApiResponseCallAdapterFactory.create())
            .build()
            .create(IpifyApi::class.java)

    @Provides
    @Singleton
    fun provideMacLookupRepositoryImpl(macLookupApi: MacLookupApi): MacLookupRepositoryImpl =
        MacLookupRepositoryImpl(macLookupApi)

    @Provides
    @Singleton
    fun provideIpifyRepositoryImpl(ipifyApi: IpifyApi): IpifyRepositoryImpl =
        IpifyRepositoryImpl(ipifyApi)

    @Provides
    @Singleton
    fun provideNetworkInterfaceRepository(macLookupRepositoryImpl: MacLookupRepositoryImpl): NetworkInterfaceRepository =
        NetworkInterfaceRepositoryImpl(macLookupRepositoryImpl)
}