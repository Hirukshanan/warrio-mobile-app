package com.warrio.app.ui.customer

import org.junit.Assert.assertEquals
import org.junit.Test

class ProductDetailsTest {
    @Test
    fun missingOptionalValueUsesFallbackLabel() {
        assertEquals("Not provided", "".orNotProvided())
        assertEquals("Not provided", "   ".orNotProvided())
    }

    @Test
    fun suppliedOptionalValueIsPreserved() {
        assertEquals("W-2026", "W-2026".orNotProvided())
    }
}
