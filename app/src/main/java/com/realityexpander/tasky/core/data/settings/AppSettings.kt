package com.realityexpander.tasky.core.data.settings

import com.realityexpander.tasky.auth_feature.domain.AuthInfo
import kotlinx.serialization.Serializable

@Serializable
data class AppSettings(
    val authInfo: AuthInfo? = null,
    var isSettingsInitialized: Boolean = false  // allows us to check if the settings is loaded
)

//suspend fun DataStore<AppSettings>.saveAuthInfo(authInfo: AuthInfo) {
//    updateData { appSettings ->
//        appSettings.copy(authInfo = authInfo)
//    }
//}
//
//suspend fun DataStore<AppSettings>.saveSettingsInitialized(firstTime: Boolean) {
//    updateData { appSettings ->
//        appSettings.copy(isSettingsInitialized = firstTime)
//    }
//}

//class AppSettingsRepositoryImpl @Inject constructor(
//    override val dataStore: DataStore<AppSettings>
//) : IAppSettingsRepository {
//
//    override suspend fun saveAuthInfo(authInfo: AuthInfo) {
//        dataStore.updateData { appSettings ->
//            appSettings.copy(authInfo = authInfo)
//        }
//    }
//
//    override suspend fun saveSettingsIsInitialized(firstTime: Boolean) {
//        dataStore.updateData { appSettings ->
//            appSettings.copy(isSettingsInitialized = firstTime)
//        }
//    }
//}