package com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.eventRepositoryImpls

import com.realityexpander.tasky.agenda_feature.common.util.EventId
import com.realityexpander.tasky.agenda_feature.data.common.convertersDTOEntityDomain.toDomain
import com.realityexpander.tasky.agenda_feature.data.common.convertersDTOEntityDomain.toEntity
import com.realityexpander.tasky.agenda_feature.data.common.convertersDTOEntityDomain.toEventDTOCreate
import com.realityexpander.tasky.agenda_feature.data.common.convertersDTOEntityDomain.toEventDTOUpdate
import com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.local.eventDao.IEventDao
import com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.remote.eventApi.IEventApi
import com.realityexpander.tasky.agenda_feature.domain.AgendaItem
import com.realityexpander.tasky.agenda_feature.domain.IEventRepository
import com.realityexpander.tasky.agenda_feature.domain.ResultUiText
import com.realityexpander.tasky.core.presentation.common.util.UiText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.ZonedDateTime
import java.util.concurrent.CancellationException

class EventRepositoryImpl(
    private val eventDao: IEventDao, // = EventDaoFakeImpl(),
    private val eventApi: IEventApi, // = EventApiFakeImpl(),
) : IEventRepository {

    override suspend fun createEvent(event: AgendaItem.Event): ResultUiText<AgendaItem.Event> {
        return try {
            eventDao.createEvent(event.toEntity())  // save to local DB first

            val response = eventApi.createEvent(event.toEventDTOCreate())
            eventDao.updateEvent(response.toDomain().toEntity())  // update with response from server

            ResultUiText.Success()  // todo return the created event
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            ResultUiText.Error(UiText.Str(e.message ?: "createEvent error"))
        }
    }

    override suspend fun getEventsForDay(zonedDateTime: ZonedDateTime): List<AgendaItem.Event> {
        return eventDao.getEventsForDay(zonedDateTime).map { it.toDomain() }
    }

    override fun getEventsForDayFlow(zonedDateTime: ZonedDateTime): Flow<List<AgendaItem.Event>> {
        return eventDao.getEventsForDayFlow(zonedDateTime).map { eventEntities ->
            eventEntities.map { eventEntity ->
                eventEntity.toDomain()
            }
        }
    }

    override suspend fun getEvent(eventId: EventId): AgendaItem.Event? {
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

    override suspend fun updateEvent(event: AgendaItem.Event): ResultUiText<AgendaItem.Event> {
        return try {
            eventDao.updateEvent(event.toEntity())  // optimistic update

            val response = eventApi.updateEvent(event.toEventDTOUpdate())
            eventDao.updateEvent(response.toDomain().toEntity())  // update with response from server

            ResultUiText.Success() // todo return the updated event
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            ResultUiText.Error(UiText.Str(e.localizedMessage ?: "updateEvent error"))
        }
    }

    override suspend fun deleteEventId(eventId: EventId): ResultUiText<AgendaItem.Event> {
        return try {
            eventDao.markEventDeletedById(eventId)

            ResultUiText.Success() // todo return the deleted event?
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            ResultUiText.Error(UiText.Str(e.message ?: "deleteEventId error"))
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

    override suspend fun deleteFinallyEventIds(eventIds: List<EventId>): ResultUiText<Void> {
        return try {
            eventDao.deleteFinallyByEventIds(eventIds)

            ResultUiText.Success() // todo return the deleted events?
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            ResultUiText.Error(UiText.Str(e.message ?: "deleteFinallyEventIds error"))
        }
    }

    override suspend fun clearAllEvents(): ResultUiText<Void> {
        return try {
            eventDao.clearAllEvents()

            ResultUiText.Success() // todo return the cleared event?
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            ResultUiText.Error(UiText.Str(e.message ?: "clearAllEvents error"))
        }
    }
}
