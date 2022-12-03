package com.realityexpander.tasky.agenda_feature.data.repositories.reminderRepository.local.reminderDao.reminderDaoImpls

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

    @Query("SELECT * FROM reminders WHERE id = :reminderId")
    override suspend fun getReminderById(reminderId: ReminderId): ReminderEntity?

    @Query("SELECT * FROM reminders")
    override suspend fun getReminders(): List<ReminderEntity>

    @Query("SELECT * FROM reminders")
    override fun getRemindersFlow(): Flow<List<ReminderEntity>>

    @Query(getRemindersForDayQuery)
    override suspend fun getRemindersForDay(zonedDateTime: ZonedDateTime): List<ReminderEntity>  // note: ZonedDateTime gets converted to UTC EpochSeconds for storage in the DB.

    @Query(getRemindersForDayQuery)
    override fun getRemindersForDayFlow(zonedDateTime: ZonedDateTime): Flow<List<ReminderEntity>>  // note: ZonedDateTime gets converted to UTC EpochSeconds for storage in the DB.


    // • UPDATE

    @Update
    override suspend fun updateReminder(reminder: ReminderEntity): Int


    // • DELETE

    @Query("DELETE FROM reminders WHERE id = :reminder")
    override suspend fun deleteReminderById(reminder: ReminderId): Int

    @Query("DELETE FROM reminders WHERE id IN (:reminderIds)")
    override suspend fun deleteRemindersByReminderIds(reminderIds: List<ReminderId>): Int

    @Delete
    override suspend fun deleteReminder(reminder: ReminderEntity): Int  // completely deletes the reminder.

    @Query("DELETE FROM reminders")
    override suspend fun clearAllReminders(): Int  // completely deletes all reminders.

    @Query(deleteRemindersForDayQuery)
    override suspend fun clearAllRemindersForDay(zonedDateTime: ZonedDateTime): Int // completely deletes all UNDELETED reminders for the given day.

    companion object {

        const val getRemindersForDayQuery =
            """
            SELECT * FROM reminders 
                WHERE ( ( `time` >= :zonedDateTime) AND (`time` < :zonedDateTime + ${DAY_IN_SECONDS}) ) -- reminder starts this day
                
            """

        const val deleteRemindersForDayQuery =
            """
            DELETE FROM reminders 
                WHERE ( ( `time` >= :zonedDateTime) AND (`time` < :zonedDateTime + ${DAY_IN_SECONDS}) ) -- reminder starts this today
            """
    }
}