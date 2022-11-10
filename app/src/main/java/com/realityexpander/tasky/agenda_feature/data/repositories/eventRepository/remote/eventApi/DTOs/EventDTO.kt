package com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.remote.eventApi.DTOs

import androidx.annotation.Keep
import com.realityexpander.tasky.agenda_feature.util.AttendeeId
import com.realityexpander.tasky.agenda_feature.util.PhotoId
import com.realityexpander.tasky.core.util.UserId
import com.realityexpander.tasky.core.util.UtcMillis
import com.realityexpander.tasky.core.util.UuidStr
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Keep
@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class EventDTO(
    val id: UuidStr,
    val title: String,
    val description: String,
    val remindAt: UtcMillis,
    val from: UtcMillis,
    val to: UtcMillis,

    val host: UserId,
    val isUserEventCreator: Boolean = false,
    val isGoing: Boolean = false,

    @SerialName("attendeeIds") // output to json
    @JsonNames("attendees") // input from json
    val attendeeIds: List<AttendeeId> = emptyList(),

    val photos: List<PhotoId> = emptyList(),
    val deletedPhotoKeys: List<PhotoId> = emptyList(),
)