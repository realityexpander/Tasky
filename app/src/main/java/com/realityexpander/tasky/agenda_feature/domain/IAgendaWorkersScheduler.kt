package com.realityexpander.tasky.agenda_feature.domain

interface IAgendaWorkersScheduler {
    suspend fun startAllWorkers()
    suspend fun cancelAllWorkers()
}