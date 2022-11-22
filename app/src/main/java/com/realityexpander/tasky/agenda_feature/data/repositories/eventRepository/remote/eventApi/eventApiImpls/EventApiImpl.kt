package com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.remote.eventApi.eventApiImpls

import android.accounts.NetworkErrorException
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.realityexpander.tasky.agenda_feature.common.util.EventId
import com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.remote.eventApi.DTOs.EventDTO
import com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.remote.eventApi.IEventApi
import com.realityexpander.tasky.core.data.remote.TaskyApi
import com.realityexpander.tasky.core.data.remote.utils.getErrorBodyMessage
import com.realityexpander.tasky.core.util.UuidStr
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import okio.*
import java.io.ByteArrayOutputStream
import java.util.*
import javax.inject.Inject


@OptIn(ExperimentalSerializationApi::class)
val jsonPrettyPrint = Json {
    prettyPrint = true
    ignoreUnknownKeys = true
    prettyPrintIndent = "   "
    encodeDefaults = true
}

const val MAX_IMAGE_SIZE = 1000000 // 1 MB

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

                    val bytes = getBytesFromUri(context, photo.uri)?.also { bytes ->
                        val size = bytes.size
                        if (size > MAX_IMAGE_SIZE) {
                            throw NetworkErrorException("Photo too large")
                        }
                    } ?: throw Exception("Photo not found")

                    MultipartBody.Part
                        .createFormData(
                            "photo$index",
                            getImageFilenameForServer(context, index, photo.uri, photo.id),
                            body = bytes.toRequestBody()
                        )
                }
            )
            if (response.isSuccessful) {
                val responseBody = response.body()
                return responseBody ?: throw Exception("Response body is null")
            } else {
                throw Exception("Error updating event: ${getErrorBodyMessage(response.errorBody()?.string())}")
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

                    val bytes = getBytesFromUri(context, photo.uri)?.also { bytes ->
                        val size = bytes.size
                        if (size > MAX_IMAGE_SIZE) {
                            throw NetworkErrorException("Photo too large")
                        }
                    } ?: throw Exception("Photo not found")

                    MultipartBody.Part
                        .createFormData(
                            "photo$index",
                            getImageFilenameForServer(context, index, photo.uri, photo.id),
                            body = bytes.toRequestBody()
                        )
                }
            )
            if (response.isSuccessful) {
                val responseBody = response.body()
                responseBody ?: throw Exception("Response body is null")
            } else {
                throw Exception("Error updating event: ${getErrorBodyMessage(response.errorBody()?.string())}")
            }
        } catch (e: NetworkErrorException) {
            throw Exception("Network Error updating event: ${e.localizedMessage}")
        } catch (e: Exception) {
            throw Exception("${e.message}")
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

    private fun getImageFilenameForServer(
        context: Context,
        index: Int,
        uri: Uri,
        id: UuidStr
    ): String {
        return "photo$index-$id." +
                (InputStreamRequestBody
                    .getFileName(context, uri)
                    ?.split(".")
                    ?.last()
                    ?: "jpg")
    }

    // Will automatically recompress to lower quality if the image is too large
    private fun getBytesFromUri(context: Context, uri: Uri): ByteArray?  {
        var bytes = context.contentResolver
            .openInputStream(uri)
            .use {
                it?.readBytes()
            }

        val imageSize = context.contentResolver.openFileDescriptor(uri, "r")
            .use {
                it?.statSize
            }

        imageSize?.also { size ->
            if (size > 1000000) {
                //throw NetworkErrorException("Photo too large")

                // recompress the photo
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes?.size ?: 0)
                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream)
                bytes = stream.toByteArray()
            }
        }

        return bytes
    }
}
