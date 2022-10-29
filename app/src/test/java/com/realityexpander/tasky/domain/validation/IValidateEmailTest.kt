package com.realityexpander.tasky.domain.validation

import com.realityexpander.tasky.domain.validation.validateEmail.EmailMatcherRegexImpl
import com.realityexpander.tasky.domain.validation.validateEmail.IValidateEmail
import com.realityexpander.tasky.domain.validation.validateEmail.ValidateEmailImpl
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Test


class IValidateEmailTest {

    private lateinit var validateEmail: IValidateEmail
    private val emailMatcherFakeImpl = EmailMatcherFakeImpl()
    private val emailMatcherRegexImpl = EmailMatcherRegexImpl()

    @Test // NOTE: we are testing the fake implementation (bc the prod impl uses android.util.Patterns.EMAIL_ADDRESS)
    fun `validateEmail() returns true for valid email`() {

        // ARRANGE
        validateEmail = ValidateEmailImpl(emailMatcherFakeImpl)

        // ACT / ASSERT
        assertTrue(validateEmail.validate("chris@demo.com")) // valid email

    }

    @Test // NOTE: we are testing the fake implementation (bc the prod impl uses android.util.Patterns.EMAIL_ADDRESS)
    fun `validateEmail() returns false for invalid email`() {

        // ARRANGE
        validateEmail = ValidateEmailImpl(emailMatcherFakeImpl)

        // ACT / ASSERT
        assertFalse(validateEmail.validate("chrismail.com")) // missing @
    }

    @Test
    fun `validateRegexEmail() returns true for valid email`() {

        // ARRANGE
        validateEmail = ValidateEmailImpl(emailMatcherRegexImpl)

        // ACT / ASSERT
        assertTrue(validateEmail.validate("chris@demo.com"))
        assertTrue(validateEmail.validate("c@ca.ca"))
        assertTrue(validateEmail.validate("chris%1234@ca123.comms"))
        assertTrue(validateEmail.validate("chris_1234@ca123.com"))
        assertTrue(validateEmail.validate("chris+1234@ca123.coll"))
        assertTrue(validateEmail.validate("chris.1234@ca123.state.tx"))
    }

    @Test
    fun `validateRegexEmail() returns false for invalid email`() {

        // ARRANGE
        validateEmail = ValidateEmailImpl(emailMatcherRegexImpl)

        // ACT / ASSERT
        assertFalse(validateEmail.validate("chrismail.com"))
        assertFalse(validateEmail.validate("c@c.c"))
        assertFalse(validateEmail.validate("c@ca.c"))
        assertFalse(validateEmail.validate("chrismail.com"))
        assertFalse(validateEmail.validate("chris@mail.com234"))
        assertFalse(validateEmail.validate("chris@mail.com234"))
    }
}