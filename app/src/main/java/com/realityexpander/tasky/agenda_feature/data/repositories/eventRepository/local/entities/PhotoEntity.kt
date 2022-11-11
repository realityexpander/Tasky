package com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.local.entities

import com.realityexpander.tasky.agenda_feature.util.PhotoId
import com.realityexpander.tasky.agenda_feature.util.UrlStr

data class PhotoEntity(
    val id: PhotoId,
    val url: UrlStr
)
