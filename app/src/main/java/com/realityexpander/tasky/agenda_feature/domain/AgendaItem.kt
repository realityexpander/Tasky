package com.realityexpander.tasky.agenda_feature.domain

import android.os.Parcelable
import com.realityexpander.tasky.agenda_feature.util.AttendeeId
import com.realityexpander.tasky.agenda_feature.util.PhotoId
import com.realityexpander.tasky.core.util.UserId
import com.realityexpander.tasky.core.util.UuidStr
import kotlinx.parcelize.Parcelize
import java.time.ZonedDateTime

abstract class AgendaItem {

    abstract val id: UuidStr

    @Parcelize
    data class Event(
        override val id: UuidStr,
        val title: String,
        val description: String,
        val remindAt: ZonedDateTime,
        val from: ZonedDateTime,
        val to: ZonedDateTime,

        val host: UserId? = null,
        val isUserEventCreator: Boolean? = null,
        val isGoing: Boolean? = null,

        val attendees: List<Attendee> = emptyList(),
        val attendeeIds: List<AttendeeId> = emptyList(),

        val photos: List<Photo> = emptyList(),
        val photoIds: List<PhotoId> = emptyList(),
        val deletedPhotoKeys: List<PhotoId> = emptyList(),

        val isDeleted: Boolean = false,
    ) : AgendaItem(), Parcelable

    @Parcelize
    data class Task(
        override val id: UuidStr,
        val title: String,
        val description: String,
        val remindAt: ZonedDateTime,
        val time: ZonedDateTime,
        val isDone: Boolean = false,

        val isDeleted: Boolean = false,
    ) : AgendaItem(), Parcelable

    @Parcelize
    data class Reminder(
        override val id: UuidStr,
        val title: String,
        val description: String,
        val remindAt: ZonedDateTime,
        val time: ZonedDateTime,

        val isDeleted: Boolean = false,
    ) : AgendaItem(), Parcelable
}