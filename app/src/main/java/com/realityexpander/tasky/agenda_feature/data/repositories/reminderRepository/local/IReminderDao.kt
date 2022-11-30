package com.realityexpander.remindery.agenda_feature.data.repositories.reminderRepository.local

import com.realityexpander.tasky.agenda_feature.common.util.ReminderId
import com.realityexpander.tasky.agenda_feature.data.repositories.reminderRepository.local.entities.ReminderEntity
import kotlinx.coroutines.flow.Flow
import java.time.ZonedDateTime

interface IReminderDao {

    suspend fun createReminder(event: ReminderEntity)
    fun upsertReminder(event: ReminderEntity)

    suspend fun getRemindersForDay(zonedDateTime: ZonedDateTime): List<ReminderEntity>
    fun getRemindersForDayFlow(zonedDateTime: ZonedDateTime): Flow<List<ReminderEntity>>
    suspend fun getReminderById(reminderId: ReminderId): ReminderEntity?
    suspend fun getReminders(): List<ReminderEntity>    // only returns the events that are *NOT* marked as deleted.
    fun getRemindersFlow(): Flow<List<ReminderEntity>>  // only returns the events that are *NOT* marked as deleted.
    suspend fun getAllReminders(): List<ReminderEntity> // returns all events, including the deleted ones.

    suspend fun updateReminder(event: ReminderEntity): Int

    suspend fun markReminderDeletedById(reminderId: ReminderId): Int    // only marks the event as deleted
    suspend fun getMarkedDeletedReminderIds(): List<ReminderId>      // gets only the "isDeleted==true" events
    suspend fun deleteFinallyByReminderIds(reminderIds: List<ReminderId>): Int
    suspend fun deleteReminder(event: ReminderEntity): Int      // completely deletes the event.

    suspend fun clearAllReminders(): Int
    suspend fun clearAllRemindersForDay(zonedDateTime: ZonedDateTime): Int
}
