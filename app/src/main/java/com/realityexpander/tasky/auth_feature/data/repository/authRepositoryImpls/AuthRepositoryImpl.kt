package com.realityexpander.tasky.auth_feature.data.repository.authRepositoryImpls

import com.realityexpander.tasky.auth_feature.data.common.convertersDTOEntityDomain.toDomain
import com.realityexpander.tasky.auth_feature.data.repository.local.IAuthDao
import com.realityexpander.tasky.auth_feature.data.repository.remote.DTOs.auth.AuthInfoDTO
import com.realityexpander.tasky.auth_feature.data.repository.remote.IAuthApi
import com.realityexpander.tasky.auth_feature.domain.AuthInfo
import com.realityexpander.tasky.auth_feature.domain.IAuthRepository
import com.realityexpander.tasky.auth_feature.domain.validation.ValidateEmail
import com.realityexpander.tasky.auth_feature.domain.validation.ValidatePassword
import com.realityexpander.tasky.auth_feature.domain.validation.ValidateUsername
import com.realityexpander.tasky.core.util.Email
import com.realityexpander.tasky.core.util.Exceptions
import com.realityexpander.tasky.core.util.Password
import com.realityexpander.tasky.core.util.Username
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class AuthRepositoryImpl @Inject constructor(
    private val authDao: IAuthDao,
    private val authApi: IAuthApi,
    override val validateEmail: ValidateEmail,
    override val validatePassword: ValidatePassword,
    override val validateUsername: ValidateUsername,
): IAuthRepository {

    override suspend fun login(
        email: Email,
        password: Password
    ): AuthInfo {
        // Sanity check to be sure the email and password are valid
        if(!validateEmail.validate(email)) {
            throw Exceptions.InvalidEmailException()
        }
        if(!validatePassword.validate(password)) {
            throw Exceptions.InvalidPasswordException()
        }

        val authInfoDTO: AuthInfoDTO? = try {
            authApi.login(email.trim(), password)
        } catch (e: Exceptions.LoginException) {
            throw e
        } catch (e: Exceptions.NetworkException) {
            throw e
        } catch (e: Exceptions.WrongPasswordException) {
            throw e
        } catch(e: CancellationException) {
            throw e
        } catch (e: Exception) {
            throw Exceptions.UnknownErrorException(e.message)
        }

        val authInfo = authInfoDTO?.copy(email = email).toDomain() // include email in the AuthInfo

        authInfo?.let {
            authDao.setAuthInfo(authInfo)
            authApi.setAuthUserId(authInfo.userId)
            return authDao.getAuthInfo()
                ?: throw Exceptions.LoginException("No AuthInfo")
        } ?: throw Exceptions.LoginException("No AuthInfo")
    }

    override suspend fun register(
        username: Username,
        email: Email,
        password: Password
    ) {
        // Sanity check to be sure the username, email, and password are valid
        if(!validateUsername.validate(username)) {
            throw Exceptions.InvalidUsernameException()
        }
        if(!validateEmail.validate(email)) {
            throw Exceptions.InvalidEmailException()
        }
        if(!validatePassword.validate(password)) {
            throw Exceptions.InvalidPasswordException()
        }

        try {
            authApi.register(username.trim(), email.trim(), password)
        } catch (e: Exceptions.RegisterException) {
            throw e
        } catch (e: Exceptions.NetworkException) {
            throw e
        } catch (e: Exceptions.EmailAlreadyExistsException) {
            throw e
        } catch (e: Exceptions.UnknownErrorException) {
            throw e
        } catch(e: CancellationException) {
            throw e
        } catch (e: Exception) {
            throw Exceptions.UnknownErrorException(e.message)
        }
    }

    override suspend fun logout() {
        try {
            authDao.clearAuthInfo()
            authApi.logout()
        } catch(e: CancellationException) {
            throw e
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun getAuthInfo(): AuthInfo? {
        return authDao.getAuthInfo()
    }

    override fun getAuthUserId(): String? {
        return IAuthApi.authUserId
    }

    override suspend fun setAuthInfo(authInfo: AuthInfo?) {
        authApi.setAuthToken(authInfo?.authToken)
        authApi.setAuthUserId(authInfo?.userId)

        authDao.setAuthInfo(authInfo)
    }

    override suspend fun clearAuthInfo() {
        authApi.clearAuthToken()
        authDao.clearAuthInfo()
    }

    override suspend fun authenticate(): Boolean {
        return try {
            authApi.authenticate()
        } catch (e: Exceptions.NetworkException) {
            throw e
        } catch (e: Exceptions.UnknownErrorException) {
            throw e
        } catch(e: CancellationException) {
            throw e
        } catch (e: Exception) {
            throw Exceptions.UnknownErrorException(e.message)
        }
    }

    override suspend fun authenticateAuthInfo(authInfo: AuthInfo?): Boolean {
        if(authInfo == null) return false
        if(authInfo.authToken.isNullOrBlank()) return false

        return try {
            authApi.authenticateAuthToken(authInfo.authToken)
        } catch(e: CancellationException) {
            false
        } catch (e: Exception) {
            false
        }
    }
}