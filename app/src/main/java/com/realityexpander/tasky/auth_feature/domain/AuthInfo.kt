package com.realityexpander.tasky.auth_feature.domain

import android.os.Parcelable
import com.realityexpander.tasky.core.util.AccessToken
import com.realityexpander.tasky.core.util.Email
import com.realityexpander.tasky.core.util.EpochMilli
import com.realityexpander.tasky.core.util.UserId
import com.realityexpander.tasky.core.util.Username
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@Parcelize
@InternalSerializationApi
@Serializable
data class AuthInfo(
    val accessToken: AccessToken? = null,
    val accessTokenExpirationTimestampEpochMilli: EpochMilli? = null,
    val refreshToken: String? = null,
    val userId: UserId? = null,
    val username: Username? = null,
    val email: Email? = null,
): Parcelable
