@file:OptIn(kotlinx.serialization.InternalSerializationApi::class)
package com.realityexpander.tasky.core.data.settings

import androidx.datastore.core.DataStore
import com.realityexpander.tasky.auth_feature.domain.AuthInfo
import com.realityexpander.tasky.core.data.settings.AppSettings
import com.realityexpander.tasky.core.domain.IAppSettingsRepository
import kotlinx.coroutines.flow.flow

class AppSettingsRepositoryFake : IAppSettingsRepository {

    override val dataStore: DataStore<AppSettings>
        get() = DataStoreFake(flow {
            emit(AppSettings())
        })

    override suspend fun saveAuthInfo(authInfo: AuthInfo) {
        // do nothing
    }

    override suspend fun getAppSettings(): AppSettings {
        return AppSettings()
    }

    override suspend fun saveIsSettingsInitialized(firstTime: Boolean) {
        // do nothing
    }
}
