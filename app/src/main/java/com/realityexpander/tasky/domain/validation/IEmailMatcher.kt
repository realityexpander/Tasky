package com.realityexpander.tasky.domain.validation

interface IEmailMatcher {
    fun matches(email: String): Boolean
}