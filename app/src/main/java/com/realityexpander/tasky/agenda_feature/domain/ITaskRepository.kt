package com.realityexpander.tasky.agenda_feature.domain

import com.realityexpander.tasky.agenda_feature.common.util.TaskId
import kotlinx.coroutines.flow.Flow
import java.time.ZonedDateTime

interface ITaskRepository {
    suspend fun createTask(task: AgendaItem.Task): ResultUiText<Void>
    suspend fun upsertTaskLocally(task: AgendaItem.Task): ResultUiText<Void>

    suspend fun getTasksForDay(zonedDateTime: ZonedDateTime): List<AgendaItem.Task>
    fun getTasksForDayFlow(zonedDateTime: ZonedDateTime): Flow<List<AgendaItem.Task>>
    suspend fun getTask(taskId: TaskId): AgendaItem.Task?

    suspend fun updateTask(task: AgendaItem.Task): ResultUiText<Void>

    // only marks the Task as deleted
    suspend fun deleteTask(taskId: TaskId): ResultUiText<Void>

    // gets only the "marked as deleted" Tasks
    suspend fun getDeletedTaskIdsLocally(): List<TaskId>
    suspend fun deleteTasksFinallyLocally(TaskIds: List<TaskId>): ResultUiText<Void>

    suspend fun clearAllTasksLocally(): ResultUiText<Void>
    suspend fun clearTasksForDayLocally(zonedDateTime: ZonedDateTime): ResultUiText<Void>
}
