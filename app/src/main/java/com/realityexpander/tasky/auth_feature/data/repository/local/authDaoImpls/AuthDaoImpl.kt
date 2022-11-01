package com.realityexpander.tasky.auth_feature.data.repository.local.authDaoImpls

import android.content.Context
import com.realityexpander.tasky.auth_feature.data.repository.local.IAuthDao
import com.realityexpander.tasky.auth_feature.domain.AuthInfo
import com.realityexpander.tasky.core.util.AuthToken
import com.realityexpander.tasky.core.util.UserId
import com.realityexpander.tasky.core.util.Username
import com.realityexpander.tasky.dataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import javax.inject.Inject

// Uses the DAO pattern to access the Proto Datastore

class AuthDaoImpl @Inject constructor(
    @ApplicationContext private val context: Context,
): IAuthDao {

    override suspend fun getAuthToken(): AuthToken? {
        return CoroutineScope(Dispatchers.IO).async {
            context.dataStore.data.first()
                .authInfo?.authToken
        }.await()
    }

    override suspend fun setAuthToken(authToken: AuthToken?) {
        context.dataStore.updateData { appSettings ->
            appSettings.copy(
                authInfo = appSettings.authInfo
                    ?.copy(authToken = authToken)
            )
        }
    }

    override suspend fun clearAuthToken() {
        context.dataStore.updateData { appSettings ->
            appSettings.copy(
                authInfo = appSettings.authInfo
                    ?.copy(authToken = null)
            )
        }
    }

    override suspend fun getAuthUsername(): Username? {
        return CoroutineScope(Dispatchers.IO).async {
            context.dataStore.data.first()
                .authInfo?.username
        }.await()
    }

    override suspend fun setAuthUsername(username: Username?) {
        context.dataStore.updateData { appSettings ->
            appSettings.copy(
                authInfo = appSettings.authInfo
                    ?.copy(username = username)
            )
        }
    }

    override suspend fun getAuthUserId(): UserId? {
        return CoroutineScope(Dispatchers.IO).async {
            context.dataStore.data.first()
                .authInfo?.userId
        }.await()
    }

    override suspend fun setAuthUserId(userId: UserId?) {
        context.dataStore.updateData { appSettings ->
            appSettings.copy(
                authInfo = appSettings.authInfo
                    ?.copy(userId = userId)
            )
        }
    }

    override suspend fun getAuthInfo(): AuthInfo? {
        return CoroutineScope(Dispatchers.IO).async {
            context.dataStore.data.first()
                .authInfo
        }.await()
    }

    override suspend fun setAuthInfo(authInfo: AuthInfo?) {
        context.dataStore.updateData { appSettings ->
            appSettings.copy(
                authInfo = authInfo
            )
        }
    }

    override suspend fun clearAuthInfo() {
        context.dataStore.updateData { appSettings ->
            appSettings.copy(
                authInfo = null
            )
        }
    }
}