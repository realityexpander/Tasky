package com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.remote.eventApi.DTOs

import com.realityexpander.tasky.agenda_feature.util.AttendeeId
import com.realityexpander.tasky.agenda_feature.util.EventId
import com.realityexpander.tasky.agenda_feature.util.UrlStr
import com.realityexpander.tasky.core.util.Email
import com.realityexpander.tasky.core.util.UtcMillis
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
@OptIn(ExperimentalSerializationApi::class)  // for @JsonNames
data class AttendeeDTO(
    @JsonNames("userId") // input from json
    val id: AttendeeId,
    val eventId: EventId,

    val email: Email,
    val fullName: String,
    val isGoing: Boolean,
    val remindAt: UtcMillis,

    val photo: UrlStr,
)