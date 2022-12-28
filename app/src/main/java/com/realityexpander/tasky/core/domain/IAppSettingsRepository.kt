package com.realityexpander.tasky.core.domain

import androidx.datastore.core.DataStore
import com.realityexpander.tasky.auth_feature.domain.AuthInfo
import com.realityexpander.tasky.core.data.settings.AppSettings

interface IAppSettingsRepository {
    abstract val dataStore: DataStore<AppSettings>

    suspend fun saveAuthInfo(authInfo: AuthInfo)

    suspend fun getAppSettings(): AppSettings
    suspend fun saveSettingsIsInitialized(firstTime: Boolean)
}