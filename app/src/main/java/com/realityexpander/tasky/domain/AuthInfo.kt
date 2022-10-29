package com.realityexpander.tasky.domain

import com.realityexpander.tasky.common.AuthToken
import com.realityexpander.tasky.common.UserId
import com.realityexpander.tasky.common.Username

data class AuthInfo(
    val authToken: AuthToken? = null,
    val userId: UserId? = null,
    val username: Username? = null,
)