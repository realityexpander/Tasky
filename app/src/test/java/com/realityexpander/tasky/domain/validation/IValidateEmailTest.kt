package com.realityexpander.tasky.domain.validation

import com.realityexpander.tasky.domain.validation.validateEmail.IValidateEmail
import com.realityexpander.tasky.domain.validation.validateEmail.ValidateEmailImpl
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Test


class IValidateEmailTest {

    private lateinit var validateEmail: IValidateEmail
    private val emailMatcherFakeImpl = EmailMatcherFakeImpl()

    @Test
    fun `validateEmail() returns true for valid email`() {

        // ARRANGE
        validateEmail = ValidateEmailImpl(emailMatcherFakeImpl)

        // ACT
        val result = validateEmail.validate("chris@demo.com")

        // ASSERT
        assertTrue(result)
    }

    @Test
    fun `validateEmail() returns false for invalid email`() {

        // ARRANGE
        validateEmail = ValidateEmailImpl(emailMatcherFakeImpl)

        // ACT
        val result = validateEmail.validate("chrismail.com")

        // ASSERT
        assertFalse(result)
    }
}