package com.realityexpander.tasky.agenda_feature.data.repositories.taskRepository.remote

import com.realityexpander.tasky.agenda_feature.common.util.TaskId
import com.realityexpander.tasky.agenda_feature.data.repositories.taskRepository.remote.DTOs.TaskDTO


interface ITaskApi {

    suspend fun createTask(task: TaskDTO): Result<Unit>
    suspend fun getTask(taskId: TaskId): TaskDTO
    suspend fun updateTask(task: TaskDTO): Result<Unit>
    suspend fun deleteTask(taskId: TaskId): Result<Unit>
}