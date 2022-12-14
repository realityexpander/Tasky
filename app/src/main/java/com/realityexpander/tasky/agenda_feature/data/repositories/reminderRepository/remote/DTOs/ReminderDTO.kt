package com.realityexpander.tasky.agenda_feature.data.repositories.reminderRepository.remote.DTOs

import com.realityexpander.tasky.agenda_feature.common.util.ReminderId
import com.realityexpander.tasky.agenda_feature.domain.AbstractAgendaItem
import com.realityexpander.tasky.agenda_feature.domain.HasTimeAsEpochMilli
import com.realityexpander.tasky.core.util.EpochMilli
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable

@Serializable
@OptIn(ExperimentalSerializationApi::class) // for JsonNames
data class ReminderDTO constructor(
    override val id: ReminderId,
    override val title: String,
    override val description: String,

//    @SerialName("remindAt")
//    @JsonNames("remindAt") // json input field name
    override val remindAt: EpochMilli,
    val time: EpochMilli,

//    @Transient
//    override val startTime: ZonedDateTime = time.toZonedDateTime(), // for sorting in Agenda
//    @Transient
//    override val remindAt: ZonedDateTime = remindAtMilli.toZonedDateTime(), // for debugging
) : AbstractAgendaItem(), HasTimeAsEpochMilli