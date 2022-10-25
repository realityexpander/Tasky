package com.realityexpander.tasky.domain.validation

import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Test


class ValidatePasswordTest {

    private val validatePassword = ValidatePassword()

    @Test
    fun `validate() returns true for valid passwords`() {

        // ARRANGE
        /* nothing */

        // ACT / ASSERT
        assertTrue(validatePassword.validate("Password1"))
        assertTrue(validatePassword.validate("1111aaAA"))
        assertTrue(validatePassword.validate("1111aaAABZ"))
        assertTrue(validatePassword.validate("1111aaAABZ!@#"))
        assertTrue(validatePassword.validate("1111aaAABZ!@#\$%^&*()_+"))
    }

    @Test
    fun `validate() returns false for invalid passwords`() {

        // ARRANGE
        /* nothing */

        // ACT / ASSERT
        assertFalse(validatePassword.validate("A"))
        assertFalse(validatePassword.validate("BB"))
        assertFalse(validatePassword.validate("aaa"))
        assertFalse(validatePassword.validate("111"))
        assertFalse(validatePassword.validate("111aaaaaa"))
        assertFalse(validatePassword.validate("Aaaaaaaa"))
        assertFalse(validatePassword.validate("1111aaaa"))
        assertFalse(validatePassword.validate("1111AAAA"))
    }
}