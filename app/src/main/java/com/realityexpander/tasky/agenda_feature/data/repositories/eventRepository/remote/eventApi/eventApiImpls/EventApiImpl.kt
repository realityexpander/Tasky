package com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.remote.eventApi.eventApiImpls

import com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.remote.eventApi.DTOs.EventDTO
import com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.remote.eventApi.IEventApi
import com.realityexpander.tasky.agenda_feature.common.util.EventId
import com.realityexpander.tasky.core.data.remote.TaskyApi
import kotlinx.serialization.json.Json
import okhttp3.MultipartBody
import javax.inject.Inject

class EventApiImpl @Inject constructor(
    private val taskyApi: TaskyApi
) : IEventApi {
    override suspend fun createEvent(event: EventDTO.Create): EventDTO.Response {
        try {
            val response = taskyApi.createEvent(
                createEventRequest =
                    MultipartBody.Part
                        .createFormData(
                            "create_event_request",
                            Json.encodeToString(EventDTO.Create.serializer(), event),
                        ),
                photos = event.photos.map {  // todo - add photo handling
                    MultipartBody.Part.createFormData("photos", "")
                },
//                photos = event.photos.mapIndexed { index, photo -> // todo - add photo handling
//
//                val photoFile = Uri.fromFile(  // todo possible solution
//                    File(
//                        context.cacheDir,
//                        context.contentResolver.getFileName(event.photos[0].uri!!)
//                    )
//                    ).toFile()
//
//                    MultipartBody.Part
//                        .createFormData(
//                            "photos",
//                            "photo$index",
//                            body = photoFile.toRequestBody() // todo convert URI to ByteArray
//                        )
//                }.also {
//                }
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
                            Json.encodeToString(EventDTO.Update.serializer(), event)
                        ),
                photos = emptyList(),
//                    photos = event.photos.mapIndexed { index, photo -> // todo - add photo handling
//                        MultipartBody.Part
//                            .createFormData(
//                                "photos",
//                                "photo$index",
//                                body = event.photos[0].toRequestBody()  // todo Convert URI to ByteArray
//                        )
//                }.also {
//                }
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
