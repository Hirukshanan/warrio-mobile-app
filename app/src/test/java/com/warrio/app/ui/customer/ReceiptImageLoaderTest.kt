package com.warrio.app.ui.customer

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ReceiptImageLoaderTest {
    @Test
    fun smallImagesKeepTheirResolution() {
        assertEquals(1, receiptSampleSize(800, 1200, 1600))
    }

    @Test
    fun portraitAndLandscapePhotosStayWithinThePreviewLimit() {
        assertEquals(4, receiptSampleSize(4000, 3000, 1600))
        assertEquals(4, receiptSampleSize(3000, 4000, 1600))
    }

    @Test
    fun ocrInputKeepsMoreDetailThanTheDisplayPreview() {
        assertEquals(2, receiptSampleSize(4000, 3000, 2400))
        assertTrue(receiptSampleSize(4000, 3000, 2400) < receiptSampleSize(4000, 3000, 1600))
    }

    @Test
    fun roundsUpDimensionsBeforeComparingToLimit() {
        assertEquals(4, receiptSampleSize(3201, 2000, 1600))
    }

    @Test(expected = IllegalArgumentException::class)
    fun rejectsInvalidImageDimensions() {
        receiptSampleSize(0, 1200, 1600)
    }
}
