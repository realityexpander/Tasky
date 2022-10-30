package com.realityexpander.tasky.domain

import android.os.Parcelable
import com.realityexpander.tasky.common.AuthToken
import com.realityexpander.tasky.common.UserId
import com.realityexpander.tasky.common.Username
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable


@Parcelize
@Serializable
data class AuthInfo(
    val authToken: AuthToken? = null,
    val userId: UserId? = null,
    val username: Username? = null,
): Parcelable {

    companion object {
        val EMPTY = AuthInfo()
        val NOT_LOGGED_IN = AuthInfo(authToken = null)
    }
}