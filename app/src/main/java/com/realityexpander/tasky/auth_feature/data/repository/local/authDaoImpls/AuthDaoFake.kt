package com.realityexpander.tasky.auth_feature.data.repository.local.authDaoImpls

import com.realityexpander.tasky.auth_feature.data.common.convertersDTOEntityDomain.toDomain
import com.realityexpander.tasky.auth_feature.data.common.convertersDTOEntityDomain.toEntity
import com.realityexpander.tasky.auth_feature.data.repository.local.IAuthDao
import com.realityexpander.tasky.auth_feature.data.repository.local.entities.AuthInfoEntity
import com.realityexpander.tasky.auth_feature.domain.AuthInfo
import com.realityexpander.tasky.core.util.*
import kotlinx.serialization.InternalSerializationApi
import javax.inject.Inject

// Simulates a local database

@OptIn(InternalSerializationApi::class)
class AuthDaoFake @Inject constructor(): IAuthDao {
    private var authInfoEntity: AuthInfoEntity? = null

    override suspend fun getAccessToken(): AccessToken? {
        return accessToken(authInfoEntity?.accessToken)
    }

    override suspend fun setAccessToken(accessToken: AccessToken?) {
        this.authInfoEntity = this.authInfoEntity?.copy(accessToken = accessToken)
    }

    override suspend fun getAccessTokenExpirationTimestampEpochMilli(): Long? {
        return authInfoEntity?.accessTokenExpirationTimestampEpochMilli
    }

    override suspend fun setAccessTokenExpirationTimestampEpochMilli(accessTokenExpirationTimestampEpochMilli: Long) {
        this.authInfoEntity = this.authInfoEntity?.copy(accessTokenExpirationTimestampEpochMilli = accessTokenExpirationTimestampEpochMilli)
    }

    override suspend fun setRefreshToken(refreshToken: String?) {
        this.authInfoEntity = this.authInfoEntity?.copy(refreshToken = refreshToken)
    }

    override suspend fun getRefreshToken(): String? {
        return authInfoEntity?.refreshToken
    }

    override suspend fun clearAuthToken() {
        this.authInfoEntity = this.authInfoEntity?.copy(accessToken = null)
    }

    override suspend fun getAuthUsername(): Username? {
        return username(authInfoEntity?.username)
    }

    override suspend fun setAuthUsername(username: Username?) {
        this.authInfoEntity = this.authInfoEntity?.copy(username = username)
    }

    override suspend fun getAuthUserId(): UserId? {
        return userId(authInfoEntity?.userId)
    }

    override suspend fun setAuthUserId(userId: UserId?) {
        this.authInfoEntity = this.authInfoEntity?.copy(userId = userId)
    }

    override suspend fun getAuthInfo(): AuthInfo? {
        return authInfoEntity.toDomain()
    }

    override suspend fun setAuthInfo(authInfo: AuthInfo?) {
        this.authInfoEntity = authInfo.toEntity()
    }

    override suspend fun clearAuthInfo() {
        this.authInfoEntity = null
    }
}
