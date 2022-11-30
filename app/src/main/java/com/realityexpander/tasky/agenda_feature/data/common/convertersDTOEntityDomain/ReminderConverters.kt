package com.realityexpander.remindery.agenda_feature.data.common.convertersDTOEntityDomain

import com.google.gson.GsonBuilder
import com.realityexpander.tasky.agenda_feature.data.repositories.reminderRepository.local.entities.ReminderEntity
import com.realityexpander.tasky.agenda_feature.data.repositories.reminderRepository.remote.DTOs.ReminderDTO
import com.realityexpander.tasky.agenda_feature.domain.AgendaItem
import com.realityexpander.tasky.core.util.toUtcMillis
import com.realityexpander.tasky.core.util.toZonedDateTime
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

// from Domain to Entity
fun AgendaItem.Reminder.toEntity(): ReminderEntity {
    return ReminderEntity(
        id = id,
        title = title,
        description = description,
        remindAt = remindAt,
        time = time,
        isDeleted = isDeleted,
    )
}

// from Entity to Domain
fun ReminderEntity.toDomain(): AgendaItem.Reminder {
   return AgendaItem.Reminder(
        id = id,
        title = title,
        description = description,
        time = time,
        remindAt = remindAt,
    )
}

// from DTO to Domain (also converts UTC time millis to local ZonedDateTime)
fun ReminderDTO.toDomain(): AgendaItem.Reminder {
    return AgendaItem.Reminder(
        id = id,
        title = title,
        description = description,
        remindAt = remindAt.toZonedDateTime(),
        time = time.toZonedDateTime(),
    )
}

// from Domain to DTO (also converts local ZonedDateTime to UTC time millis)
fun AgendaItem.Reminder.toDTO(): ReminderDTO {
    return ReminderDTO(
        id = id,
        title = title,
        description = description,
        time = time.toUtcMillis(),
        remindAt = remindAt.toUtcMillis(),
    )
}


fun main() {

    val reminder = AgendaItem.Reminder(
        id = "id",
        title = "title",
        description = "description",
        time = ZonedDateTime.now().truncatedTo(ChronoUnit.MINUTES),
        remindAt = ZonedDateTime.now().truncatedTo(ChronoUnit.MINUTES),
    )

    val gson = GsonBuilder()
        .setPrettyPrinting()
        .create()

    // Simulate ReminderDTO.Response
    val reminderDTO = reminder.toDTO()
    val reminderDTOtoDomain = reminderDTO.toDomain()
    val reminderDTOtoDomainToEntity = reminderDTOtoDomain.toEntity()
    val reminderDTOtoDomainToEntityToDomain = reminderDTOtoDomainToEntity.toDomain()

    //println(gson.toJson(reminder))
    println("reminder == reminderDTOtoDomainToEntityToDomain: ${gson.toJson(reminderDTOtoDomainToEntityToDomain) == gson.toJson(reminder)}")
}