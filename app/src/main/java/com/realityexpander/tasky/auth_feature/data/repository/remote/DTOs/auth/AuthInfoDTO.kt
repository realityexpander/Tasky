package com.realityexpander.tasky.auth_feature.data.repository.remote.DTOs.auth

import com.realityexpander.tasky.core.common.AuthToken
import com.realityexpander.tasky.core.common.UserId
import com.realityexpander.tasky.core.common.Username
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

// Authentication response from the server
@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class AuthInfoDTO(
    @JsonNames("token")
    val authToken: AuthToken? = null,
    val userId: UserId? = null,
    @JsonNames("fullName")
    val username: Username? = null,
)