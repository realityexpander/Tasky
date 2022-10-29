package com.realityexpander.tasky.data.repository.local

import com.realityexpander.tasky.common.AuthToken
import com.realityexpander.tasky.common.UserId
import com.realityexpander.tasky.common.Username

data class AuthInfoEntity(
    val authToken: AuthToken? = null,
    val userId: UserId? = null,
    val username: Username? = null,
)