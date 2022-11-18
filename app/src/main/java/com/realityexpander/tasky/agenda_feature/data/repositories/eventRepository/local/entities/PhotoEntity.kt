package com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.local.entities

import android.net.Uri
import com.realityexpander.tasky.agenda_feature.common.util.PhotoId
import com.realityexpander.tasky.agenda_feature.common.util.UrlStr

data class PhotoEntity(
    val id: PhotoId,
    val url: UrlStr? = null,    // url of the photo on server
    val uri: Uri? = null        // path to the photo on device
)
