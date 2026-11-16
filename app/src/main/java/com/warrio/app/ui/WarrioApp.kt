package com.warrio.app.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.warrio.app.ui.auth.LoginRoute
import com.warrio.app.ui.auth.RegistrationRoute
import com.warrio.app.ui.customer.CustomerTab
import com.warrio.app.ui.customer.DashboardScreen
import com.warrio.app.ui.customer.ProductWalletScreen
import com.warrio.app.ui.customer.ReceiptScanScreen
import com.warrio.app.ui.customer.WalletProductUiModel
import com.warrio.app.ui.product.ManualProductRoute

@Composable
fun WarrioApp(
    navController: NavHostController = rememberNavController(),
) {
    val walletProducts = remember { mutableStateListOf<WalletProductUiModel>() }

    NavHost(
        navController = navController,
        startDestination = WarrioDestination.Login.route,
    ) {
        composable(WarrioDestination.Login.route) {
            LoginRoute(
                onAuthenticated = { navController.openDashboard() },
                onOpenRegistration = {
                    navController.navigate(WarrioDestination.Register.route)
                },
            )
        }
        composable(WarrioDestination.Register.route) {
            RegistrationRoute(
                onAuthenticated = { navController.openDashboard() },
                onOpenLogin = { navController.popBackStack() },
            )
        }
        composable(WarrioDestination.Dashboard.route) {
            DashboardScreen(
                onOpenWallet = {
                    navController.navigate(WarrioDestination.Wallet.route) {
                        launchSingleTop = true
                    }
                },
            )
        }
        composable(WarrioDestination.Wallet.route) {
            ProductWalletScreen(
                onScanReceipt = {
                    navController.navigate(WarrioDestination.ReceiptScan.route) {
                        launchSingleTop = true
                    }
                },
                products = walletProducts,
                onSelectTab = { selectedTab ->
                    if (selectedTab == CustomerTab.DASHBOARD) {
                        navController.popBackStack(
                            route = WarrioDestination.Dashboard.route,
                            inclusive = false,
                        )
                    }
                },
                onAddProduct = {
                    navController.navigate(WarrioDestination.AddProduct.route)
                },
            )
        }
        composable(WarrioDestination.AddProduct.route) {
            ManualProductRoute(
                onBack = { navController.popBackStack() },
                onProductCreated = { product ->
                    walletProducts.add(product)
                    navController.popBackStack()
                },
            )
        }
        composable(WarrioDestination.ReceiptScan.route) {
            ReceiptScanScreen(onBackToWallet = { navController.popBackStack() })
        }
    }
}

sealed class WarrioDestination(val route: String) {
    data object Login : WarrioDestination("login")
    data object Register : WarrioDestination("register")
    data object Dashboard : WarrioDestination("dashboard")
    data object Wallet : WarrioDestination("wallet")
    data object ReceiptScan : WarrioDestination("receipt-scan")
    data object AddProduct : WarrioDestination("add-product")
}

private fun NavHostController.openDashboard() {
    navigate(WarrioDestination.Dashboard.route) {
        popUpTo(WarrioDestination.Login.route) { inclusive = true }
        launchSingleTop = true
    }
}

