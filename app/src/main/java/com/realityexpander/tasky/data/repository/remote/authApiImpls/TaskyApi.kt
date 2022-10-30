package com.realityexpander.tasky.data.repository.remote.authApiImpls

import com.realityexpander.tasky.BuildConfig
import com.realityexpander.tasky.common.AuthToken
import com.realityexpander.tasky.data.repository.remote.ApiCredentialsDTO
import com.realityexpander.tasky.data.repository.remote.AuthInfoDTO
import com.realityexpander.tasky.data.repository.remote.IAuthApi
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
        //authToken: String = ""
        @Header("Authorization") authorizationHeader: String? = IAuthApi.authorizationHeader
    ): Response<Void>


    companion object {
        const val BASE_URL = "https://tasky.pl-coding.com/"
        const val API_KEY = BuildConfig.API_KEY

//        var authToken: String? = null
//            private set
//
//        var authorizationHeader: String? = null
//            private set
//            get() = "Bearer ${Companion.authToken}" ?: "{NULL TOKEN}"
//
//        @JvmName("setAuthToken1")
//        fun setAuthToken(authToken: AuthToken?) {
//            Companion.authToken = authToken
//        }
//
//        fun createAuthorizationHeader(authToken: String?): String {
//            return "Bearer $authToken" ?: "{NULL TOKEN}"
//        }
    }
}
