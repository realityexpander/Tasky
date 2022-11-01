package com.realityexpander.tasky.domain.validation

import com.realityexpander.tasky.auth_feature.domain.validation.ValidateEmail
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Test


class ValidateEmailTest {

    private lateinit var validateEmail: ValidateEmail

    @Test
    fun `validateEmail() returns true for valid email`() {

        // ARRANGE
        validateEmail = ValidateEmail()

        // ACT / ASSERT
        assertTrue(validateEmail.validate("chris@demo.com"))
        assertTrue(validateEmail.validate("c@ca.ca"))
        assertTrue(validateEmail.validate("chris%1234@ca123.comms"))
        assertTrue(validateEmail.validate("chris_1234@ca123.com"))
        assertTrue(validateEmail.validate("chris+1234@ca123.coll"))
        assertTrue(validateEmail.validate("chris.1234@ca123.state.tx"))
    }

    @Test
    fun `validateEmail() returns false for invalid email`() {

        // ARRANGE
        validateEmail = ValidateEmail()

        // ACT / ASSERT
        assertFalse(validateEmail.validate("chrismail.com"))
        assertFalse(validateEmail.validate("c@c.c"))
        assertFalse(validateEmail.validate("c@ca.c"))
        assertFalse(validateEmail.validate("chrismail.com"))
        assertFalse(validateEmail.validate("chris@mail.com234"))
        assertFalse(validateEmail.validate("chris@mail.com234"))
    }
}