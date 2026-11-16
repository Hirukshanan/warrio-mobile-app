package com.warrio.app.ui.customer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DashboardScreen(
    onOpenWallet: () -> Unit,
) {
    CustomerScaffold(
        selectedTab = CustomerTab.DASHBOARD,
        onSelectTab = { selectedTab ->
            if (selectedTab == CustomerTab.WALLET) onOpenWallet()
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Text(
                text = "Customer dashboard",
                style = MaterialTheme.typography.headlineMedium,
            )
            Text(
                text = "Keep your products, warranties, and claims in one place.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                DashboardSummaryCard(
                    value = "0",
                    label = "Products",
                    modifier = Modifier.weight(1f),
                )
                DashboardSummaryCard(
                    value = "0",
                    label = "Warranties",
                    modifier = Modifier.weight(1f),
                )
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(
                        text = "Your product wallet is ready",
                        style = MaterialTheme.typography.titleLarge,
                    )
                    Text(
                        text = "Registered and scanned products will appear in your wallet.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Button(onClick = onOpenWallet) {
                        Text("Open wallet")
                    }
                }
            }
        }
    }
}

@Composable
private fun DashboardSummaryCard(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
