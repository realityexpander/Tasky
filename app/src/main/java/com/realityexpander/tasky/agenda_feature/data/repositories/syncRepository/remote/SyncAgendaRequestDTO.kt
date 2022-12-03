package com.realityexpander.tasky.agenda_feature.data.repositories.syncRepository.remote

import com.realityexpander.tasky.agenda_feature.common.util.EventId
import com.realityexpander.tasky.agenda_feature.common.util.ReminderId
import com.realityexpander.tasky.agenda_feature.common.util.TaskId
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
