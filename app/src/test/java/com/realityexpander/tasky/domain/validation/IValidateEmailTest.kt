package com.realityexpander.tasky.domain.validation

import com.realityexpander.tasky.data.validation.ValidateEmailImpl
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Test


class IValidateEmailTest {

    private lateinit var validateEmail: IValidateEmail

    private val emailMatcherFakeReturnsTrue = object : IEmailMatcher {
        override fun matches(email: String): Boolean {
            return true
        }
    }

    private val emailMatcherFakeReturnsFalse = object : IEmailMatcher {
        override fun matches(email: String): Boolean {
            return false
        }
    }

    @Test
    fun `validateEmail() returns true for valid email`() {

        // ARRANGE
        validateEmail = ValidateEmailImpl(emailMatcherFakeReturnsTrue)

        // ACT
        val result = validateEmail.validateEmail("chris@demo.com")

        // ASSERT
        assertTrue(result)
    }

    @Test
    fun `validateEmail() returns false for invalid email`() {

        // ARRANGE
        validateEmail = ValidateEmailImpl(emailMatcherFakeReturnsFalse)

        // ACT
        val result = validateEmail.validateEmail("chrismail.com")

        // ASSERT
        assertFalse(result)
    }
}