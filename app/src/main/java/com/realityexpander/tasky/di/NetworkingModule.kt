package com.realityexpander.tasky.di

import android.util.Log
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.realityexpander.tasky.BuildConfig
import com.realityexpander.tasky.auth_feature.data.repository.local.IAuthDao
import com.realityexpander.tasky.auth_feature.data.repository.remote.IAuthApi
import com.realityexpander.tasky.auth_feature.data.repository.remote.util.createAuthorizationHeader
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

                // If AuthToken is valid, add it to the request.
                IAuthApi.getAuthToken {
                    authDao.getAuthToken() // if invalid, attempt to fetch AuthToken from the AuthDao
                }?.let { authToken ->
                    requestBuilder
                        .addHeader("Authorization", createAuthorizationHeader(authToken))
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

                if (message.length > 500)
                    return print("=== more than 500 characters ===")
            }
        }

        val okHttpClientBuilder = OkHttpClient.Builder()
            .dispatcher(dispatcher)
            .addInterceptor(addHeadersInterceptor)
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
        @AuthDaoProdUsingBinds authDao: IAuthDao,
    ): TaskyApi {

        return Retrofit.Builder()
            .baseUrl(TaskyApi.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(converterFactory)  // for serialization
            .build()
            .create()
    }

}