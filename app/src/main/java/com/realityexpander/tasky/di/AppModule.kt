package com.realityexpander.tasky.di

import com.realityexpander.tasky.data.repository.AuthRepositoryImpl
import com.realityexpander.tasky.data.repository.remote.AuthApiImpl
import com.realityexpander.tasky.data.repository.local.AuthDaoImpl
import com.realityexpander.tasky.data.validation.EmailMatcherImpl
import com.realityexpander.tasky.data.validation.ValidateEmailImpl
import com.realityexpander.tasky.domain.IAuthRepository
import com.realityexpander.tasky.domain.validation.IValidateEmail
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAuthRepository(): IAuthRepository =
        AuthRepositoryImpl(
            authApi = AuthApiImpl(),
            authDao = AuthDaoImpl(),
            validateEmail = ValidateEmailImpl(EmailMatcherImpl())
        )

    @Provides
    @Singleton
    fun provideValidateEmail(): IValidateEmail =
        ValidateEmailImpl(emailMatcher = EmailMatcherImpl())

}