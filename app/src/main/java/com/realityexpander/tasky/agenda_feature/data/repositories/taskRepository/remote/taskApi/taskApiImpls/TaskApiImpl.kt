package com.realityexpander.tasky.agenda_feature.data.repositories.taskRepository.remote.taskApi.taskApiImpls

import android.accounts.NetworkErrorException
import android.content.Context
import com.realityexpander.tasky.agenda_feature.common.util.TaskId
import com.realityexpander.tasky.agenda_feature.data.repositories.taskRepository.remote.DTOs.TaskDTO
import com.realityexpander.tasky.agenda_feature.data.repositories.taskRepository.remote.ITaskApi
import com.realityexpander.tasky.core.data.remote.TaskyApi
import com.realityexpander.tasky.core.data.remote.utils.getErrorBodyMessage
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
    private val context: Context
) : ITaskApi {

    override suspend fun createTask(task: TaskDTO): Result<Unit> {
        try {
            val response = taskyApi.createTask(task)
            if (response.isSuccessful) {
                //val responseBody = response.body()
                // return responseBody ?: throw Exception("Response body is null") // todo remove
                return Result.success(Unit)
            } else {
                throw Exception(getErrorBodyMessage(response.errorBody()?.string()))
            }
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
//                val responseBody = response.body()
//                return responseBody ?: throw Exception("Response body is null")  // todo remove
                return Result.success(Unit)
            } else {
                return Result.failure(
                    Exception("Error updating task: ${getErrorBodyMessage(response.errorBody()?.string())}")
                )
            }
        } catch (e: NetworkErrorException) {
            throw Exception("Network Error updating task: ${e.localizedMessage}")
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