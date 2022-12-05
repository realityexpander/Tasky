package com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.eventRepositoryImpls

import com.realityexpander.tasky.agenda_feature.common.util.EventId
import com.realityexpander.tasky.agenda_feature.data.common.convertersDTOEntityDomain.toDomain
import com.realityexpander.tasky.agenda_feature.data.common.convertersDTOEntityDomain.toEntity
import com.realityexpander.tasky.agenda_feature.data.common.convertersDTOEntityDomain.toEventDTOCreate
import com.realityexpander.tasky.agenda_feature.data.common.convertersDTOEntityDomain.toEventDTOUpdate
import com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.local.IEventDao
import com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.remote.eventApi.IEventApi
import com.realityexpander.tasky.agenda_feature.data.repositories.syncRepository.ISyncRepository
import com.realityexpander.tasky.agenda_feature.domain.AgendaItem
import com.realityexpander.tasky.agenda_feature.domain.IEventRepository
import com.realityexpander.tasky.agenda_feature.domain.ResultUiText
import com.realityexpander.tasky.auth_feature.domain.IAuthRepository
import com.realityexpander.tasky.core.presentation.common.util.UiText
import com.realityexpander.tasky.core.util.rethrowIfCancellation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.ZonedDateTime

class EventRepositoryImpl(
    private val eventDao: IEventDao,
    private val eventApi: IEventApi,
    private val syncRepository: ISyncRepository,
    private val authRepository: IAuthRepository   // to get `AuthInfo` for setting `isGoing` for `EventDTO.Update`
) : IEventRepository {

    // • CREATE

    override suspend fun createEvent(event: AgendaItem.Event, isRemoteOnly: Boolean): ResultUiText<AgendaItem.Event> {
        return try {
            if(!isRemoteOnly) {
                eventDao.createEvent(event.copy(isSynced = false).toEntity())  // save to local DB first
                syncRepository.addCreatedSyncItem(event)
            }

            val response = eventApi.createEvent(event.toEventDTOCreate())

            // update with response from server and mark isSynced
            eventDao.updateEvent(
                response
                    .toDomain()
                    .copy(isSynced = true)
                    .toEntity()
            )
            syncRepository.removeCreatedSyncItem(event)

            ResultUiText.Success(response.toDomain())
        } catch (e: Exception) {
            e.rethrowIfCancellation()
            ResultUiText.Error(UiText.Str(e.message ?: "createEvent error"))
        }
    }

    // • READ

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

    override suspend fun getEvent(eventId: EventId, isLocalOnly: Boolean): AgendaItem.Event? {
        return eventDao.getEventById(eventId)?.toDomain()
    }

    // • UPDATE / UPSERT

    override suspend fun updateEvent(event: AgendaItem.Event, isRemoteOnly: Boolean): ResultUiText<AgendaItem.Event> {
        return try {
            if(!isRemoteOnly) {
                eventDao.updateEvent(event.copy(isSynced = false).toEntity())  // optimistic update
                syncRepository.addUpdatedSyncItem(event)
            }

            val response =
                eventApi.updateEvent(
                    event.toEventDTOUpdate(
                        authUserId = authRepository.getAuthUserId()
                    ))

            // update with response from server and mark synced
            eventDao.updateEvent(
                response
                    .toDomain()
                    .copy(isSynced = true)
                    .toEntity())
            syncRepository.removeUpdatedSyncItem(event)

            ResultUiText.Success(response.toDomain())
        } catch (e: Exception) {
            e.rethrowIfCancellation()
            ResultUiText.Error(UiText.Str(e.localizedMessage ?: "updateEvent error"))
        }
    }

    override suspend fun upsertEventLocally(event: AgendaItem.Event): ResultUiText<Void> {
        return try {
            eventDao.upsertEvent(event.toEntity())  // save to local DB ONLY

            ResultUiText.Success()
        } catch (e: Exception) {
            e.rethrowIfCancellation()
            ResultUiText.Error(UiText.Str(e.message ?: "upsertEvent error"))
        }
    }

    // • DELETE

    override suspend fun deleteEvent(event: AgendaItem.Event): ResultUiText<Void> {
        return try {
            eventDao.deleteEvent(event.toEntity())
            syncRepository.addDeletedSyncItem(event)

            // Attempt to delete on server
            val response = eventApi.deleteEvent(event.toEventDTOUpdate())
            if (response.isSuccess) {
                syncRepository.removeDeletedSyncItem(event)
                ResultUiText.Success()
            } else {
                // Should show error here? Or silently fail? Show off-line message?
                ResultUiText.Error(UiText.Str(response.exceptionOrNull()?.localizedMessage ?: "deleteEvent error"))
            }
        } catch (e: Exception) {
            e.rethrowIfCancellation()
            ResultUiText.Error(UiText.Str(e.message ?: "deleteEvent error"))
        }
    }

    // • CLEAR / CLEANUP

    override suspend fun clearAllEventsLocally(): ResultUiText<Void> {
        return try {
            eventDao.clearAllEvents()

            ResultUiText.Success() // todo return the cleared event, for undo
        } catch (e: Exception) {
            e.rethrowIfCancellation()
            ResultUiText.Error(UiText.Str(e.message ?: "clearAllEvents error"))
        }
    }

    override suspend fun clearEventsForDayLocally(zonedDateTime: ZonedDateTime): ResultUiText<Void> {
        return try {
            eventDao.clearAllSyncedEventsForDay(zonedDateTime)

            ResultUiText.Success() // todo return the cleared event, for undo
        } catch (e: Exception) {
            e.rethrowIfCancellation()
            ResultUiText.Error(UiText.Str(e.message ?: "clearEventsForDay error"))
        }
    }
}
