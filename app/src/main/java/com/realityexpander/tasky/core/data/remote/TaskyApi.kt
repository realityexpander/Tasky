package com.realityexpander.tasky.core.data.remote

import com.realityexpander.tasky.BuildConfig
import com.realityexpander.tasky.agenda_feature.data.repositories.agendaRepository.remote.DTOs.AgendaDayDTO
import com.realityexpander.tasky.agenda_feature.data.repositories.agendaRepository.remote.DTOs.AgendaSyncDTO
import com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.remote.eventApi.DTOs.EventDTO
import com.realityexpander.tasky.agenda_feature.util.TimeZoneStr
import com.realityexpander.tasky.auth_feature.data.repository.remote.DTOs.auth.ApiCredentialsDTO
import com.realityexpander.tasky.auth_feature.data.repository.remote.DTOs.auth.AuthInfoDTO
import com.realityexpander.tasky.auth_feature.data.repository.remote.util.createAuthorizationHeader
import com.realityexpander.tasky.core.util.AuthToken
import com.realityexpander.tasky.core.util.UuidStr
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

    // todo implement logout
    @GET("logout")
    suspend fun logout(
        // Uses the Authorization Header created in the the interceptor
    ): Response<Void>

    ////////////////// AGENDA //////////////////

    @GET("agenda")
    suspend fun getAgenda(
        timezone: TimeZoneStr,  // ex: "Europe/Berlin"
        time: Long,             // epoch millis in UTC
    ): Response<AgendaDayDTO>

    @POST("syncAgenda")
    suspend fun syncAgenda(
        @Body syncItems: AgendaSyncDTO,
    ): Response<Void>


    ////////////////// EVENT //////////////////

    @POST("event")
    suspend fun createEvent(
        @Body event: EventDTO, // todo implement multi-part request
    ): Response<EventDTO>

    @GET("event/{eventId}")
    suspend fun getEvent(
        @Path("eventId") eventId: UuidStr,
    ): Response<EventDTO>

    @DELETE("event/{eventId}")
    suspend fun deleteEvent(
        @Path("eventId") eventId: UuidStr,
    ): Response<Void>

    @PUT("event")
    suspend fun updateEvent(
        @Body event: EventDTO,
    ): Response<EventDTO>
}
