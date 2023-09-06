package com.realityexpander.tasky.di

import com.realityexpander.tasky.auth_feature.data.repository.authRepositoryImpls.AuthRepositoryFake
import com.realityexpander.tasky.auth_feature.data.repository.authRepositoryImpls.AuthRepositoryImpl
import com.realityexpander.tasky.auth_feature.data.repository.local.IAuthDao
import com.realityexpander.tasky.auth_feature.data.repository.local.authDaoImpls.AuthDaoFake
import com.realityexpander.tasky.auth_feature.data.repository.local.authDaoImpls.AuthDaoImpl
import com.realityexpander.tasky.auth_feature.data.repository.remote.IAuthApi
import com.realityexpander.tasky.auth_feature.data.repository.remote.authApiImpls.AuthApiFake
import com.realityexpander.tasky.auth_feature.data.repository.remote.authApiImpls.AuthApiImpl
import com.realityexpander.tasky.auth_feature.domain.IAuthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthRepositoryModule {

    @Binds
    @Singleton
    @AuthDaoFakeUsingBinds
    abstract fun bindAuthDaoFake(
        authDaoFake: AuthDaoFake  // <-- provides this instance...
    ): IAuthDao // <-- ... for this interface.

    @Binds
    @Singleton
    @AuthDaoProdUsingBinds
    abstract fun bindAuthDaoProd(
        authDaoProdImpl: AuthDaoImpl  // <-- provides this instance...
    ): IAuthDao // <-- ... for this interface.

    @Binds
    @Singleton
    @AuthApiFakeUsingBinds
    abstract fun bindAuthApiFake(
        authApiFake: AuthApiFake // <-- provides this instance...
    ): IAuthApi // <-- ... for this interface.

    @Binds
    @Singleton
    @AuthApiProdUsingBinds
    abstract fun bindAuthApiProd(
        authApiProdImpl: AuthApiImpl // <-- provides this instance...
    ): IAuthApi // <-- ... for this interface.

    @Binds
    @Singleton
    @AuthRepositoryFakeUsingBinds
    abstract fun bindAuthRepositoryFake(
        authRepositoryFake: AuthRepositoryFake // <-- provides this instance...
    ): IAuthRepository // <-- ... for this interface.

    @Binds
    @Singleton
    @AuthRepositoryProdUsingBinds
    abstract fun bindAuthRepositoryProd(
        authRepositoryProdImpl: AuthRepositoryImpl // <-- provides this instance...
    ): IAuthRepository // <-- ... for this interface.
}
