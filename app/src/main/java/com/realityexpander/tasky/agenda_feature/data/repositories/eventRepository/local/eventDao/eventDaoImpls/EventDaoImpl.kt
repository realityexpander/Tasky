package com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.local.eventDao.eventDaoImpls

import androidx.room.*
import com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.local.entities.EventEntity
import com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.local.eventDao.IEventDao
import com.realityexpander.tasky.agenda_feature.util.EventId
import com.realityexpander.tasky.core.util.DAY_IN_SECONDS
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import java.time.ZonedDateTime

@Dao
interface EventDaoImpl : IEventDao {

    // • CREATE

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    override suspend fun createEvent(event: EventEntity)


    // • READ

    @Query("SELECT * FROM events WHERE id = :eventId AND isDeleted = 0")
    override suspend fun getEventById(eventId: EventId): EventEntity?

    @Query("SELECT * FROM events WHERE isDeleted = 0")  // only returns the events that are *NOT* marked as deleted
    override suspend fun getEvents(): List<EventEntity>

    @Query("SELECT * FROM events")                      // returns all events (marked deleted or not)
    override suspend fun getAllEvents(): List<EventEntity>

    @Query("SELECT * FROM events WHERE isDeleted = 0")  // only returns the events that are *NOT* marked as deleted.
    override fun getEventsFlow(): Flow<List<EventEntity>>

    @Query("""
        SELECT * FROM events WHERE isDeleted = 0 
            AND (
                    (`from` >= :zonedDateTime AND (`from` < (:zonedDateTime + ${DAY_IN_SECONDS})))
                 OR (`to`   >= :zonedDateTime AND (`to`   < (:zonedDateTime + ${DAY_IN_SECONDS})))
            )
        """)
    override suspend fun getEventsForDay(zonedDateTime: ZonedDateTime): List<EventEntity>  // note: ZonedDateTime gets converted to UTC EpochSeconds for storage in the DB.


    // • UPDATE

    @Update
    override suspend fun updateEvent(event: EventEntity): Int


    // • DELETE

    @Query("UPDATE events SET isDeleted = 1 WHERE id = :eventId")
    override suspend fun markEventDeletedById(eventId: EventId): Int   // only marks the event as deleted.

    @Query("SELECT id FROM events WHERE isDeleted = 1")
    override suspend fun getMarkedDeletedEventIds(): List<EventId>

    @Query("DELETE FROM events WHERE id IN (:eventIds)")
    override suspend fun deleteFinallyByEventIds(eventIds: List<EventId>): Int  // completely deletes the events.

    @Delete
    override suspend fun deleteEvent(event: EventEntity): Int  // completely deletes the event.

    @Query("DELETE FROM events")
    override suspend fun clearAllEvents(): Int  // completely deletes all events.
}
