package com.realityexpander.tasky.agenda_feature.domain

import com.realityexpander.tasky.core.presentation.util.ResultUiText
import kotlinx.coroutines.flow.Flow
import java.time.ZonedDateTime

interface ITaskRepository {
    suspend fun createTask(task: AgendaItem.Task, isRemoteOnly: Boolean = false): ResultUiText<Void>
    suspend fun upsertTaskLocally(task: AgendaItem.Task): ResultUiText<Void>

    suspend fun getTasksForDay(zonedDateTime: ZonedDateTime): List<AgendaItem.Task>
    fun getTasksForDayFlow(zonedDateTime: ZonedDateTime): Flow<List<AgendaItem.Task>>
    suspend fun getTask(taskId: TaskId, isLocalOnly: Boolean = false): AgendaItem.Task?
    fun getTasksForRemindAtDateTimeRangeFlow(from: ZonedDateTime, to: ZonedDateTime): Flow<List<AgendaItem.Task>>

    suspend fun updateTask(task: AgendaItem.Task, isRemoteOnly: Boolean = false): ResultUiText<Void>

    suspend fun deleteTask(task: AgendaItem.Task): ResultUiText<Void>

    suspend fun clearAllTasksLocally(): ResultUiText<Void>
    suspend fun clearTasksForDayLocally(zonedDateTime: ZonedDateTime): ResultUiText<Void>
}
