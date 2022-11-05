package com.realityexpander.tasky.agenda_feature.presentation.common.enums

sealed interface AgendaItemType {
    object Task : AgendaItemType
    object Reminder : AgendaItemType
    object Event : AgendaItemType
}