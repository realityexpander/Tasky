package com.realityexpander.tasky.agenda_feature.data.repositories.taskRepository.remote.DTOs

import com.realityexpander.tasky.agenda_feature.common.util.TaskId
import com.realityexpander.tasky.agenda_feature.domain.AgendaItem
import com.realityexpander.tasky.core.util.EpochMilli
import com.realityexpander.tasky.core.util.toZonedDateTime
import kotlinx.serialization.*
import kotlinx.serialization.json.JsonNames
import java.time.ZonedDateTime

@Serializable
@OptIn(ExperimentalSerializationApi::class) // for JsonNames
data class TaskDTO(
    override val id: TaskId,
    override val title: String,
    override val description: String,

    @SerialName("remindAt")
    @JsonNames("remindAt") // json input field name
    val remindAtMilli: EpochMilli,
    val time: EpochMilli,

    @Required
    val isDone: Boolean = false,

    @Transient
    override val startTime: ZonedDateTime = time.toZonedDateTime(), // for sorting in Agenda
    @Transient
    override val remindAt: ZonedDateTime = remindAtMilli.toZonedDateTime(), // for debugging
) : AgendaItem()