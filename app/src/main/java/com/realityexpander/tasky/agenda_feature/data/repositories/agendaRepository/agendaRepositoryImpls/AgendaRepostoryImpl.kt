package com.realityexpander.tasky.agenda_feature.data.repositories.agendaRepository.agendaRepositoryImpls

import com.realityexpander.remindery.agenda_feature.data.common.convertersDTOEntityDomain.toDomain
import com.realityexpander.tasky.R
import com.realityexpander.tasky.agenda_feature.common.util.EventId
import com.realityexpander.tasky.agenda_feature.common.util.ReminderId
import com.realityexpander.tasky.agenda_feature.common.util.TaskId
import com.realityexpander.tasky.agenda_feature.data.common.convertersDTOEntityDomain.toDTO
import com.realityexpander.tasky.agenda_feature.data.common.convertersDTOEntityDomain.toDomain
import com.realityexpander.tasky.agenda_feature.data.repositories.agendaRepository.remote.IAgendaApi
import com.realityexpander.tasky.agenda_feature.data.repositories.attendeeRepository.IAttendeeRepository
import com.realityexpander.tasky.agenda_feature.domain.*
import com.realityexpander.tasky.core.presentation.common.util.UiText
import com.realityexpander.tasky.core.util.Email
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import java.time.ZonedDateTime
import javax.inject.Inject

class AgendaRepositoryImpl @Inject constructor(
    private val agendaApi: IAgendaApi,
    private val eventRepository: IEventRepository,
    private val attendeeRepository: IAttendeeRepository,
    private val reminderRepository: IReminderRepository,
    private val taskRepository: ITaskRepository,
) : IAgendaRepository {

    ///////////////////////////////////////////////
    // • AGENDA

    override suspend fun getAgendaForDay(dateTime: ZonedDateTime): List<AgendaItem> {
        val events = eventRepository.getEventsForDay(dateTime)
        val tasks = taskRepository.getTasksForDay(dateTime)                             // todo implement tasks repo
//        val reminders = reminderRepository.getReminders(dateTime)                 // todo implement reminders repo
        return events + tasks // + reminders
    }

    override suspend fun updateAgendaForDayFromRemote(dateTime: ZonedDateTime) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Get fresh data
                val result = agendaApi.getAgenda(dateTime)

//                eventRepository.clearEventsForDayLocally(dateTime) // clear local data // todo add this?
                // Insert fresh data into db
                result.events.forEach { event ->
                    val result2 =
                        eventRepository.upsertEventLocally(event.toDomain())
                    if(result2 is ResultUiText.Error) {
                        throw IllegalStateException(result2.message.asStrOrNull())
                    }
                }

//                taskRepository.clearTasksForDayLocally(dateTime) // clear local data // todo add this?
                // Insert fresh data into db
                result.tasks.forEach { task ->
                    val result2 =
                        taskRepository.upsertTaskLocally(task.toDomain())
                    if(result2 is ResultUiText.Error) {
                        throw IllegalStateException(result2.message.asStrOrNull())
                    }
                }

//                reminderRepository.clearRemindersForDayLocally(dateTime) // clear local data // todo add this?
                // Insert fresh data into db
                result.reminders.forEach { reminder ->
                    val result2 =
                        reminderRepository.upsertReminderLocally(reminder.toDomain())
                    if(result2 is ResultUiText.Error) {
                        throw IllegalStateException(result2.message.asStrOrNull())
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // don't send error to user, just log it (silent fail is ok here)
            }
        }
    }

    override fun getAgendaForDayFlow(dateTime: ZonedDateTime): Flow<List<AgendaItem>> {

        return flow {
            supervisorScope {
                    try {
                        updateAgendaForDayFromRemote(dateTime)

                        emitAll(combine(
                            taskRepository.getTasksForDayFlow(dateTime),
                            eventRepository.getEventsForDayFlow(dateTime),
                            reminderRepository.getRemindersForDayFlow(dateTime)
                        ) { tasks, events, reminders ->
                            events + tasks + reminders
                        })
                    }
                    catch (e: Exception) {
                        e.printStackTrace()
                        // don't send error to user, just log it (silent fail is ok here)
                    }

                }
        }
    }

    override suspend fun syncAgenda(): ResultUiText<Void> {
        val deletedEventIds = eventRepository.getDeletedEventIdsLocally()
        val deletedTaskIds = taskRepository.getDeletedTaskIdsLocally()
        val deletedReminderIds = reminderRepository.getDeletedReminderIdsLocally()

        val deletedSuccessfully =
            agendaApi.syncAgenda(
                AgendaSync(
                    deleteEventIds = deletedEventIds,
                    deleteTaskIds = deletedTaskIds,
                    deleteReminderIds = deletedReminderIds,
                ).toDTO()
            )

        if (deletedSuccessfully) {
            return eventRepository.deleteEventsFinallyLocally(deletedEventIds)
        } else {
            return ResultUiText.Error(UiText.ResOrStr(R.string.agenda_sync_error, "Failed to sync agenda - deleteFinallyEventIds"))
        }
    }

    ///////////////////////////////////////////////
    // • EVENTS

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

    override suspend fun clearAllEventsLocally(): ResultUiText<Void> {
        return eventRepository.clearAllEventsLocally()
    }

    override suspend fun validateAttendeeExists(attendeeEmail: Email): ResultUiText<Attendee> {
        return attendeeRepository.getAttendee(attendeeEmail)
    }

    override suspend fun removeLoggedInUserFromEventId(eventId: EventId): ResultUiText<Void> {
        eventRepository.deleteEventsFinallyLocally(listOf(eventId))
        return attendeeRepository.removeLoggedInUserFromEventId(eventId)
    }

    ///////////////////////////////////////////////
    // • TASKS

    override suspend fun createTask(task: AgendaItem.Task): ResultUiText<Void> {
        return taskRepository.createTask(task)
    }

    override suspend fun getTask(taskId: TaskId): AgendaItem.Task? {
        return taskRepository.getTask(taskId)
    }

    override suspend fun updateTask(task: AgendaItem.Task): ResultUiText<Void> {
        return taskRepository.updateTask(task)
    }

    override suspend fun deleteTaskId(taskId: TaskId): ResultUiText<Void> {
        return taskRepository.deleteTask(taskId)
    }

    override suspend fun clearAllTasksLocally(): ResultUiText<Void> {
        return taskRepository.clearAllTasksLocally()
    }

    ///////////////////////////////////////////////
    // • REMINDER

    override suspend fun createReminder(reminder: AgendaItem.Reminder): ResultUiText<Void> {
        return reminderRepository.createReminder(reminder)
    }

    override suspend fun getReminder(reminderId: ReminderId): AgendaItem.Reminder? {
        return reminderRepository.getReminder(reminderId)
    }

    override suspend fun updateReminder(reminder: AgendaItem.Reminder): ResultUiText<Void> {
        return reminderRepository.updateReminder(reminder)
    }

    override suspend fun deleteReminderId(reminderId: ReminderId): ResultUiText<Void> {
        return reminderRepository.deleteReminder(reminderId)
    }

    override suspend fun clearAllRemindersLocally(): ResultUiText<Void> {
        return reminderRepository.clearAllRemindersLocally()
    }

}