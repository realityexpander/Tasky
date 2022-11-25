package com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.remote.eventApi.eventApiImpls

import android.accounts.NetworkErrorException
import android.content.Context
import android.net.Uri
import com.realityexpander.tasky.agenda_feature.common.util.EventId
import com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.remote.eventApi.DTOs.EventDTO
import com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.remote.eventApi.DTOs.PhotoDTO
import com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.remote.eventApi.IEventApi
import com.realityexpander.tasky.core.data.getBytesRecompressed
import com.realityexpander.tasky.core.data.remote.TaskyApi
import com.realityexpander.tasky.core.data.remote.utils.getErrorBodyMessage
import com.realityexpander.tasky.core.util.UPLOAD_IMAGE_MAX_SIZE
import com.realityexpander.tasky.core.util.UuidStr
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import okio.*
import java.util.*
import javax.inject.Inject


@OptIn(ExperimentalSerializationApi::class)
val jsonPrettyPrint = Json {
    prettyPrint = true
    ignoreUnknownKeys = true
    prettyPrintIndent = "   "
    encodeDefaults = true
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
                photos = getMultipartBodyPartsForLocalPhotos(event.photos),
            )
            if (response.isSuccessful) {
                val responseBody = response.body()
                return responseBody ?: throw Exception("Response body is null")
            } else {
                throw Exception(getErrorBodyMessage(response.errorBody()?.string()))
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
        try {
            val response = taskyApi.updateEvent(
                updateEventRequest =
                    MultipartBody.Part
                        .createFormData(
                            "update_event_request",
                            jsonPrettyPrint.encodeToString(EventDTO.Update.serializer(), event)
                        ),
                photos = getMultipartBodyPartsForLocalPhotos(event.photos),
            )
            if (response.isSuccessful) {
                val responseBody = response.body()
                return responseBody ?: throw Exception("Response body is null")
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

    ////////////////// HELPER FUNCTIONS //////////////////

    private fun getMultipartBodyPartsForLocalPhotos(
        photos: List<PhotoDTO.Local>
    ): List<MultipartBody.Part> {
        return photos.mapIndexed { index, photo ->
            val bytes = photo.uri.getBytesRecompressed(context, UPLOAD_IMAGE_MAX_SIZE)
                ?: throw Exception("Photo not found")

            MultipartBody.Part
                .createFormData(
                    "photo$index",
                    getImageFilenameForServer(context, index, photo.uri, photo.id),
                    body = bytes.toRequestBody()
                )
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

}
