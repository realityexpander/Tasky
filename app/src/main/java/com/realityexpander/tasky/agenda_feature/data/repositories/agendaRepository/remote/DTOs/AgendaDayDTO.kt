package com.realityexpander.tasky.agenda_feature.data.repositories.agendaRepository.remote.DTOs

import com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.remote.eventApi.DTOs.EventDTO
import com.realityexpander.tasky.agenda_feature.data.repositories.reminderRepository.remote.DTOs.ReminderDTO
import com.realityexpander.tasky.agenda_feature.data.repositories.taskRepository.remote.DTOs.TaskDTO
import kotlinx.serialization.Serializable

@Serializable
data class AgendaDayDTO(
    val events: List<EventDTO.Response>,
    val tasks: List<TaskDTO>,
    val reminders: List<ReminderDTO>,
)