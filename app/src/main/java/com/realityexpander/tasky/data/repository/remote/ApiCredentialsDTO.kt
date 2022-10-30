package com.realityexpander.tasky.data.repository.remote

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.JsonNames

@OptIn(ExperimentalSerializationApi::class)
data class ApiCredentialsDTO constructor(
//    @field:Json(name="fullName") val username: String? = null,
    @JsonNames("fullName") val username: String? = null,
    val email: String,
    val password: String
)
