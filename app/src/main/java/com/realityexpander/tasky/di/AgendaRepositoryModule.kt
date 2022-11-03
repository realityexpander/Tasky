package com.realityexpander.tasky.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
abstract class AgendaRepositoryModule {

//    @Binds
//    @Singleton
//    @AgendaDaoFakeUsingBinds
//    abstract fun bindAuthDaoFake(
//        authDaoFakeImpl: AuthDaoFakeImpl  // <-- provides this instance...
//    ): IAuthDao // <-- ... for this interface.

}