package com.realityexpander.tasky.auth_feature.data.repository.remote.DTOs.auth

import com.realityexpander.tasky.core.util.AuthToken
import com.realityexpander.tasky.core.util.UserId
import com.realityexpander.tasky.core.util.Username
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
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
    @Transient
    val email: String? = null,
)