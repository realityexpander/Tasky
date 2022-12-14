package com.realityexpander.tasky.agenda_feature.presentation.common.util

import android.content.Context
import android.net.Uri
import com.realityexpander.tasky.core.data.getBytesRecompressed
import com.realityexpander.tasky.core.util.UPLOAD_IMAGE_MAX_SIZE

fun Uri.isImageSizeTooLargeToUpload(
    context: Context,
    maxUploadSize: Int = UPLOAD_IMAGE_MAX_SIZE
): Boolean {
    this.getBytesRecompressed(context, maxUploadSize)?.size?.let { size ->
        return size > maxUploadSize
    }

    return true // if we can't get the size, assume it's too big
}