package com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.local.entities

import com.realityexpander.tasky.agenda_feature.util.PhotoId
import com.realityexpander.tasky.agenda_feature.util.UrlStr
    import kotlinx.serialization.Serializable

@Serializable
data class PhotoRemoteEntity(
    val id: PhotoId,
    val url: UrlStr    // url of the photo on server
)
