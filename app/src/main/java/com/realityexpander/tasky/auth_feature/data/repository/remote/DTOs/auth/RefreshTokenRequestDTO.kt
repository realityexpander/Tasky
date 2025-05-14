package com.realityexpander.tasky.auth_feature.data.repository.remote.DTOs.auth

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@Serializable
@InternalSerializationApi // Authentication response from the server
@OptIn(ExperimentalSerializationApi::class)
data class RefreshTokenRequestDTO(
    val refreshToken: String? = null,
    val userId: String? = null
)
