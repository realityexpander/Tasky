package com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.local.eventDao

import com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.local.entities.EventEntity
import com.realityexpander.tasky.agenda_feature.util.EventId
import kotlinx.coroutines.flow.Flow
import java.time.ZonedDateTime

interface IEventDao {

    suspend fun createEvent(event: EventEntity)

    suspend fun getEventsForDay(zonedDateTime: ZonedDateTime): List<EventEntity>
    suspend fun getEventById(eventId: EventId): EventEntity?
    suspend fun getEvents(): List<EventEntity>    // only returns the events that are *NOT* marked as deleted.
    suspend fun getAllEvents(): List<EventEntity> // returns all events, including the deleted ones.
    fun getEventsFlow(): Flow<List<EventEntity>>  // only returns the events that are *NOT* marked as deleted.

    suspend fun updateEvent(event: EventEntity): Int

    suspend fun markEventDeletedById(eventId: EventId): Int    // only marks the event as deleted
    suspend fun getMarkedDeletedEventIds(): List<EventId>      // gets only the "isDeleted==true" events
    suspend fun deleteFinallyByEventIds(eventIds: List<EventId>): Int
    suspend fun deleteEvent(event: EventEntity): Int      // completely deletes the event.

    suspend fun clearAllEvents(): Int
}

