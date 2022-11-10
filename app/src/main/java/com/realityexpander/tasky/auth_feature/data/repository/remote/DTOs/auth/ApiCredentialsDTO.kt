package com.realityexpander.tasky.auth_feature.data.repository.remote.DTOs.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Authentication credentials to send to the server

@Serializable
data class ApiCredentialsDTO constructor(
    @SerialName("fullName")
    val username: String? = null,
    val email: String,
    val password: String
)