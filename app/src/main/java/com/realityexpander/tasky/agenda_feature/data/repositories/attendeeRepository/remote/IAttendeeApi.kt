package com.realityexpander.tasky.agenda_feature.data.repositories.attendeeRepository.remote

import com.realityexpander.tasky.agenda_feature.common.util.EventId
import com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.remote.eventApi.DTOs.AttendeeExistsResponseDTO
import com.realityexpander.tasky.core.util.Email

interface IAttendeeApi {
    suspend fun getAttendee(email: Email): Result<AttendeeExistsResponseDTO>
    suspend fun deleteAttendeeFromEvent(eventId: EventId): Boolean
}