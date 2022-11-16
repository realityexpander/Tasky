package com.realityexpander.tasky.agenda_feature.data.repositories.attendeeRepository.attendeeRepositoryImpls

import com.realityexpander.tasky.agenda_feature.common.util.EventId
import com.realityexpander.tasky.agenda_feature.data.common.convertersDTOEntityDomain.toDomain
import com.realityexpander.tasky.agenda_feature.data.repositories.attendeeRepository.IAttendeeRepository
import com.realityexpander.tasky.agenda_feature.data.repositories.attendeeRepository.remote.IAttendeeApi
import com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.remote.eventApi.DTOs.AttendeeExistsResponseDTO
import com.realityexpander.tasky.agenda_feature.domain.Attendee
import com.realityexpander.tasky.core.data.remote.TaskyApi
import com.realityexpander.tasky.core.data.remote.utils.getErrorBodyMessage
import com.realityexpander.tasky.core.util.Email
import javax.inject.Inject

class AttendeeRepositoryImpl @Inject constructor(
    private val attendeeApi: IAttendeeApi,
) : IAttendeeRepository {

    override suspend fun getAttendee(email: Email): Result<Attendee> {
        val result = attendeeApi.getAttendee(email)

        return when (result.isSuccess) {
            true -> Result.success(
                result.getOrThrow().attendee?.toDomain()
                    ?: throw Exception("Api Error - Attendee value for success not found")
            )
            false -> Result.failure(
                Exception(
                    result.exceptionOrNull()?.message
                        ?: "Api Error - Attendee value for failure not found"
                )
            )
        }
    }

    override suspend fun deleteAttendeeFromEvent(eventId: EventId): Boolean {
//            val deletedAttendeeIds = eventRepository.getDeletedAttendeeIds()  // todo implement soon
        return true
    }
}

// Todo move to own file soon
class AttendeeApiImpl @Inject constructor(
    private val taskyApi: TaskyApi
) : IAttendeeApi {

    override suspend fun getAttendee(email: Email): Result<AttendeeExistsResponseDTO> {
        try {
            val response = taskyApi.getAttendee(email)

            if (response.isSuccessful) {
                response.body()?.let { attendeeExistsResponseDTO ->
                    if (attendeeExistsResponseDTO.doesUserExist) {
                        return Result.success(attendeeExistsResponseDTO)
                    } else {
                        return Result.failure(Exception("User email does not exist."))
                    }
                }
            }

            return Result.failure(Exception(getErrorBodyMessage(response.errorBody()?.string())))
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    override suspend fun deleteAttendeeFromEvent(eventId: EventId): Boolean {
        return try {
            taskyApi.deleteAttendee(eventId)
            true
        } catch (e: Exception) {
            false
        }
    }
}