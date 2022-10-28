package com.realityexpander.tasky.data.repository.remote

import com.realityexpander.tasky.BuildConfig
import okhttp3.ResponseBody
import retrofit2.http.*

interface TaskyApi {

    @POST("login")
    suspend fun login(
        @Body credentials: TaskyCredentials,
        @Header("x-api-key") apiKey: String = API_KEY
    ): ResponseBody

    @POST("register")
    suspend fun register(
        @Body credentials: TaskyCredentials,
        @Header("x-api-key") apiKey: String = API_KEY
    ): ResponseBody

    companion object {
        const val BASE_URL = "https://www.tasky.pl-coding.com"
        const val API_KEY = BuildConfig.API_KEY
    }
}