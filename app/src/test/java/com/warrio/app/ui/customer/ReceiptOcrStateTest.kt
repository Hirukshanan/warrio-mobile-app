package com.warrio.app.ui.customer

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.yield
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ReceiptOcrStateTest {
    @Test
    fun extractionShowsLoadingUntilRecognitionCompletes() = runBlocking {
        val response = CompletableDeferred<String>()
        val state = ReceiptOcrState(this) { response.await() }
        assertEquals(ReceiptOcrStatus.IDLE, state.uiState.status)

        val job = state.extract()!!
        assertEquals(ReceiptOcrStatus.PROCESSING, state.uiState.status)
        response.complete("  SHOP\nTotal 250.00  ")
        job.join()

        assertEquals(ReceiptOcrStatus.SUCCESS, state.uiState.status)
        assertEquals("SHOP\nTotal 250.00", state.uiState.text)
    }

    @Test
    fun blankRecognitionHasAnExplicitEmptyStateAndAllowsManualCorrection() = runBlocking {
        val state = ReceiptOcrState(this) { " \n\t " }
        state.extract()!!.join()

        assertEquals(ReceiptOcrStatus.EMPTY, state.uiState.status)
        assertEquals("", state.uiState.text)
        state.editText("Total 250.00")
        assertEquals("Total 250.00", state.uiState.text)
    }

    @Test
    fun recognizedTextCanBeCorrectedOrClearedWithoutRunningOcrAgain() = runBlocking {
        var calls = 0
        val state = ReceiptOcrState(this) { calls++; "Total 25O.OO" }
        state.extract()!!.join()

        state.editText("Total 250.00")
        assertEquals("Total 250.00", state.uiState.text)
        assertEquals(ReceiptOcrStatus.SUCCESS, state.uiState.status)
        state.editText("")
        assertEquals("", state.uiState.text)
        assertEquals(1, calls)
    }

    @Test
    fun recognitionFailureCanBeRetried() = runBlocking {
        var calls = 0
        val state = ReceiptOcrState(this) {
            calls++
            if (calls == 1) throw IllegalStateException("Synthetic OCR failure")
            "Total 250.00"
        }
        state.extract()!!.join()
        assertEquals(ReceiptOcrStatus.FAILED, state.uiState.status)
        assertEquals("", state.uiState.text)

        state.extract()!!.join()
        assertEquals(ReceiptOcrStatus.SUCCESS, state.uiState.status)
        assertEquals("Total 250.00", state.uiState.text)
    }

    @Test
    fun duplicateRequestsAndEditsAreIgnoredWhileProcessing() = runBlocking {
        val response = CompletableDeferred<String>()
        var calls = 0
        val state = ReceiptOcrState(this) { calls++; response.await() }
        val job = state.extract()!!
        assertNull(state.extract())
        state.editText("Must not replace active recognition")
        assertEquals("", state.uiState.text)

        response.complete("Total 250.00")
        job.join()
        assertEquals(1, calls)
    }

    @Test
    fun cancellationDoesNotBecomeAnOcrFailureOrPublishALateResult() = runBlocking {
        val response = CompletableDeferred<String>()
        val state = ReceiptOcrState(this) { response.await() }
        val job = state.extract()!!
        yield()
        job.cancelAndJoin()
        response.complete("Old receipt result")

        assertEquals(ReceiptOcrStatus.IDLE, state.uiState.status)
        assertEquals("", state.uiState.text)
    }

    @Test
    fun intentionalReExtractionReplacesPreviousEdits() = runBlocking {
        var recognized = "First receipt text"
        val state = ReceiptOcrState(this) { recognized }
        state.extract()!!.join()
        state.editText("Corrected by customer")

        recognized = "Second extraction result"
        val job = state.extract()!!
        assertEquals("", state.uiState.text)
        job.join()
        assertEquals("Second extraction result", state.uiState.text)
    }
}
