package com.realityexpander.tasky.di

import android.content.Context
import com.realityexpander.tasky.agenda_feature.domain.IRemindAtAlarmManager
import com.realityexpander.tasky.agenda_feature.domain.IRemindAtNotificationManager
import com.realityexpander.tasky.core.presentation.notifications.RemindAtAlarmManagerImpl
import com.realityexpander.tasky.core.presentation.notifications.RemindAtNotificationManagerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RemindAtModule {

    //////////////////////////////////////////
    // Remind At Alarms & Notifications

    @Provides
    @Singleton
    fun provideRemindAtAlarmManager(@ApplicationContext context: Context): IRemindAtAlarmManager {
        return RemindAtAlarmManagerImpl(context)
    }

    @Provides
    @Singleton
    fun provideRemindAtNotificationManager(@ApplicationContext context: Context): IRemindAtNotificationManager {
        return RemindAtNotificationManagerImpl(context)
    }
}