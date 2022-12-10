package com.realityexpander.tasky.agenda_feature.data.repositories.taskRepository.local

import com.realityexpander.tasky.agenda_feature.common.util.TaskId
import com.realityexpander.tasky.agenda_feature.data.repositories.taskRepository.local.entities.TaskEntity
import kotlinx.coroutines.flow.Flow
import java.time.ZonedDateTime

interface ITaskDao {

    suspend fun createTask(task: TaskEntity)
    fun upsertTask(task: TaskEntity)

    suspend fun getTasksForDay(zonedDateTime: ZonedDateTime): List<TaskEntity>
    fun getTasksForDayFlow(zonedDateTime: ZonedDateTime): Flow<List<TaskEntity>>
    suspend fun getTaskById(taskId: TaskId): TaskEntity?
    suspend fun getTasks(): List<TaskEntity>
    fun getTasksFlow(): Flow<List<TaskEntity>>
    fun getTasksForRemindAtDateTimeRangeFlow(startDateTime: ZonedDateTime, endDateTime: ZonedDateTime): Flow<List<TaskEntity>>

    suspend fun updateTask(task: TaskEntity): Int

    suspend fun deleteTaskById(taskId: TaskId): Int
    suspend fun deleteTasksByIds(taskIds: List<TaskId>): Int
    suspend fun deleteTask(task: TaskEntity): Int

    suspend fun clearAllTasks(): Int
    suspend fun clearAllSyncedTasksForDay(zonedDateTime: ZonedDateTime): Int
}
