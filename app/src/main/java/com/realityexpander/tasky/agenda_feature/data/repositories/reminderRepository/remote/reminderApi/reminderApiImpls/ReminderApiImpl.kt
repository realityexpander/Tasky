package com.realityexpander.remindery.agenda_feature.data.repositories.reminderRepository.remote.reminderApi.reminderApiImpls

import android.accounts.NetworkErrorException
import com.realityexpander.tasky.agenda_feature.common.util.ReminderId
import com.realityexpander.tasky.agenda_feature.data.repositories.reminderRepository.remote.DTOs.ReminderDTO
import com.realityexpander.tasky.agenda_feature.data.repositories.reminderRepository.remote.reminderApi.IReminderApi
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


class ReminderApiImpl @Inject constructor(
    private val taskyApi: TaskyApi,
) : IReminderApi {

    override suspend fun createReminder(reminder: ReminderDTO): Result<Unit> {
        try {
            val response = taskyApi.createReminder(reminder)
            if (response.isSuccessful) {
                return Result.success(Unit)
            } else {
                return Result.failure(Exception("Error updating reminder: ${response.message()}"))
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
                return Result.failure(Exception("Error updating reminder: ${response.message()}"))
            }
        } catch (e: NetworkErrorException) {
            return Result.failure(
                Exception("Error updating reminder (network): ${e.localizedMessage}")
            )
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