package com.realityexpander.tasky.domain.validation.validateEmail

class ValidateEmailImpl (
    private val emailMatcher: IEmailMatcher = EmailMatcherAndroidImpl()
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