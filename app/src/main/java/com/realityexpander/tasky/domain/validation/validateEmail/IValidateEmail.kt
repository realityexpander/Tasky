package com.realityexpander.tasky.domain.validation.validateEmail

interface IValidateEmail {
    fun validate(email: String): Boolean
}