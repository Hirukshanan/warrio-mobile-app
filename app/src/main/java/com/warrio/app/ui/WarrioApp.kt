package com.warrio.app.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
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
import com.warrio.app.ui.claim.ClaimSubmissionScreen
import com.warrio.app.ui.claim.WarrantyClaimUiModel
import com.warrio.app.ui.claim.addClaimToProduct
import org.json.JSONArray
import org.json.JSONObject
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
    val appContext = LocalContext.current.applicationContext
    val claimPreferences = remember {
        appContext.getSharedPreferences("warrio_claims", android.content.Context.MODE_PRIVATE)
    }
    val productPreferences = remember {
        appContext.getSharedPreferences("warrio_products", android.content.Context.MODE_PRIVATE)
    }
    val walletProducts = remember {
        mutableStateListOf<WalletProductUiModel>().apply {
            val savedProducts = runCatching {
                JSONArray(productPreferences.getString("products", "[]")).let { array ->
                    (0 until array.length()).mapNotNull { index ->
                        val item = array.optJSONObject(index) ?: return@mapNotNull null
                        val name = item.optString("name")
                        if (name.isBlank()) return@mapNotNull null
                        WalletProductUiModel(
                            id = item.optString("id").ifBlank { java.util.UUID.randomUUID().toString() },
                            name = name,
                            brand = item.optString("brand"),
                            modelNumber = item.optString("modelNumber"),
                            serialNumber = item.optString("serialNumber"),
                            purchaseDate = item.optString("purchaseDate"),
                            warrantyMonths = item.optInt("warrantyMonths"),
                        )
                    }
                }
            }.getOrDefault(emptyList())
            addAll(savedProducts)
        }
    }
    val productDocuments = remember {
        mutableStateMapOf<String, List<DocumentAttachmentUiModel>>()
    }
    val productClaims = remember {
        mutableStateMapOf<String, List<WarrantyClaimUiModel>>().apply {
            val savedClaims = runCatching {
                JSONArray(claimPreferences.getString("claims", "[]")).let { array ->
                    (0 until array.length()).mapNotNull { index ->
                        val item = array.optJSONObject(index) ?: return@mapNotNull null
                        WarrantyClaimUiModel(
                            id = item.optString("id"),
                            productId = item.optString("productId"),
                            description = item.optString("description"),
                            status = runCatching {
                                com.warrio.app.ui.claim.WarrantyClaimStatus.valueOf(item.optString("status"))
                            }.getOrDefault(com.warrio.app.ui.claim.WarrantyClaimStatus.SUBMITTED),
                            submittedAtEpochMillis = item.optLong("submittedAtEpochMillis"),
                        )
                    }
                }
            }.getOrDefault(emptyList())
            savedClaims.groupBy { it.productId }.forEach { (productId, claims) -> put(productId, claims) }
        }
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
                products = walletProducts,
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
                    productPreferences.edit()
                        .putString("products", walletProducts.toJson().toString())
                        .apply()
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
                    productPreferences.edit()
                        .putString("products", walletProducts.toJson().toString())
                        .apply()
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
                claims = selectedProduct?.let { productClaims[it.id] }.orEmpty(),
                onBack = { navController.popBackStack() },
                onAddDocument = {
                    navController.navigate(WarrioDestination.AddDocument.route)
                },
                onStartClaim = {
                    navController.navigate(WarrioDestination.SubmitClaim.route)
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
        composable(WarrioDestination.SubmitClaim.route) {
            ClaimSubmissionScreen(
                product = selectedProduct,
                onBack = { navController.popBackStack() },
                onSubmitClaim = { claim ->
                    addClaimToProduct(productClaims, claim)
                    val allClaims = productClaims.values.flatten()
                    val saved = JSONArray().apply {
                        allClaims.forEach { item ->
                            put(JSONObject().apply {
                                put("id", item.id)
                                put("productId", item.productId)
                                put("description", item.description)
                                put("status", item.status.name)
                                put("submittedAtEpochMillis", item.submittedAtEpochMillis)
                            })
                        }
                    }
                    claimPreferences.edit().putString("claims", saved.toString()).apply()
                    navController.popBackStack()
                },
            )
        }
    }
}

private fun List<WalletProductUiModel>.toJson(): JSONArray = JSONArray().apply {
    forEach { product ->
        put(JSONObject().apply {
            put("id", product.id)
            put("name", product.name)
            put("brand", product.brand)
            put("modelNumber", product.modelNumber)
            put("serialNumber", product.serialNumber)
            put("purchaseDate", product.purchaseDate)
            put("warrantyMonths", product.warrantyMonths)
        })
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
    data object SubmitClaim : WarrioDestination("submit-claim")
}

private fun NavHostController.openDashboard() {
    navigate(WarrioDestination.Dashboard.route) {
        popUpTo(WarrioDestination.Login.route) { inclusive = true }
        launchSingleTop = true
    }
}

