package com.warrio.app.ui.customer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.util.UUID

data class WalletProductUiModel(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val brand: String,
    val modelNumber: String,
    val serialNumber: String,
    val purchaseDate: String,
    val warrantyMonths: Int,
)

@Composable
fun ProductWalletScreen(
    products: List<WalletProductUiModel>,
    onSelectTab: (CustomerTab) -> Unit,
    onScanReceipt: () -> Unit,
    onAddProduct: () -> Unit,
    onOpenProduct: (WalletProductUiModel) -> Unit,
) {
    CustomerScaffold(
        selectedTab = CustomerTab.WALLET,
        onSelectTab = onSelectTab,
    ) { innerPadding ->
        if (products.isEmpty()) {
            EmptyWallet(
                modifier = Modifier.padding(innerPadding),
                onAddProduct = onAddProduct,
                onScanReceipt = onScanReceipt,
            )
        } else {
            WalletProductList(
                products = products,
                modifier = Modifier.padding(innerPadding),
                onAddProduct = onAddProduct,
                onScanReceipt = onScanReceipt,
                onOpenProduct = onOpenProduct,
            )
        }
    }
}

@Composable
private fun EmptyWallet(
    modifier: Modifier,
    onAddProduct: () -> Unit,
    onScanReceipt: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Product wallet",
            style = MaterialTheme.typography.headlineMedium,
        )
        Text(
            text = "No products yet",
            modifier = Modifier.padding(top = 20.dp),
            style = MaterialTheme.typography.titleLarge,
        )
        Text(
            text = "Products you register manually or add from a receipt will appear here.",
            modifier = Modifier.padding(top = 8.dp),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Button(
            onClick = onAddProduct,
            modifier = Modifier.padding(top = 20.dp),
        ) {
            Text("Register a product")
        }
        Button(
            onClick = onScanReceipt,
            modifier = Modifier.padding(top = 12.dp),
        ) {
            Text("Scan receipt")
        }
    }
}

@Composable
private fun WalletProductList(
    products: List<WalletProductUiModel>,
    modifier: Modifier,
    onAddProduct: () -> Unit,
    onScanReceipt: () -> Unit,
    onOpenProduct: (WalletProductUiModel) -> Unit,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Text(
                text = "Product wallet",
                modifier = Modifier.padding(top = 24.dp),
                style = MaterialTheme.typography.headlineMedium,
            )
        }
        item {
            Button(onClick = onAddProduct) {
                Text("Register another product")
            }
        }
        item {
            Button(onClick = onScanReceipt) {
                Text("Scan receipt")
            }
        }
        items(products) { product ->
            Card(
                onClick = { onOpenProduct(product) },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(product.name, style = MaterialTheme.typography.titleLarge)
                    Text(product.brand, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    if (product.modelNumber.isNotBlank()) {
                        Text("Model: ${product.modelNumber}")
                    }
                    if (product.serialNumber.isNotBlank()) {
                        Text("Serial: ${product.serialNumber}")
                    }
                    Text("Purchased: ${product.purchaseDate}")
                    Text("Warranty: ${product.warrantyMonths} months")
                    Text(
                        text = "SELF_ADDED",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelLarge,
                    )
                    Text(
                        text = "View details",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }
        }
        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}
