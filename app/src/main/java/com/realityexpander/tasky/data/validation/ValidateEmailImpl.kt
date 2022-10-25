package com.realityexpander.tasky.data.validation

import com.realityexpander.tasky.domain.validation.IEmailMatcher
import com.realityexpander.tasky.domain.validation.IValidateEmail

class ValidateEmailImpl(
    private val emailMatcher: IEmailMatcher = EmailMatcherImpl()
): IValidateEmail {
    override fun validateEmail(email: String): Boolean {
        return email.isNotBlank()
            && emailMatcher.matches(email)
    }
}
