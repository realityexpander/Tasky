package com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.local.eventDao.eventDaoImpls

import androidx.room.*
import com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.local.entities.EventEntity
import com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.local.eventDao.IEventDao
import com.realityexpander.tasky.agenda_feature.util.EventId
import kotlinx.coroutines.flow.Flow
import java.time.ZonedDateTime

@Dao
interface EventDaoImpl : IEventDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    override suspend fun createEvent(event: EventEntity)


    @Query("SELECT * FROM events WHERE id = :eventId AND isDeleted = 0")
    override suspend fun getEventById(eventId: EventId): EventEntity?

    @Query("SELECT * FROM events WHERE isDeleted = 0")
    override suspend fun getAllEvents(): List<EventEntity>

    @Query("SELECT * FROM events WHERE isDeleted = 0")
    override fun getAllEventsFlow(): Flow<List<EventEntity>>

    @Query("SELECT * FROM events WHERE isDeleted = 0 AND " +
            "(`from` >= :zonedDateTime OR `to` <= :zonedDateTime)"
    )
    override suspend fun getEventsForDay(zonedDateTime: ZonedDateTime): List<EventEntity>


    @Update
    override suspend fun updateEvent(event: EventEntity): Int


    @Query("UPDATE events SET isDeleted = 1 WHERE id = :eventId")
    override suspend fun deleteEventById(eventId: EventId): Int

    @Query("SELECT id FROM events WHERE isDeleted = 1")
    override suspend fun getDeletedEventIds(): List<EventId>

    @Query("DELETE FROM events WHERE id IN (:eventIds)")
    override suspend fun deleteFinallyByEventIds(eventIds: List<EventId>): Int

    @Delete
    override suspend fun deleteEvent(event: EventEntity): Int

    @Query("DELETE FROM events")
    override suspend fun clearAllEvents(): Int
}