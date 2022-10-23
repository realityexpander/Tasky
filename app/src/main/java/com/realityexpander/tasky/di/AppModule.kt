package com.realityexpander.tasky.di

import com.realityexpander.tasky.data.repository.AuthRepositoryImpl
import com.realityexpander.tasky.data.repository.local.AuthApiImpl
import com.realityexpander.tasky.data.repository.remote.AuthDaoImpl
import com.realityexpander.tasky.data.validation.ValidateEmailImpl
import com.realityexpander.tasky.domain.IAuthRepository
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
    fun provideAuthRepository(): IAuthRepository = AuthRepositoryImpl(
        authApi = AuthApiImpl(),
        authDao = AuthDaoImpl(),
        validateEmail = ValidateEmailImpl()
    )

}