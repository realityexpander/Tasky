package com.realityexpander.tasky.agenda_feature.data.repositories.agendaRepository.agendaRepositoryImpls

import com.realityexpander.tasky.agenda_feature.common.RepositoryResult
import com.realityexpander.tasky.agenda_feature.data.common.convertersDTOEntityDomain.toDTO
import com.realityexpander.tasky.agenda_feature.data.repositories.agendaRepository.remote.IAgendaApi
import com.realityexpander.tasky.agenda_feature.domain.AgendaItem
import com.realityexpander.tasky.agenda_feature.domain.AgendaSync
import com.realityexpander.tasky.agenda_feature.domain.IAgendaRepository
import com.realityexpander.tasky.agenda_feature.domain.IEventRepository
import com.realityexpander.tasky.agenda_feature.util.EventId
import kotlinx.coroutines.flow.Flow
import java.time.ZonedDateTime
import javax.inject.Inject

class AgendaRepositoryImpl @Inject constructor(
    private val eventRepository: IEventRepository, // = EventRepositoryImpl(),
//    private val taskRepository: ITaskRepository,                                  // todo implement tasks repo
//    private val reminderRepository: IReminderRepository,                          // todo implement reminders repo
    private val agendaApi: IAgendaApi,
) : IAgendaRepository {

    override suspend fun getAgendaForDay(dateTime: ZonedDateTime): List<AgendaItem> {
        val events = eventRepository.getEventsForDay(dateTime)
//        val tasks = taskRepository.getTasks(dateTime)                             // todo implement tasks repo
//        val reminders = reminderRepository.getReminders(dateTime)                 // todo implement reminders repo
        return events // + tasks + reminders
    }

    override fun getAgendaForDayFlow(dateTime: ZonedDateTime): Flow<List<AgendaItem>> {
        val events = eventRepository.getEventsForDayFlow(dateTime)
//        val tasks = taskRepository.getTasks(dateTime)                             // todo implement tasks repo
//        val reminders = reminderRepository.getReminders(dateTime)                 // todo implement reminders repo
        return events // + tasks + reminders
    }

    override suspend fun syncAgenda(): RepositoryResult {
        val deletedEventIds = eventRepository.getDeletedEventIds()
//        val deletedTaskIds = taskRepository.getDeletedTaskIds()                   // todo implement tasks repo
//        val deletedReminderIds = reminderRepository.getDeletedReminderIds()       // todo implement reminders repo

        val deletedSuccessfully =
            agendaApi.syncAgenda(
                AgendaSync(
                    deleteEventIds = deletedEventIds,
//                    deleteTaskIds = deletedTaskIds,                               // todo implement tasks repo
//                    deleteReminderIds = deletedReminderIds,                       // todo implement reminders repo
                ).toDTO()
            )

        if (deletedSuccessfully) {
            return eventRepository.deleteFinallyEventIds(deletedEventIds)
        } else {
            return RepositoryResult.Error("Failed to sync agenda - deleteFinallyEventIds")
        }
    }

    override suspend fun createEvent(event: AgendaItem.Event): RepositoryResult {
        return eventRepository.createEvent(event)
    }

    override suspend fun getEvent(eventId: EventId): AgendaItem.Event? {
        return eventRepository.getEvent(eventId)
    }

    override suspend fun updateEvent(event: AgendaItem.Event): RepositoryResult {
        return eventRepository.updateEvent(event)
    }

    override suspend fun deleteEventId(eventId: EventId): RepositoryResult {
        return eventRepository.deleteEventId(eventId)
    }

    override suspend fun clearAllEvents(): RepositoryResult {
        return eventRepository.clearAllEvents()
    }
}