package com.realityexpander.tasky.data.repository.local

import com.realityexpander.tasky.common.AuthToken
import com.realityexpander.tasky.common.UserId
import com.realityexpander.tasky.common.Username
import com.realityexpander.tasky.data.repository.AuthInfo
import javax.inject.Inject


class AuthDaoFakeImpl @Inject constructor(): IAuthDao {
    private var authInfo: AuthInfo = AuthInfo("", "", "")

    override suspend fun getAuthToken(): AuthToken {
        return AuthToken(authInfo.token)
    }

    override suspend fun setAuthToken(authToken: AuthToken) {
        this.authInfo = this.authInfo.copy(token = authToken)
    }

    override suspend fun getAuthUsername(): Username {
        return Username(authInfo.username)
    }

    override suspend fun setAuthUsername(username: Username) {
        this.authInfo = this.authInfo.copy(username = username)
    }

    override suspend fun getAuthUserId(): UserId {
        return UserId(authInfo.userId)
    }

    override suspend fun setAuthUserId(userId: UserId) {
        this.authInfo = this.authInfo.copy(userId = userId)
    }

    override suspend fun clearAuthToken() {
        this.authInfo = this.authInfo.copy(token = "")
    }

    override suspend fun getAuthInfo(): AuthInfo {
        return authInfo
    }

    override suspend fun setAuthInfo(authInfo: AuthInfo) {
        this.authInfo = authInfo
    }

    override suspend fun clearAuthInfo() {
        this.authInfo = AuthInfo("", "", "")
    }
}