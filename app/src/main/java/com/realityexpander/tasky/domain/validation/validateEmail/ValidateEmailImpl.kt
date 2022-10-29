package com.realityexpander.tasky.domain.validation.validateEmail

import javax.inject.Inject

class ValidateEmailImpl (
    private val emailMatcher: IEmailMatcher = EmailMatcherImpl()
): IValidateEmail {
    override fun validate(email: String): Boolean {
        return email.isNotBlank()
            && emailMatcher.matches(email)
    }
}

class ValidateEmailRegexImpl (
    private val emailMatcher: IEmailMatcher = EmailMatcherRegexImpl()
): IValidateEmail {
    override fun validate(email: String): Boolean {
        return email.isNotBlank()
                && emailMatcher.matches(email)
    }
}