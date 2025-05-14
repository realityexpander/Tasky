@file:OptIn(kotlinx.serialization.InternalSerializationApi::class)
package com.realityexpander.tasky.agenda_feature.data.repositories.attendeeRepository.remote

import com.realityexpander.tasky.agenda_feature.common.util.EventId
import com.realityexpander.tasky.agenda_feature.data.repositories.attendeeRepository.remote.DTOs.GetAttendeeResponseDTO
import com.realityexpander.tasky.core.util.Email

interface IAttendeeApi {
    suspend fun getAttendee(email: Email): Result<GetAttendeeResponseDTO>
    suspend fun deleteAttendee(eventId: EventId): Result<Unit>
}
