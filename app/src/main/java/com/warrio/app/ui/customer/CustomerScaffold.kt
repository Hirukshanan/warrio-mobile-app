package com.warrio.app.ui.customer

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

enum class CustomerTab {
    DASHBOARD,
    WALLET,
}

@Composable
fun CustomerScaffold(
    selectedTab: CustomerTab,
    onSelectTab: (CustomerTab) -> Unit,
    content: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(
        bottomBar = {
            NavigationBar {
                CustomerNavigationItem(
                    label = "Home",
                    shortLabel = "H",
                    selected = selectedTab == CustomerTab.DASHBOARD,
                    onClick = { onSelectTab(CustomerTab.DASHBOARD) },
                )
                CustomerNavigationItem(
                    label = "Wallet",
                    shortLabel = "W",
                    selected = selectedTab == CustomerTab.WALLET,
                    onClick = { onSelectTab(CustomerTab.WALLET) },
                )
            }
        },
        content = content,
    )
}

@Composable
private fun RowScope.CustomerNavigationItem(
    label: String,
    shortLabel: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = { Text(shortLabel) },
        label = { Text(label) },
    )
}
