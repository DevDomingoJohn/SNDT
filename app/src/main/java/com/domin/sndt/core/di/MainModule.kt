package com.domin.sndt.core.di

import android.app.Application
import android.content.Context
import android.net.wifi.WifiManager
import com.domin.sndt.core.domain.NetworkInterfaceRepository
import com.domin.sndt.core.domain.WifiManagerRepository
import com.domin.sndt.core.network.NetworkInterfaceRepositoryImpl
import com.domin.sndt.core.network.WifiManagerRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
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
    fun provideNetworkInterfaceRepository(): NetworkInterfaceRepository {
        return NetworkInterfaceRepositoryImpl()
    }
}