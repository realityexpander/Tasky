package com.realityexpander.tasky.auth_feature.data.common.convertersDTOEntityDomain

import com.realityexpander.tasky.auth_feature.data.repository.local.entities.AuthInfoEntity
import com.realityexpander.tasky.auth_feature.data.repository.remote.DTOs.auth.AuthInfoDTO
import com.realityexpander.tasky.auth_feature.domain.AuthInfo


// Convert AuthInfo to AuthInfoDTO
fun AuthInfo?.toDTO(): AuthInfoDTO? {
    return this?.let {
        AuthInfoDTO(
            authToken = it.authToken,
            userId = it.userId,
            username = it.username
        )
    }
}

// Convert AuthInfoDTO to AuthInfo
fun AuthInfoDTO?.toDomain(): AuthInfo? {
    return this?.let {
        AuthInfo(
            authToken = it.authToken,
            userId = it.userId,
            username = it.username
        )
    }
}

// Convert AuthInfo to AuthInfoEntity
fun AuthInfo?.toEntity(): AuthInfoEntity? {
    return this?.let {
        AuthInfoEntity(
            authToken = it.authToken,
            userId = it.userId,
            username = it.username
        )
    }
}

// Convert AuthInfoEntity to AuthInfo
fun AuthInfoEntity?.toDomain(): AuthInfo? {
    return this?.let {
        AuthInfo(
            authToken = it.authToken,
            userId = it.userId,
            username = it.username
        )
    }
}