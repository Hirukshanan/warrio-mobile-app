package com.warrio.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.warrio.app.ui.auth.LoginRoute
import com.warrio.app.ui.auth.RegistrationRoute

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
            DashboardScreen()
        }
    }
}

sealed class WarrioDestination(val route: String) {
    data object Login : WarrioDestination("login")
    data object Register : WarrioDestination("register")
    data object Dashboard : WarrioDestination("dashboard")
}

private fun NavHostController.openDashboard() {
    navigate(WarrioDestination.Dashboard.route) {
        popUpTo(WarrioDestination.Login.route) { inclusive = true }
        launchSingleTop = true
    }
}

@Composable
private fun DashboardScreen() {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = "Customer dashboard")
            Text(
                text = "Your product wallet will appear here.",
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}

