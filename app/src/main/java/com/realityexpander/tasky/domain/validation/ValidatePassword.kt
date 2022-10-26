package com.realityexpander.tasky.domain.validation

class ValidatePassword() {
    fun validate(password: String): Boolean {
        if(password.isEmpty()) return false

        return password.length in 9..30             // 9-30 chars
//            && password.contains(Regex("[a-z]"))    // at least one letter
//            && password.contains(Regex("[0-9]"))    // at least one digit
//            && password.contains(Regex("[A-Z]"))    // at least one uppercase letter
    }
}
