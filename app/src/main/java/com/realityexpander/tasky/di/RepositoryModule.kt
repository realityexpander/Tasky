package com.realityexpander.tasky.di

import com.realityexpander.tasky.data.repository.AuthRepositoryImpl
import com.realityexpander.tasky.data.repository.local.AuthDaoFakeImpl
import com.realityexpander.tasky.data.repository.local.IAuthDao
import com.realityexpander.tasky.data.repository.remote.AuthApiImpl
import com.realityexpander.tasky.data.repository.remote.IAuthApi
import com.realityexpander.tasky.domain.IAuthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindIAuthDao(
        authDaoFakeImpl: AuthDaoFakeImpl
    ): IAuthDao

    @Binds
    @Singleton
    abstract fun bindIAuthApi(
        authApiImpl: AuthApiImpl
    ): IAuthApi

    @Binds
    @Singleton
    abstract fun bindIAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl // <-- provides this instance...
    ): IAuthRepository // <-- ... for this interface.
}

//@Module
//@InstallIn(SingletonComponent::class)
//class DaoModule {
//
//    @Provides
//    @Singleton
//    fun provideAuthDao(): IAuthDao = AuthDaoFakeImpl()
//}