package com.warrio.app.ui.product

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.warrio.app.product.ManualProductValidator
import com.warrio.app.ui.customer.WalletProductUiModel

data class ManualProductUiState(
    val name: String = "",
    val brand: String = "",
    val modelNumber: String = "",
    val serialNumber: String = "",
    val purchaseDate: String = "",
    val warrantyMonths: String = "",
    val nameError: String? = null,
    val brandError: String? = null,
    val purchaseDateError: String? = null,
    val warrantyMonthsError: String? = null,
    val createdProduct: WalletProductUiModel? = null,
)

class ManualProductViewModel : ViewModel() {
    var uiState by mutableStateOf(ManualProductUiState())
        private set

    internal fun prepareReceiptReview(text: String) {
        uiState = receiptProductDraft(text)
    }

    fun updateName(value: String) {
        uiState = uiState.copy(name = value, nameError = null)
    }

    fun updateBrand(value: String) {
        uiState = uiState.copy(brand = value, brandError = null)
    }

    fun updateModelNumber(value: String) {
        uiState = uiState.copy(modelNumber = value)
    }

    fun updateSerialNumber(value: String) {
        uiState = uiState.copy(serialNumber = value)
    }

    fun updatePurchaseDate(value: String) {
        uiState = uiState.copy(purchaseDate = value, purchaseDateError = null)
    }

    fun updateWarrantyMonths(value: String) {
        uiState = uiState.copy(warrantyMonths = value, warrantyMonthsError = null)
    }

    fun submit() {
        if (uiState.createdProduct != null) return
        val validation = ManualProductValidator.validate(
            name = uiState.name,
            brand = uiState.brand,
            purchaseDate = uiState.purchaseDate,
            warrantyMonths = uiState.warrantyMonths,
        )
        if (!validation.isValid) {
            uiState = uiState.copy(
                nameError = validation.nameError,
                brandError = validation.brandError,
                purchaseDateError = validation.purchaseDateError,
                warrantyMonthsError = validation.warrantyMonthsError,
            )
            return
        }

        uiState = uiState.copy(
            createdProduct = WalletProductUiModel(
                name = uiState.name.trim(),
                brand = uiState.brand.trim(),
                modelNumber = uiState.modelNumber.trim(),
                serialNumber = uiState.serialNumber.trim(),
                purchaseDate = uiState.purchaseDate,
                warrantyMonths = uiState.warrantyMonths.toInt(),
            ),
        )
    }
}

@Composable
fun ManualProductRoute(
    onBack: () -> Unit,
    onProductCreated: (WalletProductUiModel) -> Unit,
    viewModel: ManualProductViewModel = viewModel(),
    reviewingReceipt: Boolean = false,
) {
    ManualProductScreen(
        uiState = viewModel.uiState,
        onNameChanged = viewModel::updateName,
        onBrandChanged = viewModel::updateBrand,
        onModelNumberChanged = viewModel::updateModelNumber,
        onSerialNumberChanged = viewModel::updateSerialNumber,
        onPurchaseDateChanged = viewModel::updatePurchaseDate,
        onWarrantyMonthsChanged = viewModel::updateWarrantyMonths,
        onSubmit = viewModel::submit,
        onBack = onBack,
        reviewingReceipt = reviewingReceipt,
    )

    LaunchedEffect(viewModel.uiState.createdProduct) {
        viewModel.uiState.createdProduct?.let(onProductCreated)
    }
}

@Composable
private fun ManualProductScreen(
    uiState: ManualProductUiState,
    onNameChanged: (String) -> Unit,
    onBrandChanged: (String) -> Unit,
    onModelNumberChanged: (String) -> Unit,
    onSerialNumberChanged: (String) -> Unit,
    onPurchaseDateChanged: (String) -> Unit,
    onWarrantyMonthsChanged: (String) -> Unit,
    onSubmit: () -> Unit,
    onBack: () -> Unit,
    reviewingReceipt: Boolean,
) {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            TextButton(onClick = onBack) {
                Text(if (reviewingReceipt) "Back to receipt" else "Back to wallet")
            }
            Text(
                text = if (reviewingReceipt) "Review product details" else "Register a product",
                style = MaterialTheme.typography.headlineMedium,
            )
            Text(
                text = if (reviewingReceipt) {
                    "Check the suggested details and fill in anything missing. The product will be added as SELF_ADDED."
                } else {
                    "Manual registrations are saved as SELF_ADDED until an authorized retailer verifies them."
                },
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            ProductTextField(
                value = uiState.name,
                onValueChange = onNameChanged,
                label = "Product name",
                error = uiState.nameError,
            )
            ProductTextField(
                value = uiState.brand,
                onValueChange = onBrandChanged,
                label = "Brand",
                error = uiState.brandError,
            )
            ProductTextField(
                value = uiState.modelNumber,
                onValueChange = onModelNumberChanged,
                label = "Model number (optional)",
            )
            ProductTextField(
                value = uiState.serialNumber,
                onValueChange = onSerialNumberChanged,
                label = "Serial number (optional)",
            )
            ProductTextField(
                value = uiState.purchaseDate,
                onValueChange = onPurchaseDateChanged,
                label = "Purchase date (YYYY-MM-DD)",
                error = uiState.purchaseDateError,
                keyboardType = KeyboardType.Number,
            )
            ProductTextField(
                value = uiState.warrantyMonths,
                onValueChange = onWarrantyMonthsChanged,
                label = "Warranty duration in months",
                error = uiState.warrantyMonthsError,
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done,
            )

            Button(
                onClick = onSubmit,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Add to wallet")
            }
            Text(
                text = "Development mode: products are kept only for this app session.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun ProductTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    error: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        isError = error != null,
        supportingText = error?.let { message -> { Text(message) } },
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = imeAction,
        ),
    )
}
