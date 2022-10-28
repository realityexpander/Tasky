package com.realityexpander.tasky.data.repository

import com.realityexpander.tasky.common.AuthToken
import com.realityexpander.tasky.common.UserId
import com.realityexpander.tasky.common.Username
import com.squareup.moshi.Json

// Authentication response from the server
data class AuthInfo(
    val token: AuthToken,
    val userId: UserId,
    @field:Json(name="fullName") val username: Username
)