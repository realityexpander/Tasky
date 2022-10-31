package com.realityexpander.tasky.di

import com.realityexpander.tasky.auth_feature.data.repository.authRepositoryImpls.AuthRepositoryImpl
import com.realityexpander.tasky.auth_feature.data.repository.local.IAuthDao
import com.realityexpander.tasky.auth_feature.data.repository.local.authDaoImpls.AuthDaoFakeImpl
import com.realityexpander.tasky.auth_feature.data.repository.remote.IAuthApi
import com.realityexpander.tasky.auth_feature.data.repository.remote.authApiImpls.AuthApiFakeImpl
import com.realityexpander.tasky.auth_feature.data.repository.remote.authApiImpls.AuthApiImpl
import com.realityexpander.tasky.auth_feature.domain.IAuthRepository
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
    @AuthDaoFakeUsingBinds
    abstract fun bindAuthDaoFake(
        authDaoFakeImpl: AuthDaoFakeImpl  // <-- provides this instance...
    ): IAuthDao // <-- ... for this interface.

    // todo: to be added later when database is implemented
//    @Binds
//    @Singleton
//    @AuthDaoProdUsingBinds
//    abstract fun bindAuthDaoProd(
//        authDaoImpl: AuthDaoImpl  // <-- provides this instance...
//    ): IAuthDao // <-- ... for this interface.

    @Binds
    @Singleton
    @AuthApiFakeUsingBinds
    abstract fun bindAuthApiFake(
        authApiFakeImpl: AuthApiFakeImpl // <-- provides this instance...
    ): IAuthApi // <-- ... for this interface.

    @Binds
    @Singleton
    @AuthApiProdUsingBinds
    abstract fun bindAuthApiProd(
        authApiImpl: AuthApiImpl // <-- provides this instance...
    ): IAuthApi // <-- ... for this interface.

    @Binds
    @Singleton
    @AuthRepositoryProdUsingBinds
    abstract fun bindAuthRepositoryProd(
        authRepositoryImpl: AuthRepositoryImpl // <-- provides this instance...
    ): IAuthRepository // <-- ... for this interface.
}
