package com.realityexpander.tasky.core.data.settings

import com.realityexpander.tasky.auth_feature.domain.AuthInfo
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@Serializable
@InternalSerializationApi
data class AppSettings(
    val authInfo: AuthInfo? = null,
    var isSettingsInitialized: Boolean = false  // allows us to check if the settings is loaded
)
