package com.realityexpander.remindery.agenda_feature.data.repositories.reminderRepository.local.reminderDao.reminderDaoImpls

import androidx.room.*
import com.realityexpander.remindery.agenda_feature.data.repositories.reminderRepository.local.IReminderDao
import com.realityexpander.tasky.agenda_feature.common.util.ReminderId
import com.realityexpander.tasky.agenda_feature.data.repositories.reminderRepository.local.entities.ReminderEntity
import com.realityexpander.tasky.core.util.DAY_IN_SECONDS
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import java.time.ZonedDateTime


@Dao
interface ReminderDaoImpl : IReminderDao {

    // • CREATE

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    override suspend fun createReminder(reminder: ReminderEntity)


    // • UPSERT

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertReminder(reminder: ReminderEntity): Long

    @Update(onConflict = OnConflictStrategy.IGNORE)
    fun update2Reminder(reminder: ReminderEntity)

    @Transaction
    override fun upsertReminder(reminder: ReminderEntity) {
        val id = insertReminder(reminder)
        if (id == -1L) {
            update2Reminder(reminder)
        }
    }


    // • READ

    @Query("SELECT * FROM reminders WHERE id = :reminderId AND isDeleted = 0")  // only returns the reminders that are *NOT* marked as deleted.
    override suspend fun getReminderById(reminderId: ReminderId): ReminderEntity?

    @Query("SELECT * FROM reminders WHERE isDeleted = 0")  // only returns the reminders that are *NOT* marked as deleted
    override suspend fun getReminders(): List<ReminderEntity>

    @Query("SELECT * FROM reminders")                      // returns all reminders (marked deleted or not)
    override suspend fun getAllReminders(): List<ReminderEntity>

    @Query("SELECT * FROM reminders WHERE isDeleted = 0")  // only returns the reminders that are *NOT* marked as deleted.
    override fun getRemindersFlow(): Flow<List<ReminderEntity>>

    @Query(getRemindersForDayQuery)
    override suspend fun getRemindersForDay(zonedDateTime: ZonedDateTime): List<ReminderEntity>  // note: ZonedDateTime gets converted to UTC EpochSeconds for storage in the DB.

    @Query(getRemindersForDayQuery)
    override fun getRemindersForDayFlow(zonedDateTime: ZonedDateTime): Flow<List<ReminderEntity>>  // note: ZonedDateTime gets converted to UTC EpochSeconds for storage in the DB.


    // • UPDATE

    @Update
    override suspend fun updateReminder(reminder: ReminderEntity): Int


    // • DELETE

    @Query("UPDATE reminders SET isDeleted = 1 WHERE id = :reminderId")
    override suspend fun markReminderDeletedById(reminderId: ReminderId): Int   // only marks the reminder as deleted.

    @Query("SELECT id FROM reminders WHERE isDeleted = 1")
    override suspend fun getMarkedDeletedReminderIds(): List<ReminderId>

    @Query("DELETE FROM reminders WHERE id IN (:reminderIds)")
    override suspend fun deleteFinallyByReminderIds(reminderIds: List<ReminderId>): Int  // completely deletes the reminders.

    @Delete
    override suspend fun deleteReminder(reminder: ReminderEntity): Int  // completely deletes the reminder.

    @Query("DELETE FROM reminders")
    override suspend fun clearAllReminders(): Int  // completely deletes all reminders.

    @Query(deleteRemindersForDayQuery)
    override suspend fun clearAllRemindersForDay(zonedDateTime: ZonedDateTime): Int // completely deletes all UNDELETED reminders for the given day.

    companion object {

        const val getRemindersForDayQuery =
            """
            SELECT * FROM reminders WHERE isDeleted = 0 
                AND 
                    ( ( `time` >= :zonedDateTime) AND (`time` < :zonedDateTime + ${DAY_IN_SECONDS}) ) -- reminder starts this day
                
            """

        const val deleteRemindersForDayQuery =
            """
            DELETE FROM reminders WHERE isDeleted = 0 
                AND 
                    ( ( `time` >= :zonedDateTime) AND (`time` < :zonedDateTime + ${DAY_IN_SECONDS}) ) -- `time` start this today
            """
    }
}