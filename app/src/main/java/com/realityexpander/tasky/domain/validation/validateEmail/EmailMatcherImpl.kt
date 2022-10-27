package com.realityexpander.tasky.domain.validation.validateEmail

class EmailMatcherImpl: IEmailMatcher {
    override fun matches(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}