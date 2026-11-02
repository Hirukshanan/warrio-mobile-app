package com.warrio.app.auth

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FakeAuthRepositoryTest {
    @Test
    fun signInReturnsDevelopmentSession() = runBlocking {
        val result = FakeAuthRepository().signIn(
            email = "customer@example.com",
            password = "password123",
        )

        assertTrue(result is AuthResult.Success)
        assertEquals(
            "customer@example.com",
            (result as AuthResult.Success).session.email,
        )
    }

    @Test
    fun configuredFailureReturnsSafeMessage() = runBlocking {
        val result = FakeAuthRepository(shouldFail = true).register(
            displayName = "Customer",
            email = "customer@example.com",
            password = "password123",
        )

        assertTrue(result is AuthResult.Failure)
        assertEquals(
            "Unable to create the account. Please try again.",
            (result as AuthResult.Failure).message,
        )
    }
}
