package com.realityexpander.tasky.core.data.remote

import com.realityexpander.tasky.BuildConfig
import com.realityexpander.tasky.agenda_feature.common.util.TimeZoneStr
import com.realityexpander.tasky.agenda_feature.data.repositories.agendaRepository.remote.DTOs.AgendaDayDTO
import com.realityexpander.tasky.agenda_feature.data.repositories.agendaRepository.remote.DTOs.AgendaSyncDTO
import com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.remote.eventApi.DTOs.EventDTO
import com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.remote.eventApi.DTOs.GetAttendeeResponseDTO
import com.realityexpander.tasky.auth_feature.data.repository.remote.DTOs.auth.ApiCredentialsDTO
import com.realityexpander.tasky.auth_feature.data.repository.remote.DTOs.auth.AuthInfoDTO
import com.realityexpander.tasky.auth_feature.data.repository.remote.util.createAuthorizationHeader
import com.realityexpander.tasky.core.util.AuthToken
import com.realityexpander.tasky.core.util.Email
import com.realityexpander.tasky.core.util.UtcMillis
import com.realityexpander.tasky.core.util.UuidStr
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface TaskyApi {

    companion object {
        const val BASE_URL = "https://tasky.pl-coding.com/"
        const val API_KEY = BuildConfig.API_KEY
    }

    ////////////////// AUTH //////////////////

    @POST("login")
    suspend fun login(
        @Body credentials: ApiCredentialsDTO,
    ): Response<AuthInfoDTO>

    @POST("register")
    suspend fun register(
        @Body credentials: ApiCredentialsDTO,
    ): Response<Void>

    @GET("authenticate")
    suspend fun authenticate(
        // Uses the Authorization Header created in the the interceptor
    ): Response<Void>

    @GET("authenticate")
    suspend fun authenticateAuthToken(
        authToken: AuthToken?,
        @Header("Authorization") authorizationHeader: String = createAuthorizationHeader(authToken),
    ): Response<Void>

    @GET("logout")
    suspend fun logout(
        // Uses the Authorization Header created in the the interceptor
    ): Response<Void>

    ////////////////// AGENDA //////////////////

    @GET("agenda")
    suspend fun getAgenda(
        timezone: TimeZoneStr,  // ex: "Europe/Berlin"
        time: UtcMillis,        // epoch millis in UTC timeZone
    ): Response<AgendaDayDTO>

    @POST("syncAgenda")
    suspend fun syncAgenda(
        @Body request: AgendaSyncDTO,
    ): Response<Void>


    ////////////////// EVENT //////////////////

    @Multipart
    @POST("event")
    suspend fun createEvent(
        @Part createEventRequest: MultipartBody.Part,   // EventDTO.Create
        @Part photos: List<MultipartBody.Part>          // List<PhotoDTO>
    ): Response<EventDTO.Response>

    @GET("event/{eventId}")
    suspend fun getEvent(
        @Path("eventId") eventId: UuidStr,
    ): Response<EventDTO.Response>

    @Multipart
    @PUT("event")
    suspend fun updateEvent(
        @Part updateEventRequest: MultipartBody.Part,   // EventDTO.Update
        @Part photos: List<MultipartBody.Part>          // List<PhotoDTO>
    ): Response<EventDTO.Response>

    @DELETE("event/{eventId}")
    suspend fun deleteEvent(
        @Path("eventId") eventId: UuidStr,
    ): Response<Void>


    ////////////////// ATTENDEE //////////////////

    @GET("attendee")
    suspend fun getAttendee(
        @Query("email") email: Email,
    ): Response<GetAttendeeResponseDTO>

    @DELETE("attendee/{eventId}")
    suspend fun deleteAttendee(
        @Path("eventId") eventId: UuidStr,
    ): Response<Void>
}
