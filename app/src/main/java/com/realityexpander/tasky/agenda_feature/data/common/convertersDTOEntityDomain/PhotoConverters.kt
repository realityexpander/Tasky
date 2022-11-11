package com.realityexpander.tasky.agenda_feature.data.common.convertersDTOEntityDomain

import com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.local.entities.PhotoEntity
import com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.remote.eventApi.DTOs.PhotoDTO
import com.realityexpander.tasky.agenda_feature.domain.Photo


fun PhotoEntity.toDomain() =
    Photo.Remote(
        id = id,
        url = url,
    )

fun PhotoDTO.toDomain() =
    Photo.Remote(
        id = id,
        url = url,
    )

fun Photo.Remote.toEntity() =
    PhotoEntity(
        id = id,
        url = url,
    )

fun Photo.Remote.toDTO() =
    PhotoDTO(
        id = id,
        url = url,
    )

// Note: This is used for local photos that are about to uploaded to the server.
fun Photo.Local.toDTO() =
    PhotoDTO(
        id = id,
        url = "URL_NOT_SET_YET",
        uri = uri,
    )