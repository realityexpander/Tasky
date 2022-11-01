package com.realityexpander.tasky.domain.validation

import com.realityexpander.tasky.auth_feature.domain.validation.ValidateUsername
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Test


class ValidateUsernameTest {

    private val validateUsername = ValidateUsername()

    @Test
    fun `validate() returns true for valid usernames`() {

        // 2-50 chars, at least one letter, at least one digit, at least one uppercase letter

        // ARRANGE
        /* nothing */

        // ACT / ASSERT
        assertTrue(validateUsername.validate("AA"))
        assertTrue(validateUsername.validate("AAAA AAAA AAAA AAAA "))
        assertTrue(validateUsername.validate("!@#\$%^&*()_+(*&@)(#*&)(@#"))
        assertTrue(validateUsername.validate("12345678901234567890123456789012345678901234567890")) // 50 chars
    }

    @Test
    fun `validate() returns false for invalid usernames`() {

        // ARRANGE
        /* nothing */

        // ACT / ASSERT
        assertFalse(validateUsername.validate(""))
        assertFalse(validateUsername.validate("A"))
        assertFalse(validateUsername.validate("123456789012345678901234567890123456789012345678901")) // 51 chars
    }
}