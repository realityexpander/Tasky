package com.realityexpander.tasky.agenda_feature.data.common.workers

import android.content.Context
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.realityexpander.tasky.R
import com.realityexpander.tasky.agenda_feature.domain.IAgendaRepository
import com.realityexpander.tasky.agenda_feature.domain.IWorkerNotifications
import com.realityexpander.tasky.agenda_feature.domain.ResultUiText
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay
import logcat.logcat
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit

// Worker to synchronize offline actions for the Agenda for the current day
//   & download any new items for the current day.
@HiltWorker
class SyncAgendaWorker @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted val workerParams: WorkerParameters,
    val agendaRepository: IAgendaRepository,
    private val workerNotifications: IWorkerNotifications
): CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {

        logcat { "SyncAgendaWorker.doWork() attemptedRuns: ${workerParams.runAttemptCount}" }
        workerParams.log()

        workerNotifications.showNotification(
            workerNotifications.createNotification(
                WORKER_NOTIFICATION_CHANNEL_ID,
                title = context.getString(R.string.agenda_sync_notification_title),
                description = context.getString(R.string.agenda_sync_uploading_items_text),
                icon = R.drawable.ic_notification_sync_upload_foreground,
                iconTintColor = ResourcesCompat.getColor(
                    context.resources,
                    R.color.tasky_green, null),
                largeIcon = ResourcesCompat.getDrawable(
                    context.resources,
                    R.drawable.tasky_logo_for_splash, null
                )?.toBitmap(100,100)
            ),
            SyncAgendaWorker.NOTIFICATION_ID
        )

        // Push up local changes to remote
        val resultSyncAgenda = agendaRepository.syncAgenda()
        if(resultSyncAgenda is ResultUiText.Success) {

            // Fetch the latest remote changes for today
            val resultUpdateLocalAgenda = agendaRepository.updateLocalAgendaDayFromRemote(
                ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS)
            )

            delay(3000) // prevent flashing notification
            workerNotifications.clearNotification(NOTIFICATION_ID)

            return when (resultUpdateLocalAgenda) {
                is ResultUiText.Success -> Result.success()
                is ResultUiText.Error -> Result.failure()
            }
        }
        workerNotifications.clearNotification(NOTIFICATION_ID)

        return Result.failure()
    }

    companion object {
        const val WORKER_NAME = "SYNC_AGENDA_WORKER"
        const val NOTIFICATION_ID = 100002
    }
}

// • Start the periodic SyncAgenda Worker (Clear the old one first)
fun startSyncAgendaWorker(applicationContext: Context) {
    val syncAgendaWorkerConstraints: Constraints = Constraints.Builder().apply {
        setRequiredNetworkType(NetworkType.CONNECTED)
        setRequiresBatteryNotLow(true)
    }.build()
    val workRequest =
        PeriodicWorkRequestBuilder<SyncAgendaWorker>(15, TimeUnit.MINUTES)
            .setConstraints(syncAgendaWorkerConstraints)
            .setInitialDelay(2, TimeUnit.MINUTES)
            .addTag(SyncAgendaWorker.WORKER_NAME)
            .addTag(TASKY_WORKERS_TAG)
            .build()
    WorkManager.getInstance(applicationContext).apply {
        cancelAllWorkByTag(SyncAgendaWorker.WORKER_NAME)
        pruneWork()
        enqueue(workRequest)
    }
}