package com.realityexpander.tasky.agenda_feature.domain

import android.os.Parcelable
import com.realityexpander.tasky.agenda_feature.common.util.PhotoId
import com.realityexpander.tasky.core.util.UserId
import com.realityexpander.tasky.core.util.UuidStr
import kotlinx.parcelize.Parcelize
import java.time.ZonedDateTime

abstract class AgendaItem {

    abstract val id: UuidStr
    abstract val title: String
    abstract val description: String

    @Parcelize
    data class Event(
        override val id: UuidStr,
        override val title: String,
        override val description: String,
        val remindAt: ZonedDateTime,
        val from: ZonedDateTime,
        val to: ZonedDateTime,

        val host: UserId? = null,
        val isUserEventCreator: Boolean? = null,    // todo does this need to be null or false?
        val isGoing: Boolean? = true,

        val attendees: List<Attendee> = emptyList(),

        val photos: List<Photo> = emptyList(),
        val deletedPhotoIds: List<PhotoId> = emptyList(),  // only used for EventDTO.Update

        val isDeleted: Boolean = false,
    ) : AgendaItem(), Parcelable

    @Parcelize
    data class Task(
        override val id: UuidStr,
        override val title: String,
        override val description: String,
        val remindAt: ZonedDateTime,
        val time: ZonedDateTime,
        val isDone: Boolean = false,

        val isDeleted: Boolean = false,
    ) : AgendaItem(), Parcelable

    @Parcelize
    data class Reminder(
        override val id: UuidStr,
        override val title: String,
        override val description: String,
        val remindAt: ZonedDateTime,
        val time: ZonedDateTime,

        val isDeleted: Boolean = false,
    ) : AgendaItem(), Parcelable
}