@file:Suppress("PackageName")
package com.realityexpander.tasky.auth_feature.data.repository.remote.DTOs.auth

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable

@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class RefreshTokenRequestDTO(
    val refreshToken: String? = null,
    val userId: String? = null
)
