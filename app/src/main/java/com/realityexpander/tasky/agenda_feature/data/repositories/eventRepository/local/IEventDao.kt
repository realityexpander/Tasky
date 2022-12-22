package com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.local

import com.realityexpander.tasky.agenda_feature.common.util.EventId
import com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.local.entities.EventEntity
import kotlinx.coroutines.flow.Flow
import java.time.ZonedDateTime

interface IEventDao {

    suspend fun createEvent(event: EventEntity)
    suspend fun upsertEvent(event: EventEntity)

    suspend fun getEventsForDay(zonedDateTime: ZonedDateTime): List<EventEntity>
    fun getEventsForDayFlow(zonedDateTime: ZonedDateTime): Flow<List<EventEntity>>
    suspend fun getEventById(eventId: EventId): EventEntity?
    suspend fun getEvents(): List<EventEntity>
    fun getEventsFlow(): Flow<List<EventEntity>>
    fun getLocalEventsForRemindAtDateTimeRangeFlow(startDateTime: ZonedDateTime, endDateTime: ZonedDateTime): Flow<List<EventEntity>>

    suspend fun updateEvent(event: EventEntity): Int

    suspend fun deleteEventById(eventId: EventId): Int
    suspend fun deleteByEventIds(eventIds: List<EventId>): Int
    suspend fun deleteEvent(event: EventEntity): Int

    suspend fun clearAllEvents(): Int
    suspend fun clearAllSyncedEventsForDay(zonedDateTime: ZonedDateTime): Int
}

