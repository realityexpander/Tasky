package com.realityexpander.tasky.agenda_feature.domain

import android.content.Context

interface IRemindAtAlarmManager {

    fun setAlarmsForAgendaItems(
        context: Context,
        agendaItems: List<AgendaItem>
    )

    fun cancelAllAlarms(
        context: Context,
        onFinished: () -> Unit = {},
    )
}