package com.realityexpander.tasky.auth_feature.data.repository.local.entities

import com.realityexpander.tasky.core.common.AuthToken
import com.realityexpander.tasky.core.common.UserId
import com.realityexpander.tasky.core.common.Username

data class AuthInfoEntity(
    val authToken: AuthToken? = null,
    val userId: UserId? = null,
    val username: Username? = null,
)