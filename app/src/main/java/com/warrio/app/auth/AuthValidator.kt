package com.warrio.app.auth

data class AuthValidationResult(
    val emailError: String? = null,
    val passwordError: String? = null,
    val displayNameError: String? = null,
    val confirmPasswordError: String? = null,
) {
    val isValid: Boolean
        get() = emailError == null &&
            passwordError == null &&
            displayNameError == null &&
            confirmPasswordError == null
}

object AuthValidator {
    private const val MINIMUM_PASSWORD_LENGTH = 8

    fun validateLogin(email: String, password: String): AuthValidationResult {
        return AuthValidationResult(
            emailError = validateEmail(email),
            passwordError = if (password.isBlank()) "Password is required." else null,
        )
    }

    fun validateRegistration(
        displayName: String,
        email: String,
        password: String,
        confirmPassword: String,
    ): AuthValidationResult {
        return AuthValidationResult(
            emailError = validateEmail(email),
            passwordError = when {
                password.isBlank() -> "Password is required."
                password.length < MINIMUM_PASSWORD_LENGTH ->
                    "Password must contain at least $MINIMUM_PASSWORD_LENGTH characters."
                else -> null
            },
            displayNameError = if (displayName.isBlank()) "Name is required." else null,
            confirmPasswordError = when {
                confirmPassword.isBlank() -> "Please confirm your password."
                confirmPassword != password -> "Passwords do not match."
                else -> null
            },
        )
    }

    private fun validateEmail(email: String): String? {
        val trimmedEmail = email.trim()
        return when {
            trimmedEmail.isBlank() -> "Email is required."
            !EMAIL_PATTERN.matches(trimmedEmail) -> "Enter a valid email address."
            else -> null
        }
    }

    private val EMAIL_PATTERN = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
}

