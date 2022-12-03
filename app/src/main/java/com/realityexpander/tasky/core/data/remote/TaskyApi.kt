package com.realityexpander.tasky.core.data.remote

import com.realityexpander.tasky.BuildConfig
import com.realityexpander.tasky.agenda_feature.common.util.ReminderId
import com.realityexpander.tasky.agenda_feature.common.util.TaskId
import com.realityexpander.tasky.agenda_feature.common.util.TimeZoneStr
import com.realityexpander.tasky.agenda_feature.data.repositories.agendaRepository.remote.DTOs.AgendaDayDTO
import com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.remote.eventApi.DTOs.EventDTO
import com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.remote.eventApi.DTOs.GetAttendeeResponseDTO
import com.realityexpander.tasky.agenda_feature.data.repositories.reminderRepository.remote.DTOs.ReminderDTO
import com.realityexpander.tasky.agenda_feature.data.repositories.syncRepository.remote.SyncAgendaRequestDTO
import com.realityexpander.tasky.agenda_feature.data.repositories.taskRepository.remote.DTOs.TaskDTO
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
        @Query("timezone") timezone: TimeZoneStr,  // ex: "Europe/Berlin"
        @Query("time") time: UtcMillis,            // epoch millis in UTC timeZone
    ): Response<AgendaDayDTO>

    @POST("syncAgenda")
    suspend fun syncAgenda(
        @Body agendaSyncRequest: SyncAgendaRequestDTO,
    ): Response<Void>


    ////////////////// EVENT //////////////////

    @Multipart
    @POST("event")
    suspend fun createEvent(
        @Part createEventRequest: MultipartBody.Part,   // EventDTO.Create
        @Part photos: List<MultipartBody.Part>          // List<PhotoDTO>
    ): Response<EventDTO.Response>

    @GET("event")
    suspend fun getEvent(
        @Query("eventId") eventId: UuidStr,
    ): Response<EventDTO.Response>

    @Multipart
    @PUT("event")
    suspend fun updateEvent(
        @Part updateEventRequest: MultipartBody.Part,   // EventDTO.Update
        @Part photos: List<MultipartBody.Part>          // List<PhotoDTO>
    ): Response<EventDTO.Response>

    @DELETE("event")
    suspend fun deleteEvent(
        @Query("eventId") eventId: UuidStr,
    ): Response<Void>


    ////////////////// ATTENDEE //////////////////

    @GET("attendee")
    suspend fun getAttendee(
        @Query("email") email: Email,
    ): Response<GetAttendeeResponseDTO>

    @DELETE("attendee")  // remove the logged-in user from the eventId (does NOT delete the attendee)
    suspend fun deleteAttendee(
        @Query("eventId") eventId: UuidStr,
    ): Response<Void>


    ////////////////// TASK //////////////////

    @POST("task")
    suspend fun createTask(
        @Body task: TaskDTO
    ): Response<Void>

    @GET("task")
    suspend fun getTask(
        @Query("taskId") taskId: TaskId,
    ): Response<TaskDTO>

    @PUT("task")
    suspend fun updateTask(
        @Body task: TaskDTO
    ): Response<Void>

    @DELETE("task")
    suspend fun deleteTask(
        @Query("taskId") taskId: TaskId
    ): Response<Void>

    ////////////////// REMINDER //////////////////

    @POST("reminder")
    suspend fun createReminder(
        @Body reminder: ReminderDTO
    ): Response<Void>

    @GET("reminder")
    suspend fun getReminder(
        @Query("reminderId") reminderId: ReminderId,
    ): Response<ReminderDTO>

    @PUT("reminder")
    suspend fun updateReminder(
        @Body reminder: ReminderDTO
    ): Response<Void>

    @DELETE("reminder")
    suspend fun deleteReminder(
        @Query("reminderId") reminderId: ReminderId
    ): Response<Void>
}
