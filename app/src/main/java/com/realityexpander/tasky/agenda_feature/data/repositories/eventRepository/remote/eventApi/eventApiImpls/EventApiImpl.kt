package com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.remote.eventApi.eventApiImpls

import android.accounts.NetworkErrorException
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.realityexpander.tasky.agenda_feature.common.util.EventId
import com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.remote.eventApi.DTOs.EventDTO
import com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.remote.eventApi.IEventApi
import com.realityexpander.tasky.core.data.remote.TaskyApi
import com.realityexpander.tasky.core.data.remote.utils.getErrorBodyMessage
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
                            body = InputStreamRequestBody(
                                context,
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

                    val filenameForServer = "photo$index-" +
                            photo.id +
                            ".${InputStreamRequestBody
                                .getFileName(context, photo.uri)
                                ?.split(".")
                                ?.last()
                                ?: "jpg"
                            }"

                    var bytes = context.contentResolver
                        .openInputStream(photo.uri)
                        .use {
                            it?.readBytes()
                        }
                    val size = context.contentResolver.openFileDescriptor(photo.uri, "r")
                        .use {
                            it?.statSize
                        }
                    size?.also {
                        if (it > 1000000) {
                            //throw NetworkErrorException("Photo too large")

                            // recompress the photo
                            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes?.size ?: 0)
                            val stream = ByteArrayOutputStream()
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream)
                            bytes = stream.toByteArray()
                        }
                    }

                    MultipartBody.Part
                        .createFormData(
                            "photo$index",
                            filenameForServer,
                            body = bytes?.toRequestBody() ?: throw Exception("Photo is null")
                        )

//                    MultipartBody.Part
//                        .createFormData(
//                            "photo$index",
//                            filenameForServer,
//                            body = InputStreamRequestBody(
//                                context,
//                                photo.uri
//                            )
//                        )
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
}
