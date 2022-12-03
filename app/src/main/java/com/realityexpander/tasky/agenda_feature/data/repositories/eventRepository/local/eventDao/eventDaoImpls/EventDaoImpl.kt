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

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertEvent(event: EventEntity): Long

    @Update(onConflict = OnConflictStrategy.IGNORE)
    fun update2Event(event: EventEntity)

    @Transaction
    override fun upsertEvent(event: EventEntity) {
        val id = insertEvent(event)
        if (id == -1L) {
            update2Event(event)
        }
    }


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
    override suspend fun deleteByEventIds(eventIds: List<EventId>): Int  // completely deletes the events.

    @Delete
    override suspend fun deleteEvent(event: EventEntity): Int

    @Query("DELETE FROM events WHERE id = :eventId")
    override suspend fun deleteEventById(eventId: EventId): Int

    @Query("DELETE FROM events")
    override suspend fun clearAllEvents(): Int  // completely deletes all events.

    @Query(deleteEventsForDayQuery)
    override suspend fun clearAllEventsForDay(zonedDateTime: ZonedDateTime): Int // completely deletes all UNDELETED events for the given day.

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
                (
                    ( ( `from` >= :zonedDateTime) AND (`from`   < :zonedDateTime + ${DAY_IN_SECONDS}) ) -- event starts within day
                    
                  --      ( ( `from` >= :zonedDateTime) AND (`to`   < :zonedDateTime + ${DAY_IN_SECONDS}) ) -- event fits within day
                  --    OR
                  --      ( ( `from` >  :zonedDateTime) AND (`from` < :zonedDateTime + ${DAY_IN_SECONDS}) ) -- `from` starts on day
                  --    OR
                  --      ( ( `to`   >  :zonedDateTime) AND (`to`   < :zonedDateTime + ${DAY_IN_SECONDS}) ) -- `to` ends on day
                  --    OR
                  --      ( ( `from` <= :zonedDateTime) AND (`to`   > :zonedDateTime + ${DAY_IN_SECONDS}) ) -- event straddles today     
                )
            """
    }
}