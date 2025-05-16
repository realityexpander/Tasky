package com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.remote.eventApi

import com.realityexpander.tasky.agenda_feature.domain.EventId
import com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.remote.eventApi.DTOs.EventDTO

interface IEventApi {
    suspend fun createEvent(event: EventDTO.Create): EventDTO.Response
    suspend fun getEvent(eventId: EventId): EventDTO.Response
    suspend fun deleteEvent(event: EventDTO.Update): Result<Unit>
    suspend fun updateEvent(event: EventDTO.Update): EventDTO.Response
}
