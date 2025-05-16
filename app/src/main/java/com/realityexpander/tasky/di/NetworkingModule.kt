package com.realityexpander.tasky.di

import android.util.Log
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.realityexpander.tasky.BuildConfig
import com.realityexpander.tasky.auth_feature.data.repository.local.IAuthDao
import com.realityexpander.tasky.auth_feature.data.repository.remote.DTOs.auth.RefreshTokenRequestDTO
import com.realityexpander.tasky.auth_feature.data.repository.remote.DTOs.auth.RefreshTokenResponseDTO
import com.realityexpander.tasky.auth_feature.data.repository.remote.IAuthApi
import com.realityexpander.tasky.auth_feature.data.repository.remote.IAuthApi.Companion.createAuthorizationAccessTokenString
import com.realityexpander.tasky.core.data.remote.TaskyApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.Dispatcher
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.POST
import java.time.ZonedDateTime
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkingModule {

    ///////////////////////////////////////////
    // Serialization  & JSON Pretty Print

    @OptIn(ExperimentalSerializationApi::class)
    @Provides
    @Singleton
    fun provideKotlinSerialization(): Converter.Factory {
        val contentType = "application/json".toMediaType()
        val json = Json {
            ignoreUnknownKeys = true
            isLenient = true
            prettyPrint = true
        }

        return json.asConverterFactory(contentType)
    }

    ///////////////////////////////////////////
    // Networking (OkHttp & Retrofit)

    private interface TaskyRefreshAccessTokenService {
        @POST("/accessToken")
        suspend fun refreshAccessToken(@Body request: RefreshTokenRequestDTO): retrofit2.Response<RefreshTokenResponseDTO>
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        @AuthDaoProdUsingBinds authDao: IAuthDao,
    ): OkHttpClient {

        // Configure to Allow more simultaneous requests
        val dispatcher = Dispatcher(Executors.newFixedThreadPool(20))
        dispatcher.maxRequests = 20
        dispatcher.maxRequestsPerHost = 20

        val addHeadersInterceptor = Interceptor { chain ->
            runBlocking(Dispatchers.IO) {

                val requestBuilder = chain.request().newBuilder()
                    .addHeader("x-api-key", TaskyApi.API_KEY)
                val request = requestBuilder
                    .build()

                chain.proceed(request)
            }
        }

        val refreshAccessTokenRetrofitClient = object {
            private val BASE_URL = TaskyApi.BASE_URL // "https://tasky.pl-coding.com/"

            private val client = OkHttpClient.Builder()
                .addInterceptor(addHeadersInterceptor)
                .build()

            val instance: Retrofit by lazy {
                Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(
                        provideKotlinSerialization()
                    )
                    .build()
            }

            val api: TaskyRefreshAccessTokenService by lazy {
                instance.create(TaskyRefreshAccessTokenService::class.java)
            }
        }

        fun refreshAccessToken() {
            runBlocking(Dispatchers.IO) {
                if(IAuthApi.refreshToken == null) {
                    throw Exception("No refresh token")
                }
                if(IAuthApi.refreshToken!!.isEmpty()) {
                    throw Exception( "Empty refresh token")
                }
                if(IAuthApi.authenticatedUserId == null) {
                    throw Exception("No authenticated user id")
                }

                val request = RefreshTokenRequestDTO(IAuthApi.refreshToken, IAuthApi.authenticatedUserId)
                val response = refreshAccessTokenRetrofitClient.api.refreshAccessToken(request)
                if (!response.isSuccessful) {
                    throw Exception("Failed to refresh access token: ${response.code()} - ${response.message()}")
                }
                val refreshedAccessTokenResult = response.body()
                    ?: throw Exception("No Access Token Result")
                val newAccessToken = refreshedAccessTokenResult.accessToken
                val newAccessTokenExpirationTimestampEpochMilli = refreshedAccessTokenResult.accessTokenExpirationTimestampEpochMilli

                val authInfo = authDao.getAuthInfo()
                    ?: throw Exception("No AuthInfo in AuthDao")

                val newAuthInfo = authInfo.copy(
                    accessToken = newAccessToken,
                    accessTokenExpirationTimestampEpochMilli = newAccessTokenExpirationTimestampEpochMilli
                )
                // Update the AuthInfo in the AuthDao & IAuthApi
                authDao.setAuthInfo(newAuthInfo)
                IAuthApi.setAuthInfo(authDao.getAuthInfo())

                Log.d("AuthInterceptor", "refreshed AccessToken success, new expiry=" +
                        // convert newAccessTokenExpirationTimestampEpochMilli to human readable string
                        ZonedDateTime.ofInstant(
                            java.util.Date(newAccessTokenExpirationTimestampEpochMilli).toInstant(),
                            java.time.ZoneId.systemDefault()
                        ).toString()
                )
            }
        }


        // Check AuthToken expiry on all requests
        val checkRefreshTokenInterceptor = Interceptor { chain ->
            runBlocking(Dispatchers.IO) {

                val requestBuilder = chain.request().newBuilder()

                // Check JWT expiry (may be different from access token expiry)
                val jwt = IAuthApi.getAccessToken { authDao.getAccessToken() }
                if (jwt != null) {
                    val jwtParts = jwt.split(".")
                    if (jwtParts.size == 3) {
                        val payload = String(android.util.Base64.decode(jwtParts[1], android.util.Base64.DEFAULT))
                        val jsonObject = JSONObject(payload)
                        val exp = jsonObject.getLong("exp") * 1000
                        if (exp < System.currentTimeMillis()) {
                            Log.d("AuthInterceptor", "JWT expired, refreshing AccessToken...")
                            refreshAccessToken()
                        }
                    }
                }

                // Check Access Token Expiry and fetch a new one if expired
                IAuthApi.accessTokenExpirationTimestampEpochMilli?.let { accessTokenExpirationTimestampEpochMilli ->
                    if(accessTokenExpirationTimestampEpochMilli < System.currentTimeMillis()) {
                        try {
                            Log.d("AuthInterceptor", "AccessToken expired, refreshing AccessToken...")
                            refreshAccessToken()
                        } catch (e: Exception) {
                            Log.e("AuthInterceptor", "Failed to refresh access token: ${e.message}")
                        }
                    }
                }
                // If AuthToken is invalid, log out and clear the AuthDao
                if(IAuthApi.getAccessToken { authDao.getAccessToken() } == null) {
                    authDao.clearAuthInfo() // will force logout
                }

                // If AuthToken is valid, add it to the request. If invalid, attempt to fetch AuthToken from the AuthDao
                IAuthApi.getAccessToken { authDao.getAccessToken() }?.let { accessToken ->
                    requestBuilder
                        .addHeader("Authorization",
                            createAuthorizationAccessTokenString(accessToken)
                        )
                }

                val request = requestBuilder
                    .build()

                chain.proceed(request)
            }

        }

        val jsonPrettyPrinter = object : HttpLoggingInterceptor.Logger {
            private fun print(m: String) {
                Log.i("API", m)
            }

            override fun log(message: String) {

                if (message.startsWith("{") || message.startsWith("[")) try {
                    JSONObject(message)
                        .toString(2)
                        .take(500)
                        .also(::print)
                } catch (e: JSONException) {
                    print(message)
                }
                else print(message)

                if (message.length > 500) {
                    print("=== ...${message.takeLast(message.length - 300)}")
                    return print("=== ${message.length - 500} more characters ===")
                }
            }
        }

        val okHttpClientBuilder = OkHttpClient.Builder()
            .dispatcher(dispatcher)
            .addInterceptor(addHeadersInterceptor)
            .addInterceptor(checkRefreshTokenInterceptor)
            .connectTimeout(1, TimeUnit.MINUTES)
            .callTimeout(1, TimeUnit.MINUTES)
            .readTimeout(1, TimeUnit.MINUTES)
            .writeTimeout(1, TimeUnit.MINUTES)

        return if(BuildConfig.DEBUG) {
            val logging = HttpLoggingInterceptor(jsonPrettyPrinter)
            logging.level = HttpLoggingInterceptor.Level.BODY
            //logging.level = HttpLoggingInterceptor.Level.HEADERS

            okHttpClientBuilder
                .addInterceptor(logging)
                .build()
        } else {
            okHttpClientBuilder
                .build()
        }
    }

    ///////////////////////////////////////////
    // Tasky API

    @Provides
    @Singleton
    fun provideTaskyApi(
        okHttpClient: OkHttpClient,
        converterFactory: Converter.Factory,
    ): TaskyApi {

        return Retrofit.Builder()
            .baseUrl(TaskyApi.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(converterFactory)  // for serialization
            .build()
            .create()
    }

}
