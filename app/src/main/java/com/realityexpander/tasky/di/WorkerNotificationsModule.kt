package com.realityexpander.tasky.di

import android.content.Context
import com.realityexpander.tasky.agenda_feature.data.common.workers.WorkerNotificationsImpl
import com.realityexpander.tasky.agenda_feature.domain.IWorkerNotifications
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WorkerNotificationsModule {

    @Provides
    @Singleton
    fun provideWorkerNotifications(@ApplicationContext context: Context): IWorkerNotifications {
        return WorkerNotificationsImpl(context)
    }
}