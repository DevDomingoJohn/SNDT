package com.domin.sndt

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
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
    fun provideConnectivityManager(context: Application): ConnectivityManager {
        return context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    @Provides
    @Singleton
    fun provideConnectivityRepository(connectivityManager: ConnectivityManager): ConnectivityRepository {
        return ConnectivityRepositoryImpl(connectivityManager)
    }
}