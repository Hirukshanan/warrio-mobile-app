package com.warrio.app.ui.customer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ProductWalletScreen(
    onSelectTab: (CustomerTab) -> Unit,
) {
    CustomerScaffold(
        selectedTab = CustomerTab.WALLET,
        onSelectTab = onSelectTab,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
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
        }
    }
}
