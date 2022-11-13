package com.realityexpander.tasky.agenda_feature.domain

import com.realityexpander.tasky.agenda_feature.common.RepositoryResult
import com.realityexpander.tasky.agenda_feature.common.util.EventId
import kotlinx.coroutines.flow.Flow
import java.time.ZonedDateTime

interface IEventRepository {
    suspend fun createEvent(event: AgendaItem.Event): RepositoryResult

    suspend fun getEventsForDay(zonedDateTime: ZonedDateTime): List<AgendaItem.Event>
    fun getEventsForDayFlow(zonedDateTime: ZonedDateTime): Flow<List<AgendaItem.Event>>
    suspend fun getEvent(eventId: EventId): AgendaItem.Event?

    suspend fun updateEvent(event: AgendaItem.Event): RepositoryResult

    suspend fun deleteEventId(eventId: EventId): RepositoryResult  // only marks the event as deleted
    suspend fun getDeletedEventIds(): List<EventId>                // gets only the "marked as deleted" events
    suspend fun deleteFinallyEventIds(eventIds: List<EventId>): RepositoryResult

    suspend fun clearAllEvents(): RepositoryResult
}
