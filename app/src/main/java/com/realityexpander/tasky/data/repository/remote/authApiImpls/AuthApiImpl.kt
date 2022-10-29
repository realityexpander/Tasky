package com.realityexpander.tasky.data.repository.remote.authApiImpls

import com.realityexpander.tasky.common.*
import com.realityexpander.tasky.data.repository.remote.ApiCredentialsDTO
import com.realityexpander.tasky.data.repository.AuthInfoDTO
import com.realityexpander.tasky.data.repository.remote.IAuthApi
import retrofit2.HttpException
import javax.inject.Inject

// Uses real network API & Retrofit calls

class AuthApiImpl @Inject constructor (
    private val taskyApi: TaskyApi
): IAuthApi {

    override suspend fun login(
        email: Email,
        password: Password
    ): AuthInfoDTO {
        if(email.isBlank() || password.isBlank())
            throw Exceptions.LoginException("Invalid email or password")

        try {
            val response =
                taskyApi.login(ApiCredentialsDTO(email = email, password = password))

            when(response.code()) {
                200 -> {
                    return AuthInfoDTO(
                        authToken(response.body()?.authToken
                            ?: throw Exceptions.LoginException("No Token")
                        ),
                        userId(response.body()?.userId
                            ?: throw Exceptions.LoginException("No User Id")
                        ),
                        username(response.body()?.username
                            ?: throw Exceptions.LoginException("No Full Name")
                    ))
                }
                409 -> throw Exceptions.LoginException(
                    getErrorBodyMessage(response.errorBody()?.string())
                )
                else -> throw Exceptions.UnknownErrorException(
                    "${response.message()} - " +
                    "${response.code()} - " +
                    getErrorBodyMessage(response.errorBody()?.string())
                )
            }
        } catch (e: Exceptions.RegisterNetworkException) {
            throw e
        } catch (e: HttpException) {
            throw Exceptions.LoginNetworkException("${e.message()} - ${e.code()}")
        } catch (e: java.net.UnknownHostException) {
            throw Exceptions.LoginNetworkException(e.message)
        } catch (e: Exception) {
            if(e !is Exceptions.UnknownErrorException)
                throw e

            throw Exceptions.UnknownErrorException(e.message ?: "Unknown Error")
        }
    }

    override suspend fun register(
        username: Username,
        email: Email,
        password: Password
    ) {
        if(username.isBlank() || email.isBlank() || password.isBlank())
            throw Exceptions.RegisterException("Invalid username, email or password")

        try {
            val response =
                taskyApi.register(
                    ApiCredentialsDTO(
                    username = username,
                    email = email,
                    password = password
                )
                )
            println("response: $response")

            when(response.code()) {
                200 -> return // Success
                409 -> throw Exceptions.RegisterException(
                    getErrorBodyMessage(response.errorBody()?.string())
                )
                else -> throw Exceptions.UnknownErrorException(
                    "${response.message()} - " +
                    "${response.code()} - " +
                    getErrorBodyMessage(response.errorBody()?.string())
                )
            }
        } catch (e: Exceptions.RegisterNetworkException) {
            throw e
        } catch (e: HttpException) {
            throw Exceptions.RegisterNetworkException("${e.message()} - ${e.code()}")
        } catch (e: java.net.UnknownHostException) {
            throw Exceptions.RegisterNetworkException(e.message)
        } catch (e: Exception) {
            if(e !is Exceptions.UnknownErrorException)
                throw e

            throw Exceptions.UnknownErrorException(e.message ?: "Unknown Error")
        }
    }

}