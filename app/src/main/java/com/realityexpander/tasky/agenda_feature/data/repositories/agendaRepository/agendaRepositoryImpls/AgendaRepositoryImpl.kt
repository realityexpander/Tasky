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

    override suspend fun updateAgendaForDayFromRemote(dateTime: ZonedDateTime) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Get fresh data
                val result = agendaApi.getAgenda(dateTime)
                if(result.isSuccess) {
                    val agenda = result.getOrNull()

                    eventRepository.clearEventsForDayLocally(dateTime) // clear local data
                    // Insert fresh data locally
                    agenda?.events?.forEach { event ->
                        val result2 =
                            eventRepository.upsertEventLocally(event.toDomain())
                        if(result2 is ResultUiText.Error) {
                            throw IllegalStateException(result2.message.asStrOrNull())
                        }
                    }

                    taskRepository.clearTasksForDayLocally(dateTime) // clear local data
                    // Insert fresh data locally
                    agenda?.tasks?.forEach { task ->
                        val result2 =
                            taskRepository.upsertTaskLocally(task.toDomain())
                        if(result2 is ResultUiText.Error) {
                            throw IllegalStateException(result2.message.asStrOrNull())
                        }
                    }

                    reminderRepository.clearRemindersForDayLocally(dateTime) // clear local data
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
                    updateAgendaForDayFromRemote(dateTime)

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

    override suspend fun syncAgenda(): ResultUiText<Void> {
        val modifiedAgendaItems = syncRepository.getModifiedAgendaItemsForSync()
        if (modifiedAgendaItems.isEmpty()) {
            return ResultUiText.Success(null)
        }

        // Sync the `Create` API calls first
        modifiedAgendaItems.forEach {
            when (it.modificationTypeForSync) {
                ModificationTypeForSync.Created -> {
                    when (it.agendaItemTypeForSync) {
                        AgendaItemTypeForSync.Event -> {
                            createEvent(
                                getEvent(it.agendaItemId, true)
                                    ?: return ResultUiText.Error(UiText.Res(R.string.error_syncing_agenda)),
                                true
                            )
                            syncRepository.deleteModifiedAgendaItemByAgendaItemId(
                                it.agendaItemId,
                                ModificationTypeForSync.Created
                            )
                        }
                        AgendaItemTypeForSync.Task -> {
                            createTask(
                                getTask(it.agendaItemId, true)
                                    ?: return ResultUiText.Error(UiText.Res(R.string.error_syncing_agenda)),
                                true
                            )
                            syncRepository.deleteModifiedAgendaItemByAgendaItemId(
                                it.agendaItemId,
                                ModificationTypeForSync.Created
                            )
                        }
                        AgendaItemTypeForSync.Reminder -> {
                            createReminder(
                                getReminder(it.agendaItemId, true)
                                    ?: return ResultUiText.Error(UiText.Res(R.string.error_syncing_agenda)),
                                true
                            )
                            syncRepository.deleteModifiedAgendaItemByAgendaItemId(
                                it.agendaItemId,
                                ModificationTypeForSync.Created
                            )
                        }
                    }
                }
                else -> {
                    // do nothing
                }
            }
        }

        // Sync the `Updates` API calls next
        modifiedAgendaItems.forEach {
            when (it.modificationTypeForSync) {
                ModificationTypeForSync.Updated -> {
                    when (it.agendaItemTypeForSync) {
                        AgendaItemTypeForSync.Event -> {
                            updateEvent(
                                getEvent(it.agendaItemId, true)
                                    ?: return ResultUiText.Error(UiText.Res(R.string.error_syncing_agenda)),
                                true
                            )
                            syncRepository.deleteModifiedAgendaItemByAgendaItemId(
                                it.agendaItemId,
                                ModificationTypeForSync.Updated
                            )
                        }
                        AgendaItemTypeForSync.Task -> {
                            updateTask(
                                getTask(it.agendaItemId, true)
                                    ?: return ResultUiText.Error(UiText.Res(R.string.error_syncing_agenda)),
                                true
                            )
                            syncRepository.deleteModifiedAgendaItemByAgendaItemId(
                                it.agendaItemId,
                                ModificationTypeForSync.Updated
                            )
                        }
                        AgendaItemTypeForSync.Reminder -> {
                            updateReminder(
                                getReminder(it.agendaItemId, true)
                                    ?: return ResultUiText.Error(UiText.Res(R.string.error_syncing_agenda)),
                                true
                            )
                            syncRepository.deleteModifiedAgendaItemByAgendaItemId(
                                it.agendaItemId,
                                ModificationTypeForSync.Updated
                            )
                        }
                    }
                }
                else -> {
                    // do nothing
                }
            }
        }

        // Send the `Deletes` last
        return  syncRepository.syncDeletedAgendaItems(modifiedAgendaItems)
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
        return taskRepository.deleteTaskBy(task)
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