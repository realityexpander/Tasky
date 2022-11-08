package com.realityexpander.tasky.agenda_feature.domain

import android.os.Parcelable
import com.realityexpander.tasky.agenda_feature.util.PhotoId
import com.realityexpander.tasky.core.domain.typeParcelers.LocalDateTimeParceler
import com.realityexpander.tasky.core.domain.typeParcelers.ZonedDateTimeParceler
import com.realityexpander.tasky.core.util.UserId
import com.realityexpander.tasky.core.util.UuidStr
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.TypeParceler
import java.time.LocalDateTime
import java.time.ZonedDateTime

abstract class AgendaItem {

    abstract val id: UuidStr

    @Parcelize
    @TypeParceler<ZonedDateTime, ZonedDateTimeParceler>
    data class Event(
        override val id: UuidStr,
        val title: String,
        val description: String,
        val remindAt: ZonedDateTime,
        val from: ZonedDateTime,
        val to: ZonedDateTime,

        val host: UserId,
        val isUserEventCreator: Boolean = false,
        val isGoing: Boolean = false,

        val attendeeIds: List<UserId> = emptyList(),
        val photos: List<PhotoId> = emptyList(),
        val deletedPhotoKeys: List<PhotoId> = emptyList(),
    ) : AgendaItem(), Parcelable

    @Parcelize
    @TypeParceler<LocalDateTime, LocalDateTimeParceler>
    data class Task(
        override val id: UuidStr,
        val title: String,
        val description: String,
        val remindAt: ZonedDateTime,
        val time: ZonedDateTime,
        val isDone: Boolean = false,
    ) : AgendaItem(), Parcelable

    @Parcelize
    @TypeParceler<LocalDateTime, LocalDateTimeParceler>
    data class Reminder(
        override val id: UuidStr,
        val title: String,
        val description: String,
        val remindAt: ZonedDateTime,
        val time: ZonedDateTime,
    ) : AgendaItem(), Parcelable
}