package com.realityexpander.tasky.domain.validation

import com.realityexpander.tasky.data.validation.ValidateEmailImpl
import com.realityexpander.tasky.data.validation.ValidatePasswordImpl
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Test


class IValidatePasswordTest {

    private val validatePassword = ValidatePasswordImpl()

    @Test
    fun `validatePassword() returns true for valid passwords`() {

        // ARRANGE
        /* nothing */

        // ACT / ASSERT
        assertTrue(validatePassword.validatePassword("Password1"))
        assertTrue(validatePassword.validatePassword("1111aaAA"))
        assertTrue(validatePassword.validatePassword("1111aaAABZ"))
        assertTrue(validatePassword.validatePassword("1111aaAABZ!@#"))
        assertTrue(validatePassword.validatePassword("1111aaAABZ!@#\$%^&*()_+"))
    }

    @Test
    fun `validatePassword() returns false for invalid passwords`() {

        // ARRANGE
        /* nothing */

        // ACT / ASSERT
        assertFalse(validatePassword.validatePassword("A"))
        assertFalse(validatePassword.validatePassword("BB"))
        assertFalse(validatePassword.validatePassword("aaa"))
        assertFalse(validatePassword.validatePassword("111"))
        assertFalse(validatePassword.validatePassword("111aaaaaa"))
        assertFalse(validatePassword.validatePassword("Aaaaaaaa"))
        assertFalse(validatePassword.validatePassword("1111aaaa"))
        assertFalse(validatePassword.validatePassword("1111AAAA"))

    }
}