package com.domin.sndt.core.di

import com.domin.sndt.core.domain.NetworkInterfaceRepository
import com.domin.sndt.core.network.NetworkInterfaceRepositoryImpl
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
    fun provideConnectivityRepository(): NetworkInterfaceRepository {
        return NetworkInterfaceRepositoryImpl()
    }
}