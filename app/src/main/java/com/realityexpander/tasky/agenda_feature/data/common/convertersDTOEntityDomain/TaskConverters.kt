package com.realityexpander.tasky.agenda_feature.data.common.convertersDTOEntityDomain

import com.google.gson.GsonBuilder
import com.realityexpander.tasky.agenda_feature.data.repositories.taskRepository.local.entities.TaskEntity
import com.realityexpander.tasky.agenda_feature.data.repositories.taskRepository.remote.DTOs.TaskDTO
import com.realityexpander.tasky.agenda_feature.domain.AgendaItem
import com.realityexpander.tasky.core.util.toUtcMillis
import com.realityexpander.tasky.core.util.toZonedDateTime
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

// from Domain to Entity
fun AgendaItem.Task.toEntity(): TaskEntity {
    return TaskEntity(
        id = id,
        title = title,
        description = description,
        remindAt = remindAt,
        time = time,
        isDone = isDone,
    )
}

// from Entity to Domain
fun TaskEntity.toDomain(): AgendaItem.Task {
   return AgendaItem.Task(
        id = id,
        title = title,
        description = description,
        time = time,
        remindAt = remindAt,
        isDone = isDone,
    )
}

// from DTO to Domain (also converts UTC time millis to local ZonedDateTime)
fun TaskDTO.toDomain(): AgendaItem.Task {
    return AgendaItem.Task(
        id = id,
        title = title,
        description = description,
        remindAt = remindAt.toZonedDateTime(),
        time = time.toZonedDateTime(),
        isDone = isDone,
    )
}

// from Domain to DTO (also converts local ZonedDateTime to UTC time millis)
fun AgendaItem.Task.toDTO(): TaskDTO {
    return TaskDTO(
        id = id,
        title = title,
        description = description,
        time = time.toUtcMillis(),
        remindAt = remindAt.toUtcMillis(),
        isDone = isDone,
    )
}



fun main() {

    val event = AgendaItem.Task(
        id = "id",
        title = "title",
        description = "description",
        time = ZonedDateTime.now().truncatedTo(ChronoUnit.MINUTES),
        remindAt = ZonedDateTime.now().truncatedTo(ChronoUnit.MINUTES),
        isDone = false,
    )

    val gson = GsonBuilder()
        .setPrettyPrinting()
        .create()

    // Simulate TaskDTO.Response
    val eventDTO = event.toDTO()
    val eventDTOtoDomain = eventDTO.toDomain()
    val eventDTOtoDomainToEntity = eventDTOtoDomain.toEntity()
    val eventDTOtoDomainToEntityToDomain = eventDTOtoDomainToEntity.toDomain()

    //println(gson.toJson(event))
    println("event == eventDTOtoDomainToEntityToDomain: ${gson.toJson(eventDTOtoDomainToEntityToDomain) == gson.toJson(event)}")
}