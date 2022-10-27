package com.realityexpander.tasky.domain.validation

import com.realityexpander.tasky.domain.validation.validateEmail.IEmailMatcher

class EmailMatcherFakeImpl: IEmailMatcher {
    override fun matches(email: String): Boolean {
        return  email.contains("@")
    }
}