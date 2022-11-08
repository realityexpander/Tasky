package com.realityexpander.tasky.agenda_feature.data.repositories.agendaRepository.agendaRepositoryImpls

import com.realityexpander.tasky.agenda_feature.data.common.convertersDTOEntityDomain.toDTO
import com.realityexpander.tasky.agenda_feature.data.repositories.agendaRepository.remote.IAgendaApi
import com.realityexpander.tasky.agenda_feature.domain.AgendaItem
import com.realityexpander.tasky.agenda_feature.domain.AgendaSync
import com.realityexpander.tasky.agenda_feature.domain.IAgendaRepository
import com.realityexpander.tasky.agenda_feature.domain.IEventRepository
import com.realityexpander.tasky.agenda_feature.util.EventId
import java.time.ZonedDateTime

class AgendaRepositoryImpl(
    private val eventRepository: IEventRepository,
//    private val taskRepository: ITaskRepository,                                  // todo implement tasks repo
//    private val reminderRepository: IReminderRepository,                          // todo implement reminders repo
    private val agendaApi: IAgendaApi,
) : IAgendaRepository {

    override suspend fun getAgendaDay(dateTime: ZonedDateTime): List<AgendaItem> {
        val events = eventRepository.getEventsForDay(dateTime)
//        val tasks = taskRepository.getTasks(dateTime)                             // todo implement tasks repo
//        val reminders = reminderRepository.getReminders(dateTime)                 // todo implement reminders repo
        return events // + tasks + reminders
    }

    override suspend fun syncAgenda(): Boolean {
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
        }

        return false
    }

    override suspend fun createEvent(event: AgendaItem.Event): Boolean {
        return eventRepository.createEvent(event)
    }

    override suspend fun getEvent(eventId: EventId): AgendaItem.Event? {
        return eventRepository.getEventId(eventId)
    }

    override suspend fun updateEvent(event: AgendaItem.Event): Boolean {
        return eventRepository.updateEvent(event)
    }

    override suspend fun deleteEventId(eventId: EventId): Boolean {
        return eventRepository.deleteEventId(eventId)
    }

    override suspend fun clearAllEvents(): Boolean {
        return eventRepository.clearAllEvents()
    }
}