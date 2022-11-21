package com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.remote.eventApi.eventApiImpls

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okio.BufferedSink
import okio.IOException
import okio.source
import java.io.File
import java.util.*

class InputStreamRequestBody(
    val context: Context,
//    val contentType: MediaType?,
//    private val contentResolver: ContentResolver,
    val uri: Uri
): RequestBody() {
//    private val contentType: MediaType?
//    private val contentResolver: ContentResolver
//    private val uri: Uri
//
//    init {
//        if (uri == null) throw NullPointerException("uri == null")
//        this.contentType = contentType
//        this.contentResolver = contentResolver
//        this.uri = uri
//    }

//    override fun contentType(): MediaType? {
//        return contentType
//    }

    override fun contentType(): MediaType? {
        return getMimeType(context, uri)?.toMediaTypeOrNull()
    }

    @Throws(IOException::class)
    override fun contentLength(): Long {
        return -1
    }

    @SuppressLint("Recycle")  // for openInputStream, its being closed with the use() function
    //@Throws(IOException::class)
    override fun writeTo(sink: BufferedSink) {

        val source = context.contentResolver.openInputStream(uri)?.source()
        source.use { source ->
            source?.let { sink.writeAll(it) }
        }
    }

    companion object {
        fun getMimeType(context: Context, uri: Uri): String? {
            return when (uri.scheme) {
                ContentResolver.SCHEME_CONTENT -> context.contentResolver.getType(uri)
                ContentResolver.SCHEME_FILE -> MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    MimeTypeMap.getFileExtensionFromUrl(uri.toString()).lowercase(Locale.US)
                )
                else -> null
            }
        }

        fun getFileName(context: Context, uri: Uri): String? {
            when (uri.scheme) {
                ContentResolver.SCHEME_FILE -> {
                    val filePath = uri.path
                    if (!filePath.isNullOrEmpty()) {
                        return File(filePath).name
                    }
                }
                ContentResolver.SCHEME_CONTENT -> {
                    return getCursorContent(uri, context.contentResolver)
                }
            }

            return null
        }

        private fun getCursorContent(uri: Uri, contentResolver: ContentResolver): String? =
            kotlin.runCatching {
                contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                    val nameColumnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (cursor.moveToFirst()) {
                        cursor.getString(nameColumnIndex)
                    } else null
                }
            }.getOrNull()
    }
}