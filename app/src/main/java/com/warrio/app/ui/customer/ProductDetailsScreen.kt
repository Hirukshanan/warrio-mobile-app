package com.warrio.app.ui.customer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.warrio.app.ui.document.DocumentAttachmentUiModel

@Composable
fun ProductDetailsScreen(
    product: WalletProductUiModel?,
    documents: List<DocumentAttachmentUiModel>,
    onBack: () -> Unit,
    onAddDocument: () -> Unit,
) {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            TextButton(onClick = onBack) {
                Text("Back to wallet")
            }

            if (product == null) {
                MissingProductDetails(onBack = onBack)
            } else {
                ProductDetails(
                    product = product,
                    documents = documents,
                    onAddDocument = onAddDocument,
                )
            }
        }
    }
}

@Composable
private fun ProductDetails(
    product: WalletProductUiModel,
    documents: List<DocumentAttachmentUiModel>,
    onAddDocument: () -> Unit,
) {
    Text(
        text = product.name,
        style = MaterialTheme.typography.headlineMedium,
    )
    Text(
        text = product.brand,
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text(
                text = "Product information",
                style = MaterialTheme.typography.titleLarge,
            )
            ProductDetailRow("Model number", product.modelNumber.orNotProvided())
            ProductDetailRow("Serial number", product.serialNumber.orNotProvided())
            ProductDetailRow("Purchase date", product.purchaseDate)
            ProductDetailRow("Warranty period", "${product.warrantyMonths} months")
        }
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                text = "Documents",
                style = MaterialTheme.typography.titleLarge,
            )
            if (documents.isEmpty()) {
                Text(
                    text = "No documents attached yet.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                documents.forEach { document ->
                    Column {
                        Text(
                            text = document.displayName,
                            fontWeight = FontWeight.Medium,
                        )
                        Text(
                            text = document.readableDescription,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
            Button(onClick = onAddDocument) {
                Text("Add document")
            }
        }
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "SELF_ADDED",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = "This product was added by the customer and is not retailer-verified.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = "Only an authorized retailer workflow or validated Warrio QR can mark a warranty as VERIFIED.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }

    Text(
        text = "Development mode: this product is available only for the current app session.",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
private fun ProductDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            modifier = Modifier.weight(1f),
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.End,
        )
    }
}

@Composable
private fun MissingProductDetails(onBack: () -> Unit) {
    Text(
        text = "Product unavailable",
        style = MaterialTheme.typography.headlineMedium,
    )
    Text(
        text = "The selected product is no longer available in this app session.",
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    TextButton(onClick = onBack) {
        Text("Return to wallet")
    }
}

internal fun String.orNotProvided(): String = ifBlank { "Not provided" }
