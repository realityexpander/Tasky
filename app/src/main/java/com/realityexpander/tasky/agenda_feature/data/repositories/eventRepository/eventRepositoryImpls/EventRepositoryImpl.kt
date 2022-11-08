package com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.eventRepositoryImpls

import com.realityexpander.tasky.agenda_feature.data.common.convertersDTOEntityDomain.toDTO
import com.realityexpander.tasky.agenda_feature.data.common.convertersDTOEntityDomain.toDomain
import com.realityexpander.tasky.agenda_feature.data.common.convertersDTOEntityDomain.toEntity
import com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.local.eventDao.IEventDao
import com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.local.eventDao.eventDaoImpls.EventDaoFakeImpl
import com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.remote.eventApi.IEventApi
import com.realityexpander.tasky.agenda_feature.domain.AgendaItem
import com.realityexpander.tasky.agenda_feature.domain.IEventRepository
import com.realityexpander.tasky.agenda_feature.util.EventId
import java.time.ZonedDateTime

class EventRepositoryImpl(
    private val eventDao: IEventDao = EventDaoFakeImpl(),
    private val eventApi: IEventApi,
) : IEventRepository {

    override suspend fun createEvent(event: AgendaItem.Event): Boolean {
        return try {
            eventDao.createEvent(event.toEntity())
            eventApi.createEvent(event.toDTO())
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun getEventsForDay(zonedDateTime: ZonedDateTime): List<AgendaItem.Event> {
        return eventDao.getEventsForDay(zonedDateTime).map { it.toDomain() }
    }

    override suspend fun getEventId(eventId: EventId): AgendaItem.Event? {
        return try {
            eventDao.getEventById(eventId)?.toDomain()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getAllEvents(): List<AgendaItem.Event> {
        return try {
            eventDao.getAllEvents().map { it.toDomain() }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun updateEvent(event: AgendaItem.Event): Boolean {
        return try {
            eventDao.updateEvent(event.toEntity())
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun deleteEventId(eventId: EventId): Boolean {
        return try {
            eventDao.deleteEventById(eventId)
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun getDeletedEventIds(): List<EventId> {
        return try {
            eventDao.getDeletedEventIds()
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun deleteFinallyEventIds(eventIds: List<EventId>): Boolean {
        return try {
            eventDao.deleteFinallyByEventIds(eventIds)
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun clearAllEvents(): Boolean {
        return try {
            eventDao.clearAllEvents()
        } catch (e: Exception) {
            false
        }
    }
}
