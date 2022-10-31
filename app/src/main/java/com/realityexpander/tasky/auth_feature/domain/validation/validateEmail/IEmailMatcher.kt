package com.realityexpander.tasky.auth_feature.domain.validation.validateEmail

interface IEmailMatcher {
    fun matches(email: String): Boolean
}