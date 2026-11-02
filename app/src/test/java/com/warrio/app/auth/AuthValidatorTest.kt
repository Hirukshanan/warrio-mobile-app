package com.warrio.app.auth

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthValidatorTest {
    @Test
    fun validLoginPassesValidation() {
        val result = AuthValidator.validateLogin(
            email = "customer@example.com",
            password = "password123",
        )

        assertTrue(result.isValid)
        assertNull(result.emailError)
        assertNull(result.passwordError)
    }

    @Test
    fun malformedEmailIsRejected() {
        val result = AuthValidator.validateLogin(
            email = "not-an-email",
            password = "password123",
        )

        assertFalse(result.isValid)
        assertEquals("Enter a valid email address.", result.emailError)
    }

    @Test
    fun shortRegistrationPasswordIsRejected() {
        val result = AuthValidator.validateRegistration(
            displayName = "Ilmam",
            email = "ilmam@example.com",
            password = "short",
            confirmPassword = "short",
        )

        assertFalse(result.isValid)
        assertEquals("Password must contain at least 8 characters.", result.passwordError)
    }

    @Test
    fun mismatchedPasswordsAreRejected() {
        val result = AuthValidator.validateRegistration(
            displayName = "Ilmam",
            email = "ilmam@example.com",
            password = "password123",
            confirmPassword = "different123",
        )

        assertFalse(result.isValid)
        assertEquals("Passwords do not match.", result.confirmPasswordError)
    }

    @Test
    fun validRegistrationPassesValidation() {
        val result = AuthValidator.validateRegistration(
            displayName = "Ilmam",
            email = "ilmam@example.com",
            password = "password123",
            confirmPassword = "password123",
        )

        assertTrue(result.isValid)
    }
}
