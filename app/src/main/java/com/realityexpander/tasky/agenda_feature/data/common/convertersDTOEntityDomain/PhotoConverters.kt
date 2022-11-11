package com.realityexpander.tasky.agenda_feature.data.common.convertersDTOEntityDomain

import com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.remote.eventApi.DTOs.PhotoDTO
import com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.local.entities.PhotoEntity
import com.realityexpander.tasky.agenda_feature.domain.Photo


fun PhotoEntity.toDomain() =
    Photo(
        id = id,
        url = url,
    )

fun PhotoDTO.toDomain() =
    Photo(
        id = id,
        url = url,
    )

fun Photo.toEntity() =
    PhotoEntity(
        id = id,
        url = url,
    )

fun Photo.toDTO() =
    PhotoDTO(
        id = id,
        url = url,
    )