package com.realityexpander.tasky.agenda_feature.data.repositories.attendeeRepository.attendeeRepositoryImpls

import com.realityexpander.tasky.agenda_feature.common.util.EventId
import com.realityexpander.tasky.agenda_feature.data.common.convertersDTOEntityDomain.toDomain
import com.realityexpander.tasky.agenda_feature.data.repositories.attendeeRepository.IAttendeeRepository
import com.realityexpander.tasky.agenda_feature.data.repositories.attendeeRepository.remote.IAttendeeApi
import com.realityexpander.tasky.agenda_feature.domain.Attendee
import com.realityexpander.tasky.auth_feature.domain.validation.ValidateEmail
import com.realityexpander.tasky.core.util.Email
import javax.inject.Inject

class AttendeeRepositoryImpl @Inject constructor(
    private val attendeeApi: IAttendeeApi,
    private val validateEmail: ValidateEmail
) : IAttendeeRepository {

    override suspend fun getAttendee(email: Email): Result<Attendee> {
        if(!validateEmail.validate(email)) {
            return Result.failure(Exception("Invalid email address."))
        }

        val result = attendeeApi.getAttendee(email)

        return when (result.isSuccess) {
            true -> Result.success(
                result.getOrThrow().attendee?.toDomain()
                    ?: throw Exception("Api Error - Attendee value for success not found")
            )
            false -> Result.failure(
                Exception(
                    result.exceptionOrNull()?.message
                        ?: "Api Error - Error message not found"
                )
            )
        }
    }

    override suspend fun deleteAttendeeFromEvent(eventId: EventId): Boolean {
//            val deletedAttendeeIds = eventRepository.getDeletedAttendeeIds()  // todo implement soon
        return true
    }
}