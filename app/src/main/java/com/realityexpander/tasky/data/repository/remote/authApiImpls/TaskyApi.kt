package com.realityexpander.tasky.data.repository.remote.authApiImpls

import com.realityexpander.tasky.BuildConfig
import com.realityexpander.tasky.data.repository.remote.ApiCredentialsDTO
import com.realityexpander.tasky.data.repository.remote.AuthInfoDTO
import retrofit2.Response
import retrofit2.http.*

interface TaskyApi {

    @POST("login")
    suspend fun login(
        @Body credentials: ApiCredentialsDTO,
    ): Response<AuthInfoDTO>

    @POST("register")
    suspend fun register(
        @Body credentials: ApiCredentialsDTO,
    ): Response<Void>


    companion object {
        const val BASE_URL = "https://tasky.pl-coding.com/"
        const val API_KEY = BuildConfig.API_KEY
    }
}
