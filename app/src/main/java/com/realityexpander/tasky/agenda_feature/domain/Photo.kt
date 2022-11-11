package com.realityexpander.tasky.agenda_feature.domain

import android.os.Parcelable
import com.realityexpander.tasky.agenda_feature.util.PhotoId
import com.realityexpander.tasky.agenda_feature.util.UrlStr
import kotlinx.parcelize.Parcelize

@Parcelize
data class Photo(
    val id: PhotoId,
    val url: UrlStr,          // url of the photo on server

    val uri: String? = null,  // points to a local file on device (if photo is new)
) : Parcelable