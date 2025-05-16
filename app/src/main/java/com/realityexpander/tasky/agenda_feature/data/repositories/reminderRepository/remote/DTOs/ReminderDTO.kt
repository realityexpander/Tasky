package com.realityexpander.tasky.agenda_feature.data.repositories.reminderRepository.remote.DTOs

import com.realityexpander.tasky.agenda_feature.domain.ReminderId
import com.realityexpander.tasky.agenda_feature.domain.AbstractAgendaItem
import com.realityexpander.tasky.agenda_feature.domain.UsesEpochMilli
import com.realityexpander.tasky.core.util.EpochMilli
import kotlinx.serialization.Serializable

@Serializable
data class ReminderDTO constructor(
    override val id: ReminderId,
    override val title: String,
    override val description: String,

    override val remindAt: EpochMilli,
    val time: EpochMilli,
) : AbstractAgendaItem(), UsesEpochMilli
