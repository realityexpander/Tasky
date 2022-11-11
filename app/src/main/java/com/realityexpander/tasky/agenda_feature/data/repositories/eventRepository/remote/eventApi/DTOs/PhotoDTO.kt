package com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.remote.eventApi.DTOs

import android.net.Uri
import com.realityexpander.tasky.agenda_feature.util.PhotoId
import com.realityexpander.tasky.agenda_feature.util.UrlStr
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.JsonNames

@Serializable
@OptIn(ExperimentalSerializationApi::class)  // for @JsonNames
data class PhotoDTO(
    @JsonNames("key")   // input from json
    val id: PhotoId,
    val url: UrlStr,

    @Transient
    val uri: Uri? = null,        // points to local file, if any. Not serialized.
)