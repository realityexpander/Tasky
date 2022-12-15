package com.realityexpander.tasky.di

import android.content.Context
import com.realityexpander.tasky.agenda_feature.data.common.workers.AgendaWorkersSchedulerImpl
import com.realityexpander.tasky.agenda_feature.data.common.workers.RefreshAgendaWeekWorker
import com.realityexpander.tasky.agenda_feature.data.common.workers.SyncAgendaWorker
import com.realityexpander.tasky.agenda_feature.domain.IAgendaWorkersScheduler
import com.realityexpander.tasky.agenda_feature.domain.IWorkerScheduler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WorkerSchedulerModule {

    /////////// WORKERS ///////////

    @Provides
    @Singleton
    fun provideAllAgendaWorkersScheduler(
        @ApplicationContext context: Context,
        @Named("SyncAgendaWorkerScheduler")
        syncAgendaWorkerScheduler: IWorkerScheduler,
        @Named("RefreshAgendaWeekWorkerScheduler")
        refreshAgendaWeekWorkerScheduler: IWorkerScheduler,
    ): IAgendaWorkersScheduler {
        return AgendaWorkersSchedulerImpl(
            context,
            syncAgendaWorkerScheduler,
            refreshAgendaWeekWorkerScheduler
        )
    }

    @Provides
    @Singleton
    @Named("SyncAgendaWorkerScheduler")
    fun provideSyncAgendaWorkerScheduler(
        @ApplicationContext context: Context
    ): IWorkerScheduler {
        return SyncAgendaWorker.WorkerScheduler(context)
    }

    @Provides
    @Singleton
    @Named("RefreshAgendaWeekWorkerScheduler")
    fun provideRefreshAgendaWeekWorkerScheduler(
        @ApplicationContext context: Context
    ): IWorkerScheduler {
        return RefreshAgendaWeekWorker.WorkerScheduler(context)
    }
}