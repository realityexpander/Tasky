package com.realityexpander.tasky.di

import android.content.Context
import com.realityexpander.tasky.core.util.ConnectivityObserver.InternetConnectivityObserverImpl
import com.realityexpander.observeconnectivity.IInternetConnectivityObserver
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
    ): IInternetConnectivityObserver {
        return InternetConnectivityObserverImpl(context)
    }
}