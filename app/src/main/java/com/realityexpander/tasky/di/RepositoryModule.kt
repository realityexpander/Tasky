package com.realityexpander.tasky.di

import com.realityexpander.tasky.data.repository.remote.authRepositoryImpls.AuthRepositoryImpl
import com.realityexpander.tasky.data.repository.local.AuthDaoFakeImpl
import com.realityexpander.tasky.data.repository.local.IAuthDao
import com.realityexpander.tasky.data.repository.remote.authApiImpls.AuthApiImpl
import com.realityexpander.tasky.data.repository.remote.IAuthApi
import com.realityexpander.tasky.data.repository.remote.authApiImpls.AuthApiFakeImpl
import com.realityexpander.tasky.data.repository.remote.authApiImpls.TaskyApi
import com.realityexpander.tasky.domain.IAuthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    @AuthDaoFakeUsingBinds
    abstract fun bindIAuthDaoFake(
        authDaoFakeImpl: AuthDaoFakeImpl  // <-- provides this instance...
    ): IAuthDao // <-- ... for this interface.

    // todo: to be added later when database is implemented
//    @Binds
//    @Singleton
//    @AuthDaoProdUsingBinds
//    abstract fun bindIAuthDao(
//        authDaoImpl: AuthDaoImpl  // <-- provides this instance...
//    ): IAuthDao // <-- ... for this interface.

    @Binds
    @Singleton
    @AuthApiFakeUsingBinds
    abstract fun bindIAuthApiFake(
        authApiFakeImpl: AuthApiFakeImpl
    ): IAuthApi

    @Binds
    @Singleton
    @AuthApiProdUsingBinds
    abstract fun bindIAuthApi(
        authApiImpl: AuthApiImpl
    ): IAuthApi

    @Binds
    @Singleton
    @AuthRepositoryProdUsingBinds
    abstract fun bindIAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl // <-- provides this instance...
    ): IAuthRepository // <-- ... for this interface.
}
