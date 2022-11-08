package com.realityexpander.tasky.agenda_feature.data.repositories.agendaRepository.remote.DTOs

import com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.remote.eventApi.DTOs.EventDTO

data class AgendaDayDTO(
    val events: List<EventDTO>,
//    val tasks: List<TaskDTO>,             // todo implement tasks
//    val reminders: List<ReminderDTO>,     // todo implement reminders
) {
}