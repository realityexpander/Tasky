package com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.remote.eventApi.DTOs

import com.realityexpander.tasky.agenda_feature.common.util.AttendeeId
import com.realityexpander.tasky.agenda_feature.common.util.EventId
import com.realityexpander.tasky.agenda_feature.common.util.UrlStr
import com.realityexpander.tasky.core.util.Email
import com.realityexpander.tasky.core.util.EpochMilli
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
@OptIn(ExperimentalSerializationApi::class)  // for @JsonNames
data class AttendeeDTO(
    @JsonNames("userId") // input from json
    val id: AttendeeId,
    val eventId: EventId? = null,

    val email: Email,
    val fullName: String,
    val isGoing: Boolean = false,
    val remindAt: EpochMilli? = null,

    val photo: UrlStr? = null,
)

@Serializable
data class GetAttendeeResponseDTO(
    val doesUserExist: Boolean,
    val attendee: AttendeeDTO? = null,
)