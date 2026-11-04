package com.warrio.app.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

enum class AuthMode {
    LOGIN,
    REGISTER,
}

data class AuthUiState(
    val displayName: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val displayNameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val generalError: String? = null,
    val isSubmitting: Boolean = false,
    val authenticatedSession: AuthSession? = null,
)

class AuthViewModel(
    private val authRepository: AuthRepository = FakeAuthRepository(),
) : ViewModel() {
    var uiState by mutableStateOf(AuthUiState())
        private set

    fun updateDisplayName(value: String) {
        uiState = uiState.copy(displayName = value, displayNameError = null, generalError = null)
    }

    fun updateEmail(value: String) {
        uiState = uiState.copy(email = value, emailError = null, generalError = null)
    }

    fun updatePassword(value: String) {
        uiState = uiState.copy(password = value, passwordError = null, generalError = null)
    }

    fun updateConfirmPassword(value: String) {
        uiState = uiState.copy(
            confirmPassword = value,
            confirmPasswordError = null,
            generalError = null,
        )
    }

    fun submit(mode: AuthMode) {
        if (uiState.isSubmitting) return

        val validation = when (mode) {
            AuthMode.LOGIN -> AuthValidator.validateLogin(uiState.email, uiState.password)
            AuthMode.REGISTER -> AuthValidator.validateRegistration(
                displayName = uiState.displayName,
                email = uiState.email,
                password = uiState.password,
                confirmPassword = uiState.confirmPassword,
            )
        }

        if (!validation.isValid) {
            uiState = uiState.copy(
                displayNameError = validation.displayNameError,
                emailError = validation.emailError,
                passwordError = validation.passwordError,
                confirmPasswordError = validation.confirmPasswordError,
                generalError = null,
            )
            return
        }

        uiState = uiState.copy(isSubmitting = true, generalError = null)
        viewModelScope.launch {
            val result = when (mode) {
                AuthMode.LOGIN -> authRepository.signIn(
                    email = uiState.email,
                    password = uiState.password,
                )
                AuthMode.REGISTER -> authRepository.register(
                    displayName = uiState.displayName,
                    email = uiState.email,
                    password = uiState.password,
                )
            }

            uiState = when (result) {
                is AuthResult.Success -> uiState.copy(
                    isSubmitting = false,
                    authenticatedSession = result.session,
                )
                is AuthResult.Failure -> uiState.copy(
                    isSubmitting = false,
                    generalError = result.message,
                )
            }
        }
    }
}

