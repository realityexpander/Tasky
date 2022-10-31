package com.realityexpander.tasky.auth_feature.domain.validation.validateEmail.emailMatcher

interface IEmailMatcher {
    fun matches(email: String): Boolean
}