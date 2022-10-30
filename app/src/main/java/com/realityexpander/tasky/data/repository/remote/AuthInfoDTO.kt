package com.realityexpander.tasky.data.repository.remote

import com.realityexpander.tasky.common.AuthToken
import com.realityexpander.tasky.common.UserId
import com.realityexpander.tasky.common.Username
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