package com.realityexpander.tasky.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.realityexpander.tasky.BuildConfig
import com.realityexpander.tasky.BuildConfig.API_KEY
import com.realityexpander.tasky.auth_feature.data.repository.authRepositoryImpls.AuthRepositoryFakeImpl
import com.realityexpander.tasky.auth_feature.data.repository.authRepositoryImpls.AuthRepositoryImpl
import com.realityexpander.tasky.auth_feature.data.repository.local.IAuthDao
import com.realityexpander.tasky.auth_feature.data.repository.local.authDaoImpls.AuthDaoFakeImpl
import com.realityexpander.tasky.auth_feature.data.repository.remote.IAuthApi
import com.realityexpander.tasky.auth_feature.data.repository.remote.authApiImpls.AuthApiFakeImpl
import com.realityexpander.tasky.auth_feature.data.repository.remote.authApiImpls.AuthApiImpl
import com.realityexpander.tasky.auth_feature.data.repository.remote.authApiImpls.TaskyApi
import com.realityexpander.tasky.auth_feature.domain.IAuthRepository
import com.realityexpander.tasky.auth_feature.domain.validation.ValidatePassword
import com.realityexpander.tasky.auth_feature.domain.validation.ValidateUsername
import com.realityexpander.tasky.auth_feature.domain.validation.validateEmail.IValidateEmail
import com.realityexpander.tasky.auth_feature.domain.validation.validateEmail.validateEmailImpls.ValidateEmailAndroidImpl
import com.realityexpander.tasky.auth_feature.domain.validation.validateEmail.emailMatcherImpls.EmailMatcherAndroidImpl
import com.realityexpander.tasky.auth_feature.domain.validation.validateEmail.emailMatcherImpls.EmailMatcherRegexImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
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
    fun provideTaskyApi(converterFactory: Converter.Factory): TaskyApi {

        val xApiKeyHeader = Interceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("x-api-key", API_KEY)
                .build()
            chain.proceed(request)
        }

        val client = if(BuildConfig.DEBUG) {
            val logging = HttpLoggingInterceptor()
//            logging.level = HttpLoggingInterceptor.Level.BODY
            logging.level = HttpLoggingInterceptor.Level.HEADERS

            OkHttpClient.Builder()
                .addInterceptor(xApiKeyHeader)
                .addInterceptor(logging)
                .build()
        } else {
            OkHttpClient.Builder()
                .addInterceptor(xApiKeyHeader)
                .build()
        }

        return Retrofit.Builder()
            .baseUrl(TaskyApi.BASE_URL)
            .client(client)
            .addConverterFactory(converterFactory)
            .build()
            .create()
    }

    @Provides
    @Singleton
    @AuthApiFakeUsingProvides
    fun provideAuthApiFakeImpl(
        /* no dependencies */
    ): IAuthApi = AuthApiFakeImpl()

    // Is there a way to have Hilt instantiate `TaskyApi` using @Binds?
    // Or is this the only way to instantiate a dependency for when creating an
    //   implementation of an interface that requires an argument?
    @Provides
    @Singleton
    @AuthApiProdUsingProvides
    fun provideAuthApiProd(
        taskyApi: TaskyApi,
    ): IAuthApi = AuthApiImpl(taskyApi)

    @Provides
    @Singleton
    @Named("ValidateEmailAndroid")
    fun provideValidateEmailAndroid(): IValidateEmail =
        ValidateEmailAndroidImpl(emailMatcher = EmailMatcherAndroidImpl())

    @Provides
    @Singleton
    @Named("ValidateEmailRegex")
    fun provideValidateEmailRegex(): IValidateEmail =
        ValidateEmailAndroidImpl(emailMatcher = EmailMatcherRegexImpl())

    @Provides
    @Singleton
    fun provideValidatePassword(): ValidatePassword = ValidatePassword()

    @Provides
    @Singleton
    fun provideValidateUsername(): ValidateUsername = ValidateUsername()

    @Provides
    @Singleton
    fun provideAuthRepository(converterFactory: Converter.Factory): IAuthRepository {
        if (USE_FAKE_REPOSITORY) {
            return provideAuthRepositoryFake(
                authApi = provideAuthApiFakeImpl(),
                authDao = AuthDaoFakeImpl(),
                validateUsername = provideValidateUsername(),
                validatePassword = provideValidatePassword(),
                validateEmail = provideValidateEmailRegex(),
            )
        } else {
            return provideAuthRepositoryProd(
                authApi = provideAuthApiProd(
                    provideTaskyApi(converterFactory)
                ),
                authDao = AuthDaoFakeImpl(),  // why cant we use the implementation from @Binds here?
                validateUsername = provideValidateUsername(),
                validatePassword = provideValidatePassword(),
                validateEmail = provideValidateEmailRegex(),
            )
        }
    }

    @Provides
    @Singleton
    @AuthRepositoryProd_AuthApiProd_AuthDaoFake
    fun provideAuthRepositoryProd(
//        @AuthApiProdUsingBinds authApi: IAuthApi,  // How to add `TaskApi` to the `authApi`? (instead of using @AuthApiProdUsingProvides above)
        @AuthApiProdUsingProvides authApi: IAuthApi,
        @AuthDaoFakeUsingBinds authDao: IAuthDao,
        validateUsername: ValidateUsername,
        @Named("ValidateEmailRegex") validateEmail: IValidateEmail,
        validatePassword: ValidatePassword
    ): IAuthRepository =
        AuthRepositoryImpl(
            authApi = authApi,
            authDao = authDao, // use fake repo until we implement database
            validateUsername = validateUsername,
            validateEmail = validateEmail,
            validatePassword = validatePassword
        )

    @Provides
    @Singleton
    @AuthRepositoryFakeUsingProvides
    fun provideAuthRepositoryFake(
        @AuthApiFakeUsingBinds authApi: IAuthApi,
        @AuthDaoFakeUsingBinds authDao: IAuthDao,
        validateUsername: ValidateUsername,
        @Named("ValidateEmailRegex") validateEmail: IValidateEmail,
        validatePassword: ValidatePassword
    ): IAuthRepository =
        AuthRepositoryFakeImpl(
            authApi = authApi,
            authDao = authDao,
            validateUsername = validateUsername,
            validateEmail = validateEmail,
            validatePassword = validatePassword
        )
}

@Qualifier
@Named("AuthDao.FAKE.usingBinds")
annotation class AuthDaoFakeUsingBinds

@Qualifier
@Named("AuthDao.PROD.usingBinds")
annotation class AuthDaoProdUsingBinds

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
@Named("AuthRepository.PROD.usingBinds")
annotation class AuthRepositoryProdUsingBinds

@Qualifier
@Named("AuthRepository.PROD w/ AuthApi.PROD, AuthDao.FAKE")
annotation class AuthRepositoryProd_AuthApiProd_AuthDaoFake