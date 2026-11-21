package com.warrio.app.ui.customer

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun ReceiptScanScreen(onBackToWallet: () -> Unit) {
    var selectedImageUri by rememberSaveable { mutableStateOf<String?>(null) }
    val picker = rememberLauncherForActivityResult(PickVisualMedia()) { uri ->
        if (uri != null) selectedImageUri = uri.toString()
    }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            TextButton(onClick = onBackToWallet) { Text("Back to wallet") }
            Text("Scan receipt", style = MaterialTheme.typography.headlineMedium)
            Text(
                text = "Choose a clear photo of one receipt.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Button(
                onClick = { picker.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly)) },
            ) {
                Text(if (selectedImageUri == null) "Choose receipt image" else "Select another image")
            }
            val uri = selectedImageUri
            if (uri == null) {
                Text("No receipt image selected.")
            } else {
                key(uri) { ReceiptPreview(Uri.parse(uri)) }
            }
        }
    }
}

private sealed interface ReceiptPreviewState {
    data object Loading : ReceiptPreviewState
    data class Ready(val image: ImageBitmap) : ReceiptPreviewState
    data object Unreadable : ReceiptPreviewState
}

@Composable
private fun ReceiptPreview(uri: Uri) {
    val resolver = LocalContext.current.contentResolver
    val state by produceState<ReceiptPreviewState>(ReceiptPreviewState.Loading, uri, resolver) {
        value = try {
            withContext(Dispatchers.IO) {
                ReceiptPreviewState.Ready(loadReceiptImage(resolver, uri).asImageBitmap())
            }
        } catch (error: Exception) {
            if (error is CancellationException) throw error
            ReceiptPreviewState.Unreadable
        }
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp)
                .padding(12.dp)
                .semantics { liveRegion = LiveRegionMode.Polite },
            contentAlignment = Alignment.Center,
        ) {
            when (val preview = state) {
                ReceiptPreviewState.Loading -> Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    CircularProgressIndicator()
                    Text("Loading receipt image…")
                }
                is ReceiptPreviewState.Ready -> Image(
                    bitmap = preview.image,
                    contentDescription = "Selected receipt image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit,
                )
                ReceiptPreviewState.Unreadable -> Text(
                    text = "Couldn’t open this image. Choose another receipt photo.",
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }
    }
    if (state is ReceiptPreviewState.Ready) {
        ReceiptTextRecognition(uri)
    }
}
