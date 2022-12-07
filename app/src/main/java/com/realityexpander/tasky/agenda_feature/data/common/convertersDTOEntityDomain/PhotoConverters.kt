package com.realityexpander.tasky.agenda_feature.data.common.convertersDTOEntityDomain

import com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.local.entities.PhotoEntity
import com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.remote.eventApi.DTOs.PhotoDTO
import com.realityexpander.tasky.agenda_feature.domain.Photo

// From Entity to Domain
fun PhotoEntity.toDomain() =
    if (uri == null)
        Photo.Remote(id = id, url = url
                ?: throw IllegalStateException("PhotoEntity with null Uri must have non-null Url"),
        )
    else
        Photo.Local(id = id, uri = uri,)

// From DTO to Domain (only allows Remote bc Local is never sent from server)
fun PhotoDTO.Remote.toDomain() =
    Photo.Remote(id = id, url = url)

// From Domain to Entity
fun Photo.toEntity() =
    when (this) {
        is Photo.Remote ->
            PhotoEntity(id = id, url = url)
        is Photo.Local ->
            PhotoEntity(id = id, uri = uri)
    }

// From Domain to DTO
fun Photo.toDTO() =
    when (this) {
        is Photo.Remote ->
            PhotoDTO.Remote(id = id, url = url)
        is Photo.Local ->
            PhotoDTO.Local(id = id, uri = uri)
    }

// From Domain to PhotoDTO.Local
fun Photo.Local.toDTO() =
    PhotoDTO.Local(id = id, uri = uri)