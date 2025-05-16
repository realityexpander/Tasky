package com.realityexpander.tasky.agenda_feature.data.repositories.taskRepository.remote.DTOs

import com.realityexpander.tasky.agenda_feature.domain.TaskId
import com.realityexpander.tasky.agenda_feature.domain.AbstractAgendaItem
import com.realityexpander.tasky.agenda_feature.domain.UsesEpochMilli
import com.realityexpander.tasky.core.util.EpochMilli
import kotlinx.serialization.Required
import kotlinx.serialization.Serializable

@Serializable
data class TaskDTO(
    override val id: TaskId,
    override val title: String,
    override val description: String,

    override val remindAt: EpochMilli,
    val time: EpochMilli,

    @Required
    val isDone: Boolean = false,
) : AbstractAgendaItem(), UsesEpochMilli
