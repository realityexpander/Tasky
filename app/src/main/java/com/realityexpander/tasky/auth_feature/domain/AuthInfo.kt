package com.realityexpander.tasky.auth_feature.domain

import android.os.Parcelable
import com.realityexpander.tasky.core.util.AuthToken
import com.realityexpander.tasky.core.util.UserId
import com.realityexpander.tasky.core.util.Username
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable


@Parcelize
@Serializable
data class AuthInfo(
    val authToken: AuthToken? = null,
    val userId: UserId? = null,
    val username: Username? = null,
): Parcelable