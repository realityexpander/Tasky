package com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.remote.eventApi

import com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.remote.eventApi.DTOs.EventDTO
import com.realityexpander.tasky.agenda_feature.util.EventId

interface IEventApi {

    suspend fun createEvent(event: EventDTO.Create): EventDTO.Response

    suspend fun getEvent(eventId: EventId): EventDTO.Response

    suspend fun deleteEvent(eventId: EventId): Boolean

    suspend fun updateEvent(event: EventDTO.Update): EventDTO.Response
}