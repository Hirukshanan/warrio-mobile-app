package com.warrio.app.ui.customer

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
internal fun ReceiptTextRecognition(uri: Uri, onReviewProduct: (String) -> Unit) {
    val resolver = LocalContext.current.contentResolver
    val scope = rememberCoroutineScope()
    val state = remember(uri, resolver, scope) {
        ReceiptOcrState(scope) {
            withContext(Dispatchers.IO) {
                // OCR uses a larger, bounded input than the display preview.
                val bitmap = loadReceiptImage(resolver, uri, maxDimension = 2400)
                try {
                    val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
                    try {
                        // Wait on IO so the bitmap/client remain alive until ML Kit
                        // finishes, even when this screen's coroutine is cancelled.
                        Tasks.await(recognizer.process(InputImage.fromBitmap(bitmap, 0))).text
                    } finally {
                        recognizer.close()
                    }
                } finally {
                    bitmap.recycle()
                }
            }
        }
    }
    val result = state.uiState

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Button(
            enabled = result.status != ReceiptOcrStatus.PROCESSING,
            onClick = { state.extract() },
        ) {
            Text(
                when (result.status) {
                    ReceiptOcrStatus.IDLE -> "Extract text"
                    ReceiptOcrStatus.PROCESSING -> "Extracting text..."
                    else -> "Try extraction again"
                },
            )
        }
        Column(
            modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            when (result.status) {
                ReceiptOcrStatus.IDLE -> Text(
                    text = "Extract the receipt text, then check it for mistakes. Latin-script text is supported.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                ReceiptOcrStatus.PROCESSING -> {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    Text("Reading receipt text...")
                }
                ReceiptOcrStatus.SUCCESS -> Text("Text extracted. Review and correct it below.")
                ReceiptOcrStatus.EMPTY -> Text(
                    "No text found. Try a clearer receipt photo or type the receipt text below.",
                )
                ReceiptOcrStatus.FAILED -> Text(
                    text = "Couldn't read the receipt. Try again or choose another image.",
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }
        if (result.status in setOf(ReceiptOcrStatus.SUCCESS, ReceiptOcrStatus.EMPTY, ReceiptOcrStatus.FAILED)) {
            OutlinedTextField(
                value = result.text,
                onValueChange = state::editText,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Receipt text (editable)") },
                minLines = 5,
                maxLines = 12,
            )
            Text(
                text = "Extracting again replaces your edits.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Button(
                onClick = { onReviewProduct(result.text) },
                enabled = result.text.isNotBlank(),
            ) {
                Text("Review product details")
            }
        }
    }
}
