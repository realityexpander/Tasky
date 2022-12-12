package com.realityexpander.tasky.agenda_feature.presentation.common.enums

import com.realityexpander.tasky.agenda_feature.domain.AgendaItem

enum class AgendaItemType(val typeNameStr: String) {
    Task("Task"),
    Reminder("Reminder"),
    Event("Event");

    fun isTypeOf(agendaItem: AgendaItem): Boolean {
        return agendaItem::class.simpleName == typeNameStr
    }
}

fun AgendaItem.toAgendaItemType(): AgendaItemType {
    return when (this) {
        is AgendaItem.Task -> AgendaItemType.Task
        is AgendaItem.Reminder -> AgendaItemType.Reminder
        is AgendaItem.Event -> AgendaItemType.Event
        else -> {
            throw Exception("AgendaItem.toAgendaItemType() - unknown AgendaItem type")
        }
    }
}

fun AgendaItem.toAgendaItemTypeStr(): String {
    return when (this) {
        is AgendaItem.Task -> AgendaItemType.Task.typeNameStr
        is AgendaItem.Reminder -> AgendaItemType.Reminder.typeNameStr
        is AgendaItem.Event -> AgendaItemType.Event.typeNameStr
        else -> {
            throw Exception("AgendaItem.toAgendaItemTypeStr() - unknown AgendaItem type")
        }
    }
}

fun String.toAgendaItemType(): AgendaItemType {
    return when (this) {
        AgendaItemType.Task.typeNameStr -> AgendaItemType.Task
        AgendaItemType.Reminder.typeNameStr -> AgendaItemType.Reminder
        AgendaItemType.Event.typeNameStr -> AgendaItemType.Event
        else -> {
            throw Exception("String.toAgendaItemType() - unknown AgendaItemType")
        }
    }
}