package com.realityexpander.tasky.agenda_feature.data.common.convertersDTOEntityDomain

import com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.local.entities.PhotoRemoteEntity
import com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.remote.eventApi.DTOs.PhotoDTO
import com.realityexpander.tasky.agenda_feature.domain.Photo

// From Entity to Domain
fun PhotoRemoteEntity.toDomain() =
    Photo.Remote(
        id = id,
        url = url,
    )

// From DTO to Domain
fun PhotoDTO.Remote.toDomain() =
    Photo.Remote(
        id = id,
        url = url,
    )

// From Domain to Entity
fun Photo.Remote.toEntity() =
    PhotoRemoteEntity(
        id = id,
        url = url,
    )

// From Domain to DTO
fun Photo.Remote.toDTO() =
    PhotoDTO.Remote(
        id = id,
        url = url,
    )

// From Domain to DTO
// Note: This is used for local photos that are about to uploaded to the server. // todo implement photo uploading
fun Photo.Local.toDTO() =
    PhotoDTO.Local(
        id = id,
        uri = uri,
    )