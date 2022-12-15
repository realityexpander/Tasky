package com.realityexpander.tasky.agenda_feature.domain

import com.realityexpander.tasky.agenda_feature.common.util.EventId
import com.realityexpander.tasky.agenda_feature.common.util.ReminderId
import com.realityexpander.tasky.agenda_feature.common.util.TaskId
import com.realityexpander.tasky.core.presentation.util.ResultUiText
import com.realityexpander.tasky.core.util.Email
import kotlinx.coroutines.flow.Flow
import java.time.ZonedDateTime

interface IAgendaRepository {

    // • Agenda Repository
    suspend fun getAgendaForDay(dateTime: ZonedDateTime): List<AgendaItem>
    fun getAgendaForDayFlow(dateTime: ZonedDateTime): Flow<List<AgendaItem>>
    suspend fun syncAgenda(): ResultUiText<Void>
    suspend fun updateLocalAgendaDayFromRemote(dateTime: ZonedDateTime): ResultUiText<Unit>
    fun getLocalAgendaItemsWithRemindAtInDateTimeRangeFlow(startDateTime: ZonedDateTime, endDateTime: ZonedDateTime): Flow<List<AgendaItem>>
    suspend fun clearAllAgendaItemsLocally(): ResultUiText<Void>

    // • Event Repository
    suspend fun createEvent(event: AgendaItem.Event, isRemoteOnly: Boolean = false): ResultUiText<AgendaItem.Event>
    suspend fun getEvent(eventId: EventId, isLocalOnly: Boolean = false): AgendaItem.Event?
    suspend fun updateEvent(event: AgendaItem.Event, isRemoteOnly: Boolean = false): ResultUiText<AgendaItem.Event>
    suspend fun deleteEvent(event: AgendaItem.Event): ResultUiText<Void>
    suspend fun clearAllEventsLocally(): ResultUiText<Void>

    // • Attendee Repository
    suspend fun validateAttendeeExists(attendeeEmail: Email): ResultUiText<Attendee>
    suspend fun removeLoggedInUserFromEvent(event: AgendaItem.Event): ResultUiText<Void>

    // • Task Repository
    suspend fun createTask(task: AgendaItem.Task, isRemoteOnly: Boolean = false): ResultUiText<Void>
    suspend fun getTask(taskId: TaskId, isLocalOnly: Boolean = false): AgendaItem.Task?
    suspend fun updateTask(task: AgendaItem.Task, isRemoteOnly: Boolean = false): ResultUiText<Void>
    suspend fun deleteTask(task: AgendaItem.Task): ResultUiText<Void>
    suspend fun clearAllTasksLocally(): ResultUiText<Void>

    // • Reminder Repository
    suspend fun createReminder(reminder: AgendaItem.Reminder, isRemoteOnly: Boolean = false): ResultUiText<Void>
    suspend fun getReminder(reminderId: ReminderId, isLocalOnly: Boolean = false): AgendaItem.Reminder?
    suspend fun updateReminder(reminder: AgendaItem.Reminder, isRemoteOnly: Boolean = false): ResultUiText<Void>
    suspend fun deleteReminder(reminder: AgendaItem.Reminder): ResultUiText<Void>
    suspend fun clearAllRemindersLocally(): ResultUiText<Void>
}