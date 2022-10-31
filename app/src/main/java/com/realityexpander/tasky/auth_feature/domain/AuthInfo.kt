package com.realityexpander.tasky.auth_feature.domain

import android.os.Parcelable
import com.realityexpander.tasky.core.common.AuthToken
import com.realityexpander.tasky.core.common.UserId
import com.realityexpander.tasky.core.common.Username
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable


@Parcelize
@Serializable
data class AuthInfo(
    val authToken: AuthToken? = null,
    val userId: UserId? = null,
    val username: Username? = null,
): Parcelable