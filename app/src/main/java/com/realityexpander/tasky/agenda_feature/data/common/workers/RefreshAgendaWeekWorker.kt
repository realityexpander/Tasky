package com.realityexpander.tasky.agenda_feature.data.common.workers

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.realityexpander.tasky.R
import com.realityexpander.tasky.agenda_feature.data.common.utils.getDateForDayOffset
import com.realityexpander.tasky.agenda_feature.domain.IAgendaRepository
import com.realityexpander.tasky.agenda_feature.domain.ResultUiText
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import java.time.ZonedDateTime

// Worker to refresh Agenda data for 10 days around the `startDate`
@HiltWorker
class RefreshAgendaWeekWorker @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted val workerParams: WorkerParameters,
    val agendaRepository: IAgendaRepository,
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
//        Log.d("RefreshAgendaWeekWorker.doWork()",  // leave this here for debugging
//                " attemptedRuns: ${workerParams.runAttemptCount}" +
//                " startDate: ${workerParams.inputData.getString(PARAMETER_START_DATE)}")

        val startDate = workerParams.inputData.getString(PARAMETER_START_DATE)?.let {
            getDateForDayOffset(ZonedDateTime.parse(it), 0)
        }

        // Fetch/Refresh the previous and coming week's Agenda items
        val success =
            (START_DAY_OFFSET..END_DAY_OFFSET).map { dayOffset ->
                if (dayOffset != 0) { // don't refresh the current day
                    val date = getDateForDayOffset(startDate, dayOffset)
                    return@map CoroutineScope(Dispatchers.IO).async {
                        agendaRepository.updateLocalAgendaDayFromRemote(date)
                    }
                }

                CoroutineScope(Dispatchers.IO).async {
                    ResultUiText.Success(Unit)
                }
            }
            //.awaitAll()  // will cancel all if any of the `async`s fail (LEAVE FOR REFERENCE)
            .map {// will NOT cancel all if any async fails (unlike .awaitAll())
                it.await() is ResultUiText.Success // return true if success
            }.all { success ->
                success == true   // if any of the async's failed, return retry
            }

        return if (success) {
            Result.success()
        } else {
            Result.retry()
        }
    }

    companion object {
        const val WORKER_NAME = "REFRESH_AGENDA_WEEK_WORKER"
        const val NOTIFICATION_ID = 100001
        const val NOTIFICATION_CHANNEL_ID = NOTIFICATION_SYNC_WORKER_CHANNEL_ID

        const val PARAMETER_START_DATE = "startDate"
        const val START_DAY_OFFSET = -5
        const val END_DAY_OFFSET = 5
    }

    init {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            context.getString(R.string.agenda_sync_refresh_worker_human_readable_notification_channel),
            NotificationManager.IMPORTANCE_LOW,
        )
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return ForegroundInfo(NOTIFICATION_ID, createNotification())
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(context.getString(R.string.agenda_sync_notification_title))
            .setContentText(context.getString(R.string.agenda_sync_notification_content_text))
            .setSmallIcon(R.drawable.ic_notification_sync_agenda_small_icon_foreground)
            .setColor(ResourcesCompat.getColor(context.resources, R.color.tasky_green, null))
            .setLargeIcon(ResourcesCompat.getDrawable(context.resources, R.drawable.tasky_logo_for_splash, null)?.toBitmap(100,100))
            .build()
    }
}