package com.realityexpander.tasky.agenda_feature.domain

import com.realityexpander.tasky.agenda_feature.util.EventId
import java.time.ZonedDateTime

interface IEventRepository {
    suspend fun createEvent(event: AgendaItem.Event): Boolean

    suspend fun getEventsForDay(zonedDateTime: ZonedDateTime): List<AgendaItem.Event>
    suspend fun getEventId(eventId: EventId): AgendaItem.Event?

    suspend fun updateEvent(event: AgendaItem.Event): Int

    suspend fun deleteEventId(eventId: EventId): Int  // only marks the event as deleted
    suspend fun getDeletedEventIds(): List<EventId>       // gets only the "marked as deleted" events
    suspend fun deleteFinallyEventIds(eventIds: List<EventId>): Int

    suspend fun clearAllEvents(): Int
}
