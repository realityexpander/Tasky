package com.realityexpander.tasky.agenda_feature.domain

import java.time.LocalDateTime

// NOTE: Skeleton data structures for the Agenda feature
// **temporary** for UI development

abstract class AgendaItem(
    val id: String,
    val title: String,
    val description: String,
    val remindAt: LocalDateTime,
) {
    abstract fun isEmpty(): Boolean

    class Event(
        id: String,
        title: String,
        description: String,
        val from: LocalDateTime,
        val to: LocalDateTime,
        remindAt: LocalDateTime,
        val attendeeIds: List<String>? = null, // todo: use UUID instead of string
    ): AgendaItem(id, title, description, remindAt) {
        companion object {
            val EMPTY = Event("", "", "", LocalDateTime.MIN, LocalDateTime.MIN, LocalDateTime.MIN, emptyList())
        }

        override fun isEmpty() = this == EMPTY
    }

}
