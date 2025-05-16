@file:Suppress("PackageName")
package com.realityexpander.tasky.auth_feature.data.repository.remote.DTOs.auth

import com.realityexpander.tasky.core.util.AccessToken
import com.realityexpander.tasky.core.util.UserId
import com.realityexpander.tasky.core.util.Username
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.JsonNames

// Authentication response from the server
@Serializable
//@OptIn(ExperimentalSerializationApi::class) // for JsonNames
data class AuthInfoDTO (
    val accessToken: AccessToken? = null,
//    @JsonNames("accessTokenExpirationTimestamp")
    @SerialName("accessTokenExpirationTimestamp")
    val accessTokenExpirationTimestampEpochMilli: Long? = null,
    val refreshToken: String? = null,
    val userId: UserId? = null,
//    @JsonNames("fullName")
    @SerialName("fullName")
    val username: Username? = null,
    @Transient
    val email: String? = null,
)

