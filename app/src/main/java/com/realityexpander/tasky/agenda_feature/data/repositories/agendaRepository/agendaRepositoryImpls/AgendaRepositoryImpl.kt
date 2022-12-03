package com.realityexpander.tasky.agenda_feature.data.repositories.agendaRepository.agendaRepositoryImpls

import com.realityexpander.remindery.agenda_feature.data.common.convertersDTOEntityDomain.toDomain
import com.realityexpander.tasky.agenda_feature.common.util.EventId
import com.realityexpander.tasky.agenda_feature.common.util.ReminderId
import com.realityexpander.tasky.agenda_feature.common.util.TaskId
import com.realityexpander.tasky.agenda_feature.data.common.convertersDTOEntityDomain.toDomain
import com.realityexpander.tasky.agenda_feature.data.repositories.agendaRepository.remote.IAgendaApi
import com.realityexpander.tasky.agenda_feature.data.repositories.attendeeRepository.IAttendeeRepository
import com.realityexpander.tasky.agenda_feature.data.repositories.syncRepository.ISyncRepository
import com.realityexpander.tasky.agenda_feature.data.repositories.syncRepository.local.AgendaItemTypeForSync
import com.realityexpander.tasky.agenda_feature.data.repositories.syncRepository.local.ModificationTypeForSync
import com.realityexpander.tasky.agenda_feature.domain.*
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
    private val syncRepository: ISyncRepository,
) : IAgendaRepository {

    ///////////////////////////////////////////////
    // • AGENDA

    override suspend fun getAgendaForDay(dateTime: ZonedDateTime): List<AgendaItem> {
        val events = eventRepository.getEventsForDay(dateTime)
        val tasks = taskRepository.getTasksForDay(dateTime)
        val reminders = reminderRepository.getRemindersForDay(dateTime)
        return events + tasks + reminders
    }

    override suspend fun updateLocalAgendaForDayFromRemote(dateTime: ZonedDateTime) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Get fresh data
                val result = agendaApi.getAgenda(dateTime)

                if(result.isSuccess) {
                    val agenda = result.getOrNull()

                    eventRepository.clearEventsForDayLocally(dateTime)
                    // Insert fresh data locally
                    agenda?.events?.forEach { event ->
                        val result2 =
                            eventRepository.upsertEventLocally(event.toDomain())
                        if(result2 is ResultUiText.Error) {
                            throw IllegalStateException(result2.message.asStrOrNull())
                        }
                    }

                    taskRepository.clearTasksForDayLocally(dateTime)
                    // Insert fresh data locally
                    agenda?.tasks?.forEach { task ->
                        val result2 =
                            taskRepository.upsertTaskLocally(task.toDomain())
                        if(result2 is ResultUiText.Error) {
                            throw IllegalStateException(result2.message.asStrOrNull())
                        }
                    }

                    reminderRepository.clearRemindersForDayLocally(dateTime)
                    // Insert fresh data locally
                    agenda?.reminders?.forEach { reminder ->
                        val result2 =
                            reminderRepository.upsertReminderLocally(reminder.toDomain())
                        if(result2 is ResultUiText.Error) {
                            throw IllegalStateException(result2.message.asStrOrNull())
                        }
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
                    updateLocalAgendaForDayFromRemote(dateTime)

                    emitAll(combine(
                        taskRepository.getTasksForDayFlow(dateTime),
                        eventRepository.getEventsForDayFlow(dateTime),
                        reminderRepository.getRemindersForDayFlow(dateTime)
                    ) { tasks, events, reminders ->
                        events + tasks + reminders
                    })
                } catch (e: Exception) {
                        e.printStackTrace()
                        // don't send error to user, just log it (silent fail is ok here)
                    }
                }
        }
    }

    // Upload local changes to remote
    override suspend fun syncAgenda(): ResultUiText<Void> {
        val syncAgendaItems = syncRepository.getSyncAgendaItemEntities()
        if (syncAgendaItems.isEmpty()) {
            return ResultUiText.Success(null)
        }

        // Sync the `Create` API calls first
        syncAgendaItems.forEach { syncItem ->
            when (syncItem.modificationTypeForSync) {
                ModificationTypeForSync.Created -> {
                    when (syncItem.agendaItemTypeForSync) {
                        AgendaItemTypeForSync.Event -> {
                            val event = getEvent(syncItem.agendaItemId, true) ?: return@forEach
                            val result = createEvent(event, true)
                            if(result is ResultUiText.Success) {
                                syncRepository.deleteSyncAgendaItemByAgendaItemId(
                                    syncItem.agendaItemId,
                                    ModificationTypeForSync.Created
                                )
                                updateEvent(event.copy(isSynced = true))
                            }
                        }
                        AgendaItemTypeForSync.Task -> {
                            val task = getTask(syncItem.agendaItemId, true) ?: return@forEach
                            val result = createTask(task, true)
                            if(result is ResultUiText.Success) {
                                syncRepository.deleteSyncAgendaItemByAgendaItemId(
                                    syncItem.agendaItemId,
                                    ModificationTypeForSync.Created
                                )
                                updateTask(task.copy(isSynced = true))
                            }
                        }
                        AgendaItemTypeForSync.Reminder -> {
                            val reminder = getReminder(syncItem.agendaItemId, true) ?: return@forEach
                            val result = createReminder(reminder, true)
                            if(result is ResultUiText.Success) {
                                syncRepository.deleteSyncAgendaItemByAgendaItemId(
                                    syncItem.agendaItemId,
                                    ModificationTypeForSync.Created
                                )
                                updateReminder(reminder.copy(isSynced = true))
                            }
                        }
                    }
                }
                else -> {
                    // do nothing
                }
            }
        }

        // Sync the `Updates` API calls next
        syncAgendaItems.forEach {
            when (it.modificationTypeForSync) {
                ModificationTypeForSync.Updated -> {
                    when (it.agendaItemTypeForSync) {
                        AgendaItemTypeForSync.Event -> {
                            val event = getEvent(it.agendaItemId, true) ?: return@forEach
                            val result = updateEvent(event, true)
                            if(result is ResultUiText.Success) {
                                syncRepository.deleteSyncAgendaItemByAgendaItemId(
                                    it.agendaItemId,
                                    ModificationTypeForSync.Updated
                                )
                                updateEvent(event.copy(isSynced = true))
                            }
                        }
                        AgendaItemTypeForSync.Task -> {
                            val task = getTask(it.agendaItemId, true) ?: return@forEach
                            val result = updateTask(task, true)
                            if(result is ResultUiText.Success) {
                                syncRepository.deleteSyncAgendaItemByAgendaItemId(
                                    it.agendaItemId,
                                    ModificationTypeForSync.Updated
                                )
                                updateTask(task.copy(isSynced = true))
                            }
                        }
                        AgendaItemTypeForSync.Reminder -> {
                            val reminder = getReminder(it.agendaItemId, true) ?: return@forEach
                            val result = updateReminder(reminder, true)
                            if(result is ResultUiText.Success) {
                                syncRepository.deleteSyncAgendaItemByAgendaItemId(
                                    it.agendaItemId,
                                    ModificationTypeForSync.Updated
                                )
                                updateReminder(reminder.copy(isSynced = true))
                            }
                        }
                    }
                }
                else -> {
                    // do nothing
                }
            }
        }

        // Send the `Deletes` last
        return  syncRepository.syncDeletedAgendaItems(syncAgendaItems)
    }

    ///////////////////////////////////////////////
    // • EVENTS

    override suspend fun createEvent(event: AgendaItem.Event, isRemoteOnly: Boolean): ResultUiText<AgendaItem.Event> {
        return eventRepository.createEvent(event, isRemoteOnly)
    }

    override suspend fun getEvent(eventId: EventId, isLocalOnly: Boolean): AgendaItem.Event? {
        return eventRepository.getEvent(eventId, isLocalOnly)
    }

    override suspend fun updateEvent(event: AgendaItem.Event, isRemoteOnly: Boolean): ResultUiText<AgendaItem.Event> {
        return eventRepository.updateEvent(event, isRemoteOnly)
    }

    override suspend fun deleteEvent(event: AgendaItem.Event): ResultUiText<Void> {
        return eventRepository.deleteEvent(event)
    }

    override suspend fun clearAllEventsLocally(): ResultUiText<Void> {
        return eventRepository.clearAllEventsLocally()
    }

    override suspend fun validateAttendeeExists(attendeeEmail: Email): ResultUiText<Attendee> {
        return attendeeRepository.getAttendee(attendeeEmail)
    }

    override suspend fun removeLoggedInUserFromEvent(event: AgendaItem.Event): ResultUiText<Void> {
        eventRepository.deleteEvent(event)
        return attendeeRepository.removeLoggedInUserFromEvent(event)
    }

    ///////////////////////////////////////////////
    // • TASKS

    override suspend fun createTask(task: AgendaItem.Task, isRemoteOnly: Boolean): ResultUiText<Void> {
        return taskRepository.createTask(task, isRemoteOnly)
    }

    override suspend fun getTask(taskId: TaskId, isLocalOnly: Boolean): AgendaItem.Task? {
        return taskRepository.getTask(taskId, isLocalOnly)
    }

    override suspend fun updateTask(task: AgendaItem.Task, isRemoteOnly: Boolean): ResultUiText<Void> {
        return taskRepository.updateTask(task, isRemoteOnly)
    }

    override suspend fun deleteTask(task: AgendaItem.Task): ResultUiText<Void> {
        return taskRepository.deleteTask(task)
    }

    override suspend fun clearAllTasksLocally(): ResultUiText<Void> {
        return taskRepository.clearAllTasksLocally()
    }

    ///////////////////////////////////////////////
    // • REMINDER

    override suspend fun createReminder(reminder: AgendaItem.Reminder, isRemoteOnly: Boolean): ResultUiText<Void> {
        return reminderRepository.createReminder(reminder, isRemoteOnly)
    }

    override suspend fun getReminder(reminderId: ReminderId, isLocalOnly: Boolean): AgendaItem.Reminder? {
        return reminderRepository.getReminder(reminderId, isLocalOnly)
    }

    override suspend fun updateReminder(reminder: AgendaItem.Reminder, isRemoteOnly: Boolean): ResultUiText<Void> {
        return reminderRepository.updateReminder(reminder, isRemoteOnly)
    }

    override suspend fun deleteReminder(reminder: AgendaItem.Reminder): ResultUiText<Void> {
        return reminderRepository.deleteReminder(reminder)
    }

    override suspend fun clearAllRemindersLocal(): ResultUiText<Void> {
        return reminderRepository.clearAllRemindersLocally()
    }

}