package com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.local.eventDao.eventDaoImpls

import androidx.room.*
import com.realityexpander.tasky.agenda_feature.common.util.EventId
import com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.local.IEventDao
import com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.local.entities.EventEntity
import com.realityexpander.tasky.core.util.DAY_IN_SECONDS
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import java.time.ZonedDateTime


@Dao
interface EventDaoImpl : IEventDao {

    // • CREATE

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    override suspend fun createEvent(event: EventEntity)


    // • UPSERT

    @Transaction
    override fun upsertEvent(event: EventEntity) {
        val id = _upsertEventExecInsertEvent(event)
        if (id == -1L) {
            _upsertEventExecUpdateEvent(event)
        }
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun _upsertEventExecInsertEvent(event: EventEntity): Long

    @Update(onConflict = OnConflictStrategy.IGNORE)
    fun _upsertEventExecUpdateEvent(event: EventEntity)


    // • READ

    @Query("SELECT * FROM events WHERE id = :eventId")
    override suspend fun getEventById(eventId: EventId): EventEntity?

    @Query("SELECT * FROM events")
    override suspend fun getEvents(): List<EventEntity>

    @Query("SELECT * FROM events")
    override fun getEventsFlow(): Flow<List<EventEntity>>

    @Query(getEventsForDayQuery)
    override suspend fun getEventsForDay(zonedDateTime: ZonedDateTime): List<EventEntity>  // note: ZonedDateTime gets converted to UTC EpochSeconds for storage in the DB.

    @Query(getEventsForDayQuery)
    override fun getEventsForDayFlow(zonedDateTime: ZonedDateTime): Flow<List<EventEntity>>  // note: ZonedDateTime gets converted to UTC EpochSeconds for storage in the DB.


    // • UPDATE

    @Update
    override suspend fun updateEvent(event: EventEntity): Int


    // • DELETE

    @Query("DELETE FROM events WHERE id IN (:eventIds)")
    override suspend fun deleteByEventIds(eventIds: List<EventId>): Int

    @Delete
    override suspend fun deleteEvent(event: EventEntity): Int

    @Query("DELETE FROM events WHERE id = :eventId")
    override suspend fun deleteEventById(eventId: EventId): Int

    @Query("DELETE FROM events")
    override suspend fun clearAllEvents(): Int

    // Deletes all SYNCED events for the given day.
    @Query(deleteEventsForDayQuery)
    override suspend fun clearAllSyncedEventsForDay(zonedDateTime: ZonedDateTime): Int

    companion object {

        const val getEventsForDayQuery =
            """
            SELECT * FROM events WHERE  
                (
                    ( ( `from` >= :zonedDateTime) AND (`from`   < :zonedDateTime + ${DAY_IN_SECONDS}) ) -- event starts within day

                  --      ( ( `from` >= :zonedDateTime) AND (`to`   < :zonedDateTime + ${DAY_IN_SECONDS}) ) -- event fits within day
                  --    OR
                  --      ( ( `from` >  :zonedDateTime) AND (`from` < :zonedDateTime + ${DAY_IN_SECONDS}) ) -- `from` starts on day
                  --    OR
                  --      ( ( `to`   >  :zonedDateTime) AND (`to`   < :zonedDateTime + ${DAY_IN_SECONDS}) ) -- `to` ends on day
                  --    OR
                  --      ( ( `from` <= :zonedDateTime) AND (`to`   > :zonedDateTime + ${DAY_IN_SECONDS}) ) -- event straddles day     
                )
            """

        const val deleteEventsForDayQuery =
            """
            DELETE FROM events WHERE 
                isSynced = 1
                AND 
                ( ( `from` >= :zonedDateTime) AND (`from`   < :zonedDateTime + ${DAY_IN_SECONDS}) ) -- event starts within day
                    
                  --      ( ( `from` >= :zonedDateTime) AND (`to`   < :zonedDateTime + ${DAY_IN_SECONDS}) ) -- event fits within day
                  --    OR
                  --      ( ( `from` >  :zonedDateTime) AND (`from` < :zonedDateTime + ${DAY_IN_SECONDS}) ) -- `from` starts on day
                  --    OR
                  --      ( ( `to`   >  :zonedDateTime) AND (`to`   < :zonedDateTime + ${DAY_IN_SECONDS}) ) -- `to` ends on day
                  --    OR
                  --      ( ( `from` <= :zonedDateTime) AND (`to`   > :zonedDateTime + ${DAY_IN_SECONDS}) ) -- event straddles today     
            """
    }
}