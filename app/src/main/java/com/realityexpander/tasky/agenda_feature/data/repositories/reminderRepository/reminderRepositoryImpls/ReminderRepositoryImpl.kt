package com.realityexpander.remindery.agenda_feature.data.repositories.reminderRepository.reminderRepositoryImpls

import com.realityexpander.remindery.agenda_feature.data.common.convertersDTOEntityDomain.toDTO
import com.realityexpander.remindery.agenda_feature.data.common.convertersDTOEntityDomain.toDomain
import com.realityexpander.remindery.agenda_feature.data.common.convertersDTOEntityDomain.toEntity
import com.realityexpander.remindery.agenda_feature.data.repositories.reminderRepository.local.IReminderDao
import com.realityexpander.tasky.R
import com.realityexpander.tasky.agenda_feature.common.util.ReminderId
import com.realityexpander.tasky.agenda_feature.data.repositories.reminderRepository.remote.reminderApi.IReminderApi
import com.realityexpander.tasky.agenda_feature.data.repositories.syncRepository.ISyncRepository
import com.realityexpander.tasky.agenda_feature.domain.AgendaItem
import com.realityexpander.tasky.agenda_feature.domain.IReminderRepository
import com.realityexpander.tasky.agenda_feature.domain.ResultUiText
import com.realityexpander.tasky.core.presentation.util.UiText
import com.realityexpander.tasky.core.util.ConnectivityObserver.InternetConnectivityObserverImpl.Companion.isInternetReachable
import com.realityexpander.tasky.core.util.rethrowIfCancellation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.ZonedDateTime

class ReminderRepositoryImpl(
    private val reminderDao: IReminderDao,
    private val reminderApi: IReminderApi,
    private val syncRepository: ISyncRepository
) : IReminderRepository {

    // • CREATE

    override suspend fun createReminder(reminder: AgendaItem.Reminder, isRemoteOnly: Boolean): ResultUiText<Void> {
        return try {
            if(!isRemoteOnly) {
                // save to local DB first
                reminderDao.createReminder(reminder.copy(isSynced = false).toEntity())
                syncRepository.addCreatedSyncItem(reminder)
            }

            if(!isInternetReachable) return ResultUiText.Error(UiText.Res(R.string.error_no_internet))

            val result = reminderApi.createReminder(reminder.toDTO())
            if(result.isSuccess) {
                syncRepository.removeCreatedSyncItem(reminder)
                reminderDao.updateReminder(reminder.copy(isSynced = true).toEntity())
            }

            ResultUiText.Success()
        } catch (e: Exception) {
            e.rethrowIfCancellation()
            ResultUiText.Error(UiText.Str(e.message ?: "createReminder error"))
        }
    }


    // • UPSERT (LOCAL ONLY)

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

    override suspend fun getReminder(reminderId: ReminderId, isLocalOnly: Boolean): AgendaItem.Reminder? {
        return reminderDao.getReminderById(reminderId)?.toDomain()
    }

    override fun getRemindersForRemindAtDateTimeRangeFlow(
        from: ZonedDateTime,
        to: ZonedDateTime
    ): Flow<List<AgendaItem.Reminder>> {
        return reminderDao.getLocalRemindersForRemindAtDateTimeRangeFlow(from, to).map { reminderEntities ->
            reminderEntities.map { reminderEntity ->
                reminderEntity.toDomain()
            }
        }
    }

    override suspend fun updateReminder(reminder: AgendaItem.Reminder, isRemoteOnly: Boolean): ResultUiText<Void> {
        return try {
            if(!isRemoteOnly) {
                // save to local DB first
                reminderDao.updateReminder(reminder.copy(isSynced = false).toEntity())
                syncRepository.addUpdatedSyncItem(reminder)
            }

            if(!isInternetReachable) return ResultUiText.Error(UiText.Res(R.string.error_no_internet))

            val result = reminderApi.updateReminder(reminder.toDTO()) // no payload from server for this
            if(result.isSuccess) {
                syncRepository.removeUpdatedSyncItem(reminder)
                reminderDao.updateReminder(reminder.copy(isSynced = true).toEntity())
            }

            ResultUiText.Success()
        } catch (e: Exception) {
            e.rethrowIfCancellation()
            ResultUiText.Error(UiText.Str(e.localizedMessage ?: "updateReminder error"))
        }
    }


    // • DELETE

    override suspend fun deleteReminder(reminder: AgendaItem.Reminder): ResultUiText<Void> {
        return try {
            reminderDao.deleteReminderById(reminder.id)
            syncRepository.addDeletedSyncItem(reminder)

            if(!isInternetReachable) return ResultUiText.Error(UiText.Res(R.string.error_no_internet))

            // Attempt to delete on server
            val response = reminderApi.deleteReminder(reminder.id)
            if (response.isSuccess) {
                syncRepository.removeDeletedSyncItem(reminder)
                ResultUiText.Success()
            } else {
                ResultUiText.Error(UiText.Str(response.exceptionOrNull()?.localizedMessage ?: "deleteReminder error"))
            }
        } catch (e: Exception) {
            e.rethrowIfCancellation()
            ResultUiText.Error(UiText.Str(e.message ?: "deleteReminder error"))
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
            reminderDao.clearAllSyncedRemindersForDay(zonedDateTime)

            ResultUiText.Success() // todo return the cleared reminder, for undo
        } catch (e: Exception) {
            e.rethrowIfCancellation()
            ResultUiText.Error(UiText.Str(e.message ?: "clearRemindersForDay error"))
        }
    }
}