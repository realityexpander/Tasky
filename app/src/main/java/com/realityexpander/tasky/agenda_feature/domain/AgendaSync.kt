package com.realityexpander.tasky.agenda_feature.domain

import com.realityexpander.tasky.agenda_feature.common.util.EventId
import com.realityexpander.tasky.agenda_feature.common.util.ReminderId
import com.realityexpander.tasky.agenda_feature.common.util.TaskId

data class AgendaSync(
    val deleteEventIds: List<EventId>? = null,
    val deleteTaskIds: List<TaskId>? = null,
    val deleteReminderIds: List<ReminderId>? = null,
)
