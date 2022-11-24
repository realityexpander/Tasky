package com.realityexpander.tasky.core.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.realityexpander.tasky.core.util.UPLOAD_IMAGE_MAX_SIZE
import java.io.ByteArrayOutputStream



// Will automatically recompress to lower quality if the image byte size exceeds the recompressThreshold
fun Uri.getBytesFromUri(context: Context, recompressThreshold: Int = UPLOAD_IMAGE_MAX_SIZE): ByteArray?  {
    var bytes = context.contentResolver
        .openInputStream(this)
        .use {
            it?.readBytes()
        }

    val imageSize = context.contentResolver.openFileDescriptor(this, "r")
        .use {
            it?.statSize
        }

    imageSize?.also { size ->
        if (size > recompressThreshold) {
            // recompress the photo
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes?.size ?: 0)
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream)
            bytes = stream.toByteArray()
        }
    }

    return bytes
}

fun Uri.isImageSizeTooLargeToUpload(context: Context, maxUploadSize: Int = UPLOAD_IMAGE_MAX_SIZE): Boolean {
    this.getBytesFromUri(context, maxUploadSize)?.size?.let { size ->
        return size > maxUploadSize
    }

    return true // if we can't get the size, assume it's too big
}