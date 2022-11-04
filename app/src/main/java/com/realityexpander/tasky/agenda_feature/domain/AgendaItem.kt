package com.realityexpander.tasky.agenda_feature.domain

import com.realityexpander.tasky.core.util.UuidStr
import java.time.LocalDateTime

// NOTE: Skeleton data structures for the Agenda feature
// **temporary** for UI development

abstract class AgendaItem(
    val id: UuidStr,
    val title: String,
    val description: String,
    val remindAt: LocalDateTime,
) {
    abstract fun isEmpty(): Boolean

    class Event(
        id: UuidStr,
        title: String,
        description: String,
        val from: LocalDateTime,
        val to: LocalDateTime,
        remindAt: LocalDateTime,
        val attendeeIds: List<UuidStr>? = null,
    ): AgendaItem(id, title, description, remindAt) {

        companion object {
            val EMPTY = Event("", "", "", LocalDateTime.MIN, LocalDateTime.MIN, LocalDateTime.MIN, emptyList())
        }

        override fun isEmpty() = this == EMPTY
    }

    class Task(
        id: UuidStr,
        title: String,
        description: String,
        val time: LocalDateTime,
        remindAt: LocalDateTime,
        val isDone: Boolean = false,
    ): AgendaItem(id, title, description, remindAt) {

        companion object {
            val EMPTY = Task("", "", "", LocalDateTime.MIN, LocalDateTime.MIN)
        }

        override fun isEmpty() = this == EMPTY
    }

}
