package com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.local.entities

import android.net.Uri
import com.realityexpander.tasky.agenda_feature.common.util.PhotoId
import com.realityexpander.tasky.agenda_feature.common.util.UrlStr
import com.realityexpander.tasky.agenda_feature.data.common.serializers.UriSerializer
import kotlinx.serialization.Serializable

@Serializable  // for Room
data class PhotoEntity(
    val id: PhotoId,
    val url: UrlStr? = null,    // url of the photo on server

    @Serializable(with = UriSerializer::class)  // for Room - List<Photo> is stored as a string
    val uri: Uri? = null        // path to the photo on device
)
