package com.warrio.app.auth

data class AuthSession(
    val userId: String,
    val email: String,
    val displayName: String?,
)

sealed interface AuthResult {
    data class Success(val session: AuthSession) : AuthResult
    data class Failure(val message: String) : AuthResult
}

interface AuthRepository {
    suspend fun signIn(email: String, password: String): AuthResult

    suspend fun register(
        displayName: String,
        email: String,
        password: String,
    ): AuthResult
}

