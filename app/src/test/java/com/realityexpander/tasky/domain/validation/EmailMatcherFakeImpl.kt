package com.realityexpander.tasky.domain.validation

class EmailMatcherFakeImpl: IEmailMatcher {
    override fun matches(email: String): Boolean {
        return  email.contains("@")
    }
}

