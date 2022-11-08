package com.realityexpander.tasky.agenda_feature.domain

import android.os.Parcelable
import com.realityexpander.tasky.core.domain.typeParcelers.LocalDateTimeParceler
import com.realityexpander.tasky.core.util.UuidStr
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.TypeParceler
import java.time.LocalDateTime

abstract class AgendaItem {

    abstract val id: UuidStr

    @Parcelize
    @TypeParceler<LocalDateTime, LocalDateTimeParceler>
    data class Event(
        override val id: UuidStr,
        val title: String,
        val description: String? = null,
//        @Serializable(with = LocalDateTimeSerializer::class)
        val remindAt: LocalDateTime,
//        @Serializable(with = LocalDateTimeSerializer::class)
        val from: LocalDateTime,
//        @Serializable(with = LocalDateTimeSerializer::class)
        val to: LocalDateTime,
        val attendeeIds: List<UuidStr>? = null,
        val photos: List<UuidStr>? = null,
    ) : AgendaItem(), Parcelable

    @Parcelize
    @TypeParceler<LocalDateTime, LocalDateTimeParceler>
    data class Task(
        override val id: UuidStr,
        val title: String,
        val description: String? = null,
//        @Serializable(with = LocalDateTimeSerializer::class)
        val remindAt: LocalDateTime,
//        @Serializable(with = LocalDateTimeSerializer::class)
        val time: LocalDateTime,
        val isDone: Boolean = false,
    ) : AgendaItem(), Parcelable

    @Parcelize
    @TypeParceler<LocalDateTime, LocalDateTimeParceler>
    data class Reminder(
        override val id: UuidStr,
        val title: String,
        val description: String? = null,
//        @Serializable(with = LocalDateTimeSerializer::class)
        val remindAt: LocalDateTime,
        val time: LocalDateTime,
    ) : AgendaItem(), Parcelable
}