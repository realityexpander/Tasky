package com.realityexpander.tasky.agenda_feature.domain

import com.realityexpander.tasky.agenda_feature.common.util.EventId
import com.realityexpander.tasky.agenda_feature.common.util.TaskId
import com.realityexpander.tasky.core.util.Email
import kotlinx.coroutines.flow.Flow
import java.time.ZonedDateTime

interface IAgendaRepository {

    // • Agenda Repository
    suspend fun getAgendaForDay(dateTime: ZonedDateTime): List<AgendaItem>
    fun getAgendaForDayFlow(dateTime: ZonedDateTime): Flow<List<AgendaItem>>
    suspend fun syncAgenda(): ResultUiText<Void>
    suspend fun updateAgendaForDayFromRemote(dateTime: ZonedDateTime)

    // • Event Repository
    suspend fun createEvent(event: AgendaItem.Event): ResultUiText<AgendaItem.Event>
    suspend fun getEvent(eventId: EventId): AgendaItem.Event?
    suspend fun updateEvent(event: AgendaItem.Event): ResultUiText<AgendaItem.Event>
    suspend fun deleteEventId(eventId: EventId): ResultUiText<Void>
    suspend fun clearAllEventsLocally(): ResultUiText<Void>

    // • Attendee Repository
    suspend fun validateAttendeeExists(attendeeEmail: Email): ResultUiText<Attendee>
    suspend fun removeLoggedInUserFromEventId(eventId: EventId): ResultUiText<Void>

    // • Task Repository
    suspend fun createTask(task: AgendaItem.Task): ResultUiText<Void>
    suspend fun getTask(taskId: TaskId): AgendaItem.Task?
    suspend fun updateTask(task: AgendaItem.Task): ResultUiText<Void>
    suspend fun deleteTaskId(taskId: TaskId): ResultUiText<Void>
    suspend fun clearAllTasksLocally(): ResultUiText<Void>

    // todo implement repository
//    suspend fun createReminder(reminder: AgendaItem.Reminder): Boolean
//    suspend fun getReminder(reminderId: ReminderId): AgendaItem.Reminder?
//    suspend fun updateReminder(reminder: AgendaItem.Reminder): Boolean
//    suspend fun deleteReminder(reminderId: ReminderId): Boolean
//    suspend fun deleteAllReminders(): Boolean
}