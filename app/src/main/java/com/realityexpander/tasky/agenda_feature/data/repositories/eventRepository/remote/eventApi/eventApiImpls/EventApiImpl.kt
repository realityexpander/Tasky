package com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.remote.eventApi.eventApiImpls

import com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.remote.eventApi.DTOs.EventDTO
import com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.remote.eventApi.IEventApi
import com.realityexpander.tasky.agenda_feature.util.EventId
import com.realityexpander.tasky.core.data.remote.TaskyApi
import kotlinx.serialization.json.Json
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

class EventApiImpl @Inject constructor(
    private val taskyApi: TaskyApi
) : IEventApi {
    override suspend fun createEvent(event: EventDTO): EventDTO {
        try {
//            val response = taskyApi.createEvent(event)
            val response = taskyApi.createEvent(
                createEventRequest =
                    MultipartBody.Part
                        .createFormData(
                            "create_event_request",
                            Json.encodeToString(EventDTO.serializer(), event),
                        ),
//                photos = emptyList()
                photos = event.photos.map {
                    MultipartBody.Part
                        .createFormData(
                            "photos",
                            "photo1",
                            body = event.photos[0].toRequestBody()
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

    override suspend fun getEvent(eventId: EventId): EventDTO {
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

    override suspend fun updateEvent(event: EventDTO): EventDTO {
        return try {
//            val response = taskyApi.updateEvent(event)
            val response = taskyApi.updateEvent(
                updateEventRequest =
                    MultipartBody.Part
                        .createFormData(
                            "update_event_request",
                            Json.encodeToString(EventDTO.serializer(), event)
                        ),
                    photos = event.photos.map {
                        MultipartBody.Part
                            .createFormData(
                                "photos",
                                "photo1",
                                body = event.photos[0].toRequestBody()
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
