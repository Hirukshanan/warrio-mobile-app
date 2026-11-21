package com.warrio.app.ui

import com.warrio.app.ui.customer.WalletProductUiModel
import com.warrio.app.ui.document.DocumentAttachmentUiModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ProductDocumentOwnershipTest {
    @Test
    fun attachmentBelongsOnlyToSelectedProductWhenProductsHaveIdenticalFields() {
        val firstProduct = identicalProduct()
        val secondProduct = identicalProduct()
        val productDocuments = mutableMapOf<String, List<DocumentAttachmentUiModel>>()
        val document = DocumentAttachmentUiModel(
            displayName = "receipt.pdf",
            mimeType = "application/pdf",
            sizeBytes = 1024L,
            uri = "content://documents/receipt",
        )

        attachDocumentToProduct(productDocuments, firstProduct.id, document)

        assertNotEquals(firstProduct.id, secondProduct.id)
        assertEquals(listOf(document), productDocuments[firstProduct.id])
        assertTrue(productDocuments[secondProduct.id].isNullOrEmpty())
    }

    private fun identicalProduct() = WalletProductUiModel(
        name = "Laptop",
        brand = "Warrio",
        modelNumber = "W-1",
        serialNumber = "SN-001",
        purchaseDate = "2026-11-21",
        warrantyMonths = 12,
    )
}
