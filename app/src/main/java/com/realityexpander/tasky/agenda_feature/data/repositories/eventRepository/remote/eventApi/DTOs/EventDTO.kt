package com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.remote.eventApi.DTOs

import com.realityexpander.tasky.agenda_feature.common.util.AttendeeId
import com.realityexpander.tasky.agenda_feature.common.util.PhotoId
import com.realityexpander.tasky.agenda_feature.util.*
import com.realityexpander.tasky.core.util.UserId
import com.realityexpander.tasky.core.util.UtcMillis
import com.realityexpander.tasky.core.util.UuidStr
import kotlinx.serialization.*

abstract class EventDTO {

    // Core information for all Event DTOs
    abstract val id: UuidStr
    abstract val title: String
    abstract val description: String
    abstract val remindAt: UtcMillis
    abstract val from: UtcMillis
    abstract val to: UtcMillis

    @Serializable
    data class Create(  // output only
        override val id: UuidStr,
        override val title: String,
        override val description: String,
        override val remindAt: UtcMillis,
        override val from: UtcMillis,
        override val to: UtcMillis,

        @Required  // forces write of empty list -> []   (instead of no field written)
        val attendeeIds: List<AttendeeId> = emptyList(),

        @Transient  // This field is used to store Local URI's for photos to upload.
        val photos: List<PhotoDTO.Local> = emptyList(),  // only local URI's are stored here
    ) : EventDTO()

    @Serializable
    data class Update(  // output only
        override val id: UuidStr,
        override val title: String,
        override val description: String,
        override val remindAt: UtcMillis,
        override val from: UtcMillis,
        override val to: UtcMillis,

        @Required
        val isGoing : Boolean,  // ONLY used to set `isGoing` status for `UpdateEventDTO`, not used anywhere else in app.

        @Required  // forces write of empty list -> []   (instead of field not written when empty)
        val attendeeIds: List<AttendeeId> = emptyList(),

        @Required
        @SerialName("deletedPhotoKeys") // output to json
        val deletedPhotoIds: List<PhotoId> = emptyList(),

        @Transient
        val photos: List<PhotoDTO.Local> = emptyList(), // only local URI's are stored here
    ) : EventDTO()

    @Serializable
    data class Response(  // input only
        override val id: UuidStr,
        override val title: String,
        override val description: String,
        override val remindAt: UtcMillis,
        override val from: UtcMillis,
        override val to: UtcMillis,

        val host: UserId,
        val isUserEventCreator: Boolean,

        // Note: Returns complete Attendee objects (not Ids)
        val attendees: List<AttendeeDTO> = emptyList(),

        // Note: Returns complete Photo objects (not Ids)
        val photos: List<PhotoDTO.Remote> = emptyList(),  // NOTE: Only Remote photos are returned
    ) : EventDTO()
}