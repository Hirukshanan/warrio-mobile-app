package com.warrio.app.ui.claim

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class WarrantyClaimUiModelTest {
    @Test
    fun emptyAndShortDescriptionsAreRejected() {
        assertEquals(
            "Describe the problem with your product.",
            validateClaimDescription("  "),
        )
        assertEquals(
            "Add at least 20 characters so the problem is clear.",
            validateClaimDescription("Screen flickers"),
        )
    }

    @Test
    fun validDescriptionIsAccepted() {
        assertNull(
            validateClaimDescription(
                "The screen started flickering yesterday after normal use.",
            ),
        )
    }

    @Test
    fun descriptionOverLimitIsRejected() {
        assertEquals(
            "Keep the description at or below 1000 characters.",
            validateClaimDescription("x".repeat(MAX_CLAIM_DESCRIPTION_LENGTH + 1)),
        )
    }

    @Test
    fun exactlyOneThousandCharactersIsAccepted() {
        assertNull(validateClaimDescription("x".repeat(MAX_CLAIM_DESCRIPTION_LENGTH)))
    }

    @Test
    fun pastedTextAfterOneThousandCharactersIsNotSilentlyAccepted() {
        val pastedDescription = "x".repeat(MAX_CLAIM_DESCRIPTION_LENGTH) +
            " additional problem details"

        assertEquals(
            "Keep the description at or below 1000 characters.",
            validateClaimDescription(pastedDescription),
        )
    }

    @Test
    fun claimsReceiveUniqueIdsAndSubmittedStatus() {
        val first = claim()
        val second = claim()

        assertNotEquals(first.id, second.id)
        assertEquals(WarrantyClaimStatus.SUBMITTED, first.status)
        assertEquals("Submitted", first.status.displayName)
    }

    @Test
    fun claimIsStoredOnlyAgainstItsProductId() {
        val claims = mutableMapOf<String, List<WarrantyClaimUiModel>>()
        val claim = claim()

        addClaimToProduct(claims, claim)

        assertEquals(listOf(claim), claims["product-1"])
        assertTrue(claims["product-2"].isNullOrEmpty())
    }

    private fun claim() = WarrantyClaimUiModel(
        productId = "product-1",
        description = "The screen started flickering yesterday after normal use.",
    )
}
