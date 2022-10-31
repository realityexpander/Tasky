package com.realityexpander.tasky.auth_feature.domain.validation.validateEmail.emailMatcherImpls

import com.realityexpander.tasky.auth_feature.domain.validation.validateEmail.IEmailMatcher

// Matches how the email is validated on the server
class EmailMatcherRegexImpl: IEmailMatcher {
    override fun matches(email: String): Boolean {
        // use regex to validate email
        val regex = Regex("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", RegexOption.IGNORE_CASE)
        return regex.matches(email)
    }
}