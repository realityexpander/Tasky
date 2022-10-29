package com.realityexpander.tasky.domain.validation.validateEmail

class EmailMatcherAndroidImpl: IEmailMatcher {
    override fun matches(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}