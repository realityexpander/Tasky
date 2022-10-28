package com.realityexpander.tasky.di

import com.realityexpander.tasky.data.repository.AuthRepositoryFakeImpl
import com.realityexpander.tasky.data.repository.remote.AuthApiFakeImpl
import com.realityexpander.tasky.data.repository.local.AuthDaoFakeImpl
import com.realityexpander.tasky.data.repository.remote.TaskyApi
import com.realityexpander.tasky.domain.IAuthRepository
import com.realityexpander.tasky.domain.validation.*
import com.realityexpander.tasky.domain.validation.validateEmail.EmailMatcherImpl
import com.realityexpander.tasky.domain.validation.validateEmail.IValidateEmail
import com.realityexpander.tasky.domain.validation.validateEmail.ValidateEmailImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideTaskyApi(): TaskyApi {
        return Retrofit.Builder()
            .baseUrl(TaskyApi.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())  // json->kotlin data classes
            .build()
            .create()
    }


    @Provides
    @Singleton
    fun provideAuthRepository(): IAuthRepository =
        AuthRepositoryFakeImpl(
            authApi = AuthApiFakeImpl(),
            authDao = AuthDaoFakeImpl(),
            validateUsername = ValidateUsername(),
            validateEmail = ValidateEmailImpl(EmailMatcherImpl()),
            validatePassword = ValidatePassword()
        )

    @Provides
    @Singleton
    fun provideValidateEmail(): IValidateEmail =
        ValidateEmailImpl(emailMatcher = EmailMatcherImpl())

    @Provides
    @Singleton
    fun provideValidatePassword(): ValidatePassword = ValidatePassword()

    @Provides
    @Singleton
    fun provideValidateUsername(): ValidateUsername = ValidateUsername()

}