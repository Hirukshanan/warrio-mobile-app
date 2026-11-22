package com.warrio.app.ui.product

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Test

class ReceiptProductDraftTest {
    @Test
    fun readsLabelledFieldsWithValidDateAndWarranty() {
        val draft = receiptProductDraft(
            """
            Product name: Desk lamp
            BRAND: Example Brand
            Model: L-100
            S/N: EX-123
            Purchase date: 2026-11-21
            Warranty: 12 months
            """.trimIndent(),
        )

        assertEquals(
            ManualProductUiState(
                name = "Desk lamp",
                brand = "Example Brand",
                modelNumber = "L-100",
                serialNumber = "EX-123",
                purchaseDate = "2026-11-21",
                warrantyMonths = "12",
            ),
            draft,
        )
    }

    @Test
    fun leavesAmbiguousOrInvalidFieldsBlank() {
        val draft = receiptProductDraft(
            """
            EXAMPLE SHOP
            Brand: First
            Brand: Second
            Date: 2026-02-30
            Warranty months: 500
            Total: 1000.00
            """.trimIndent(),
        )

        assertEquals(ManualProductUiState(), draft)
        assertEquals("", receiptProductDraft("Date: 11/12/2026\nWarranty: 2 years").purchaseDate)
        assertEquals("", receiptProductDraft("Warranty: 2 years").warrantyMonths)
    }

    @Test
    fun multipleProductsDoNotCombineProductSpecificFields() {
        val draft = receiptProductDraft(
            """
            Item: Desk lamp
            Brand: Example Brand
            Item: Display
            Serial: EX-222
            Warranty months: 12
            Date: 2026-11-21
            """.trimIndent(),
        )

        assertEquals(ManualProductUiState(purchaseDate = "2026-11-21"), draft)
    }

    @Test
    fun productIsCreatedOnlyAfterValidConfirmationAndUsesCustomerEdits() {
        val model = ManualProductViewModel()
        model.prepareReceiptReview("Product: Desk Iamp\nWarranty: 12 months")
        assertNull(model.uiState.createdProduct)
        model.submit()
        assertNull(model.uiState.createdProduct)

        model.updateName("Desk lamp")
        model.updateBrand("Example Brand")
        model.updatePurchaseDate("2026-11-21")
        model.submit()

        val product = checkNotNull(model.uiState.createdProduct)
        assertEquals("Desk lamp", product.name)
        assertEquals("Example Brand", product.brand)
        assertEquals(12, product.warrantyMonths)
        model.submit()
        assertSame(product, model.uiState.createdProduct)
    }
}
