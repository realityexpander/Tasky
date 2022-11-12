package com.realityexpander.tasky.agenda_feature.data.common.convertersDTOEntityDomain

import com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.local.entities.PhotoEntity
import com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.remote.eventApi.DTOs.PhotoDTO
import com.realityexpander.tasky.agenda_feature.domain.Photo


fun PhotoEntity.toDomain() =
    Photo.Remote(
        id = id,
        url = url,
    )

fun PhotoDTO.Remote.toDomain() =
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
    PhotoDTO.Remote(
        id = id,
        url = url,
    )

// Note: This is used for local photos that are about to uploaded to the server. // todo implement photo uploading
fun Photo.Local.toDTO() =
    PhotoDTO.Local(
        id = id,
        uri = uri,
    )