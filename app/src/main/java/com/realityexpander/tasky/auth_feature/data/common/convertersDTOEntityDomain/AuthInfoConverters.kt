package com.realityexpander.tasky.auth_feature.data.common.convertersDTOEntityDomain

import com.realityexpander.tasky.auth_feature.data.repository.local.entities.AuthInfoEntity
import com.realityexpander.tasky.auth_feature.data.repository.remote.DTOs.auth.AuthInfoDTO
import com.realityexpander.tasky.auth_feature.domain.AuthInfo


// Convert AuthInfo to AuthInfoDTO
fun AuthInfo?.toDTO(): AuthInfoDTO? {
    return AuthInfoDTO(
        authToken = this?.authToken,
        userId = this?.userId,
        username = this?.username
    )
}

// Convert AuthInfoDTO to AuthInfo
fun AuthInfoDTO?.toDomain(): AuthInfo? {
    return this?.let {
        AuthInfo(
            authToken = authToken,
            userId = userId,
            username = username
        )
    }
}

// Convert AuthInfo to AuthInfoEntity
fun AuthInfo?.toEntity(): AuthInfoEntity? {
    return this?.let {
        AuthInfoEntity(
            authToken = authToken,
            userId = userId,
            username = username
        )
    }
}

// Convert AuthInfoEntity to AuthInfo
fun AuthInfoEntity?.toDomain(): AuthInfo? {
    return this?.let {
        AuthInfo(
            authToken = authToken,
            userId = userId,
            username = username
        )
    }
}

fun main() {
    val authInfo = AuthInfo(
        authToken = "authToken",
        userId = "userId",
        username = "username"
    )
    val authInfoDTO = authInfo.toDTO()
    val authInfoEntity = authInfo.toEntity()
    val authInfoFromDTO = authInfoDTO?.toDomain()
    val authInfoFromEntity = authInfoEntity?.toDomain()

    println(authInfo)
    println(authInfoDTO == authInfo.toDTO())
    println(authInfoEntity == authInfo.toEntity())
    println(authInfoFromDTO == authInfoDTO.toDomain())
    println(authInfoFromEntity == authInfoEntity.toDomain())


    // Test null values

    val authInfo2: AuthInfo? = null

    val authInfoDTO2 = authInfo2.toDTO()
    val authInfoEntity2 = authInfo2.toEntity()
    val authInfoFromDTO2 = authInfoDTO2.toDomain()
    val authInfoFromEntity2 = authInfoEntity2.toDomain()

    println()
    println(authInfo2)
    println(authInfoDTO2 == authInfo2.toDTO())
    println(authInfoEntity2 == authInfo2.toEntity())
    println(authInfoFromDTO2 == authInfoDTO2.toDomain())
    println(authInfoFromEntity2 == authInfoEntity2.toDomain())
}