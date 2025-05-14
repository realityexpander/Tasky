package com.realityexpander.tasky.auth_feature.data.repository.remote.authApiImpls

import com.realityexpander.tasky.auth_feature.data.repository.remote.DTOs.auth.ApiCredentialsDTO
import com.realityexpander.tasky.auth_feature.data.repository.remote.DTOs.auth.AuthInfoDTO
import com.realityexpander.tasky.auth_feature.data.repository.remote.IAuthApi
import com.realityexpander.tasky.core.data.remote.TaskyApi
import com.realityexpander.tasky.core.data.remote.utils.getErrorBodyMessage
import com.realityexpander.tasky.core.util.AccessToken
import com.realityexpander.tasky.core.util.Email
import com.realityexpander.tasky.core.util.Exceptions
import com.realityexpander.tasky.core.util.InternetConnectivityObserver.InternetConnectivityObserverImpl.Companion.isInternetReachable
import com.realityexpander.tasky.core.util.Password
import com.realityexpander.tasky.core.util.Username
import com.realityexpander.tasky.core.util.accessToken
import com.realityexpander.tasky.core.util.userId
import com.realityexpander.tasky.core.util.username
import kotlinx.serialization.InternalSerializationApi
import retrofit2.HttpException
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

// Uses real network API & Retrofit calls

@OptIn(InternalSerializationApi::class)
class AuthApiImpl @Inject constructor (
    private val taskyApi: TaskyApi
): IAuthApi {

    override suspend fun login(
        email: Email,
        password: Password
    ): AuthInfoDTO {

        if(!isInternetReachable)
            throw Exceptions.NetworkException("No internet connection")

        try {
            val response =
                taskyApi.login(ApiCredentialsDTO(email = email, password = password))

            when(response.code()) {
                200 -> {
                    return AuthInfoDTO(
                        accessToken = accessToken(response.body()?.accessToken
                            ?: throw Exceptions.LoginException("No Access Token")
                        ),
                        accessTokenExpirationTimestampEpochMilli = response.body()?.accessTokenExpirationTimestampEpochMilli
                            ?: throw Exceptions.LoginException("No Access Token Expiration Date"),
                        refreshToken = response.body()?.refreshToken
                            ?: throw Exceptions.LoginException("No Refresh Token"),
                        userId = userId(response.body()?.userId
                            ?: throw Exceptions.LoginException("No User Id")
                        ),
                        username = username(response.body()?.username
                            ?: throw Exceptions.LoginException("No Full Name"),
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
        } catch (e: Exceptions.NetworkException) {
            throw e
        } catch (e: Exceptions.UnknownErrorException) {
            throw e
        } catch (e: Exceptions.LoginException) {
            throw e
        } catch (e: HttpException) {
            throw Exceptions.NetworkException("${e.message()} - ${e.code()}")
        } catch (e: java.net.UnknownHostException) {
            throw Exceptions.NetworkException(e.message)
        } catch(e: CancellationException) {
            throw e
        } catch (e: Exception) {
            throw Exceptions.UnknownErrorException(e.message ?: "Unknown Error")
        }
    }

    override suspend fun register(
        username: Username,
        email: Email,
        password: Password
    ) {
        if(!isInternetReachable)
            throw Exceptions.NetworkException("No internet connection")

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
        } catch (e: Exceptions.RegisterException) {
            throw e
        } catch (e: Exceptions.UnknownErrorException) {
            throw e
        } catch (e: HttpException) {
            throw Exceptions.NetworkException("${e.message()} - ${e.code()}")
        } catch (e: java.net.UnknownHostException) {
            throw Exceptions.NetworkException(e.message)
        } catch(e: CancellationException) {
            throw e
        } catch (e: Exception) {
            throw Exceptions.UnknownErrorException(e.message ?: "Unknown Error")
        }
    }

    override suspend fun authenticate(): Boolean {
        if(!isInternetReachable) return false

        try {
            // Use the current user's AuthToken from the IAuthApi companion object
            val response = taskyApi.authenticate()
            
            return when(response.code()) {
                200 -> true // Success
                401 -> false
                429 -> throw Exceptions.NetworkException(
                    "Rate Limit Exceeded - " +
                    "${response.code()} - " +
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
        } catch(e: CancellationException) {
            throw e
        } catch (e: Exception) {
            throw Exceptions.UnknownErrorException(e.message ?: "Unknown Error")
        }
    }

    override suspend fun authenticateAccessToken(accessToken: AccessToken?): Boolean {
        accessToken ?: return false
        if(!isInternetReachable) return false

        try {
            val response = taskyApi.authenticateAuthToken(accessToken)

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
        } catch(e: CancellationException) {
            throw e
        } catch (e: Exception) {
            throw Exceptions.UnknownErrorException(e.message ?: "Unknown Error")
        }
    }

    override suspend fun logout() {
        if(!isInternetReachable) return

        try {
            taskyApi.logout()
            IAuthApi.clearAuthInfo()
        } catch (e: Exceptions.NetworkException) {
            throw e
        } catch (e: Exceptions.UnknownErrorException) {
            throw e
        } catch (e: HttpException) {
            throw Exceptions.NetworkException("${e.message()} - ${e.code()}")
        } catch (e: java.net.UnknownHostException) {
            throw Exceptions.NetworkException(e.message)
        } catch(e: CancellationException) {
            throw e
        } catch (e: Exception) {
            throw Exceptions.UnknownErrorException(e.message ?: "Unknown Error")
        }

    }
}
