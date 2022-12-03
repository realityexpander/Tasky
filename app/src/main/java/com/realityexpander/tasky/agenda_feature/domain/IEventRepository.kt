package com.realityexpander.tasky.agenda_feature.domain

import com.realityexpander.tasky.agenda_feature.common.util.EventId
import kotlinx.coroutines.flow.Flow
import java.time.ZonedDateTime

interface IEventRepository {
    suspend fun createEvent(event: AgendaItem.Event, isRemoteOnly: Boolean = false): ResultUiText<AgendaItem.Event>
    suspend fun upsertEventLocally(event: AgendaItem.Event): ResultUiText<Void>

    suspend fun getEventsForDay(zonedDateTime: ZonedDateTime): List<AgendaItem.Event>
    fun getEventsForDayFlow(zonedDateTime: ZonedDateTime): Flow<List<AgendaItem.Event>>
    suspend fun getEvent(eventId: EventId, isLocalOnly: Boolean = false): AgendaItem.Event?

    suspend fun updateEvent(event: AgendaItem.Event, isRemoteOnly: Boolean = false): ResultUiText<AgendaItem.Event>

    suspend fun deleteEvent(event: AgendaItem.Event): ResultUiText<Void>

    suspend fun clearAllEventsLocally(): ResultUiText<Void>
    suspend fun clearEventsForDayLocally(zonedDateTime: ZonedDateTime): ResultUiText<Void>
}
