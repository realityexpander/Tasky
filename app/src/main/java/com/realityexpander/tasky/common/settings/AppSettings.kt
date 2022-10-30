package com.realityexpander.tasky.common.settings

import androidx.datastore.core.DataStore
import com.realityexpander.tasky.domain.AuthInfo
import kotlinx.serialization.Serializable

@Serializable
data class AppSettings(
    val authInfo: AuthInfo = AuthInfo(),
    var settingsLoaded: Boolean = true        // allows us to check if the initial data is loaded
)

suspend fun DataStore<AppSettings>.setAuthInfo(authInfo: AuthInfo) {
    updateData { appSettings ->
        appSettings.copy(authInfo = authInfo)
    }
}

suspend fun DataStore<AppSettings>.setFirstTime(firstTime: Boolean) {
    updateData { appSettings ->
        appSettings.copy(settingsLoaded = firstTime)
    }
}

