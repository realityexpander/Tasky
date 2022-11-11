package com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.remote.eventApi.DTOs

import androidx.annotation.Keep
import com.realityexpander.tasky.agenda_feature.util.*
import com.realityexpander.tasky.core.util.UserId
import com.realityexpander.tasky.core.util.UtcMillis
import com.realityexpander.tasky.core.util.UuidStr
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

//@Keep
//@Serializable
//@OptIn(ExperimentalSerializationApi::class)
//data class EventDTO1(
//    val id: UuidStr,
//    val title: String,
//    val description: String,
//    val remindAt: UtcMillis,
//    val from: UtcMillis,
//    val to: UtcMillis,
//
//    val host: UserId? = null,
//    val isUserEventCreator: Boolean? = null,
//    val isGoing: Boolean? = null,
//
//    @SerialName("attendeeIds") // output to json
//    @JsonNames("attendees") // input from json
//    val attendeeIds: List<AttendeeId> = listOf(attendeeId(emptyId)),
//
//    val photos: List<PhotoId> = emptyList(),
//    val deletedPhotoKeys: List<PhotoId> = emptyList(),
//)


abstract class EventDTO {

    // Core information for all Event DTOs
    abstract val id: UuidStr
    abstract val title: String
    abstract val description: String
    abstract val remindAt: UtcMillis
    abstract val from: UtcMillis
    abstract val to: UtcMillis

    @Keep
    @Serializable
    @OptIn(ExperimentalSerializationApi::class)  // for @JsonNames
    data class Create(
        override val id: UuidStr,
        override val title: String,
        override val description: String,
        override val remindAt: UtcMillis,
        override val from: UtcMillis,
        override val to: UtcMillis,

        val attendeeIds: List<AttendeeId> = listOf(attendeeId(emptyId)),  // must include field in request, even if empty.

        @Transient
        val photos: List<PhotoDTO> = emptyList(),
    ) : EventDTO()

    @Keep
    @Serializable
    @OptIn(ExperimentalSerializationApi::class)  // for @JsonNames
    data class Update(
        override val id: UuidStr,
        override val title: String,
        override val description: String,
        override val remindAt: UtcMillis,
        override val from: UtcMillis,
        override val to: UtcMillis,

        val isGoing : Boolean = true,
        val attendeeIds: List<AttendeeId> = listOf(attendeeId(emptyId)), // must include field in request, even if empty

        @SerialName("deletedPhotoKeys") // output to json
        val deletedPhotoIds: List<PhotoId> = listOf(photoId(emptyId)),  // must include field in request, even if empty.

        @Transient
        val photos: List<PhotoDTO> = emptyList(),
    ) : EventDTO()

    @Keep
    @Serializable
    @OptIn(ExperimentalSerializationApi::class)  // for @JsonNames
    data class Response(
        override val id: UuidStr,
        override val title: String,
        override val description: String,
        override val remindAt: UtcMillis,
        override val from: UtcMillis,
        override val to: UtcMillis,

        val host: UserId,
        val isUserEventCreator: Boolean,
        val isGoing: Boolean? = null,

        // Note: Returns complete Attendee objects (not Ids)
        val attendees: List<AttendeeDTO> = emptyList(),

        // Note: Returns complete Photo objects (not Ids)
        val photos: List<PhotoDTO> = emptyList(),
    ) : EventDTO()
}