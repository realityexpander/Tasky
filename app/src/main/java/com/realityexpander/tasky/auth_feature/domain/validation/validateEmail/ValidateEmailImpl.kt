package com.realityexpander.tasky.auth_feature.domain.validation.validateEmail

import com.realityexpander.tasky.auth_feature.domain.validation.validateEmail.emailMatcherImpls.EmailMatcherAndroidImpl

class ValidateEmailImpl (
    private val emailMatcher: IEmailMatcher = EmailMatcherAndroidImpl()
): IValidateEmail {
    override fun validate(email: String): Boolean {
        return email.isNotBlank()
            && emailMatcher.matches(email)
    }
}