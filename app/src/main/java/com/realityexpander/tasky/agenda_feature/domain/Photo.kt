package com.realityexpander.tasky.agenda_feature.domain

import android.net.Uri
import android.os.Parcelable
import com.realityexpander.tasky.agenda_feature.common.util.PhotoId
import com.realityexpander.tasky.agenda_feature.common.util.UrlStr
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class Photo(
    @Transient open val id: PhotoId,  // @Transient because it confuses GSON serializer
) : Parcelable {

    data class Remote(
        override val id: PhotoId,
        val url: UrlStr,      // url of the photo on server
    ) : Photo(id = id)

    data class Local(
        override val id: PhotoId,
        val uri: Uri,         // path to the photo on device
    ) : Photo(id = id)
}