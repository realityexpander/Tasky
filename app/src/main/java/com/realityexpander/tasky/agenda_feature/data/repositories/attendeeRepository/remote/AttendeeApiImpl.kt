@file:OptIn(kotlinx.serialization.InternalSerializationApi::class)
package com.realityexpander.tasky.agenda_feature.data.repositories.attendeeRepository.remote

import android.accounts.NetworkErrorException
import com.realityexpander.tasky.agenda_feature.common.util.EventId
import com.realityexpander.tasky.agenda_feature.data.repositories.attendeeRepository.remote.DTOs.GetAttendeeResponseDTO
import com.realityexpander.tasky.core.data.remote.TaskyApi
import com.realityexpander.tasky.core.data.remote.utils.getErrorBodyMessage
import com.realityexpander.tasky.core.util.Email
import javax.inject.Inject

class AttendeeApiImpl @Inject constructor(
    private val taskyApi: TaskyApi
) : IAttendeeApi {

    override suspend fun getAttendee(email: Email): Result<GetAttendeeResponseDTO> {
        try {
            val response = taskyApi.getAttendee(email)

            if (response.isSuccessful) {
                response.body()?.let { getAttendeeResponseDTO ->
                    if (getAttendeeResponseDTO.doesUserExist) {
                        return Result.success(getAttendeeResponseDTO)
                    } else {
                        return Result.failure(Exception("User email does not exist"))
                    }
                }
            }

            return Result.failure(Exception(getErrorBodyMessage(response.errorBody()?.string())))
        } catch (e: NetworkErrorException) {
            return Result.failure(e)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    // Removes the logged-in user from an Event. It does not delete the actual Attendee.
    override suspend fun deleteAttendee(eventId: EventId): Result<Unit> {
        return try {
            taskyApi.deleteAttendee(eventId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
