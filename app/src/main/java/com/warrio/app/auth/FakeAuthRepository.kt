package com.warrio.app.auth

/**
 * Local development implementation. It performs no network request and must be
 * replaced by the Firebase-backed implementation before production use.
 */
class FakeAuthRepository(
    private val shouldFail: Boolean = false,
) : AuthRepository {
    override suspend fun signIn(email: String, password: String): AuthResult {
        if (shouldFail) {
            return AuthResult.Failure("Unable to sign in. Please try again.")
        }

        return AuthResult.Success(
            AuthSession(
                userId = "fake-customer-id",
                email = email.trim(),
                displayName = null,
            ),
        )
    }

    override suspend fun register(
        displayName: String,
        email: String,
        password: String,
    ): AuthResult {
        if (shouldFail) {
            return AuthResult.Failure("Unable to create the account. Please try again.")
        }

        return AuthResult.Success(
            AuthSession(
                userId = "fake-customer-id",
                email = email.trim(),
                displayName = displayName.trim(),
            ),
        )
    }
}

