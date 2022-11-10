package com.realityexpander.tasky.agenda_feature.data.repositories.agendaRepository.remote.DTOs

import com.realityexpander.tasky.agenda_feature.util.EventId
import com.realityexpander.tasky.agenda_feature.util.ReminderId
import com.realityexpander.tasky.agenda_feature.util.TaskId

data class AgendaSyncDTO(
    val deleteEventIds: List<EventId>? = null,
    val deleteTaskIds: List<TaskId>? = null,
    val deleteReminderIds: List<ReminderId>? = null,
)