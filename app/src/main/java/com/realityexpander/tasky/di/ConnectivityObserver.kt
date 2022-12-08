package com.realityexpander.tasky.di

import android.content.Context
import com.realityexpander.observeconnectivity.ConnectivityObserverImpl
import com.realityexpander.observeconnectivity.IConnectivityObserver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ConnectivityObserverModule {

    @Provides
    @Singleton
    fun provideConnectivityObserverProd(
        @ApplicationContext context: Context
    ): IConnectivityObserver {
        return ConnectivityObserverImpl(context)
    }
}