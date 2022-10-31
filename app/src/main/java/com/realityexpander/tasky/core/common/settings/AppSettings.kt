package com.realityexpander.tasky.core.common.settings

import androidx.datastore.core.DataStore
import com.realityexpander.tasky.auth_feature.domain.AuthInfo
import kotlinx.serialization.Serializable

@Serializable
data class AppSettings(
    val authInfo: AuthInfo? = null,
    var isSettingsInitialized: Boolean = false  // allows us to check if the initial data is loaded
)

suspend fun DataStore<AppSettings>.saveAuthInfo(authInfo: AuthInfo) {
    updateData { appSettings ->
        appSettings.copy(authInfo = authInfo)
    }
}

suspend fun DataStore<AppSettings>.setSettingsInitialized(firstTime: Boolean) {
    updateData { appSettings ->
        appSettings.copy(isSettingsInitialized = firstTime)
    }
}

