package com.realityexpander.tasky.auth_feature.domain.validation.validateEmail.emailMatcherImpls

import com.realityexpander.tasky.auth_feature.domain.validation.validateEmail.IEmailMatcher

class EmailMatcherAndroidImpl: IEmailMatcher {
    override fun matches(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}