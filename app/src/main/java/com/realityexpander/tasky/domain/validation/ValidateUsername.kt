package com.realityexpander.tasky.domain.validation

class ValidateUsername() {
    fun validate(username: String): Boolean {
        if(username.isEmpty()) return false

        return username.length in 2..50             // 2-50 chars
    }
}
