package com.realityexpander.tasky.auth_feature.domain.validation.validateEmail.emailMatcher.emailMatcherImpls

import com.realityexpander.tasky.auth_feature.domain.validation.validateEmail.emailMatcher.IEmailMatcher

class EmailMatcherAndroidImpl: IEmailMatcher {
    override fun matches(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}