package com.warrio.app.ui.customer

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

internal enum class ReceiptOcrStatus {
    IDLE,
    PROCESSING,
    SUCCESS,
    EMPTY,
    FAILED,
}

internal data class ReceiptOcrUiState(
    val status: ReceiptOcrStatus = ReceiptOcrStatus.IDLE,
    val text: String = "",
)

internal class ReceiptOcrState(
    private val scope: CoroutineScope,
    private val recognize: suspend () -> String,
) {
    var uiState by mutableStateOf(ReceiptOcrUiState())
        private set

    fun extract(): Job? {
        if (uiState.status == ReceiptOcrStatus.PROCESSING) return null
        uiState = ReceiptOcrUiState(status = ReceiptOcrStatus.PROCESSING)
        return scope.launch {
            try {
                val text = recognize().trim()
                uiState = ReceiptOcrUiState(
                    status = if (text.isBlank()) ReceiptOcrStatus.EMPTY else ReceiptOcrStatus.SUCCESS,
                    text = text,
                )
            } catch (error: Exception) {
                if (error is CancellationException) {
                    uiState = ReceiptOcrUiState()
                    throw error
                }
                uiState = ReceiptOcrUiState(status = ReceiptOcrStatus.FAILED)
            }
        }
    }

    fun editText(text: String) {
        if (uiState.status == ReceiptOcrStatus.PROCESSING) return
        uiState = uiState.copy(text = text)
    }
}
