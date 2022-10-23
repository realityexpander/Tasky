package com.realityexpander.tasky.domain.validation

import com.realityexpander.tasky.data.validation.ValidateEmailImpl
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Test


class IValidateEmailTest {

    private lateinit var validateEmail: IValidateEmail

    private val emailMatcherFake = object : IEmailMatcher {
        override fun matches(email: String): Boolean {
            return  email.contains("@")
        }
    }

    @Test
    fun `validateEmail() returns true for valid email`() {

        // ARRANGE
        validateEmail = ValidateEmailImpl(emailMatcherFake)

        // ACT
        val result = validateEmail.validateEmail("chris@demo.com")

        // ASSERT
        assertTrue(result)
    }

    @Test
    fun `validateEmail() returns false for invalid email`() {

        // ARRANGE
        validateEmail = ValidateEmailImpl(emailMatcherFake)

        // ACT
        val result = validateEmail.validateEmail("chrismail.com")

        // ASSERT
        assertFalse(result)
    }
}