package com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.remote.eventApi.DTOs

import com.realityexpander.tasky.agenda_feature.common.util.AttendeeId
import com.realityexpander.tasky.agenda_feature.common.util.PhotoId
import com.realityexpander.tasky.agenda_feature.domain.AbstractAgendaItem
import com.realityexpander.tasky.agenda_feature.domain.UsesEpochMilli
import com.realityexpander.tasky.agenda_feature.util.*
import com.realityexpander.tasky.core.util.*
import kotlinx.serialization.*

abstract class EventDTO : AbstractAgendaItem(), UsesEpochMilli {

    // Core information for all Event DTO Types
    abstract val from: EpochMilli
    abstract val to: EpochMilli

    @Serializable
    data class Create(  // POST only
        override val id: UuidStr,
        override val title: String,
        override val description: String,

        override val remindAt: EpochMilli,
        override val from: EpochMilli,
        override val to: EpochMilli,

        @Required  // forces write of empty list -> []   (instead of no field written)
        val attendeeIds: List<AttendeeId> = emptyList(),

        @Transient  // This field is used to store Local URI's for photos to upload.
        val photos: List<PhotoDTO.Local> = emptyList(),  // only local URI's are stored here
    ) : EventDTO()


    @Serializable
    data class Update(  // PUT only
        override val id: UuidStr,
        override val title: String,
        override val description: String,

        override val remindAt: EpochMilli,
        override val from: EpochMilli,
        override val to: EpochMilli,

        @Required
        val isGoing : Boolean,  // ONLY used to set `isGoing` status for `UpdateEventDTO`, not used anywhere else in app.

        @Required  // forces write of empty list -> []   (instead of field not written when empty)
        val attendeeIds: List<AttendeeId> = emptyList(),

        @Required
        @SerialName("deletedPhotoKeys") // json output field name
        val deletedPhotoIds: List<PhotoId> = emptyList(),

        @Transient
        val photos: List<PhotoDTO.Local> = emptyList(), // Local URI's are stored here for uploading
    ) : EventDTO()

    @Serializable
    data class Response constructor(  // RESPONSE only
        override val id: UuidStr,
        override val title: String,
        override val description: String,

        override val remindAt: EpochMilli,
        override val from: EpochMilli,
        override val to: EpochMilli,

        val host: UserId,
        val isUserEventCreator: Boolean,

        // Note: Returns complete Attendee objects (not Ids)
        val attendees: List<AttendeeDTO> = emptyList(),

        // Note: Returns complete Photo objects (not Ids)
        val photos: List<PhotoDTO.Remote> = emptyList(),  // NOTE: Only Remote photos are returned
    ) : EventDTO()
}