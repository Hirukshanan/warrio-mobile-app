package com.warrio.app.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import com.warrio.app.ui.auth.LoginRoute
import com.warrio.app.ui.auth.RegistrationRoute
import com.warrio.app.ui.customer.CustomerTab
import com.warrio.app.ui.customer.DashboardScreen
import com.warrio.app.ui.customer.ProductWalletScreen
import com.warrio.app.ui.customer.ReceiptScanScreen
import com.warrio.app.ui.customer.ProductDetailsScreen
import com.warrio.app.ui.customer.WalletProductUiModel
import com.warrio.app.ui.document.DocumentAttachmentUiModel
import com.warrio.app.ui.document.DocumentUploadScreen
import com.warrio.app.ui.product.ManualProductRoute
import com.warrio.app.ui.product.ManualProductViewModel

@Composable
fun WarrioApp(
    navController: NavHostController = rememberNavController(),
) {
    val walletProducts = remember { mutableStateListOf<WalletProductUiModel>() }
    val productDocuments = remember {
        mutableStateMapOf<String, List<DocumentAttachmentUiModel>>()
    }
    var selectedProduct by remember { mutableStateOf<WalletProductUiModel?>(null) }

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
                onOpenProduct = { product ->
                    selectedProduct = product
                    navController.navigate(WarrioDestination.ProductDetails.route)
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
            val reviewModel: ManualProductViewModel = viewModel()
            ReceiptScanScreen(
                onBackToWallet = { navController.popBackStack() },
                onReviewProduct = { text ->
                    reviewModel.prepareReceiptReview(text)
                    navController.navigate(WarrioDestination.ReceiptProductReview.route) {
                        launchSingleTop = true
                    }
                },
            )
        }
        dialog(
            route = WarrioDestination.ReceiptProductReview.route,
            dialogProperties = DialogProperties(usePlatformDefaultWidth = false),
        ) { entry ->
            val receiptEntry = remember(entry) {
                navController.getBackStackEntry(WarrioDestination.ReceiptScan.route)
            }
            ManualProductRoute(
                onBack = { navController.popBackStack() },
                onProductCreated = { product ->
                    walletProducts.add(product)
                    navController.popBackStack(WarrioDestination.Wallet.route, inclusive = false)
                },
                viewModel = viewModel(viewModelStoreOwner = receiptEntry),
                reviewingReceipt = true,
            )
        }
        composable(WarrioDestination.ProductDetails.route) {
            ProductDetailsScreen(
                product = selectedProduct,
                documents = selectedProduct?.let { productDocuments[it.id] }.orEmpty(),
                onBack = { navController.popBackStack() },
                onAddDocument = {
                    navController.navigate(WarrioDestination.AddDocument.route)
                },
            )
        }
        composable(WarrioDestination.AddDocument.route) {
            DocumentUploadScreen(
                productName = selectedProduct?.name,
                onBack = { navController.popBackStack() },
                onAttachDocument = { document ->
                    selectedProduct?.let { product ->
                        attachDocumentToProduct(
                            productDocuments = productDocuments,
                            productId = product.id,
                            document = document,
                        )
                    }
                    navController.popBackStack()
                },
            )
        }
    }
}

internal fun attachDocumentToProduct(
    productDocuments: MutableMap<String, List<DocumentAttachmentUiModel>>,
    productId: String,
    document: DocumentAttachmentUiModel,
) {
    productDocuments[productId] = productDocuments[productId].orEmpty() + document
}

sealed class WarrioDestination(val route: String) {
    data object Login : WarrioDestination("login")
    data object Register : WarrioDestination("register")
    data object Dashboard : WarrioDestination("dashboard")
    data object Wallet : WarrioDestination("wallet")
    data object ReceiptScan : WarrioDestination("receipt-scan")
    data object ReceiptProductReview : WarrioDestination("receipt-product-review")
    data object AddProduct : WarrioDestination("add-product")
    data object ProductDetails : WarrioDestination("product-details")
    data object AddDocument : WarrioDestination("add-document")
}

private fun NavHostController.openDashboard() {
    navigate(WarrioDestination.Dashboard.route) {
        popUpTo(WarrioDestination.Login.route) { inclusive = true }
        launchSingleTop = true
    }
}

