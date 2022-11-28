package com.realityexpander.tasky.agenda_feature.data.repositories.agendaRepository.remote.DTOs

import com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.remote.eventApi.DTOs.EventDTO
import kotlinx.serialization.Serializable

@Serializable
data class AgendaDayDTO(
    val events: List<EventDTO.Response>,
//    val tasks: List<TaskDTO>,             // todo implement tasks
//    val reminders: List<ReminderDTO>,     // todo implement reminders
)