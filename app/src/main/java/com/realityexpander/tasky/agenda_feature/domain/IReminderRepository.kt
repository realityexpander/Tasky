package com.realityexpander.tasky.agenda_feature.domain

import com.realityexpander.tasky.agenda_feature.common.util.ReminderId
import kotlinx.coroutines.flow.Flow
import java.time.ZonedDateTime

interface IReminderRepository {
    suspend fun createReminder(task: AgendaItem.Reminder): ResultUiText<Void>
    suspend fun upsertReminderLocally(task: AgendaItem.Reminder): ResultUiText<Void>

    suspend fun getRemindersForDay(zonedDateTime: ZonedDateTime): List<AgendaItem.Reminder>
    fun getRemindersForDayFlow(zonedDateTime: ZonedDateTime): Flow<List<AgendaItem.Reminder>>
    suspend fun getReminder(taskId: ReminderId): AgendaItem.Reminder?

    suspend fun updateReminder(task: AgendaItem.Reminder): ResultUiText<Void>

    // only marks the Reminder as deleted
    suspend fun deleteReminder(taskId: ReminderId): ResultUiText<Void>

    // gets only the "marked as deleted" Reminders
    suspend fun getDeletedReminderIdsLocally(): List<ReminderId>
    suspend fun deleteRemindersFinallyLocally(ReminderIds: List<ReminderId>): ResultUiText<Void>

    suspend fun clearAllRemindersLocally(): ResultUiText<Void>
    suspend fun clearRemindersForDayLocally(zonedDateTime: ZonedDateTime): ResultUiText<Void>
}
