@file:OptIn(kotlinx.serialization.InternalSerializationApi::class)
package com.realityexpander.tasky.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.room.Room
import com.realityexpander.tasky.agenda_feature.data.repositories.TaskyDatabase
import com.realityexpander.tasky.agenda_feature.data.repositories.agendaRepository.agendaRepositoryImpls.AgendaRepositoryImpl
import com.realityexpander.tasky.agenda_feature.data.repositories.agendaRepository.remote.AgendaApiImpl
import com.realityexpander.tasky.agenda_feature.data.repositories.agendaRepository.remote.IAgendaApi
import com.realityexpander.tasky.agenda_feature.data.repositories.attendeeRepository.IAttendeeRepository
import com.realityexpander.tasky.agenda_feature.data.repositories.attendeeRepository.attendeeRepositoryImpls.AttendeeRepositoryImpl
import com.realityexpander.tasky.agenda_feature.data.repositories.attendeeRepository.remote.AttendeeApiImpl
import com.realityexpander.tasky.agenda_feature.data.repositories.attendeeRepository.remote.IAttendeeApi
import com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.eventRepositoryImpls.EventRepositoryImpl
import com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.local.IEventDao
import com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.remote.eventApi.IEventApi
import com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.remote.eventApi.eventApiImpls.EventApiImpl
import com.realityexpander.tasky.agenda_feature.data.repositories.reminderRepository.local.IReminderDao
import com.realityexpander.tasky.agenda_feature.data.repositories.reminderRepository.reminderRepositoryImpls.ReminderRepositoryImpl
import com.realityexpander.tasky.agenda_feature.data.repositories.reminderRepository.remote.reminderApi.IReminderApi
import com.realityexpander.tasky.agenda_feature.data.repositories.reminderRepository.remote.reminderApi.reminderApiImpls.ReminderApiImpl
import com.realityexpander.tasky.agenda_feature.data.repositories.syncRepository.ISyncRepository
import com.realityexpander.tasky.agenda_feature.data.repositories.syncRepository.local.ISyncDao
import com.realityexpander.tasky.agenda_feature.data.repositories.syncRepository.remote.ISyncApi
import com.realityexpander.tasky.agenda_feature.data.repositories.syncRepository.remote.syncApiImpls.SyncApiImpl
import com.realityexpander.tasky.agenda_feature.data.repositories.syncRepository.syncRepositoryImpls.SyncRepositoryImpl
import com.realityexpander.tasky.agenda_feature.data.repositories.taskRepository.local.ITaskDao
import com.realityexpander.tasky.agenda_feature.data.repositories.taskRepository.remote.ITaskApi
import com.realityexpander.tasky.agenda_feature.data.repositories.taskRepository.remote.taskApi.taskApiImpls.TaskApiImpl
import com.realityexpander.tasky.agenda_feature.data.repositories.taskRepository.taskRepositoryImpls.TaskRepositoryImpl
import com.realityexpander.tasky.agenda_feature.domain.*
import com.realityexpander.tasky.auth_feature.data.repository.authRepositoryImpls.AuthRepositoryFake
import com.realityexpander.tasky.auth_feature.data.repository.authRepositoryImpls.AuthRepositoryImpl
import com.realityexpander.tasky.auth_feature.data.repository.local.IAuthDao
import com.realityexpander.tasky.auth_feature.data.repository.local.authDaoImpls.AuthDaoImpl
import com.realityexpander.tasky.auth_feature.data.repository.remote.IAuthApi
import com.realityexpander.tasky.auth_feature.data.repository.remote.authApiImpls.AuthApiFake
import com.realityexpander.tasky.auth_feature.data.repository.remote.authApiImpls.AuthApiImpl
import com.realityexpander.tasky.auth_feature.domain.IAuthRepository
import com.realityexpander.tasky.auth_feature.domain.validation.ValidateEmail
import com.realityexpander.tasky.auth_feature.domain.validation.ValidatePassword
import com.realityexpander.tasky.auth_feature.domain.validation.ValidateUsername
import com.realityexpander.tasky.core.data.remote.TaskyApi
import com.realityexpander.tasky.core.data.settings.AppSettings
import com.realityexpander.tasky.core.data.settings.AppSettingsRepositoryImpl
import com.realityexpander.tasky.core.domain.IAppSettingsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import javax.inject.Named
import javax.inject.Qualifier
import javax.inject.Singleton

const val USE_FAKE_REPOSITORY = false

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // APP SETTINGS REPOSITORY /////////

    @Provides
    @Singleton
    fun provideAppSettingsRepository(
        dataStore: DataStore<AppSettings>
    ): IAppSettingsRepository = AppSettingsRepositoryImpl(dataStore)


    ////////// AUTHENTICATION REPOSITORY //////////

    @Provides
    @Singleton
    fun provideValidateEmail(): ValidateEmail = ValidateEmail()

    @Provides
    @Singleton
    fun provideValidatePassword(): ValidatePassword = ValidatePassword()

    @Provides
    @Singleton
    fun provideValidateUsername(): ValidateUsername = ValidateUsername()

    @Provides
    @Singleton
    @AuthRepositoryFakeUsingProvides
    fun provideAuthRepositoryFake(
        @AuthApiFakeUsingBinds authApi: IAuthApi, // if authApi is not passed in, it will use the @annotated implementation
        @AuthDaoFakeUsingBinds authDao: IAuthDao, // if authDao is not passed in, it will use the @annotated implementation
        validateUsername: ValidateUsername,
        validateEmail: ValidateEmail,
        validatePassword: ValidatePassword
    ): IAuthRepository =
        AuthRepositoryFake(
            authApi = authApi,
            authDao = authDao,
            validateUsername = validateUsername,
            validateEmail = validateEmail,
            validatePassword = validatePassword
        )

    @Provides
    @Singleton
    @AuthRepositoryProdUsingProvides
    fun provideAuthRepositoryProd(
        @AuthApiProdUsingBinds authApi: IAuthApi, // if authApi is not passed in, it will use the @annotated implementation
        @AuthDaoProdUsingBinds authDao: IAuthDao, // if authDao is not passed in, it will use the @annotated implementation
        validateUsername: ValidateUsername,
        validateEmail: ValidateEmail,
        validatePassword: ValidatePassword
    ): IAuthRepository =
        AuthRepositoryImpl(
            authApi = authApi,
            authDao = authDao,
            validateUsername = validateUsername,
            validateEmail = validateEmail,
            validatePassword = validatePassword
        )

    @Provides
    @Singleton
    // Hilt chooses this one as the `default` implementation because its not annotated with @Named
    fun provideAuthRepository(
        @AuthApiFakeUsingBinds authApiFake: IAuthApi,
        @AuthApiProdUsingBinds authApiProd: IAuthApi,

        @AuthDaoFakeUsingBinds authDaoFake: IAuthDao,
        @AuthDaoProdUsingBinds authDaoProd: IAuthDao,

        validateEmail: ValidateEmail,
        validatePassword: ValidatePassword,
        validateUsername: ValidateUsername,
    ): IAuthRepository {
        // This function calls the `provideAuthRepositoryXXXX` functions above,
        //   depending on if we are using the `Fake` or `Prod` implementation.

        if (USE_FAKE_REPOSITORY) {
            // Since we are using the @annotated parameters, these values will be passed
            //   into the provideAuthRepositoryFake function. It will override the @annotations
            //   in the function signature and use these values instead.
            return provideAuthRepositoryFake(
                authApi = authApiFake,
                authDao = authDaoFake,
                validateUsername = validateUsername,
                validatePassword = validatePassword,
                validateEmail = validateEmail,
            )
        } else {
            // Since we are using the @annotated parameters, these values will be passed
            //   into the provideAuthRepositoryProd function. It will override the @annotations
            //   in the function signature and use these values instead.
            return provideAuthRepositoryProd(
                authApi = authApiProd,
                authDao = authDaoProd,
                validateUsername = validateUsername,
                validatePassword = validatePassword,
                validateEmail = validateEmail
            )
        }
    }

    /////////// DATABASE ///////////

    @Provides
    @Singleton
    fun provideTaskyDatabase(
        @ApplicationContext context: Context
    ): TaskyDatabase =
        Room.databaseBuilder(
            context,
            TaskyDatabase::class.java,
            TaskyDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()


    /////////// AGENDA REPOSITORY ///////////

    @Provides
    @Singleton
    fun provideAgendaApi(
        taskyApi: TaskyApi,
        okHttpClient: OkHttpClient,
    ) : IAgendaApi =
        AgendaApiImpl(taskyApi, okHttpClient)


    @Provides
    @Singleton
    fun provideAgendaRepository(
        eventRepository: IEventRepository,
        attendeeRepository: IAttendeeRepository,
        taskRepository: ITaskRepository,
        reminderRepository: IReminderRepository,
        syncRepository: ISyncRepository,
        agendaApi: IAgendaApi,
    ): IAgendaRepository =
        AgendaRepositoryImpl(
            agendaApi = agendaApi,
            eventRepository = eventRepository,
            attendeeRepository = attendeeRepository,
            taskRepository = taskRepository,
            reminderRepository = reminderRepository,
            syncRepository = syncRepository,
        )

    /////////// EVENTS REPOSITORY ///////////

    @Provides
    @Singleton
    fun provideEventDaoProd(
        taskyDatabase: TaskyDatabase
    ): IEventDao =
        taskyDatabase.eventDao()

    @Provides
    @Singleton
    fun provideEventApiProd(
        taskyApi: TaskyApi,
        @ApplicationContext context: Context
    ): IEventApi =
        EventApiImpl(taskyApi, context)

    @Provides
    @Singleton
    fun provideEventRepository(
        eventDao: IEventDao,
        eventApi: IEventApi,
        syncRepository: ISyncRepository,
        authRepository: IAuthRepository
    ): IEventRepository =
        EventRepositoryImpl(eventDao, eventApi, syncRepository, authRepository)

    /////////// TASKS REPOSITORY ///////////

    @Provides
    @Singleton
    fun provideTaskDaoProd(
        taskyDatabase: TaskyDatabase
    ): ITaskDao =
        taskyDatabase.taskDao()

    @Provides
    @Singleton
    fun provideTaskApiProd(
        taskyApi: TaskyApi,
    ): ITaskApi =
        TaskApiImpl(taskyApi)

    @Provides
    @Singleton
    fun provideTaskRepository(
        taskDao: ITaskDao,
        taskApi: ITaskApi,
        syncRepository: ISyncRepository,
    ): ITaskRepository =
        TaskRepositoryImpl(taskDao, taskApi, syncRepository)


    /////////// REMINDER REPOSITORY ///////////

    @Provides
    @Singleton
    fun provideReminderDaoProd(
        taskyDatabase: TaskyDatabase
    ): IReminderDao =
        taskyDatabase.reminderDao()

    @Provides
    @Singleton
    fun provideReminderApiProd(
        taskyApi: TaskyApi,
    ): IReminderApi =
        ReminderApiImpl(taskyApi)

    @Provides
    @Singleton
    fun provideReminderRepository(
        reminderDao: IReminderDao,
        reminderApi: IReminderApi,
        syncRepository: ISyncRepository,
    ): IReminderRepository =
        ReminderRepositoryImpl(reminderDao, reminderApi, syncRepository)


    /////////// SYNC REPOSITORY ///////////

    @Provides
    @Singleton
    fun provideSyncDaoProd(
        taskyDatabase: TaskyDatabase
    ): ISyncDao =
        taskyDatabase.syncDao()

    @Provides
    @Singleton
    fun provideSyncApiProd(
        taskyApi: TaskyApi,
    ): ISyncApi =
        SyncApiImpl(taskyApi)

    @Provides
    @Singleton
    fun provideSyncRepository(
        syncDao: ISyncDao,
        syncApi: ISyncApi,
    ): ISyncRepository =
        SyncRepositoryImpl(syncApi, syncDao)


    /////////// ATTENDEE REPOSITORY ///////////

    @Provides
    @Singleton
    fun provideAttendeeApiProd(taskyApi: TaskyApi): IAttendeeApi =
        AttendeeApiImpl(taskyApi)

    @Provides
    @Singleton
    fun provideAttendeeRepository(
        attendeeApi: IAttendeeApi,
        validateEmail: ValidateEmail,
    ): IAttendeeRepository =
        AttendeeRepositoryImpl(
            attendeeApi = attendeeApi,
            validateEmail = validateEmail,
        )


    //////////////////////////////////////
    /// Unused but left here for reference

    @Provides
    @Singleton
    @AuthDaoProdUsingProvides
    fun provideAuthDaoProd(
        @ApplicationContext context: Context,
        appSettingsRepository: IAppSettingsRepository
    ): IAuthDao {
        return AuthDaoImpl(
            context,
            appSettingsRepository
        )
    }

    @Provides
    @Singleton
    @AuthApiFakeUsingProvides
    fun provideAuthApiFakeImpl(
        /* no dependencies */
    ): IAuthApi = AuthApiFake()

    @Provides
    @Singleton
    @AuthApiProdUsingProvides
    fun provideAuthApiProd(
        taskyApi: TaskyApi,
    ): IAuthApi = AuthApiImpl(taskyApi)

    ////////// AGENDA REPOSITORY //////////
}

@Qualifier
@Named("AuthDao.FAKE.usingBinds")
annotation class AuthDaoFakeUsingBinds

@Qualifier
@Named("AuthDao.PROD.usingBinds")
annotation class AuthDaoProdUsingBinds

@Qualifier
@Named("AuthDao.FAKE.usingProvides")
annotation class AuthDaoFakeUsingProvides

@Qualifier
@Named("AuthDao.PROD.usingProvides")
annotation class AuthDaoProdUsingProvides

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
@Named("AuthRepository.PROD.usingProvides")
annotation class AuthRepositoryProdUsingProvides

@Qualifier
@Named("AuthRepository.FAKE.usingBinds")
annotation class AuthRepositoryFakeUsingBinds

@Qualifier
@Named("AuthRepository.PROD.usingBinds")
annotation class AuthRepositoryProdUsingBinds
