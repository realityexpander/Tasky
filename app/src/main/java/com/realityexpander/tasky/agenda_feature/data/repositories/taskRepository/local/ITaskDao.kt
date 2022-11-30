package com.realityexpander.tasky.agenda_feature.data.repositories.taskRepository.local

import com.realityexpander.tasky.agenda_feature.common.util.TaskId
import com.realityexpander.tasky.agenda_feature.data.repositories.taskRepository.local.entities.TaskEntity
import kotlinx.coroutines.flow.Flow
import java.time.ZonedDateTime

interface ITaskDao {

    suspend fun createTask(event: TaskEntity)
    fun upsertTask(event: TaskEntity)

    suspend fun getTasksForDay(zonedDateTime: ZonedDateTime): List<TaskEntity>
    fun getTasksForDayFlow(zonedDateTime: ZonedDateTime): Flow<List<TaskEntity>>
    suspend fun getTaskById(taskId: TaskId): TaskEntity?
    suspend fun getTasks(): List<TaskEntity>    // only returns the events that are *NOT* marked as deleted.
    fun getTasksFlow(): Flow<List<TaskEntity>>  // only returns the events that are *NOT* marked as deleted.
    suspend fun getAllTasks(): List<TaskEntity> // returns all events, including the deleted ones.

    suspend fun updateTask(event: TaskEntity): Int

    suspend fun markTaskDeletedById(taskId: TaskId): Int    // only marks the event as deleted
    suspend fun getMarkedDeletedTaskIds(): List<TaskId>      // gets only the "isDeleted==true" events
    suspend fun deleteFinallyByTaskIds(taskIds: List<TaskId>): Int
    suspend fun deleteTask(task: TaskEntity): Int      // completely deletes the event.

    suspend fun clearAllTasks(): Int
    suspend fun clearAllTasksForDay(zonedDateTime: ZonedDateTime): Int
}
