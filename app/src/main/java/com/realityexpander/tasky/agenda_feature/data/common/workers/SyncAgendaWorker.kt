package com.realityexpander.tasky.agenda_feature.data.common.workers

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.realityexpander.tasky.R
import com.realityexpander.tasky.agenda_feature.domain.IAgendaRepository
import com.realityexpander.tasky.agenda_feature.domain.ResultUiText
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

// Worker to synchronize offline actions for the Agenda for the current day
//   & download any new items for the current day.
@HiltWorker
class SyncAgendaWorker @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted val workerParams: WorkerParameters,
    val agendaRepository: IAgendaRepository
): CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {

        Log.d("SyncAgendaWorker", "SyncAgendaWorker.doWork()attemptedRuns: ${workerParams.runAttemptCount}")
        showNotification(createNotification())

        // Push up local changes to remote
        val resultSyncAgenda = agendaRepository.syncAgenda()
        if(resultSyncAgenda is ResultUiText.Success) {

            // Fetch the latest remote changes for today
            val resultUpdateLocalAgenda = agendaRepository.updateLocalAgendaDayFromRemote(
                ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS)
            )

            delay(3000) // prevent flashing notification
            clearNotification()

            return when (resultUpdateLocalAgenda) {
                is ResultUiText.Success -> Result.success()
                is ResultUiText.Error -> Result.failure()
            }
        }
        clearNotification()

        return Result.failure()
    }

    companion object {
        const val WORKER_NAME = "SYNC_AGENDA_WORKER"
        const val NOTIFICATION_ID = 100002
        const val NOTIFICATION_CHANNEL_ID = NOTIFICATION_SYNC_WORKER_CHANNEL_ID
    }

    init {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            WORKER_NAME,
            NotificationManager.IMPORTANCE_LOW,
        )
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun showNotification(notification: Notification) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun clearNotification() {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(NOTIFICATION_ID)
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(context.getString(R.string.agenda_sync_notification_title))
            .setContentText(context.getString(R.string.agenda_sync_uploading_items_text))
            .setSmallIcon(R.drawable.ic_notification_sync_upload_foreground)
            .setColor(ResourcesCompat.getColor(context.resources, R.color.tasky_green, null))
            .setLargeIcon(ResourcesCompat.getDrawable(context.resources, R.drawable.tasky_logo_for_splash, null)?.toBitmap(100,100))
            .setAutoCancel(true)
            .build()
    }
}