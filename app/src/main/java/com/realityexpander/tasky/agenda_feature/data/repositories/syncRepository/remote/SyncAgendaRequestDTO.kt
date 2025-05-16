package com.realityexpander.tasky.agenda_feature.data.repositories.syncRepository.remote

import com.realityexpander.tasky.agenda_feature.domain.EventId
import com.realityexpander.tasky.agenda_feature.domain.ReminderId
import com.realityexpander.tasky.agenda_feature.domain.TaskId
import kotlinx.serialization.Required
import kotlinx.serialization.Serializable

@Serializable
data class SyncAgendaRequestDTO(
    @Required
    val deletedEventIds: List<EventId>? = null,

    @Required
    val deletedTaskIds: List<TaskId>? = null,

    @Required
    val deletedReminderIds: List<ReminderId>? = null,
)
