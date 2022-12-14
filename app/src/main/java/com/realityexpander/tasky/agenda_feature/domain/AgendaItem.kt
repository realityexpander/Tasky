package com.realityexpander.tasky.agenda_feature.domain

import android.os.Parcelable
import com.realityexpander.tasky.agenda_feature.common.util.PhotoId
import com.realityexpander.tasky.core.util.EpochMilli
import com.realityexpander.tasky.core.util.UserId
import com.realityexpander.tasky.core.util.UuidStr
import kotlinx.parcelize.Parcelize
import java.time.ZonedDateTime

//    abstract val id: UuidStr
//    abstract val title: String
//    abstract val description: String
//    abstract val remindAt: ZonedDateTime

abstract class AbstractAgendaItem {
    abstract val id: UuidStr
    abstract val title: String
    abstract val description: String
}

abstract class AgendaItem :
    AbstractAgendaItem(),
    HasTimeAsZonedDateTime, 
    HasStartTime
{

//    abstract val startTime: ZonedDateTime  // for sorting in Agenda

    @Parcelize
    data class Event(
        override val id: UuidStr,
        override val title: String,
        override val description: String,

        val from: ZonedDateTime,
        val to: ZonedDateTime,
        override val startTime: ZonedDateTime = from, // for sorting in Agenda
        override val remindAt: ZonedDateTime,

        val host: UserId? = null,
        val isUserEventCreator: Boolean = false,
        val attendees: List<Attendee> = emptyList(),

        val photos: List<Photo> = emptyList(),
        val deletedPhotoIds: List<PhotoId> = emptyList(),  // only used for EventDTO.Update

        val isSynced: Boolean = false,
    ) : AgendaItem(), Parcelable

    @Parcelize
    data class Task(
        override val id: UuidStr,
        override val title: String,
        override val description: String,

        val time: ZonedDateTime,
        override val startTime: ZonedDateTime = time, // for sorting in Agenda
        override val remindAt: ZonedDateTime,

        val isDone: Boolean = false,

        val isSynced: Boolean = false,
    ) : AgendaItem(), Parcelable
    @Parcelize
    data class Reminder(
        override val id: UuidStr,
        override val title: String,
        override val description: String,

        val time: ZonedDateTime,
        override val startTime: ZonedDateTime = time, // for sorting in Agenda
        override val remindAt: ZonedDateTime,

        val isSynced: Boolean = false,
    ) : AgendaItem(), Parcelable
}

interface HasTimeAsZonedDateTime {
    val remindAt: ZonedDateTime
}

interface HasTimeAsEpochMilli {
    val remindAt: EpochMilli
}

interface HasStartTime {
    val startTime: ZonedDateTime
}