package com.warrio.app.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.warrio.app.ui.auth.LoginRoute
import com.warrio.app.ui.auth.RegistrationRoute
import com.warrio.app.ui.customer.CustomerTab
import com.warrio.app.ui.customer.DashboardScreen
import com.warrio.app.ui.customer.ProductWalletScreen

@Composable
fun WarrioApp(
    navController: NavHostController = rememberNavController(),
) {
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
                onSelectTab = { selectedTab ->
                    if (selectedTab == CustomerTab.DASHBOARD) {
                        navController.popBackStack(
                            route = WarrioDestination.Dashboard.route,
                            inclusive = false,
                        )
                    }
                },
            )
        }
    }
}

sealed class WarrioDestination(val route: String) {
    data object Login : WarrioDestination("login")
    data object Register : WarrioDestination("register")
    data object Dashboard : WarrioDestination("dashboard")
    data object Wallet : WarrioDestination("wallet")
}

private fun NavHostController.openDashboard() {
    navigate(WarrioDestination.Dashboard.route) {
        popUpTo(WarrioDestination.Login.route) { inclusive = true }
        launchSingleTop = true
    }
}

