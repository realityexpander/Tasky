package com.realityexpander.tasky.agenda_feature.data.repositories.agendaRepository.agendaRepositoryImpls

import com.realityexpander.tasky.R
import com.realityexpander.tasky.agenda_feature.common.util.EventId
import com.realityexpander.tasky.agenda_feature.data.common.convertersDTOEntityDomain.toDTO
import com.realityexpander.tasky.agenda_feature.data.common.convertersDTOEntityDomain.toDomain
import com.realityexpander.tasky.agenda_feature.data.repositories.agendaRepository.remote.IAgendaApi
import com.realityexpander.tasky.agenda_feature.data.repositories.attendeeRepository.IAttendeeRepository
import com.realityexpander.tasky.agenda_feature.domain.*
import com.realityexpander.tasky.core.presentation.common.util.UiText
import com.realityexpander.tasky.core.util.Email
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
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

        return channelFlow {
//        return flow<List<AgendaItem>> {
            supervisorScope {
                launch(Dispatchers.IO) {
                    try {
                        //eventRepository.clearEventsForDay(dateTime)
                        send(eventRepository.getEventsForDay(dateTime)) // send stale local data

                        // Get fresh data
                        val result = agendaApi.getAgenda(dateTime)
                        result.events.forEach { event ->
                            // Insert fresh data into db
                            val result2 =
                                eventRepository.upsertEventLocally(event.toDomain())
                            if(result2 is ResultUiText.Error) {
                                throw IllegalStateException(result2.message.asStrOrNull())
                            }
                        }

                        // Emit the current state from DB
                        eventRepository.getEventsForDayFlow(dateTime).collect { events ->
                            send(events)
                        }
                    }
                    catch (e: CancellationException) {
                        /* intentionally eat this exception */
                    }
                    catch (e: Exception) {
                        e.printStackTrace()
                        // don't send error to user, just log it (silent fail is ok here)
                    }

                    //emitAll(eventRepository.getEventsForDayFlow(dateTime))
                }

//                launch {
//                    // todo implement api call for tasks - agendaApi.getTasks(dateTime)
////                    emitAll(eventRepository.getTasksForDayFlow(dateTime))
//                }

//                launch {
//                    // todo implement api call for reminders - agendaApi.getReminders(dateTime)
////                    emitAll(eventRepository.getReminderForDayFlow(dateTime))
//                }
            }
        }
    }

    override suspend fun syncAgenda(): ResultUiText<Void> {
        val deletedEventIds = eventRepository.getDeletedEventIdsLocally()
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
            return eventRepository.deleteEventsFinallyLocally(deletedEventIds)
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

    override suspend fun deleteEventId(eventId: EventId): ResultUiText<Void> {
        return eventRepository.deleteEvent(eventId)
    }

    override suspend fun clearAllEvents(): ResultUiText<Void> {
        return eventRepository.clearAllEventsLocally()
    }

    override suspend fun validateAttendeeExists(attendeeEmail: Email): ResultUiText<Attendee> {
        return attendeeRepository.getAttendee(attendeeEmail)
    }

    override suspend fun removeLoggedInUserFromEventId(eventId: EventId): ResultUiText<Void> {
        eventRepository.deleteEventsFinallyLocally(listOf(eventId))
        return attendeeRepository.removeLoggedInUserFromEventId(eventId)
    }
}