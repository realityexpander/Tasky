package com.realityexpander.tasky.auth_feature.data.repository.remote.authApiImpls

import com.realityexpander.tasky.auth_feature.data.repository.remote.DTOs.auth.ApiCredentialsDTO
import com.realityexpander.tasky.auth_feature.data.repository.remote.DTOs.auth.AuthInfoDTO
import com.realityexpander.tasky.auth_feature.data.repository.remote.IAuthApi
import com.realityexpander.tasky.core.common.*
import com.realityexpander.tasky.core.data.remote.utils.getErrorBodyMessage
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
                    )
                    )
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
        } catch (e: Exceptions.NetworkException) {
            throw e
        } catch (e: Exceptions.UnknownErrorException) {
            throw e
        } catch (e: HttpException) {
            throw Exceptions.NetworkException("${e.message()} - ${e.code()}")
        } catch (e: java.net.UnknownHostException) {
            throw Exceptions.NetworkException(e.message)
        } catch (e: Exception) {
            throw Exceptions.UnknownErrorException(e.message ?: "Unknown Error")
        }
    }

    override suspend fun register(
        username: Username,
        email: Email,
        password: Password
    ) {
        try {
            val response =
                taskyApi.register(
                    ApiCredentialsDTO(
                    username = username,
                    email = email,
                    password = password
                ))

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
        } catch (e: Exceptions.NetworkException) {
            throw e
        } catch (e: Exceptions.UnknownErrorException) {
            throw e
        } catch (e: HttpException) {
            throw Exceptions.NetworkException("${e.message()} - ${e.code()}")
        } catch (e: java.net.UnknownHostException) {
            throw Exceptions.NetworkException(e.message)
        } catch (e: Exception) {
            throw Exceptions.UnknownErrorException(e.message ?: "Unknown Error")
        }
    }

    // Pass a null AuthToken to use the current user's token
    override suspend fun authenticate(authToken: AuthToken?): Boolean {
        try {
            val response =  authToken?.let {
                // Use the provided token
                taskyApi.authenticate(IAuthApi.createAuthorizationHeader(authToken))
            } ?:
                // Use the current user's AuthToken from the IAuthApi companion object
                taskyApi.authenticate()

            return when(response.code()) {
                200 -> true // Success
                401 -> false
                else -> throw Exceptions.UnknownErrorException(
                    "${response.message()} - " +
                    "${response.code()} - " +
                    getErrorBodyMessage(response.errorBody()?.string())
                )
            }
        } catch (e: Exceptions.NetworkException) {
            throw e
        } catch (e: Exceptions.UnknownErrorException) {
            throw e
        } catch (e: HttpException) {
            throw Exceptions.NetworkException("${e.message()} - ${e.code()}")
        } catch (e: java.net.UnknownHostException) {
            throw Exceptions.NetworkException(e.message)
        } catch (e: Exception) {
            throw Exceptions.UnknownErrorException(e.message ?: "Unknown Error")
        }
    }

}

















































