package com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.remote.eventApi.eventApiImpls

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import com.realityexpander.tasky.agenda_feature.common.util.EventId
import com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.remote.eventApi.DTOs.EventDTO
import com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.remote.eventApi.IEventApi
import com.realityexpander.tasky.core.data.remote.TaskyApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okio.*
import javax.inject.Inject

val jsonPrettyPrint = Json {
    prettyPrint = true
    ignoreUnknownKeys = true
    prettyPrintIndent = "     "
    encodeDefaults = true
}

class InputStreamRequestBody(
    contentType: MediaType?,
    contentResolver: ContentResolver,
    uri: Uri?
): RequestBody() {
    private val contentType: MediaType?
    private val contentResolver: ContentResolver
    private val uri: Uri

    init {
        if (uri == null) throw NullPointerException("uri == null")
        this.contentType = contentType
        this.contentResolver = contentResolver
        this.uri = uri
    }

    override fun contentType(): MediaType? {
        return contentType
    }

    @Throws(IOException::class)
    override fun contentLength(): Long {
        return -1
    }

    @SuppressLint("Recycle")  // for openInputStream, its being closed with the use() function
    @Throws(IOException::class)
    override fun writeTo(sink: BufferedSink) {

        val source = contentResolver.openInputStream(uri)?.source()
        source.use { source ->
            source?.let { sink.writeAll(it) }
        }
    }
}

class EventApiImpl @Inject constructor(
    private val taskyApi: TaskyApi,
    private val context: Context
) : IEventApi {

    override suspend fun createEvent(event: EventDTO.Create): EventDTO.Response {
        try {
            val response = taskyApi.createEvent(
                createEventRequest =
                    MultipartBody.Part
                        .createFormData(
                            "create_event_request",
                            jsonPrettyPrint.encodeToString(EventDTO.Create.serializer(), event),
                        ),
                photos = event.photos.mapIndexed { index, photo -> // todo - add photo handling
                    MultipartBody.Part
                        .createFormData(
                            "photos",
                            "photo$index",
//                                body = photoFile.toRequestBody() // todo convert URI to ByteArray
                            body = InputStreamRequestBody(
                                "image/*".toMediaTypeOrNull(),
                                context.contentResolver,
                                photo.uri
                            )
                        )
                    }.also {
                    }
            )
            if (response.isSuccessful) {
                val responseBody = response.body()
                return responseBody ?: throw Exception("Response body is null")
            } else {
                throw Exception("Error creating event: ${response.errorBody()}")
            }
        } catch (e: Exception) {
            throw Exception("Error creating event: ${e.message}")
        }
    }

    override suspend fun getEvent(eventId: EventId): EventDTO.Response {
        try {
            val response = taskyApi.getEvent(eventId)
            if (response.isSuccessful) {
                val responseBody = response.body()
                return responseBody ?: throw Exception("Response body is null")
            } else {
                throw Exception("Error getting event: ${response.errorBody()}")
            }
        } catch (e: Exception) {
            throw Exception("Error getting event: ${e.message}")
        }
    }

    override suspend fun deleteEvent(eventId: EventId): Boolean {
        return try {
            val response = taskyApi.deleteEvent(eventId)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun updateEvent(event: EventDTO.Update): EventDTO.Response {
        return try {
            val response = taskyApi.updateEvent(
                updateEventRequest =
                    MultipartBody.Part
                        .createFormData(
                            "update_event_request",
                            jsonPrettyPrint.encodeToString(EventDTO.Update.serializer(), event)
                        ),
                photos = event.photos.mapIndexed { index, photo ->
                    MultipartBody.Part
                        .createFormData(
                            "photos",
                            "photo$index",
                            body = InputStreamRequestBody(
                                "image/jpeg".toMediaTypeOrNull(),
//                                "application/octet-stream".toMediaTypeOrNull(),
                                context.contentResolver,
                                photo.uri
                            )
                        )
                    }.also {
                    }
            )
            if (response.isSuccessful) {
                val responseBody = response.body()
                responseBody ?: throw Exception("Response body is null")
            } else {
                throw Exception("Error updating event: ${response.errorBody()}")
            }
        } catch (e: Exception) {
            throw Exception("Error updating event: ${e.message}")
        }
    }
}
