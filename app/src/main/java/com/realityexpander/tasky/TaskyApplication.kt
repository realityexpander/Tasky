package com.realityexpander.tasky

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.realityexpander.tasky.agenda_feature.data.common.workers.RefreshAgendaWeekWorker
import com.realityexpander.tasky.agenda_feature.data.common.workers.SyncAgendaWorker
import dagger.hilt.android.HiltAndroidApp
import logcat.AndroidLogcatLogger
import logcat.LogPriority
import logcat.logcat
import javax.inject.Inject

@HiltAndroidApp
class TaskyApplication: Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build()
    }

    override fun onCreate() {
        super.onCreate()

        // Log all priorities in debug builds, no-op in release builds.
        AndroidLogcatLogger.installOnDebuggableApp(this, minPriority = LogPriority.VERBOSE)

        logcat { "TaskyApplication.onCreate(): starting Tasky workers" }
//        startSyncAgendaWorker(applicationContext)
//        startRefreshAgendaWeekWorker(applicationContext)
        SyncAgendaWorker.startWorker(applicationContext)
        RefreshAgendaWeekWorker.startWorker(applicationContext)
    }
}