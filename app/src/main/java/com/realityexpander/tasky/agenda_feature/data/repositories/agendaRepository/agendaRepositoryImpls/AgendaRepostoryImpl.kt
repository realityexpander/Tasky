package com.realityexpander.tasky.agenda_feature.data.repositories.agendaRepository.agendaRepositoryImpls

import com.realityexpander.tasky.R
import com.realityexpander.tasky.agenda_feature.common.util.EventId
import com.realityexpander.tasky.agenda_feature.data.common.convertersDTOEntityDomain.toDTO
import com.realityexpander.tasky.agenda_feature.data.repositories.agendaRepository.remote.IAgendaApi
import com.realityexpander.tasky.agenda_feature.data.repositories.attendeeRepository.IAttendeeRepository
import com.realityexpander.tasky.agenda_feature.domain.*
import com.realityexpander.tasky.core.presentation.common.util.UiText
import com.realityexpander.tasky.core.util.Email
import kotlinx.coroutines.flow.Flow
import java.time.ZonedDateTime
import javax.inject.Inject

class AgendaRepositoryImpl @Inject constructor(
    private val eventRepository: IEventRepository, // = EventRepositoryImpl(),
    private val attendeeRepository: IAttendeeRepository, // = AttendeeRepositoryImpl(),
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

    override suspend fun syncAgenda(): ResultUiText<Void> {
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
            return ResultUiText.Error(UiText.ResOrStr(R.string.agenda_sync_error, "Failed to sync agenda - deleteFinallyEventIds"))
        }
    }

    override suspend fun createEvent(event: AgendaItem.Event): ResultUiText<AgendaItem.Event> {
        return eventRepository.createEvent(event)
    }

    override suspend fun getEvent(eventId: EventId): AgendaItem.Event? {
        return eventRepository.getEvent(eventId)
    }

    override suspend fun updateEvent(event: AgendaItem.Event): ResultUiText<AgendaItem.Event> {
        return eventRepository.updateEvent(event)
    }

    override suspend fun deleteEventId(eventId: EventId): ResultUiText<AgendaItem.Event> {
        return eventRepository.deleteEventId(eventId)
    }

    override suspend fun clearAllEvents(): ResultUiText<Void> {
        return eventRepository.clearAllEvents()
    }

    override suspend fun validateAttendeeExists(attendeeEmail: Email): ResultUiText<Attendee> {
        return attendeeRepository.getAttendee(attendeeEmail)
    }
}