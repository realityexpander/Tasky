package com.realityexpander.tasky.agenda_feature.domain

import android.os.Parcelable
import com.realityexpander.tasky.agenda_feature.common.util.PhotoId
import com.realityexpander.tasky.core.util.UserId
import com.realityexpander.tasky.core.util.UuidStr
import kotlinx.parcelize.Parcelize
import java.time.ZonedDateTime

abstract class AgendaItem : java.io.Serializable {

    abstract val id: UuidStr
    abstract val title: String
    abstract val description: String
    abstract val startTime: ZonedDateTime  // for sorting in Agenda

    @Parcelize
    data class Event(
        override val id: UuidStr,
        override val title: String,
        override val description: String,
        val isSynced: Boolean = false,

        val from: ZonedDateTime,
        override var startTime: ZonedDateTime = from, // for sorting in Agenda
        val to: ZonedDateTime,
        val remindAt: ZonedDateTime,

        val host: UserId? = null,
        val isUserEventCreator: Boolean = false,
        val attendees: List<Attendee> = emptyList(),

        val photos: List<Photo> = emptyList(),
        val deletedPhotoIds: List<PhotoId> = emptyList(),  // only used for EventDTO.Update
    ) : AgendaItem(), Parcelable, java.io.Serializable

    @Parcelize
    data class Task(
        override val id: UuidStr,
        override val title: String,
        override val description: String,
        val isSynced: Boolean = false,

        val time: ZonedDateTime,
        override var startTime: ZonedDateTime = time, // for sorting in Agenda
        val remindAt: ZonedDateTime,

        val isDone: Boolean = false,
    ) : AgendaItem(), Parcelable, java.io.Serializable

    @Parcelize
    data class Reminder(
        override val id: UuidStr,
        override val title: String,
        override val description: String,
        val isSynced: Boolean = false,

        val time: ZonedDateTime,
        override var startTime: ZonedDateTime = time, // for sorting in Agenda
        val remindAt: ZonedDateTime,
    ) : AgendaItem(), Parcelable, java.io.Serializable
}