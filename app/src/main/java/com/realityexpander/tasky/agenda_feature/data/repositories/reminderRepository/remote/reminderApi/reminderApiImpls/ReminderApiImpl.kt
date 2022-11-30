package com.realityexpander.remindery.agenda_feature.data.repositories.reminderRepository.remote.reminderApi.reminderApiImpls

import android.accounts.NetworkErrorException
import android.content.Context
import com.realityexpander.tasky.agenda_feature.common.util.ReminderId
import com.realityexpander.tasky.agenda_feature.data.repositories.reminderRepository.remote.DTOs.ReminderDTO
import com.realityexpander.tasky.agenda_feature.data.repositories.reminderRepository.remote.reminderApi.IReminderApi
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


class ReminderApiImpl @Inject constructor(
    private val taskyApi: TaskyApi,
    private val context: Context
) : IReminderApi {

    override suspend fun createReminder(reminder: ReminderDTO): Result<Unit> {
        try {
            val response = taskyApi.createReminder(reminder)
            if (response.isSuccessful) {
                //val responseBody = response.body()
                // return responseBody ?: throw Exception("Response body is null") // todo remove
                return Result.success(Unit)
            } else {
                throw Exception(getErrorBodyMessage(response.errorBody()?.string()))
            }
        } catch (e: Exception) {
            throw Exception("Error creating reminder: ${e.message}")
        }
    }

    override suspend fun getReminder(reminderId: ReminderId): ReminderDTO {
        try {
            val result = taskyApi.getReminder(reminderId)
            if (result.isSuccessful) {
                val reminderDTO = result.body()
                return reminderDTO ?: throw Exception("Response body is null")
            } else {
                throw Exception("Error getting reminder: ${result.errorBody()}")
            }
        } catch (e: Exception) {
            throw Exception("Error getting reminder: ${e.message}")
        }
    }

    override suspend fun updateReminder(reminder: ReminderDTO): Result<Unit> {
        try {
            val response = taskyApi.updateReminder(reminder)
            if (response.isSuccessful) {
                return Result.success(Unit)
            } else {
                return Result.failure(
                    Exception("Error updating reminder: ${getErrorBodyMessage(response.errorBody()?.string())}")
                )
            }
        } catch (e: NetworkErrorException) {
            throw Exception("Network Error updating reminder: ${e.localizedMessage}")
        } catch (e: Exception) {
            throw Exception("${e.message}")
        }
    }

    override suspend fun deleteReminder(reminderId: ReminderId): Result<Unit> {
        return try {
            val response = taskyApi.deleteReminder(reminderId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error deleting reminder: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}