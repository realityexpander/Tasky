package com.realityexpander.tasky.auth_feature.data.repository.local.entities

import com.realityexpander.tasky.core.util.AccessToken
import com.realityexpander.tasky.core.util.Email
import com.realityexpander.tasky.core.util.UserId
import com.realityexpander.tasky.core.util.Username

data class AuthInfoEntity(
    val accessToken: AccessToken? = null,
    val refreshToken: String? = null,
    val accessTokenExpirationTimestampEpochMilli: Long? = null,
    val userId: UserId? = null,
    val username: Username? = null,
    val email: Email? = null,
)
