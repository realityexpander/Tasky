package com.realityexpander.tasky.domain.validation

import com.realityexpander.tasky.domain.validation.IEmailMatcher

class EmailMatcherImpl: IEmailMatcher {
    override fun matches(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}