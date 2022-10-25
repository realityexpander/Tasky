package com.realityexpander.tasky.di

import com.realityexpander.tasky.data.repository.AuthRepositoryFakeImpl
import com.realityexpander.tasky.data.repository.remote.AuthApiFakeImpl
import com.realityexpander.tasky.data.repository.local.AuthDaoFakeImpl
import com.realityexpander.tasky.domain.IAuthRepository
import com.realityexpander.tasky.domain.validation.*
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
        AuthRepositoryFakeImpl(
            authApi = AuthApiFakeImpl(),
            authDao = AuthDaoFakeImpl(),
            validateEmail = ValidateEmailImpl(EmailMatcherImpl())
        )

    @Provides
    @Singleton
    fun provideValidateEmail(): IValidateEmail =
        ValidateEmailImpl(emailMatcher = EmailMatcherImpl())

    @Provides
    @Singleton
    fun provideValidatePassword(): ValidatePassword = ValidatePassword()

}