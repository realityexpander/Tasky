package com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.eventRepositoryImpls

import com.realityexpander.tasky.agenda_feature.common.RepositoryResult
import com.realityexpander.tasky.agenda_feature.data.common.convertersDTOEntityDomain.toDTO
import com.realityexpander.tasky.agenda_feature.data.common.convertersDTOEntityDomain.toDomain
import com.realityexpander.tasky.agenda_feature.data.common.convertersDTOEntityDomain.toEntity
import com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.local.eventDao.IEventDao
import com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.local.eventDao.eventDaoImpls.EventDaoFakeImpl
import com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.remote.eventApi.IEventApi
import com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.remote.eventApi.eventApiImpls.EventApiFakeImpl
import com.realityexpander.tasky.agenda_feature.domain.AgendaItem
import com.realityexpander.tasky.agenda_feature.domain.IEventRepository
import com.realityexpander.tasky.agenda_feature.util.EventId
import java.time.ZonedDateTime
import java.util.concurrent.CancellationException

class EventRepositoryImpl(
    private val eventDao: IEventDao = EventDaoFakeImpl(),
    private val eventApi: IEventApi = EventApiFakeImpl(),
) : IEventRepository {

    override suspend fun createEvent(event: AgendaItem.Event): RepositoryResult {
        return try {
            eventDao.createEvent(event.toEntity())
            eventApi.createEvent(event.toDTO())

            RepositoryResult.Success
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            RepositoryResult.Error(e.message ?: "createEvent error")
        }
    }

    override suspend fun getEventsForDay(zonedDateTime: ZonedDateTime): List<AgendaItem.Event> {
        return eventDao.getEventsForDay(zonedDateTime).map { it.toDomain() }
    }

    override suspend fun getEventId(eventId: EventId): AgendaItem.Event? {
        return try {
            eventDao.getEventById(eventId)?.toDomain()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getAllEvents(): List<AgendaItem.Event> {
        return try {
            eventDao.getEvents().map { it.toDomain() }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun updateEvent(event: AgendaItem.Event): RepositoryResult {
        return try {
            eventDao.updateEvent(event.toEntity())

            RepositoryResult.Success
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            RepositoryResult.Error(e.message ?: "updateEvent error")
        }
    }

    override suspend fun deleteEventId(eventId: EventId): RepositoryResult {
        return try {
            eventDao.markEventDeletedById(eventId)

            RepositoryResult.Success
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            RepositoryResult.Error(e.message ?: "deleteEventId error")
        }
    }

    override suspend fun getDeletedEventIds(): List<EventId> {
        return try {
            eventDao.getMarkedDeletedEventIds()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun deleteFinallyEventIds(eventIds: List<EventId>): RepositoryResult {
        return try {
            eventDao.deleteFinallyByEventIds(eventIds)

            RepositoryResult.Success
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            RepositoryResult.Error(e.message ?: "deleteFinallyEventIds error")
        }
    }

    override suspend fun clearAllEvents(): RepositoryResult {
        return try {
            eventDao.clearAllEvents()

            RepositoryResult.Success
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            RepositoryResult.Error(e.message ?: "clearAllEvents error")
        }
    }
}
