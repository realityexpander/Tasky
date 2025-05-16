package com.realityexpander.tasky.di

import android.content.Context
import com.realityexpander.tasky.core.util.internetConnectivityObserver.IInternetConnectivityObserver
import com.realityexpander.tasky.core.util.internetConnectivityObserver.InternetConnectivityObserverImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object InternetConnectivityObserverModule {

    @Provides
    @Singleton
    fun provideInternetConnectivityObserverProd(
        @ApplicationContext context: Context
    ): IInternetConnectivityObserver {
        return InternetConnectivityObserverImpl(context)
    }
}
