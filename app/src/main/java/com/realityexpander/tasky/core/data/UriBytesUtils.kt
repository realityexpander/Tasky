package com.realityexpander.tasky.core.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.realityexpander.tasky.core.util.UPLOAD_IMAGE_MAX_SIZE
import java.io.ByteArrayOutputStream



// Will automatically recompress to lower quality if the image byte size exceeds the recompressThreshold
fun Uri.getBytesRecompressed(
    context: Context,
    recompressThreshold: Int = UPLOAD_IMAGE_MAX_SIZE
): ByteArray?  {
    val imageSize = this.getSize(context) ?: return null
    var bytes = this.getBytes(context) ?: return null

    // Recompress if the image exceeds the recompressThreshold
    imageSize.also { size ->
        if (size > recompressThreshold) {
            // recompress the photo
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size ?: 0)
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream)
            bytes = stream.toByteArray()
        }
    }

    return bytes
}

fun Uri.getBytes(context: Context): ByteArray?  {
    return context.contentResolver
        .openInputStream(this)
        .use {
            it?.readBytes()
        }
}

fun Uri.getSize(context: Context): Long?  {
    return context.contentResolver.openFileDescriptor(this, "r")
        .use {
            it?.statSize
        }
}

fun Uri.isAvailable(context: Context): Boolean {
    return try {
        context.contentResolver.openFileDescriptor(this, "r")
            .use {
                it != null
            }
    } catch (e: Exception) {
        false
    }
}