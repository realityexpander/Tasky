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
import kotlin.coroutines.cancellation.CancellationException

class AuthRepositoryFakeImpl(
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
        } catch (e: Exceptions.WrongPasswordException) {
            throw e
        } catch(e: CancellationException) {
            throw e
        } catch (e: Exception) {
            throw Exceptions.UnknownErrorException(e.message)
        }

        val authInfo = authInfoDTO.toDomain()

        authInfo?.let {
            authDao.setAuthInfo(authInfo)
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
        } catch (e: Exceptions.EmailAlreadyExistsException) {
            throw e
        } catch(e: CancellationException) {
            throw e
        } catch (e: Exception) {
            throw Exceptions.UnknownErrorException(e.message)
        }
    }

    override suspend fun getAuthInfo(): AuthInfo? {
        return authDao.getAuthInfo()
    }

    override suspend fun setAuthInfo(authInfo: AuthInfo?) {
        authApi.setAuthToken(authInfo?.authToken)
        authDao.setAuthInfo(authInfo)
    }

    override suspend fun clearAuthInfo() {
        authApi.clearAuthToken()
        authDao.clearAuthInfo()
    }

    override suspend fun authenticate(): Boolean {
        return authApi.authenticate()
    }

    override suspend fun authenticateAuthInfo(authInfo: AuthInfo?): Boolean {
        return authApi.authenticateAuthToken(authInfo?.authToken)
    }
}