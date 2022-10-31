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
import com.realityexpander.tasky.core.common.*

class AuthRepositoryFakeImpl(
    private val authDao: IAuthDao,
    private val authApi: IAuthApi,
    override val validateEmail: ValidateEmail,
    override val validatePassword: ValidatePassword,
    override val validateUsername: ValidateUsername,
): IAuthRepository {

    override suspend fun login(email: String, password: String): AuthInfo {
        if(!validateEmail.validate(email)) {
            throw Exceptions.InvalidEmailException()
        }
        if(!validatePassword.validate(password)) {
            throw Exceptions.InvalidPasswordException()
        }

        val authInfoDTO: AuthInfoDTO = try {
            authApi.login(email, password)
        } catch (e: Exceptions.LoginException) {
            throw e
        } catch (e: Exceptions.WrongPasswordException) {
            throw e
        } catch (e: Exception) {
            throw Exceptions.UnknownErrorException(e.message)
        }

        val authInfo = authInfoDTO.toDomain()

        if(!authInfo.authToken.isNullOrBlank()) {
            authDao.setAuthInfo(authInfo)
            return authDao.getAuthInfo()
        } else {
            throw Exceptions.LoginException("No AuthInfo")
        }
    }

    override suspend fun register(
        username: Username,
        email: Email,
        password: Password
    ) {
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
            authApi.register(username, email, password)
        } catch (e: Exceptions.EmailAlreadyExistsException) {
            throw e
        } catch (e: Exception) {
            throw Exceptions.UnknownErrorException(e.message)
        }
    }

    override suspend fun getAuthToken(): AuthToken? {
        return authDao.getAuthToken()
    }

    override suspend fun getAuthInfo(): AuthInfo? {
        return authDao.getAuthInfo()
    }

    override suspend fun clearAuthInfo() {
        authDao.clearAuthInfo()
    }

    override suspend fun authenticateAuthInfo(authInfo: AuthInfo?): Boolean {
        return authApi.authenticate(authInfo?.authToken)
    }
}