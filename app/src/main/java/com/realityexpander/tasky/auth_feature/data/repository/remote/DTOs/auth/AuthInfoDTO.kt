package com.realityexpander.tasky.auth_feature.data.repository.remote.DTOs.auth

import com.realityexpander.tasky.core.util.AccessToken
import com.realityexpander.tasky.core.util.UserId
import com.realityexpander.tasky.core.util.Username
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.JsonNames

@Serializable
@InternalSerializationApi // Authentication response from the server
@OptIn(ExperimentalSerializationApi::class)
data class AuthInfoDTO(
    val accessToken: AccessToken? = null,
    @JsonNames("accessTokenExpirationTimestamp")
    val accessTokenExpirationTimestampEpochMilli: Long? = null,
    val refreshToken: String? = null,
    val userId: UserId? = null,
    @JsonNames("fullName")
    val username: Username? = null,
    @Transient
    val email: String? = null,
)

