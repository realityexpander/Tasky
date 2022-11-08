package com.realityexpander.tasky.agenda_feature.domain

import com.realityexpander.tasky.agenda_feature.util.EventId
import java.time.ZonedDateTime

interface IAgendaRepository {

    suspend fun getAgendaDay(dateTime: ZonedDateTime): List<AgendaItem>
    suspend fun syncAgenda(): Boolean

    suspend fun createEvent(event: AgendaItem.Event): Boolean
    suspend fun getEvent(eventId: EventId): AgendaItem.Event?
    suspend fun updateEvent(event: AgendaItem.Event): Boolean
    suspend fun deleteEventId(eventId: EventId): Boolean
    suspend fun clearAllEvents(): Boolean

    // todo implement repository
//    suspend fun createTask(task: AgendaItem.Task): Boolean
//    suspend fun getTask(taskId: TaskId): AgendaItem.Task?
//    suspend fun updateTask(task: AgendaItem.Task): Boolean
//    suspend fun deleteTask(taskId: TaskId): Boolean
//    suspend fun deleteAllTasks(): Boolean

    // todo implement repository
//    suspend fun createReminder(reminder: AgendaItem.Reminder): Boolean
//    suspend fun getReminder(reminderId: ReminderId): AgendaItem.Reminder?
//    suspend fun updateReminder(reminder: AgendaItem.Reminder): Boolean
//    suspend fun deleteReminder(reminderId: ReminderId): Boolean
//    suspend fun deleteAllReminders(): Boolean

}