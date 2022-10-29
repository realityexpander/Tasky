package com.realityexpander.tasky.data.repository.remote

import com.squareup.moshi.Json

data class ApiCredentialsDTO(
    @field:Json(name="fullName") val username: String? = null,
    val email: String,
    val password: String
)
