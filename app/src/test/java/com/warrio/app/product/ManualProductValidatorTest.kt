package com.warrio.app.product

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ManualProductValidatorTest {
    @Test
    fun emptyRequiredFieldsAreRejected() {
        val result = ManualProductValidator.validate("", "", "", "")

        assertFalse(result.isValid)
        assertEquals("Product name is required.", result.nameError)
        assertEquals("Brand is required.", result.brandError)
        assertEquals("Purchase date is required.", result.purchaseDateError)
        assertEquals("Warranty duration is required.", result.warrantyMonthsError)
    }

    @Test
    fun impossibleDateIsRejected() {
        val result = validInput(purchaseDate = "2026-02-30")

        assertEquals("Use a valid date in YYYY-MM-DD format.", result.purchaseDateError)
    }

    @Test
    fun incorrectlyFormattedDateIsRejected() {
        val result = validInput(purchaseDate = "16/11/2026")

        assertEquals("Use a valid date in YYYY-MM-DD format.", result.purchaseDateError)
    }

    @Test
    fun warrantyOutsideSupportedRangeIsRejected() {
        assertEquals(
            "Warranty duration must be between 1 and 120 months.",
            validInput(warrantyMonths = "0").warrantyMonthsError,
        )
        assertEquals(
            "Warranty duration must be between 1 and 120 months.",
            validInput(warrantyMonths = "121").warrantyMonthsError,
        )
    }

    @Test
    fun validManualProductIsAccepted() {
        assertTrue(validInput().isValid)
    }

    private fun validInput(
        purchaseDate: String = "2026-11-16",
        warrantyMonths: String = "24",
    ): ManualProductValidation {
        return ManualProductValidator.validate(
            name = "Laptop",
            brand = "Warrio Demo",
            purchaseDate = purchaseDate,
            warrantyMonths = warrantyMonths,
        )
    }
}
