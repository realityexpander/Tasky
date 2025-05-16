package com.realityexpander.tasky.agenda_feature.data.repositories.taskRepository.remote.taskApi.taskApiImpls

import android.accounts.NetworkErrorException
import com.realityexpander.tasky.agenda_feature.domain.TaskId
import com.realityexpander.tasky.agenda_feature.data.repositories.taskRepository.remote.DTOs.TaskDTO
import com.realityexpander.tasky.agenda_feature.data.repositories.taskRepository.remote.ITaskApi
import com.realityexpander.tasky.core.data.remote.TaskyApi
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import javax.inject.Inject


@OptIn(ExperimentalSerializationApi::class)
val jsonPrettyPrint = Json {
    prettyPrint = true
    ignoreUnknownKeys = true
    prettyPrintIndent = "   "
    encodeDefaults = true
}


class TaskApiImpl @Inject constructor(
    private val taskyApi: TaskyApi,
) : ITaskApi {

    override suspend fun createTask(task: TaskDTO): Result<Unit> {
        try {
            val response = taskyApi.createTask(task)
            if (response.isSuccessful) {
                return Result.success(Unit)
            } else {
                return Result.failure(
                    Exception("Error updating task: ${response.message()}")
                )
            }
        } catch (e: NetworkErrorException) {
            return Result.failure(Exception("Error updating task: ${e.localizedMessage}"))
        } catch (e: Exception) {
            throw Exception("Error creating task: ${e.message}")
        }
    }

    override suspend fun getTask(taskId: TaskId): TaskDTO {
        try {
            val response = taskyApi.getTask(taskId)
            if (response.isSuccessful) {
                val responseBody = response.body()
                return responseBody ?: throw Exception("Response body is null")
            } else {
                throw Exception("Error getting task: ${response.errorBody()}")
            }
        } catch (e: Exception) {
            throw Exception("Error getting task: ${e.message}")
        }
    }

    override suspend fun updateTask(task: TaskDTO): Result<Unit> {
        try {
            val response = taskyApi.updateTask(task)
            if (response.isSuccessful) {
                return Result.success(Unit)
            } else {
                return Result.failure(Exception("Error updating task: ${response.message()}"))
            }
        } catch (e: NetworkErrorException) {
            return Result.failure(
                Exception("Error updating task (network): ${e.localizedMessage}")
            )
        } catch (e: Exception) {
            throw Exception("${e.message}")
        }
    }

    override suspend fun deleteTask(taskId: TaskId): Result<Unit> {
        return try {
            val response = taskyApi.deleteTask(taskId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error deleting task: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
