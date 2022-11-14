package com.realityexpander.tasky.di

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.realityexpander.tasky.BuildConfig
import com.realityexpander.tasky.agenda_feature.data.repositories.TaskyDatabase
import com.realityexpander.tasky.agenda_feature.data.repositories.agendaRepository.agendaRepositoryImpls.AgendaRepositoryImpl
import com.realityexpander.tasky.agenda_feature.data.repositories.agendaRepository.remote.AgendaApiImpl
import com.realityexpander.tasky.agenda_feature.data.repositories.agendaRepository.remote.IAgendaApi
import com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.eventRepositoryImpls.EventRepositoryImpl
import com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.local.eventDao.IEventDao
import com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.remote.eventApi.IEventApi
import com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.remote.eventApi.eventApiImpls.EventApiImpl
import com.realityexpander.tasky.agenda_feature.domain.IAgendaRepository
import com.realityexpander.tasky.agenda_feature.domain.IEventRepository
import com.realityexpander.tasky.auth_feature.data.repository.authRepositoryImpls.AuthRepositoryFakeImpl
import com.realityexpander.tasky.auth_feature.data.repository.authRepositoryImpls.AuthRepositoryImpl
import com.realityexpander.tasky.auth_feature.data.repository.local.IAuthDao
import com.realityexpander.tasky.auth_feature.data.repository.local.authDaoImpls.AuthDaoImpl
import com.realityexpander.tasky.auth_feature.data.repository.remote.IAuthApi
import com.realityexpander.tasky.auth_feature.data.repository.remote.authApiImpls.AuthApiFakeImpl
import com.realityexpander.tasky.auth_feature.data.repository.remote.authApiImpls.AuthApiImpl
import com.realityexpander.tasky.auth_feature.data.repository.remote.util.createAuthorizationHeader
import com.realityexpander.tasky.auth_feature.domain.IAuthRepository
import com.realityexpander.tasky.auth_feature.domain.validation.ValidateEmail
import com.realityexpander.tasky.auth_feature.domain.validation.ValidatePassword
import com.realityexpander.tasky.auth_feature.domain.validation.ValidateUsername
import com.realityexpander.tasky.core.data.remote.TaskyApi
import com.realityexpander.tasky.core.data.remote.TaskyApi.Companion.API_KEY
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Named
import javax.inject.Qualifier
import javax.inject.Singleton

const val USE_FAKE_REPOSITORY = false

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @OptIn(ExperimentalSerializationApi::class)
    @Provides
    @Singleton
    fun provideKotlinSerialization(): Converter.Factory {
        val contentType = "application/json".toMediaType()
        val json = Json {
            ignoreUnknownKeys = true
            isLenient = true
        }

        return json.asConverterFactory(contentType)
    }

    @Provides
    @Singleton
    fun provideTaskyApi(
        converterFactory: Converter.Factory,
        @AuthDaoProdUsingBinds authDao: IAuthDao,
    ): TaskyApi {

        val addHeadersInterceptor = Interceptor { chain ->

            runBlocking(Dispatchers.IO) {

                val requestBuilder = chain.request().newBuilder()
                    .addHeader("x-api-key", API_KEY)

                // Check for a valid AuthToken in the IAuthApi Companion object.
                //   If not valid, attempt to get it from the AuthDao.
                //   If valid, set it in the IAuthApi Companion object, for faster access.
                if(IAuthApi.authToken == null) {
                    val authToken = authDao.getAuthToken() // could take a while.
                    if(authToken != null) {
                        IAuthApi.authToken = authToken
                    }
                }

                // If AuthToken is valid, add it to the request.
                IAuthApi.authToken?.let { authToken ->
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
                if (message.length > 500)
                    return print("=== more than 500 characters ===")

                if (message.startsWith("{") || message.startsWith("[")) try {
                    JSONObject(message).toString(2).also(::print)
                } catch (e: JSONException) {
                    print(message)
                }
                else print(message)
            }
        }

        val client = if(BuildConfig.DEBUG) {
            val logging = HttpLoggingInterceptor(jsonPrettyPrinter)
            logging.level = HttpLoggingInterceptor.Level.BODY
//            logging.level = HttpLoggingInterceptor.Level.HEADERS

            OkHttpClient.Builder()
                .addInterceptor(addHeadersInterceptor)
                .addInterceptor(logging)
                .build()
        } else {
            OkHttpClient.Builder()
                .addInterceptor(addHeadersInterceptor)
                .build()
        }

        return Retrofit.Builder()
            .baseUrl(TaskyApi.BASE_URL)
            .client(client)
            .addConverterFactory(converterFactory)
            .build()
            .create()
    }

    ////////// AUTHENTICATION REPOSITORY //////////

    @Provides
    @Singleton
    fun provideValidateEmail(): ValidateEmail = ValidateEmail()

    @Provides
    @Singleton
    fun provideValidatePassword(): ValidatePassword = ValidatePassword()

    @Provides
    @Singleton
    fun provideValidateUsername(): ValidateUsername = ValidateUsername()

    @Provides
    @Singleton
    @AuthRepositoryFakeUsingProvides
    fun provideAuthRepositoryFake(
        @AuthApiFakeUsingBinds authApi: IAuthApi, // if authApi is not passed in, it will use the @annotated implementation
        @AuthDaoFakeUsingBinds authDao: IAuthDao, // if authDao is not passed in, it will use the @annotated implementation
        validateUsername: ValidateUsername,
        validateEmail: ValidateEmail,
        validatePassword: ValidatePassword
    ): IAuthRepository =
        AuthRepositoryFakeImpl(
            authApi = authApi,
            authDao = authDao,
            validateUsername = validateUsername,
            validateEmail = validateEmail,
            validatePassword = validatePassword
        )

    @Provides
    @Singleton
    @AuthRepositoryProdUsingProvides
    fun provideAuthRepositoryProd(
        @AuthApiProdUsingBinds authApi: IAuthApi, // if authApi is not passed in, it will use the @annotated implementation
        @AuthDaoProdUsingBinds authDao: IAuthDao, // if authDao is not passed in, it will use the @annotated implementation
        validateUsername: ValidateUsername,
        validateEmail: ValidateEmail,
        validatePassword: ValidatePassword
    ): IAuthRepository =
        AuthRepositoryImpl(
            authApi = authApi,
            authDao = authDao,
            validateUsername = validateUsername,
            validateEmail = validateEmail,
            validatePassword = validatePassword
        )

    @Provides
    @Singleton
    // Hilt chooses this one as the `default` implementation because its not annotated with @Named
    fun provideAuthRepository(
        @AuthApiFakeUsingBinds authApiFake: IAuthApi,
        @AuthApiProdUsingBinds authApiProd: IAuthApi,

        @AuthDaoFakeUsingBinds authDaoFake: IAuthDao,
        @AuthDaoProdUsingBinds authDaoProd: IAuthDao,

        validateEmail: ValidateEmail,
        validatePassword: ValidatePassword,
        validateUsername: ValidateUsername,
    ): IAuthRepository {
        // This function calls the `provideAuthRepositoryXXXX` functions above,
        //   depending on if we are using the `Fake` or `Prod` implementation.

        if (USE_FAKE_REPOSITORY) {
            // Since we are using the @annotated parameters, these values will be passed
            //   into the provideAuthRepositoryFake function. It will override the @annotations
            //   in the function signature and use these values instead.
            return provideAuthRepositoryFake(
                authApi = authApiFake,
                authDao = authDaoFake,
                validateUsername = validateUsername,
                validatePassword = validatePassword,
                validateEmail = validateEmail,
            )
        } else {
            // Since we are using the @annotated parameters, these values will be passed
            //   into the provideAuthRepositoryProd function. It will override the @annotations
            //   in the function signature and use these values instead.
            return provideAuthRepositoryProd(
                authApi = authApiProd,
                authDao = authDaoProd,
                validateUsername = validateUsername,
                validatePassword = validatePassword,
                validateEmail = validateEmail
            )
        }
    }


    /////////// DATABASE ///////////

    @Provides
    @Singleton
    fun provideTaskyDatabase(
        @ApplicationContext context: Context
    ): TaskyDatabase =
        Room.databaseBuilder(
            context,
            TaskyDatabase::class.java,
            TaskyDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()


    /////////// AGENDA REPOSITORY ///////////

    @Provides
    @Singleton
    fun provideAgendaApi(
        taskyApi: TaskyApi
    ) : IAgendaApi =
        AgendaApiImpl(taskyApi)


    @Provides
    @Singleton
    fun provideAgendaRepository(
        eventRepository: IEventRepository,
//        taskRepository: ITaskRepository,              // todo implement soon
//        reminderRepository: IReminderRepository,      // todo implement soon
        agendaApi: IAgendaApi,
    ): IAgendaRepository =
        AgendaRepositoryImpl(
            eventRepository,
            agendaApi
        )

    /////////// EVENTS REPOSITORY ///////////

    @Provides
    @Singleton
    fun provideEventDaoProd(
        taskyDatabase: TaskyDatabase
    ): IEventDao =
        taskyDatabase.eventDao()

    @Provides
    @Singleton
    fun provideEventApiProd(taskyApi: TaskyApi): IEventApi =
        EventApiImpl(taskyApi)

    @Provides
    @Singleton
    fun provideEventRepository(
        eventDao: IEventDao,
        eventApi: IEventApi
    ): IEventRepository =
        EventRepositoryImpl(eventDao, eventApi)


    //////////////////////////////////////
    /// Unused but left here for reference

    @Provides
    @Singleton
    @AuthDaoProdUsingProvides
    fun provideAuthDaoProd(
        @ApplicationContext context: Context
    ): IAuthDao {
        return AuthDaoImpl(context)
    }

    @Provides
    @Singleton
    @AuthApiFakeUsingProvides
    fun provideAuthApiFakeImpl(
        /* no dependencies */
    ): IAuthApi = AuthApiFakeImpl()

    @Provides
    @Singleton
    @AuthApiProdUsingProvides
    fun provideAuthApiProd(
        taskyApi: TaskyApi,
    ): IAuthApi = AuthApiImpl(taskyApi)

    ////////// AGENDA REPOSITORY //////////
}

@Qualifier
@Named("AuthDao.FAKE.usingBinds")
annotation class AuthDaoFakeUsingBinds

@Qualifier
@Named("AuthDao.PROD.usingBinds")
annotation class AuthDaoProdUsingBinds

@Qualifier
@Named("AuthDao.FAKE.usingProvides")
annotation class AuthDaoFakeUsingProvides

@Qualifier
@Named("AuthDao.PROD.usingProvides")
annotation class AuthDaoProdUsingProvides

@Qualifier
@Named("AuthApi.FAKE.usingBinds")
annotation class AuthApiFakeUsingBinds

@Qualifier
@Named("AuthApi.PROD.usingBinds")
annotation class AuthApiProdUsingBinds

@Qualifier
@Named("AuthApi.FAKE.usingProvides")
annotation class AuthApiFakeUsingProvides

@Qualifier
@Named("AuthApi.PROD.usingProvides")
annotation class AuthApiProdUsingProvides

@Qualifier
@Named("AuthRepository.FAKE.usingProvides")
annotation class AuthRepositoryFakeUsingProvides

@Qualifier
@Named("AuthRepository.PROD.usingProvides")
annotation class AuthRepositoryProdUsingProvides

@Qualifier
@Named("AuthRepository.FAKE.usingBinds")
annotation class AuthRepositoryFakeUsingBinds

@Qualifier
@Named("AuthRepository.PROD.usingBinds")
annotation class AuthRepositoryProdUsingBinds
