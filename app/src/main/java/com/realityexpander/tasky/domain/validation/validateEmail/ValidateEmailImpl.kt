package com.realityexpander.tasky.domain.validation.validateEmail

class ValidateEmailImpl(
    private val emailMatcher: IEmailMatcher = EmailMatcherImpl()
): IValidateEmail {
    override fun validate(email: String): Boolean {
        return email.isNotBlank()
            && emailMatcher.matches(email)
    }
}
