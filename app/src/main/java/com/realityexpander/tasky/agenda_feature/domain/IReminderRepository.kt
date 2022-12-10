package com.realityexpander.tasky.agenda_feature.domain

import com.realityexpander.tasky.agenda_feature.common.util.ReminderId
import kotlinx.coroutines.flow.Flow
import java.time.ZonedDateTime

interface IReminderRepository {
    suspend fun createReminder(reminder: AgendaItem.Reminder, isRemoteOnly: Boolean = false): ResultUiText<Void>
    suspend fun upsertReminderLocally(reminder: AgendaItem.Reminder): ResultUiText<Void>

    suspend fun getRemindersForDay(zonedDateTime: ZonedDateTime): List<AgendaItem.Reminder>
    fun getRemindersForDayFlow(zonedDateTime: ZonedDateTime): Flow<List<AgendaItem.Reminder>>
    suspend fun getReminder(reminderId: ReminderId, isLocalOnly: Boolean = false): AgendaItem.Reminder?
    fun getRemindersForRemindAtDateTimeRangeFlow(from: ZonedDateTime, to: ZonedDateTime): Flow<List<AgendaItem.Reminder>>

    suspend fun updateReminder(reminder: AgendaItem.Reminder, isRemoteOnly: Boolean = false): ResultUiText<Void>

    suspend fun deleteReminder(reminder: AgendaItem.Reminder): ResultUiText<Void>

    suspend fun clearAllRemindersLocally(): ResultUiText<Void>
    suspend fun clearRemindersForDayLocally(zonedDateTime: ZonedDateTime): ResultUiText<Void>
}
