package com.realityexpander.remindery.agenda_feature.data.repositories.reminderRepository.local

import com.realityexpander.tasky.agenda_feature.common.util.ReminderId
import com.realityexpander.tasky.agenda_feature.data.repositories.reminderRepository.local.entities.ReminderEntity
import kotlinx.coroutines.flow.Flow
import java.time.ZonedDateTime

interface IReminderDao {

    suspend fun createReminder(reminder: ReminderEntity)
    fun upsertReminder(reminder: ReminderEntity)

    suspend fun getRemindersForDay(zonedDateTime: ZonedDateTime): List<ReminderEntity>
    fun getRemindersForDayFlow(zonedDateTime: ZonedDateTime): Flow<List<ReminderEntity>>
    suspend fun getReminderById(reminderId: ReminderId): ReminderEntity?
    suspend fun getReminders(): List<ReminderEntity>
    fun getRemindersFlow(): Flow<List<ReminderEntity>>

    suspend fun updateReminder(reminder: ReminderEntity): Int

    suspend fun deleteReminderById(reminder: ReminderId): Int
    suspend fun deleteRemindersByReminderIds(reminderIds: List<ReminderId>): Int
    suspend fun deleteReminder(reminder: ReminderEntity): Int      // completely deletes the event.

    suspend fun clearAllReminders(): Int
    suspend fun clearAllRemindersForDay(zonedDateTime: ZonedDateTime): Int
}
