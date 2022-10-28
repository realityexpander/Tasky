package com.realityexpander.tasky.domain.validation.validateEmail

interface IEmailMatcher {
    fun matches(email: String): Boolean
}