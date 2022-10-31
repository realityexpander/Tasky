package com.realityexpander.tasky.auth_feature.data.common.convertersDTOEntityDomain

import com.realityexpander.tasky.auth_feature.data.repository.local.entities.AuthInfoEntity
import com.realityexpander.tasky.auth_feature.data.repository.remote.DTOs.auth.AuthInfoDTO
import com.realityexpander.tasky.auth_feature.domain.AuthInfo


// Convert AuthInfo to AuthInfoDTO
fun AuthInfo.toDTO(): AuthInfoDTO {
    return AuthInfoDTO(
        authToken = authToken,
        userId = userId,
        username = username,
    )
}

// Convert AuthInfoDTO to AuthInfo
fun AuthInfoDTO.toDomain(): AuthInfo {
    return AuthInfo(
        authToken = authToken,
        userId = userId,
        username = username,
    )
}

// Convert AuthInfo to AuthInfoEntity
fun AuthInfo.toEntity(): AuthInfoEntity {
    return AuthInfoEntity(
        authToken = authToken,
        userId = userId,
        username = username,
    )
}

// Convert AuthInfoEntity to AuthInfo
fun AuthInfoEntity.toDomain(): AuthInfo {
    return AuthInfo(
        authToken = authToken,
        userId = userId,
        username = username,
    )
}