package com.realityexpander.tasky

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.*
import com.realityexpander.tasky.agenda_feature.data.common.workers.RefreshAgendaWeekWorker
import com.realityexpander.tasky.agenda_feature.data.common.workers.SyncAgendaWorker
import dagger.hilt.android.HiltAndroidApp
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit
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

        // • Start the periodic SyncAgenda Worker (Clear the old one first)
        val syncAgendaWorkerConstraints: Constraints = Constraints.Builder().apply {
            setRequiredNetworkType(NetworkType.CONNECTED)
            setRequiresBatteryNotLow(true)
        }.build()
        val workRequest =
            PeriodicWorkRequestBuilder<SyncAgendaWorker>(10, TimeUnit.MINUTES)
                .setConstraints(syncAgendaWorkerConstraints)
                .setInitialDelay(5, TimeUnit.MINUTES)
                .addTag(SyncAgendaWorker.WORKER_NAME)
                .build()
        WorkManager.getInstance(applicationContext).cancelAllWorkByTag(SyncAgendaWorker.WORKER_NAME)
        WorkManager.getInstance(applicationContext).pruneWork()
        WorkManager.getInstance(applicationContext)
            .enqueue(workRequest)

        // • Start the 'Agenda Week' Refresh Worker
        val refreshAgendaWeekConstraints: Constraints = Constraints.Builder().apply {
            setRequiredNetworkType(NetworkType.CONNECTED)
        }.build()
        val data = Data.Builder()
            .putString(RefreshAgendaWeekWorker.PARAMETER_START_DATE,
                ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS).toString()
            )
            .build()
        val name = RefreshAgendaWeekWorker.WORKER_NAME
        val agendaWeekWorkRequest =
            OneTimeWorkRequestBuilder<RefreshAgendaWeekWorker>()
                .setConstraints(refreshAgendaWeekConstraints)
                .setInputData(data)
                .addTag(name)
                .addTag("For weeks around ${data.getString(RefreshAgendaWeekWorker.PARAMETER_START_DATE)}")
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .setBackoffCriteria(BackoffPolicy.LINEAR, 1, TimeUnit.MINUTES)
                .build()
        WorkManager.getInstance(applicationContext)
            .enqueueUniqueWork(
                name,
                ExistingWorkPolicy.REPLACE,
                agendaWeekWorkRequest
            )

    }
}