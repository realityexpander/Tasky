package com.realityexpander.tasky.auth_feature.domain.validation.validateEmail.validateEmailImpls

import com.realityexpander.tasky.auth_feature.domain.validation.validateEmail.IValidateEmail
import com.realityexpander.tasky.auth_feature.domain.validation.validateEmail.emailMatcher.IEmailMatcher
import com.realityexpander.tasky.auth_feature.domain.validation.validateEmail.emailMatcher.emailMatcherImpls.EmailMatcherRegexImpl

class ValidateEmailRegexImpl (
    private val emailMatcher: IEmailMatcher = EmailMatcherRegexImpl()
): IValidateEmail {
    override fun validate(email: String): Boolean {
        return email.isNotBlank()
                && emailMatcher.matches(email)
    }
}