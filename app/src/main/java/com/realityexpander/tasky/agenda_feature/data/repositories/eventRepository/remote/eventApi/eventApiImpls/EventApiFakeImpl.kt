package com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.remote.eventApi.eventApiImpls

import com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.remote.eventApi.DTOs.EventDTO
import com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.remote.eventApi.IEventApi
import com.realityexpander.tasky.core.util.UuidStr
import kotlinx.coroutines.delay
import javax.inject.Inject

class EventApiFakeImpl @Inject constructor(): IEventApi {

    /////////////////// FAKE API IMPLEMENTATION ///////////////////////

    override suspend fun createEvent(event: EventDTO): Boolean {
        return createEventOnFakeServer(event)
    }

    override suspend fun getEvent(eventId: UuidStr): EventDTO? {
        return getEventOnFakeServer(eventId)
    }

    override suspend fun deleteEvent(eventId: UuidStr): Boolean {
        return deleteEventOnFakeServer(eventId)
    }

    override suspend fun updateEvent(event: EventDTO): Boolean {
        return updateEventOnFakeServer(event)
    }

    /////////////// Fake Server simulation functions //////////////////////

    private val events_onServer = mutableListOf<EventDTO>()

    private suspend fun getEventOnFakeServer(eventId: UuidStr): EventDTO? {
        // simulate network delay
        delay(500)

        return events_onServer.find { it.id == eventId }
    }

    private suspend fun createEventOnFakeServer(event: EventDTO): Boolean {
        // simulate network delay
        delay(500)

        events_onServer.add(event)
        return true
    }

    private suspend fun updateEventOnFakeServer(event: EventDTO): Boolean {
        // simulate network delay
        delay(500)

        val index = events_onServer.indexOfFirst { it.id == event.id }
        if (index == -1) return false
        events_onServer[index] = event
        return true
    }

    private suspend fun deleteEventOnFakeServer(eventId: UuidStr): Boolean {
        // simulate network delay
        delay(500)

        val index = events_onServer.indexOfFirst { it.id == eventId }
        if (index == -1) return false
        events_onServer.removeAt(index)
        return true
    }
}