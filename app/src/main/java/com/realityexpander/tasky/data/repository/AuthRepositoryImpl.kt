package com.realityexpander.tasky.data.repository

import com.realityexpander.tasky.common.Exceptions
import com.realityexpander.tasky.common.AuthToken
import com.realityexpander.tasky.data.repository.local.IAuthApi
import com.realityexpander.tasky.data.repository.remote.IAuthDao
import com.realityexpander.tasky.domain.IAuthRepository
import com.realityexpander.tasky.domain.validation.IValidateEmail

class AuthRepositoryImpl(
    private val authDao: IAuthDao,
    private val authApi: IAuthApi,
    private val validateEmail: IValidateEmail
): IAuthRepository {

    override suspend fun login(email: String, password: String): AuthToken {
        if(!validateEmail.validateEmail(email)) {
            throw Exceptions.InvalidEmailException()
        }

        val token: AuthToken = try {
            authApi.login(email, password)
        } catch (e: Exceptions.LoginException) {
            throw e
        } catch (e: Exception) {
            throw Exceptions.UnknownException(e.message)
        }

        if(token != "") {
            authDao.setAuthToken(token)
            return authDao.getAuthToken()
        } else {
            throw Exceptions.LoginException()
        }
    }

    override suspend fun register(email: String, password: String): AuthToken {
        if(!validateEmail.validateEmail(email)) {
            throw Exceptions.InvalidEmailException()
        }

        val token: AuthToken = try {
            authApi.register(email, password)
        } catch (e: Exceptions.EmailAlreadyExistsException) {
            throw e
        } catch (e: Exception) {
            throw Exceptions.UnknownException(e.message)
        }

        if(token != "") {
            authDao.setAuthToken(token)
            return authDao.getAuthToken()
        } else {
            throw Exceptions.LoginException()
        }
    }
}