package com.warrio.app.ui.customer

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private sealed interface ReceiptTextState {
    data object Idle : ReceiptTextState
    data object Processing : ReceiptTextState
    data class Recognized(val text: String) : ReceiptTextState
    data object Failed : ReceiptTextState
}

@Composable
internal fun ReceiptTextRecognition(uri: Uri) {
    val resolver = LocalContext.current.contentResolver
    val scope = rememberCoroutineScope()
    var state by remember(uri) { mutableStateOf<ReceiptTextState>(ReceiptTextState.Idle) }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Button(
            enabled = state != ReceiptTextState.Processing,
            onClick = {
                state = ReceiptTextState.Processing
                scope.launch {
                    state = try {
                        val text = withContext(Dispatchers.IO) {
                            // Use a separate, larger input than the display preview for small text.
                            val bitmap = loadReceiptImage(resolver, uri, maxDimension = 2400)
                            try {
                                val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
                                try {
                                    // Await on IO so bitmap/client remain alive until ML Kit finishes,
                                    // even if navigation or another selection cancels this UI scope.
                                    Tasks.await(recognizer.process(InputImage.fromBitmap(bitmap, 0))).text.trim()
                                } finally {
                                    recognizer.close()
                                }
                            } finally {
                                bitmap.recycle()
                            }
                        }
                        ReceiptTextState.Recognized(text)
                    } catch (error: Exception) {
                        if (error is CancellationException) throw error
                        ReceiptTextState.Failed
                    }
                }
            },
        ) {
            Text(
                when (state) {
                    ReceiptTextState.Idle -> "Extract text"
                    ReceiptTextState.Processing -> "Extracting text..."
                    else -> "Try extraction again"
                },
            )
        }
        Column(
            modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            when (val result = state) {
                ReceiptTextState.Idle -> Text(
                    text = "Extract the receipt text, then check it for mistakes. Latin-script text is supported.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                ReceiptTextState.Processing -> {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    Text("Reading receipt text...")
                }
                is ReceiptTextState.Recognized -> {
                    if (result.text.isBlank()) {
                        Text("No text found. Try a clearer, well-lit receipt photo with readable text.")
                    } else {
                        Text("Recognized text", style = MaterialTheme.typography.titleLarge)
                        Text(
                            "Check this text against your receipt. Product details have not been saved.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        SelectionContainer { Text(result.text) }
                    }
                }
                ReceiptTextState.Failed -> Text(
                    text = "Couldn't read the receipt. Try again or choose another image.",
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }
    }
}
