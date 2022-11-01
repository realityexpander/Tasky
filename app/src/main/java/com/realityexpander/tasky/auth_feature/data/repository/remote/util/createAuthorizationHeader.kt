package com.realityexpander.tasky.auth_feature.data.repository.remote.util

fun createAuthorizationHeader(authToken: String?): String {
    return "Bearer ${authToken ?: "NULL_AUTH_TOKEN"}"
}