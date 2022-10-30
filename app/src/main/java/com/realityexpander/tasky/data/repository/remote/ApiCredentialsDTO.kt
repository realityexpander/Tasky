package com.realityexpander.tasky.data.repository.remote

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class ApiCredentialsDTO constructor(
    @SerialName("fullName") // outgoing
    @JsonNames("fullName") // incoming
    val username: String? = null,
    val email: String,
    val password: String
)