package com.realityexpander.tasky.agenda_feature.data.repositories.attendeeRepository.attendeeRepositoryImpls

import com.realityexpander.tasky.R
import com.realityexpander.tasky.agenda_feature.common.util.EventId
import com.realityexpander.tasky.agenda_feature.data.common.convertersDTOEntityDomain.toDomain
import com.realityexpander.tasky.agenda_feature.data.repositories.attendeeRepository.IAttendeeRepository
import com.realityexpander.tasky.agenda_feature.data.repositories.attendeeRepository.remote.IAttendeeApi
import com.realityexpander.tasky.agenda_feature.domain.Attendee
import com.realityexpander.tasky.agenda_feature.domain.ResultUiText
import com.realityexpander.tasky.auth_feature.domain.validation.ValidateEmail
import com.realityexpander.tasky.core.presentation.common.util.UiText
import com.realityexpander.tasky.core.util.Email
import javax.inject.Inject

class AttendeeRepositoryImpl @Inject constructor(
    private val attendeeApi: IAttendeeApi,
    private val validateEmail: ValidateEmail
) : IAttendeeRepository {

    override suspend fun getAttendee(email: Email): ResultUiText<Attendee> {
        if (!validateEmail.validate(email)) {
            return ResultUiText.Error(
                UiText.ResOrStr(
                    R.string.error_invalid_email,
                    "Invalid email address"
                )
            )
        }

        val result = attendeeApi.getAttendee(email)

        return if (result.isSuccess) {
            val attendeeResponseDTO = result.getOrNull()

            attendeeResponseDTO?.attendee?.let { attendee ->
                ResultUiText.Success(attendee.toDomain())
            }
            ?: ResultUiText.Error(
                UiText.ResOrStr(
                    R.string.attendee_add_attendee_api_error,
                    "Api Error"
                ),
                exceptionMessage = result.exceptionOrNull()?.message
            )
        } else {
            ResultUiText.Error(
                UiText.Str(
                    result.exceptionOrNull()?.message ?: "Api Unknown Error"
                ),
                exceptionMessage = result.exceptionOrNull()?.message
            )
        }
    }

    override suspend fun deleteAttendeeFromEvent(eventId: EventId): Boolean {
//            val deletedAttendeeIds = eventRepository.getDeletedAttendeeIds()  // todo implement soon
        return true
    }
}