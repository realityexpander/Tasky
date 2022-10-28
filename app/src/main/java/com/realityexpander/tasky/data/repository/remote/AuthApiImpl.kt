package com.realityexpander.tasky.data.repository.remote

import com.realityexpander.tasky.common.*
import com.realityexpander.tasky.data.repository.AuthInfo
import javax.inject.Inject

class AuthApiImpl @Inject constructor (
    private val taskyApi: TaskyApi
): IAuthApi {

    override suspend fun login(
        email: Email,
        password: Password
    ): AuthInfo {
        if(email.isBlank() || password.isBlank()) 
            throw Exceptions.LoginException("Invalid email or password")

        try {
            val response = 
                taskyApi.login(TaskyCredentials(email = email, password = password))
            return AuthInfo(
                AuthToken(response.body()?.token
                    ?: throw Exceptions.LoginException("No Token")
                ),
                UserId(response.body()?.userId
                    ?: throw Exceptions.LoginException("No User Id")
                ),
                Username(response.body()?.username
                    ?: throw Exceptions.LoginException("No Full Name")
                )
            )
        } catch (e: Exception) {
            throw Exceptions.LoginException(e.message)
        }
    }

    override suspend fun register(
        username: Username,
        email: Email,
        password: Password
    ): AuthInfo {
        if(username.isBlank() || email.isBlank() || password.isBlank())
            throw Exceptions.RegisterException("Invalid username, email or password")

        try {
            val response =
                taskyApi.register(TaskyCredentials(
                    username = username,
                    email = email,
                    password = password
                ))
            return AuthInfo(
                AuthToken(response.body()?.token
                    ?: throw Exceptions.RegisterException("No Token")
                ),
                UserId(response.body()?.userId
                    ?: throw Exceptions.RegisterException("No User Id")
                ),
                Username(response.body()?.username
                    ?: throw Exceptions.RegisterException("No Full Name")
                )
            )
        } catch (e: Exception) {
            throw Exceptions.RegisterException(e.message)
        }
    }
}