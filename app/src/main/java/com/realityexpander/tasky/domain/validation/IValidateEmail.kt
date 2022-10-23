package com.realityexpander.tasky.domain.validation

interface IValidateEmail {
    fun validateEmail(email: String): Boolean
}