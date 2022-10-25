package com.realityexpander.tasky.data.repository.local

import com.realityexpander.tasky.common.AuthToken

class AuthDaoImpl: IAuthDao {
    private var authToken: AuthToken = AuthToken("")

    override suspend fun getAuthToken(): AuthToken {
        return authToken
    }

    override suspend fun setAuthToken(authToken: AuthToken) {
        this.authToken = authToken
    }
}