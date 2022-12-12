package com.realityexpander.tasky.agenda_feature.domain

interface IRemindAtAlarmManager {

    fun setAlarmsForAgendaItems(
        agendaItems: List<AgendaItem>
    )

    fun cancelAllAlarms(
        onFinished: () -> Unit = {},
    )
}