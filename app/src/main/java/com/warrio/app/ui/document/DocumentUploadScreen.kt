package com.warrio.app.ui.document

import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun DocumentUploadScreen(
    productName: String?,
    onBack: () -> Unit,
    onAttachDocument: (DocumentAttachmentUiModel) -> Unit,
) {
    val context = LocalContext.current
    var selectedDocument by remember { mutableStateOf<DocumentAttachmentUiModel?>(null) }
    var validationError by remember { mutableStateOf<String?>(null) }
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            runCatching {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION,
                )
            }
            val document = readDocumentMetadata(context.contentResolver, uri)
            selectedDocument = document
            validationError = validateDocument(document)
        }
    }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            TextButton(onClick = onBack) {
                Text("Back to product")
            }
            Text(
                text = "Add a document",
                style = MaterialTheme.typography.headlineMedium,
            )
            Text(
                text = productName?.let { "Attach a receipt or warranty document to $it." }
                    ?: "Select a product before attaching a document.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    val document = selectedDocument
                    if (document == null) {
                        Text("No document selected", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "Supported formats: PDF and images, up to 10 MB.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    } else {
                        Text(document.displayName, style = MaterialTheme.typography.titleMedium)
                        Text(
                            document.readableDescription,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }

                    validationError?.let { error ->
                        Text(error, color = MaterialTheme.colorScheme.error)
                    }

                    OutlinedButton(
                        onClick = { picker.launch(arrayOf("application/pdf", "image/*")) },
                    ) {
                        Text(if (document == null) "Choose document" else "Choose another")
                    }
                }
            }

            Button(
                onClick = { selectedDocument?.let(onAttachDocument) },
                enabled = productName != null && selectedDocument != null && validationError == null,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Attach to product")
            }
            Text(
                text = "Development mode: attachments are retained only for the current app session and are not uploaded.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

private fun readDocumentMetadata(
    contentResolver: ContentResolver,
    uri: Uri,
): DocumentAttachmentUiModel {
    var displayName = "Selected document"
    var sizeBytes: Long? = null

    contentResolver.query(
        uri,
        arrayOf(OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE),
        null,
        null,
        null,
    )?.use { cursor ->
        if (cursor.moveToFirst()) {
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
            if (nameIndex >= 0 && !cursor.isNull(nameIndex)) {
                displayName = cursor.getString(nameIndex)
            }
            if (sizeIndex >= 0 && !cursor.isNull(sizeIndex)) {
                sizeBytes = cursor.getLong(sizeIndex)
            }
        }
    }

    return DocumentAttachmentUiModel(
        displayName = displayName,
        mimeType = contentResolver.getType(uri).orEmpty(),
        sizeBytes = sizeBytes,
        uri = uri.toString(),
    )
}
