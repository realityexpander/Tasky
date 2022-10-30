package com.realityexpander.tasky.data.repository.local

import com.realityexpander.tasky.common.AuthToken
import com.realityexpander.tasky.common.authToken
import com.realityexpander.tasky.common.UserId
import com.realityexpander.tasky.common.userId
import com.realityexpander.tasky.common.Username
import com.realityexpander.tasky.common.username
import com.realityexpander.tasky.data.common.convertersDTOEntityDomain.toDomain
import com.realityexpander.tasky.data.common.convertersDTOEntityDomain.toEntity
import com.realityexpander.tasky.domain.AuthInfo
import javax.inject.Inject

// Uses the DAO pattern to access the Proto Datastore

class AuthDaoImpl @Inject constructor(): IAuthDao {
    private var authInfoEntity: AuthInfoEntity =
        AuthInfoEntity(null, null, null)

    override suspend fun getAuthToken(): AuthToken? {
        return authToken(authInfoEntity.authToken)
    }

    override suspend fun setAuthToken(authToken: AuthToken?) {
        this.authInfoEntity = this.authInfoEntity.copy(authToken = authToken)
    }

    override suspend fun clearAuthToken() {
        this.authInfoEntity = this.authInfoEntity.copy(authToken = null)
    }

    override suspend fun getAuthUsername(): Username? {
        return username(authInfoEntity.username)
    }

    override suspend fun setAuthUsername(username: Username?) {
        this.authInfoEntity = this.authInfoEntity.copy(username = username)
    }

    override suspend fun getAuthUserId(): UserId? {
        return userId(authInfoEntity.userId)
    }

    override suspend fun setAuthUserId(userId: UserId?) {
        this.authInfoEntity = this.authInfoEntity.copy(userId = userId)
    }

    override suspend fun getAuthInfo(): AuthInfo {
        return authInfoEntity.toDomain()
    }

    override suspend fun setAuthInfo(authInfo: AuthInfo) {
        this.authInfoEntity = authInfo.toEntity()
    }

    override suspend fun clearAuthInfo() {
        this.authInfoEntity = AuthInfoEntity(null, null, null)
    }
}