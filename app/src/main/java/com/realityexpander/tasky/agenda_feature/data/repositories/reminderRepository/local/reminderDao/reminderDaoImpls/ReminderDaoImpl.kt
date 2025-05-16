package com.realityexpander.tasky.agenda_feature.data.repositories.reminderRepository.local.reminderDao.reminderDaoImpls

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.realityexpander.tasky.agenda_feature.domain.ReminderId
import com.realityexpander.tasky.agenda_feature.data.repositories.reminderRepository.local.IReminderDao
import com.realityexpander.tasky.agenda_feature.data.repositories.reminderRepository.local.entities.ReminderEntity
import com.realityexpander.tasky.core.util.DAY_IN_SECONDS
import kotlinx.coroutines.flow.Flow
import java.time.ZonedDateTime


@Dao
interface ReminderDaoImpl : IReminderDao {

    // • CREATE

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    override suspend fun createReminder(reminder: ReminderEntity)

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

    @Query(
        """
        SELECT * FROM reminders 
            WHERE ( ( remindAt >= :startDateTime) AND (remindAt < :endDateTime) ) -- remindAt starts this day
                
        """
    )
    override fun getLocalRemindersForRemindAtDateTimeRangeFlow(
        startDateTime: ZonedDateTime,
        endDateTime: ZonedDateTime
    ): Flow<List<ReminderEntity>>


    // • UPDATE

    @Update
    override suspend fun updateReminder(reminder: ReminderEntity): Int


    // • UPSERT

    @Transaction
    override suspend fun upsertReminder(reminder: ReminderEntity) {
        val id = _insertReminder(reminder)
        if (id == -1L) {
            _updateReminder(reminder)
        }
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    @Suppress("FunctionName")
    suspend fun _insertReminder(reminder: ReminderEntity): Long

    @Update(onConflict = OnConflictStrategy.IGNORE)
    @Suppress("FunctionName")
    suspend fun _updateReminder(reminder: ReminderEntity)


    // • DELETE

    @Delete
    override suspend fun deleteReminder(reminder: ReminderEntity): Int

    @Query("DELETE FROM reminders WHERE id = :reminderId")
    override suspend fun deleteReminderById(reminderId: ReminderId): Int

    @Query("DELETE FROM reminders WHERE id IN (:reminderIds)")
    override suspend fun deleteRemindersByReminderIds(reminderIds: List<ReminderId>): Int

    @Query("DELETE FROM reminders")
    override suspend fun clearAllReminders(): Int

    // Deletes all SYNCED reminders for the given day.
    @Query(
        """
        DELETE FROM reminders WHERE 
            isSynced = 1
            AND ( ( `time` >= :zonedDateTime) AND (`time` < :zonedDateTime + ${DAY_IN_SECONDS}) ) -- reminder starts this today
        """)
    override suspend fun clearAllSyncedRemindersForDay(zonedDateTime: ZonedDateTime): Int

    companion object {

        const val getRemindersForDayQuery =
            """
            SELECT * FROM reminders 
                WHERE ( ( `time` >= :zonedDateTime) AND (`time` < :zonedDateTime + ${DAY_IN_SECONDS}) ) -- reminder starts this day
                
            """
    }
}
