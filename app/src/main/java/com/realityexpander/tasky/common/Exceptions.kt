package com.realityexpander.tasky.common

object Exceptions {
    const val USERNAME_NOT_VALID = "Username is not valid"
    const val EMAIL_NOT_VALID = "Email is not valid"
    const val PASSWORD_NOT_VALID = "Password is not valid"
    const val PASSWORDS_DO_NOT_MATCH = "Passwords do not match"
    const val EMAIL_ALREADY_EXISTS = "Email already exists"
    const val EMAIL_DOES_NOT_EXIST = "Email does not exist"
    const val WRONG_PASSWORD = "Wrong password"
    const val UNKNOWN_ERROR = "Unknown error"
    const val LOGIN_ERROR = "Login error"

    class InvalidUsernameException : Exception(USERNAME_NOT_VALID)
    class InvalidEmailException: Exception(EMAIL_NOT_VALID)
    class InvalidPasswordException: Exception(PASSWORD_NOT_VALID)
    class PasswordsNotMatchException: Exception(PASSWORDS_DO_NOT_MATCH)
    class EmailAlreadyExistsException: Exception(EMAIL_ALREADY_EXISTS)
    class EmailNotExistsException: Exception(EMAIL_DOES_NOT_EXIST)
    class WrongPasswordException: Exception(WRONG_PASSWORD)
    class LoginException(message: String? = null): Exception(message ?: LOGIN_ERROR)
    class UnknownErrorException(message: String? = null): Exception(message)
}