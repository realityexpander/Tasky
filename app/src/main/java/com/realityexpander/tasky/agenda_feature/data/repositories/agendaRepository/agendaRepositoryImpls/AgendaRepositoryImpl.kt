package com.realityexpander.tasky.agenda_feature.data.repositories.agendaRepository.agendaRepositoryImpls

import com.realityexpander.remindery.agenda_feature.data.common.convertersDTOEntityDomain.toDomain
import com.realityexpander.tasky.R
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
import com.realityexpander.tasky.core.presentation.util.UiText
import com.realityexpander.tasky.core.util.Email
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
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
        // Sequential DB queries - left for reference
//        val events = eventRepository.getEventsForDay(dateTime)
//        val tasks = taskRepository.getTasksForDay(dateTime)
//        val reminders = reminderRepository.getRemindersForDay(dateTime)
//        return events + tasks + reminders

        // run DB queries in parallel
        return supervisorScope {
            val events = async { eventRepository.getEventsForDay(dateTime) }
            val tasks = async { taskRepository.getTasksForDay(dateTime) }
            val reminders = async { reminderRepository.getRemindersForDay(dateTime) }

            events.await() + tasks.await() + reminders.await()
        }
    }

    override fun getAgendaForDayFlow(dateTime: ZonedDateTime): Flow<List<AgendaItem>> {
        CoroutineScope(Dispatchers.IO).launch {
            updateLocalAgendaDayFromRemote(dateTime)
        }

        return flow {
            try {
                emitAll(combine(
                    taskRepository.getTasksForDayFlow(dateTime),
                    eventRepository.getEventsForDayFlow(dateTime),
                    reminderRepository.getRemindersForDayFlow(dateTime)
                ) { tasks,
                    events,
                    reminders ->
                    events + tasks + reminders
                })
            } catch (e: Exception) {
                e.printStackTrace()
                // don't send error to user, just log it (silent fail is ok here)
            }
        }
    }

    override suspend fun updateLocalAgendaDayFromRemote(dateTime: ZonedDateTime): ResultUiText<Unit> {
        try {
            // Get fresh data
            val result = agendaApi.getAgenda(dateTime)

            if (result.isSuccess) {
                val agenda = result.getOrNull()

                eventRepository.clearEventsForDayLocally(dateTime)
                // Insert fresh data locally
                agenda?.events?.forEach { event ->
                    val result2 =
                        eventRepository.upsertEventLocally(event.toDomain())
                    if (result2 is ResultUiText.Error) {
                        throw IllegalStateException(result2.message.asStrOrNull())
                    }
                }

                taskRepository.clearTasksForDayLocally(dateTime)
                // Insert fresh data locally
                agenda?.tasks?.forEach { task ->
                    val result2 =
                        taskRepository.upsertTaskLocally(task.toDomain())
                    if (result2 is ResultUiText.Error) {
                        throw IllegalStateException(result2.message.asStrOrNull())
                    }
                }

                reminderRepository.clearRemindersForDayLocally(dateTime)
                // Insert fresh data locally
                agenda?.reminders?.forEach { reminder ->
                    val result2 =
                        reminderRepository.upsertReminderLocally(reminder.toDomain())
                    if (result2 is ResultUiText.Error) {
                        throw IllegalStateException(result2.message.asStrOrNull())
                    }
                }

                return ResultUiText.Success(Unit)

            } else {
                return ResultUiText.Error(
                    UiText.Res(R.string.agenda_error_network)
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // don't send error to user, just log it (silent fail is ok here)
            return ResultUiText.Error(
                UiText.Res(R.string.agenda_error_network)
            )
        }

    }

    // Upload local changes (made while offline) to remote
    // Note: ResultUiText.Success is returned even if there are no changes to sync.
    // Note: ResultUiText.Error is returned if there are changes to sync, and ANY sync item fails.
    // ALL SYNC ITEMS WILL BE ATTEMPTED TO BE UPLOADED.
    override suspend fun syncAgenda(): ResultUiText<Void> {
        val syncItems = syncRepository.getSyncItems()
        if (syncItems.isEmpty()) {
            return ResultUiText.Success(null)
        }

        var isFailure = false

        // Sync the `Create` API calls first...
        syncItems.forEach { syncItem ->
            when (syncItem.modificationTypeForSync) {
                ModificationTypeForSync.Created -> {
                    val success = when (syncItem.agendaItemTypeForSync) {
                        AgendaItemTypeForSync.Event -> {
                            val event = getEvent(
                                eventId = syncItem.agendaItemId,
                                isLocalOnly = true
                            ) ?: return@forEach
                            createEvent(event = event, isRemoteOnly = true) is ResultUiText.Success
                        }
                        AgendaItemTypeForSync.Task -> {
                            val task = getTask(
                                taskId = syncItem.agendaItemId,
                                isLocalOnly = true
                            ) ?: return@forEach
                            createTask(task = task, isRemoteOnly = true) is ResultUiText.Success
                        }
                        AgendaItemTypeForSync.Reminder -> {
                            val reminder = getReminder(
                                reminderId = syncItem.agendaItemId,
                                isLocalOnly = true
                            ) ?: return@forEach
                            createReminder(
                                reminder = reminder,
                                isRemoteOnly = true
                            ) is ResultUiText.Success
                        }
                    }
                    if (!success) {
                        isFailure = true
                    }
                }
                else -> {
                    // do nothing
                }
            }
        }

        // Sync the `Update` API calls next...
        syncItems.forEach { syncItem ->
            when (syncItem.modificationTypeForSync) {
                ModificationTypeForSync.Updated -> {
                    val success = when (syncItem.agendaItemTypeForSync) {
                        AgendaItemTypeForSync.Event -> {
                            val event = getEvent(
                                eventId = syncItem.agendaItemId,
                                isLocalOnly = true
                            ) ?: return@forEach
                            updateEvent(event = event, isRemoteOnly = true) is ResultUiText.Success
                        }
                        AgendaItemTypeForSync.Task -> {
                            val task = getTask(
                                taskId = syncItem.agendaItemId,
                                isLocalOnly = true
                            ) ?: return@forEach
                            updateTask(task = task, isRemoteOnly = true) is ResultUiText.Success
                        }
                        AgendaItemTypeForSync.Reminder -> {
                            val reminder = getReminder(
                                reminderId = syncItem.agendaItemId,
                                isLocalOnly = true
                            ) ?: return@forEach
                            updateReminder(
                                reminder = reminder,
                                isRemoteOnly = true
                            ) is ResultUiText.Success
                        }
                    }
                    if (!success) {
                        isFailure = true
                    }
                }
                else -> {
                    // do nothing
                }
            }
        }

        // Sync the `Delete` last.
        if (syncRepository.syncDeletedAgendaItems(syncItems) is ResultUiText.Error) {
            isFailure = true
        }

        return if (isFailure) {
            ResultUiText.Error(UiText.Res(R.string.error_sync_failed))
        } else {
            ResultUiText.Success(null)
        }
    }

    ///////////////////////////////////////////////
    // • EVENT

    override suspend fun createEvent(
        event: AgendaItem.Event,
        isRemoteOnly: Boolean
    ): ResultUiText<AgendaItem.Event> {
        return eventRepository.createEvent(event, isRemoteOnly)
    }

    override suspend fun getEvent(
        eventId: EventId,
        isLocalOnly: Boolean
    ): AgendaItem.Event? {
        return eventRepository.getEvent(eventId, isLocalOnly)
    }

    override suspend fun updateEvent(
        event: AgendaItem.Event,
        isRemoteOnly: Boolean
    ): ResultUiText<AgendaItem.Event> {
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
    // • TASK

    override suspend fun createTask(
        task: AgendaItem.Task,
        isRemoteOnly: Boolean
    ): ResultUiText<Void> {
        return taskRepository.createTask(task, isRemoteOnly)
    }

    override suspend fun getTask(
        taskId: TaskId,
        isLocalOnly: Boolean
    ): AgendaItem.Task? {
        return taskRepository.getTask(taskId, isLocalOnly)
    }

    override suspend fun updateTask(
        task: AgendaItem.Task,
        isRemoteOnly: Boolean
    ): ResultUiText<Void> {
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

    override suspend fun createReminder(
        reminder: AgendaItem.Reminder,
        isRemoteOnly: Boolean
    ): ResultUiText<Void> {
        return reminderRepository.createReminder(reminder, isRemoteOnly)
    }

    override suspend fun getReminder(
        reminderId: ReminderId,
        isLocalOnly: Boolean
    ): AgendaItem.Reminder? {
        return reminderRepository.getReminder(reminderId, isLocalOnly)
    }

    override suspend fun updateReminder(
        reminder: AgendaItem.Reminder,
        isRemoteOnly: Boolean
    ): ResultUiText<Void> {
        return reminderRepository.updateReminder(reminder, isRemoteOnly)
    }

    override suspend fun deleteReminder(reminder: AgendaItem.Reminder): ResultUiText<Void> {
        return reminderRepository.deleteReminder(reminder)
    }

    override suspend fun clearAllRemindersLocal(): ResultUiText<Void> {
        return reminderRepository.clearAllRemindersLocally()
    }

}