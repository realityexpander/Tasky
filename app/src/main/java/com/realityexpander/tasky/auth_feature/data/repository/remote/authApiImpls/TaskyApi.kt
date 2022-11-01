package com.realityexpander.tasky.auth_feature.data.repository.remote.authApiImpls

import com.realityexpander.tasky.BuildConfig
import com.realityexpander.tasky.auth_feature.data.repository.remote.DTOs.auth.ApiCredentialsDTO
import com.realityexpander.tasky.auth_feature.data.repository.remote.DTOs.auth.AuthInfoDTO
import com.realityexpander.tasky.auth_feature.data.repository.remote.util.createAuthorizationHeader
import com.realityexpander.tasky.core.util.AuthToken
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

    @GET("authenticate")
    suspend fun authenticate(
        // Uses the Authorization Header created in the the interceptor
    ): Response<Void>

    @GET("authenticate")
    suspend fun authenticateAuthToken(
        authToken: AuthToken?,
        @Header("Authorization") authorizationHeader: String = createAuthorizationHeader(authToken),
    ): Response<Void>

    companion object {
        const val BASE_URL = "https://tasky.pl-coding.com/"
        const val API_KEY = BuildConfig.API_KEY
    }
}
