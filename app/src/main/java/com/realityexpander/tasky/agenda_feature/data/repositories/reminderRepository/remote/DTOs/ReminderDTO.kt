package com.realityexpander.tasky.agenda_feature.data.repositories.reminderRepository.remote.DTOs

import com.realityexpander.tasky.agenda_feature.common.util.ReminderId
import com.realityexpander.tasky.agenda_feature.domain.AgendaItem
import com.realityexpander.tasky.core.util.UtcMillis
import com.realityexpander.tasky.core.util.toZonedDateTime
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.time.ZonedDateTime

@Serializable
data class ReminderDTO(
    override val id: ReminderId,
    override val title: String,
    override val description: String,
    val remindAt: UtcMillis,
    val time: UtcMillis,

    @Transient
    override val startTime: ZonedDateTime = time.toZonedDateTime(), // for sorting in Agenda
    @Transient
    override val remindAtTime: ZonedDateTime = remindAt.toZonedDateTime(),
) : AgendaItem()