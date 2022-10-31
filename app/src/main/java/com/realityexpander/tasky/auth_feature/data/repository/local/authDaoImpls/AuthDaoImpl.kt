package com.realityexpander.tasky.auth_feature.data.repository.local.authDaoImpls

import com.realityexpander.tasky.auth_feature.data.common.convertersDTOEntityDomain.toDomain
import com.realityexpander.tasky.auth_feature.data.common.convertersDTOEntityDomain.toEntity
import com.realityexpander.tasky.auth_feature.data.repository.local.IAuthDao
import com.realityexpander.tasky.auth_feature.data.repository.local.entities.AuthInfoEntity
import com.realityexpander.tasky.auth_feature.domain.AuthInfo
import com.realityexpander.tasky.core.common.*
import javax.inject.Inject

// Uses the DAO pattern to access the Proto Datastore

class AuthDaoImpl @Inject constructor(): IAuthDao {
    private var authInfoEntity: AuthInfoEntity? = null

    override suspend fun getAuthToken(): AuthToken? {
        return authToken(authInfoEntity?.authToken)
    }

    override suspend fun setAuthToken(authToken: AuthToken?) {
        this.authInfoEntity = this.authInfoEntity?.copy(authToken = authToken)
    }

    override suspend fun clearAuthToken() {
        this.authInfoEntity = this.authInfoEntity?.copy(authToken = null)
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
        this.authInfoEntity = AuthInfoEntity(null, null, null)
    }
}