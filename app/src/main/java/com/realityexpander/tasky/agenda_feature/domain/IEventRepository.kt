package com.realityexpander.tasky.agenda_feature.domain

import com.realityexpander.tasky.agenda_feature.common.util.EventId
import com.realityexpander.tasky.core.util.UserId
import kotlinx.coroutines.flow.Flow
import java.time.ZonedDateTime

interface IEventRepository {
    suspend fun createEvent(event: AgendaItem.Event): ResultUiText<AgendaItem.Event>
    suspend fun upsertEventLocally(event: AgendaItem.Event): ResultUiText<Void>

    suspend fun getEventsForDay(zonedDateTime: ZonedDateTime): List<AgendaItem.Event>
    fun getEventsForDayFlow(zonedDateTime: ZonedDateTime): Flow<List<AgendaItem.Event>>
    suspend fun getEvent(eventId: EventId): AgendaItem.Event?

    suspend fun updateEvent(event: AgendaItem.Event, loggedInUserId: UserId): ResultUiText<AgendaItem.Event>

    // only marks the event as deleted
    suspend fun deleteEvent(eventId: EventId): ResultUiText<Void>

    // gets only the "marked as deleted" events
    suspend fun getDeletedEventIdsLocally(): List<EventId>
    suspend fun deleteEventsFinallyLocally(eventIds: List<EventId>): ResultUiText<Void>

    suspend fun clearAllEventsLocally(): ResultUiText<Void>
    suspend fun clearEventsForDayLocally(zonedDateTime: ZonedDateTime): ResultUiText<Void>
}
