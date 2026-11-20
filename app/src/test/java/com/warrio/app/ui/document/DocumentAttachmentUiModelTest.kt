package com.warrio.app.ui.document

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DocumentAttachmentUiModelTest {
    @Test
    fun acceptsPdfAndImageDocuments() {
        assertNull(validateDocument(document(mimeType = "application/pdf")))
        assertNull(validateDocument(document(mimeType = "image/jpeg")))
    }

    @Test
    fun rejectsUnsupportedDocumentType() {
        assertEquals(
            "Choose a PDF or image file.",
            validateDocument(document(mimeType = "text/plain")),
        )
    }

    @Test
    fun rejectsDocumentsLargerThanTenMegabytes() {
        assertEquals(
            "Choose a file smaller than 10 MB.",
            validateDocument(
                document(
                    mimeType = "application/pdf",
                    sizeBytes = MAX_DOCUMENT_SIZE_BYTES + 1,
                ),
            ),
        )
    }

    @Test
    fun formatsDocumentSizeForDisplay() {
        assertEquals("512 bytes", 512L.toReadableFileSize())
        assertEquals("2.0 KB", 2048L.toReadableFileSize())
        assertEquals("1.5 MB", 1_572_864L.toReadableFileSize())
        assertEquals("Unknown size", null.toReadableFileSize())
    }

    private fun document(
        mimeType: String,
        sizeBytes: Long? = 1024L,
    ) = DocumentAttachmentUiModel(
        displayName = "receipt.pdf",
        mimeType = mimeType,
        sizeBytes = sizeBytes,
        uri = "content://documents/receipt",
    )
}
