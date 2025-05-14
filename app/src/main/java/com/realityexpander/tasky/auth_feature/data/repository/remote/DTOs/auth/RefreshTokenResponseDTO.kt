package com.realityexpander.tasky.auth_feature.data.repository.remote.DTOs.auth

import com.realityexpander.tasky.core.util.AccessToken
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
@InternalSerializationApi // Authentication response from the server
@OptIn(ExperimentalSerializationApi::class)
data class RefreshTokenResponseDTO(
    val accessToken: AccessToken,
    @JsonNames("expirationTimestamp")
    val accessTokenExpirationTimestampEpochMilli: Long
)
