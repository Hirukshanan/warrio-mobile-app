package com.warrio.app.ui.claim

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.warrio.app.ui.customer.WalletProductUiModel

@Composable
fun ClaimSubmissionScreen(
    product: WalletProductUiModel?,
    onBack: () -> Unit,
    onSubmitClaim: (WarrantyClaimUiModel) -> Unit,
) {
    var description by remember { mutableStateOf("") }
    var descriptionError by remember { mutableStateOf<String?>(null) }

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
                Text("Back to product")
            }
            Text(
                text = "Start a warranty claim",
                style = MaterialTheme.typography.headlineMedium,
            )

            if (product == null) {
                Text(
                    text = "The selected product is no longer available in this app session.",
                    color = MaterialTheme.colorScheme.error,
                )
            } else {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Text(product.name, style = MaterialTheme.typography.titleLarge)
                        Text(
                            product.brand,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                Text(
                    text = "Explain the fault, when it started, and anything you have already tried.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { value ->
                        description = value
                        descriptionError = null
                    },
                    label = { Text("Problem description") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 5,
                    maxLines = 10,
                    isError = descriptionError != null,
                    supportingText = {
                        Text(
                            descriptionError
                                ?: "${description.length}/$MAX_CLAIM_DESCRIPTION_LENGTH characters",
                        )
                    },
                )
                Button(
                    onClick = {
                        val error = validateClaimDescription(description)
                        descriptionError = error
                        if (error == null) {
                            onSubmitClaim(
                                WarrantyClaimUiModel(
                                    productId = product.id,
                                    description = description.trim(),
                                ),
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Submit claim")
                }
                Text(
                    text = "Development mode: this submission is retained only for the current app session and is not sent to a retailer or service provider.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
