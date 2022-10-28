package com.realityexpander.tasky.data.repository

import com.realityexpander.tasky.common.Exceptions
import com.realityexpander.tasky.common.AuthToken
import com.realityexpander.tasky.data.repository.remote.IAuthApi
import com.realityexpander.tasky.data.repository.local.IAuthDao
import com.realityexpander.tasky.domain.IAuthRepository
import com.realityexpander.tasky.domain.validation.validateEmail.IValidateEmail
import com.realityexpander.tasky.domain.validation.ValidatePassword
import com.realityexpander.tasky.domain.validation.ValidateUsername

class AuthRepositoryFakeImpl(
    private val authDao: IAuthDao,
    private val authApi: IAuthApi,
    private val validateEmail: IValidateEmail,
    private val validatePassword: ValidatePassword,
    private val validateUsername: ValidateUsername,
): IAuthRepository {

    override suspend fun login(email: String, password: String): AuthToken {
        if(!validateEmail.validate(email)) {
            throw Exceptions.InvalidEmailException()
        }

        val token: AuthToken = try {
            authApi.login(email, password)
        } catch (e: Exceptions.LoginException) {
            throw e
        } catch (e: Exceptions.WrongPasswordException) {
            throw e
        } catch (e: Exception) {
            throw Exceptions.UnknownErrorException(e.message)
        }

        if(token != "") {
            authDao.setAuthToken(token)
            return authDao.getAuthToken()
        } else {
            throw Exceptions.LoginException("No Token")
        }
    }

    override suspend fun register(
        username: String,
        email: String,
        password: String
    ): AuthToken {
        if(!validateUsername.validate(username)) {
            throw Exceptions.InvalidUsernameException()
        }
        if(!validateEmail.validate(email)) {
            throw Exceptions.InvalidEmailException()
        }
        if(!validatePassword.validate(password)) {
            throw Exceptions.InvalidPasswordException()
        }

        val token: AuthToken = try {
            authApi.register(username, email, password)
        } catch (e: Exceptions.EmailAlreadyExistsException) {
            throw e
        } catch (e: Exception) {
            throw Exceptions.UnknownErrorException(e.message)
        }

        if(token != "") {
            authDao.setAuthToken(token)
            return authDao.getAuthToken()
        } else {
            throw Exceptions.LoginException()
        }
    }
}