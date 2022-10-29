package com.realityexpander.tasky.data.repository.remote

import com.realityexpander.tasky.common.AuthToken
import com.realityexpander.tasky.common.UserId
import com.realityexpander.tasky.common.Username
import com.squareup.moshi.Json

// Authentication response from the server
data class AuthInfoDTO(
    @field:Json(name="token") val authToken: AuthToken? = null,
    val userId: UserId? = null,
    @field:Json(name="fullName") val username: Username? = null,
)


