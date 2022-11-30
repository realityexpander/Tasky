package com.realityexpander.remindery.agenda_feature.data.repositories.reminderRepository.reminderRepositoryImpls

import com.realityexpander.remindery.agenda_feature.data.common.convertersDTOEntityDomain.toDTO
import com.realityexpander.remindery.agenda_feature.data.common.convertersDTOEntityDomain.toDomain
import com.realityexpander.remindery.agenda_feature.data.common.convertersDTOEntityDomain.toEntity
import com.realityexpander.remindery.agenda_feature.data.repositories.reminderRepository.local.IReminderDao
import com.realityexpander.tasky.agenda_feature.common.util.ReminderId
import com.realityexpander.tasky.agenda_feature.data.repositories.reminderRepository.remote.reminderApi.IReminderApi
import com.realityexpander.tasky.agenda_feature.domain.AgendaItem
import com.realityexpander.tasky.agenda_feature.domain.IReminderRepository
import com.realityexpander.tasky.agenda_feature.domain.ResultUiText
import com.realityexpander.tasky.core.presentation.common.util.UiText
import com.realityexpander.tasky.core.util.rethrowIfCancellation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.ZonedDateTime

class ReminderRepositoryImpl(
    private val reminderDao: IReminderDao,
    private val reminderApi: IReminderApi,
) : IReminderRepository {

    // • CREATE

    override suspend fun createReminder(reminder: AgendaItem.Reminder): ResultUiText<Void> {
        return try {
            // todo add to list of updates to send to server

            reminderDao.createReminder(reminder.toEntity())  // save to local DB first
            reminderApi.createReminder(reminder.toDTO())

            ResultUiText.Success()
        } catch (e: Exception) {
            e.rethrowIfCancellation()
            ResultUiText.Error(UiText.Str(e.message ?: "createReminder error"))
        }
    }


    // • UPSERT

    override suspend fun upsertReminderLocally(reminder: AgendaItem.Reminder): ResultUiText<Void> {
        return try {
            reminderDao.upsertReminder(reminder.toEntity())  // save to local DB ONLY

            ResultUiText.Success()
        } catch (e: Exception) {
            e.rethrowIfCancellation()
            ResultUiText.Error(UiText.Str(e.message ?: "upsertReminder error"))
        }
    }


    // • READ

    override suspend fun getRemindersForDay(zonedDateTime: ZonedDateTime): List<AgendaItem.Reminder> {
        return reminderDao.getRemindersForDay(zonedDateTime).map { it.toDomain() }
    }

    override fun getRemindersForDayFlow(zonedDateTime: ZonedDateTime): Flow<List<AgendaItem.Reminder>> {
        return reminderDao.getRemindersForDayFlow(zonedDateTime).map { reminderEntities ->
            reminderEntities.map { reminderEntity ->
                reminderEntity.toDomain()
            }
        }
    }

    override suspend fun getReminder(reminderId: ReminderId): AgendaItem.Reminder? {
        return try {
            reminderDao.getReminderById(reminderId)?.toDomain() // get from local DB first

            val response = reminderApi.getReminder(reminderId)
            reminderDao.updateReminder(response.toDomain().toEntity())  // update with response from server

            //ResultUiText.Success(response.toDomain())
            response.toDomain()
        } catch (e: Exception) {
            e.rethrowIfCancellation()
            null
        }
    }

    suspend fun getAllRemindersLocally(): List<AgendaItem.Reminder> {
        return try {
            reminderDao.getReminders().map { it.toDomain() }
        } catch (e: Exception) {
            e.rethrowIfCancellation()
            emptyList()
        }
    }

    override suspend fun updateReminder(reminder: AgendaItem.Reminder): ResultUiText<Void> {
        return try {
            // todo add to list of updates to send to server

            reminderDao.updateReminder(reminder.toEntity())  // optimistic update
            reminderApi.updateReminder(reminder.toDTO())

            ResultUiText.Success()
        } catch (e: Exception) {
            e.rethrowIfCancellation()
            ResultUiText.Error(UiText.Str(e.localizedMessage ?: "updateReminder error"))
        }
    }

    // • DELETE

    override suspend fun deleteReminder(reminderId: ReminderId): ResultUiText<Void> {
        return try {
            // Optimistic delete
            reminderDao.markReminderDeletedById(reminderId)

            // Attempt to delete on server
            val response = reminderApi.deleteReminder(reminderId)
            if (response.isSuccess) {
                // Success, delete fully from local DB
                reminderDao.deleteFinallyByReminderIds(listOf(reminderId))  // just one reminder
                ResultUiText.Success()
            } else {
                ResultUiText.Error(UiText.Str(response.exceptionOrNull()?.localizedMessage ?: "deleteReminder error"))
            }
        } catch (e: Exception) {
            e.rethrowIfCancellation()
            ResultUiText.Error(UiText.Str(e.message ?: "deleteReminderId error"))
        }
    }

    override suspend fun getDeletedReminderIdsLocally(): List<ReminderId> {
        return try {
            reminderDao.getMarkedDeletedReminderIds()
        } catch (e: Exception) {
            e.rethrowIfCancellation()
            emptyList()
        }
    }

    override suspend fun deleteRemindersFinallyLocally(reminderIds: List<ReminderId>): ResultUiText<Void> {
        return try {
            reminderDao.deleteFinallyByReminderIds(reminderIds)

            ResultUiText.Success() // todo return the deleted reminders, for undo
        } catch (e: Exception) {
            e.rethrowIfCancellation()
            ResultUiText.Error(UiText.Str(e.message ?: "deleteFinallyReminderIds error"))
        }
    }

    // • CLEAR / CLEANUP

    override suspend fun clearAllRemindersLocally(): ResultUiText<Void> {
        return try {
            reminderDao.clearAllReminders()

            ResultUiText.Success() // todo return the cleared reminder, yes for undo
        } catch (e: Exception) {
            e.rethrowIfCancellation()
            ResultUiText.Error(UiText.Str(e.message ?: "clearAllReminders error"))
        }
    }

    override suspend fun clearRemindersForDayLocally(zonedDateTime: ZonedDateTime): ResultUiText<Void> {
        return try {
            reminderDao.clearAllRemindersForDay(zonedDateTime)

            ResultUiText.Success() // todo return the cleared reminder, for undo
        } catch (e: Exception) {
            e.rethrowIfCancellation()
            ResultUiText.Error(UiText.Str(e.message ?: "clearRemindersForDay error"))
        }
    }
}