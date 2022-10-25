package com.realityexpander.tasky.domain.validation

interface IValidateEmail {
    fun validate(email: String): Boolean
}