package com.realityexpander.tasky.agenda_feature.domain

import com.realityexpander.tasky.core.util.UuidStr
import java.time.LocalDateTime

// NOTE: Skeleton data structures for the Agenda feature
// **temporary** for UI development

abstract class AgendaItem(
    val id: UuidStr,
    val title: String,
    val description: String? = null,
    val remindAt: LocalDateTime,
) {
    abstract fun isEmpty(): Boolean

    class Event(
        id: UuidStr,
        title: String,
        description: String? = null,
        remindAt: LocalDateTime,
        val from: LocalDateTime,
        val to: LocalDateTime,
        val attendeeIds: List<UuidStr>? = null,
        val photos: List<UuidStr>? = null,
    ): AgendaItem(id, title, description, remindAt) {

        companion object {
            val EMPTY = Event("", "", "", LocalDateTime.MIN, LocalDateTime.MIN, LocalDateTime.MIN, emptyList())
        }

        override fun isEmpty() = this == EMPTY

        fun copy(
            id: UuidStr = this.id,
            title: String = this.title,
            description: String? = this.description,
            remindAt: LocalDateTime = this.remindAt,
            from: LocalDateTime = this.from,
            to: LocalDateTime = this.to,
            attendeeIds: List<UuidStr>? = this.attendeeIds,
            photos: List<UuidStr>? = this.photos,
        ) = Event(
            id = id,
            title = title,
            description = description,
            remindAt = remindAt,
            from = from,
            to = to,
            attendeeIds = attendeeIds,
            photos = photos,
        )
    }

    class Task(
        id: UuidStr,
        title: String,
        description: String? = null,
        remindAt: LocalDateTime,
        val time: LocalDateTime,
        val isDone: Boolean = false,
    ): AgendaItem(id, title, description, remindAt) {

        companion object {
            val EMPTY = Task("", "", "", LocalDateTime.MIN, LocalDateTime.MIN)
        }

        override fun isEmpty() = this == EMPTY

        fun copy(
            id: UuidStr = this.id,
            title: String = this.title,
            description: String? = this.description,
            remindAt: LocalDateTime = this.remindAt,
            time: LocalDateTime = this.time,
            isDone: Boolean = this.isDone,
        ) = Task(
            id = id,
            title = title,
            description = description,
            remindAt = remindAt,
            time = time,
            isDone = isDone,
        )
    }

    class Reminder(
        id: UuidStr,
        title: String,
        description: String? = null,
        remindAt: LocalDateTime,
        val time: LocalDateTime,
    ): AgendaItem(id, title, description, remindAt) {

        companion object {
            val EMPTY = Reminder("", "", "", LocalDateTime.MIN, LocalDateTime.MIN)
        }

        override fun isEmpty() = this == EMPTY

        fun copy(
            id: UuidStr = this.id,
            title: String = this.title,
            description: String? = this.description,
            remindAt: LocalDateTime = this.remindAt,
            time: LocalDateTime = this.time,
        ) = Reminder(
            id = id,
            title = title,
            description = description,
            remindAt = remindAt,
            time = time,
        )
    }

}
