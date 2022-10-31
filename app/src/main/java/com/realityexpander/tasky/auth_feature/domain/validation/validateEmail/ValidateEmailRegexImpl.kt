package com.realityexpander.tasky.auth_feature.domain.validation.validateEmail

import com.realityexpander.tasky.auth_feature.domain.validation.validateEmail.emailMatcherImpls.EmailMatcherRegexImpl

class ValidateEmailRegexImpl (
    private val emailMatcher: IEmailMatcher = EmailMatcherRegexImpl()
): IValidateEmail {
    override fun validate(email: String): Boolean {
        return email.isNotBlank()
                && emailMatcher.matches(email)
    }
}