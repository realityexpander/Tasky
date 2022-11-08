package com.realityexpander.tasky.agenda_feature.data.common.convertersDTOEntityDomain

import com.realityexpander.tasky.agenda_feature.data.repositories.agendaRepository.remote.DTOs.AgendaSyncDTO
import com.realityexpander.tasky.agenda_feature.domain.AgendaSync

// from Domain to DTO
fun AgendaSync.toDTO(): AgendaSyncDTO {
    return this.let {
        AgendaSyncDTO(
            deleteEventIds = it.deleteEventIds,
            deleteTaskIds = it.deleteTaskIds,
            deleteReminderIds = it.deleteReminderIds,
        )
    }
}

// from DTO to Domain
fun AgendaSyncDTO.toDomain(): AgendaSync {
    return this.let {
        AgendaSync(
            deleteEventIds = it.deleteEventIds,
            deleteTaskIds = it.deleteTaskIds,
            deleteReminderIds = it.deleteReminderIds,
        )
    }
}