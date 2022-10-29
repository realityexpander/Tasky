package com.realityexpander.tasky.data.repository.authRepositoryImpls

import com.realityexpander.tasky.common.Email
import com.realityexpander.tasky.common.Exceptions
import com.realityexpander.tasky.common.Password
import com.realityexpander.tasky.common.Username
import com.realityexpander.tasky.data.common.convertersDTOEntityDomain.toDomain
import com.realityexpander.tasky.data.repository.remote.AuthInfoDTO
import com.realityexpander.tasky.data.repository.remote.IAuthApi
import com.realityexpander.tasky.data.repository.local.IAuthDao
import com.realityexpander.tasky.domain.AuthInfo
import com.realityexpander.tasky.domain.IAuthRepository
import com.realityexpander.tasky.domain.validation.validateEmail.IValidateEmail
import com.realityexpander.tasky.domain.validation.ValidatePassword
import com.realityexpander.tasky.domain.validation.ValidateUsername
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authDao: IAuthDao,
    private val authApi: IAuthApi,
    override val validateEmail: IValidateEmail,
    override val validatePassword: ValidatePassword,
    override val validateUsername: ValidateUsername,
): IAuthRepository {

    override suspend fun login(email: Email, password: Password): AuthInfo {
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
        } catch (e: Exceptions.LoginNetworkException) {
            throw e
        } catch (e: Exceptions.WrongPasswordException) {
            throw e
        } catch (e: Exception) {
            throw Exceptions.UnknownErrorException(e.message)
        }

        val authInfo = authInfoDTO.toDomain()

        if(authInfoDTO.authToken != "") {
            authDao.setAuthInfo(authInfo)
            return authDao.getAuthInfo()
        } else {
            throw Exceptions.LoginException("No Token")
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
        } catch (e: Exceptions.RegisterException) {
            throw e
        } catch (e: Exceptions.RegisterNetworkException) {
            throw e
        } catch (e: Exceptions.EmailAlreadyExistsException) {
            throw e
        } catch (e: Exceptions.UnknownErrorException) {
            throw e
        } catch (e: Exception) {
            throw Exceptions.UnknownErrorException(e.message)
        }
    }
}