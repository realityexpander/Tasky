package com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.local.eventDao

import com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.local.entities.EventEntity
import com.realityexpander.tasky.agenda_feature.util.EventId
import kotlinx.coroutines.flow.Flow
import java.time.ZonedDateTime

interface IEventDao {

    suspend fun createEvent(event: EventEntity)

    suspend fun getEventsForDay(zonedDateTime: ZonedDateTime): List<EventEntity>
    suspend fun getEventById(eventId: EventId): EventEntity?
    suspend fun getAllEvents(): List<EventEntity>
    suspend fun getAllEventsFlow(): Flow<List<EventEntity>>

    suspend fun updateEvent(event: EventEntity): Boolean

    suspend fun deleteEventById(eventId: EventId): Boolean  // only marks the event as deleted
    suspend fun getDeletedEventIds(): List<EventId>       // gets only the "marked as deleted" events
    suspend fun deleteFinallyByEventIds(eventIds: List<EventId>): Boolean

    suspend fun clearAllEvents(): Boolean
}

