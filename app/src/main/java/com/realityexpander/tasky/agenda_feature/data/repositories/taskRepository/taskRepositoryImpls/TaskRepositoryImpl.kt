package com.realityexpander.tasky.agenda_feature.data.repositories.taskRepository.taskRepositoryImpls

import com.realityexpander.tasky.R
import com.realityexpander.tasky.agenda_feature.common.util.TaskId
import com.realityexpander.tasky.agenda_feature.data.common.convertersDTOEntityDomain.toDTO
import com.realityexpander.tasky.agenda_feature.data.common.convertersDTOEntityDomain.toDomain
import com.realityexpander.tasky.agenda_feature.data.common.convertersDTOEntityDomain.toEntity
import com.realityexpander.tasky.agenda_feature.data.repositories.syncRepository.ISyncRepository
import com.realityexpander.tasky.agenda_feature.data.repositories.taskRepository.local.ITaskDao
import com.realityexpander.tasky.agenda_feature.data.repositories.taskRepository.remote.ITaskApi
import com.realityexpander.tasky.agenda_feature.domain.AgendaItem
import com.realityexpander.tasky.agenda_feature.domain.ITaskRepository
import com.realityexpander.tasky.agenda_feature.domain.ResultUiText
import com.realityexpander.tasky.core.presentation.util.UiText
import com.realityexpander.tasky.core.util.ConnectivityObserver.ConnectivityObserverImpl.Companion.isInternetAvailable
import com.realityexpander.tasky.core.util.rethrowIfCancellation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.ZonedDateTime

class TaskRepositoryImpl(
    private val taskDao: ITaskDao,
    private val taskApi: ITaskApi,
    private val syncRepository: ISyncRepository,
) : ITaskRepository {

    // • CREATE

    override suspend fun createTask(task: AgendaItem.Task, isRemoteOnly: Boolean): ResultUiText<Void> {
        return try {
            if(!isRemoteOnly) {
                taskDao.createTask(task.copy(isSynced = false).toEntity())  // save to local DB first
                syncRepository.addCreatedSyncItem(task)
            }

            if(!isInternetAvailable) return ResultUiText.Error(UiText.Res(R.string.error_no_internet))

            val result = taskApi.createTask(task.toDTO())
            if(result.isSuccess) {
                syncRepository.removeCreatedSyncItem(task)
                taskDao.updateTask(task.copy(isSynced = true).toEntity())
            }

            ResultUiText.Success()
        } catch (e: Exception) {
            e.rethrowIfCancellation()
            ResultUiText.Error(UiText.Str(e.message ?: "createTask error"))
        }
    }

    // • READ

    override suspend fun getTask(taskId: TaskId, isLocalOnly: Boolean): AgendaItem.Task? {
        return taskDao.getTaskById(taskId)?.toDomain()
    }

    override suspend fun getTasksForDay(zonedDateTime: ZonedDateTime): List<AgendaItem.Task> {
        return taskDao.getTasksForDay(zonedDateTime).map { it.toDomain() }
    }

    override fun getTasksForDayFlow(zonedDateTime: ZonedDateTime): Flow<List<AgendaItem.Task>> {
        return taskDao.getTasksForDayFlow(zonedDateTime).map { taskEntities ->
            taskEntities.map { taskEntity ->
                taskEntity.toDomain()
            }
        }
    }

    override fun getTasksForRemindAtDateTimeRangeFlow(from: ZonedDateTime, to: ZonedDateTime): Flow<List<AgendaItem.Task>> {
        return taskDao.getTasksForRemindAtDateTimeRangeFlow(from, to).map { taskEntities ->
            taskEntities.map { taskEntity ->
                taskEntity.toDomain()
            }
        }
    }

    // • UPDATE / UPSERT

    override suspend fun updateTask(task: AgendaItem.Task, isRemoteOnly: Boolean): ResultUiText<Void> {
        return try {
            if(!isRemoteOnly) {
                taskDao.updateTask(task.copy(isSynced = false).toEntity())  // save to local DB first
                syncRepository.addUpdatedSyncItem(task)
            }

            if(!isInternetAvailable) return ResultUiText.Error(UiText.Res(R.string.error_no_internet))

            val result = taskApi.updateTask(task.toDTO())
            if(result.isSuccess) {
                syncRepository.removeUpdatedSyncItem(task)
                taskDao.updateTask(task.copy(isSynced = true).toEntity())
            }
            ResultUiText.Success()

        } catch (e: Exception) {
            e.rethrowIfCancellation()
            ResultUiText.Error(UiText.Str(e.localizedMessage ?: "updateTask error"))
        }
    }

    override suspend fun upsertTaskLocally(task: AgendaItem.Task): ResultUiText<Void> {
        return try {
            taskDao.upsertTask(task.toEntity())  // save to local DB ONLY

            ResultUiText.Success()
        } catch (e: Exception) {
            e.rethrowIfCancellation()
            ResultUiText.Error(UiText.Str(e.message ?: "upsertTask error"))
        }
    }

    // • DELETE

    override suspend fun deleteTask(task: AgendaItem.Task): ResultUiText<Void> {
        return try {
            taskDao.deleteTaskById(task.id)
            syncRepository.addDeletedSyncItem(task)

            if(!isInternetAvailable) return ResultUiText.Error(UiText.Res(R.string.error_no_internet))

            // Attempt to delete on server
            val result = taskApi.deleteTask(task.id)
            if (result.isSuccess) {
                syncRepository.removeDeletedSyncItem(task)
                ResultUiText.Success()
            } else {
                ResultUiText.Error(UiText.Str(result.exceptionOrNull()?.localizedMessage ?: "deleteReminder error"))
            }
        } catch (e: Exception) {
            e.rethrowIfCancellation()
            ResultUiText.Error(UiText.Str(e.message ?: "deleteReminder error"))
        }

    }

    // • CLEAR / CLEANUP

    override suspend fun clearAllTasksLocally(): ResultUiText<Void> {
        return try {
            taskDao.clearAllTasks()

            ResultUiText.Success() // todo return the cleared task, yes for undo
        } catch (e: Exception) {
            e.rethrowIfCancellation()
            ResultUiText.Error(UiText.Str(e.message ?: "clearAllTasksLocally error"))
        }
    }

    override suspend fun clearTasksForDayLocally(zonedDateTime: ZonedDateTime): ResultUiText<Void> {
        return try {
            taskDao.clearAllSyncedTasksForDay(zonedDateTime)

            ResultUiText.Success() // todo return the cleared task, for undo
        } catch (e: Exception) {
            e.rethrowIfCancellation()
            ResultUiText.Error(UiText.Str(e.message ?: "clearTasksForDayLocally error"))
        }
    }
}