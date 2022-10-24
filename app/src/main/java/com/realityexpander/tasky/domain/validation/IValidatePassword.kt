package com.realityexpander.tasky.domain.validation

interface IValidatePassword {
    fun validatePassword(password: String): Boolean
}