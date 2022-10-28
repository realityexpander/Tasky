package com.realityexpander.tasky.data.repository.remote

import com.realityexpander.tasky.BuildConfig
import com.realityexpander.tasky.data.repository.AuthInfo
import retrofit2.Response
import retrofit2.http.*

interface TaskyApi {

    @POST("login")
    suspend fun login(
        @Body credentials: TaskyCredentials,
        @Header("x-api-key") apiKey: String = API_KEY
    ): Response<AuthInfo>

    @POST("register")
    suspend fun register(
        @Body credentials: TaskyCredentials,
        @Header("x-api-key") apiKey: String = API_KEY
    ): Response<AuthInfo>


    companion object {
        const val BASE_URL = "https://tasky.pl-coding.com"
        const val API_KEY = BuildConfig.API_KEY
    }
}
