package com.realityexpander.tasky.auth_feature.data.repository.local.entities

import com.realityexpander.tasky.core.util.AuthToken
import com.realityexpander.tasky.core.util.UserId
import com.realityexpander.tasky.core.util.Username

data class AuthInfoEntity(
    val authToken: AuthToken? = null,
    val userId: UserId? = null,
    val username: Username? = null,
)