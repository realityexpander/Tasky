package com.realityexpander.tasky.auth_feature.domain.validation.validateEmail

interface IValidateEmail {
    fun validate(email: String): Boolean
}