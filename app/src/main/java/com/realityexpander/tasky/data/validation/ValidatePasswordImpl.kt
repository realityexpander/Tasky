package com.realityexpander.tasky.data.validation

import com.realityexpander.tasky.domain.validation.IEmailMatcher
import com.realityexpander.tasky.domain.validation.IValidateEmail
import com.realityexpander.tasky.domain.validation.IValidatePassword

class ValidatePasswordImpl(): IValidatePassword {
    override fun validatePassword(password: String): Boolean {
        if(password.isEmpty()) return false

        return password.length in 6..30             // 6-30 chars
            && password.contains(Regex("[a-z]"))    // at least one letter
            && password.contains(Regex("[0-9]"))    // at least one digit
            && password.contains(Regex("[A-Z]"))    // at least one uppercase letter
    }
}