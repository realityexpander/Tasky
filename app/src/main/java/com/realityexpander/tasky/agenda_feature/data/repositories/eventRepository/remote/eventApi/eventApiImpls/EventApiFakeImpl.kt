package com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.remote.eventApi.eventApiImpls

import com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.remote.eventApi.DTOs.EventDTO
import com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.remote.eventApi.IEventApi
import com.realityexpander.tasky.agenda_feature.util.EventId
import com.realityexpander.tasky.core.util.UuidStr
import kotlinx.coroutines.delay
import javax.inject.Inject

class EventApiFakeImpl @Inject constructor(): IEventApi {

    /////////////////// FAKE API IMPLEMENTATION ///////////////////////

    override suspend fun createEvent(event: EventDTO): EventDTO {
        try {
            return createEventOnFakeServer(event)
        } catch (e: Exception) {
            throw Exception("Error creating event: ${e.message}")
        }
    }

    override suspend fun getEvent(eventId: EventId): EventDTO {
        try{
            return getEventOnFakeServer(eventId)
        } catch (e: Exception) {
            throw Exception("Error getting event: ${e.message}")
        }
    }

    override suspend fun updateEvent(event: EventDTO): EventDTO {
        try {
            return updateEventOnFakeServer(event)
        } catch (e: Exception) {
            throw Exception("Error updating event: ${e.message}")
        }
    }

    override suspend fun deleteEvent(eventId: EventId): Boolean {
        try {
            return deleteEventOnFakeServer(eventId)
        } catch (e: Exception) {
            throw Exception("Error deleting event: ${e.message}")
        }
    }
    /////////////// Fake Server simulation functions //////////////////////

    private val events_onFakeServer = mutableListOf<EventDTO>()

    private suspend fun createEventOnFakeServer(event: EventDTO): EventDTO {
        // simulate network delay
        delay(500)

        events_onFakeServer.add(event)
        return event
    }

    private suspend fun getEventOnFakeServer(eventId: UuidStr): EventDTO {
        // simulate network delay
        delay(500)

        return events_onFakeServer.find { it.id == eventId } ?: throw Exception("Event not found")
    }

    private suspend fun updateEventOnFakeServer(event: EventDTO): EventDTO {
        // simulate network delay
        delay(500)

        val index = events_onFakeServer.indexOfFirst { it.id == event.id }
        if (index == -1) {
            throw Exception("Event not found")
        }
        events_onFakeServer[index] = event

        return event
    }

    private suspend fun deleteEventOnFakeServer(eventId: UuidStr): Boolean {
        // simulate network delay
        delay(500)

        val index = events_onFakeServer.indexOfFirst { it.id == eventId }
        if (index == -1) return false
        events_onFakeServer.removeAt(index)
        return true
    }
}