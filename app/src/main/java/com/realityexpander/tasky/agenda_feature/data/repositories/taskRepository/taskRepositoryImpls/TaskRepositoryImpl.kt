package com.realityexpander.tasky.agenda_feature.data.repositories.taskRepository.taskRepositoryImpls

import com.realityexpander.tasky.agenda_feature.common.util.TaskId
import com.realityexpander.tasky.agenda_feature.data.common.convertersDTOEntityDomain.toDTO
import com.realityexpander.tasky.agenda_feature.data.common.convertersDTOEntityDomain.toDomain
import com.realityexpander.tasky.agenda_feature.data.common.convertersDTOEntityDomain.toEntity
import com.realityexpander.tasky.agenda_feature.data.repositories.taskRepository.local.ITaskDao
import com.realityexpander.tasky.agenda_feature.data.repositories.taskRepository.remote.ITaskApi
import com.realityexpander.tasky.agenda_feature.domain.AgendaItem
import com.realityexpander.tasky.agenda_feature.domain.ITaskRepository
import com.realityexpander.tasky.agenda_feature.domain.ResultUiText
import com.realityexpander.tasky.core.presentation.common.util.UiText
import com.realityexpander.tasky.core.util.rethrowIfCancellation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.ZonedDateTime

class TaskRepositoryImpl(
    private val taskDao: ITaskDao,
    private val taskApi: ITaskApi,
) : ITaskRepository {

    // • CREATE

    override suspend fun createTask(task: AgendaItem.Task): ResultUiText<Void> {
        return try {
            // todo add to list of updates to send to server

            taskDao.createTask(task.toEntity())  // save to local DB first
            taskApi.createTask(task.toDTO())

            ResultUiText.Success()
        } catch (e: Exception) {
            e.rethrowIfCancellation()
            ResultUiText.Error(UiText.Str(e.message ?: "createTask error"))
        }
    }


    // • UPSERT

    override suspend fun upsertTaskLocally(task: AgendaItem.Task): ResultUiText<Void> {
        return try {
            taskDao.upsertTask(task.toEntity())  // save to local DB ONLY

            ResultUiText.Success()
        } catch (e: Exception) {
            e.rethrowIfCancellation()
            ResultUiText.Error(UiText.Str(e.message ?: "upsertTask error"))
        }
    }


    // • READ

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

    override suspend fun getTask(taskId: TaskId): AgendaItem.Task? {
        return try {
            taskDao.getTaskById(taskId)?.toDomain() // get from local DB first

            val response = taskApi.getTask(taskId)
            taskDao.updateTask(response.toDomain().toEntity())  // update with response from server

            //ResultUiText.Success(response.toDomain())
            response.toDomain()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getAllTasksLocally(): List<AgendaItem.Task> {
        return try {
            taskDao.getTasks().map { it.toDomain() }
        } catch (e: Exception) {
            e.rethrowIfCancellation()
            emptyList()
        }
    }

    override suspend fun updateTask(task: AgendaItem.Task): ResultUiText<Void> {
        return try {
            // todo add to list of updates to send to server

            taskDao.updateTask(task.toEntity())  // optimistic update
            taskApi.updateTask(task.toDTO())

            ResultUiText.Success()
        } catch (e: Exception) {
            e.rethrowIfCancellation()
            ResultUiText.Error(UiText.Str(e.localizedMessage ?: "updateTask error"))
        }
    }

    // • DELETE

    override suspend fun deleteTask(taskId: TaskId): ResultUiText<Void> {
        return try {
            // Optimistic delete
            taskDao.markTaskDeletedById(taskId)

            // Attempt to delete on server
            val response = taskApi.deleteTask(taskId)
            if (response.isSuccess) {
                // Success, delete fully from local DB
                taskDao.deleteFinallyByTaskIds(listOf(taskId))  // just one task
                ResultUiText.Success()
            } else {
                ResultUiText.Error(UiText.Str(response.exceptionOrNull()?.localizedMessage ?: "deleteTask error"))
            }
        } catch (e: Exception) {
            e.rethrowIfCancellation()
            ResultUiText.Error(UiText.Str(e.message ?: "deleteTaskId error"))
        }
    }

    override suspend fun getDeletedTaskIdsLocally(): List<TaskId> {
        return try {
            taskDao.getMarkedDeletedTaskIds()
        } catch (e: Exception) {
            e.rethrowIfCancellation()
            emptyList()
        }
    }

    override suspend fun deleteTasksFinallyLocally(taskIds: List<TaskId>): ResultUiText<Void> {
        return try {
            taskDao.deleteFinallyByTaskIds(taskIds)

            ResultUiText.Success() // todo return the deleted tasks, for undo
        } catch (e: Exception) {
            e.rethrowIfCancellation()
            ResultUiText.Error(UiText.Str(e.message ?: "deleteFinallyTaskIds error"))
        }
    }

    // • CLEAR / CLEANUP

    override suspend fun clearAllTasksLocally(): ResultUiText<Void> {
        return try {
            taskDao.clearAllTasks()

            ResultUiText.Success() // todo return the cleared task, yes for undo
        } catch (e: Exception) {
            e.rethrowIfCancellation()
            ResultUiText.Error(UiText.Str(e.message ?: "clearAllTasks error"))
        }
    }

    override suspend fun clearTasksForDayLocally(zonedDateTime: ZonedDateTime): ResultUiText<Void> {
        return try {
            taskDao.clearAllTasksForDay(zonedDateTime)

            ResultUiText.Success() // todo return the cleared task, for undo
        } catch (e: Exception) {
            e.rethrowIfCancellation()
            ResultUiText.Error(UiText.Str(e.message ?: "clearTasksForDay error"))
        }
    }
}