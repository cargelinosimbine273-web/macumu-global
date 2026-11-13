package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.ActionPermission
import com.example.ui.AppRole
import com.example.ui.MacumuViewModel
import com.example.ui.Screen
import com.example.ui.components.AuthOrRegisterDialog
import com.example.ui.components.AwaitingApprovalDialog
import com.example.ui.components.DeveloperPinDialog
import com.example.ui.components.MacumuBottomBar
import com.example.ui.components.RoleSelectorHeader
import com.example.ui.screens.WelcomeRoleScreen
import com.example.ui.screens.account.AccountScreen
import com.example.ui.screens.admin.AdminScreen
import com.example.ui.screens.affiliate.AffiliateScreen
import com.example.ui.screens.buyer.BuyerHomeScreen
import com.example.ui.screens.buyer.CartCheckoutScreen
import com.example.ui.screens.buyer.OrderTrackingScreen
import com.example.ui.screens.buyer.ProductDetailScreen
import com.example.ui.screens.seller.PublishProductScreen
import com.example.ui.components.EcranAutenticacao
import com.example.ui.screens.seller.SellerDashboardScreen
import com.example.ui.theme.DarkNavyBackground
import com.example.ui.theme.DarkNavyCard
import com.example.ui.theme.DarkNavyCardElevated
import com.example.ui.theme.MacumuOrange
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
                    var utilizadorAtual by remember {
                        mutableStateOf(auth.currentUser)
                    }
                    var isVisitante by remember {
                        mutableStateOf(false)
                    }

                    // Se já estiver logado, entra direto na app principal; senão, mostra o login
                    if (utilizadorAtual != null || isVisitante) {
                        MacumuApp() // O teu ecrã principal existente
                    } else {
                        EcranAutenticacao(
                            onAuthSucesso = {
                                // Quando faz login/registo com sucesso, atualiza o estado para entrar na app principal
                                utilizadorAtual = auth.currentUser
                            },
                            onContinuarVisitante = {
                                isVisitante = true
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MacumuApp(
    viewModel: MacumuViewModel = viewModel()
) {
    val currentRole by viewModel.currentRole.collectAsStateWithLifecycle()
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val currency by viewModel.currency.collectAsStateWithLifecycle()
    val language by viewModel.language.collectAsStateWithLifecycle()

    val products by viewModel.products.collectAsStateWithLifecycle()
    val filteredProducts by viewModel.filteredProducts.collectAsStateWithLifecycle()
    val cartItems by viewModel.cartItems.collectAsStateWithLifecycle()
    val orders by viewModel.orders.collectAsStateWithLifecycle()
    val financialStats by viewModel.financialStats.collectAsStateWithLifecycle()
    val withdrawals by viewModel.withdrawals.collectAsStateWithLifecycle()
    val affiliateLinks by viewModel.affiliateLinks.collectAsStateWithLifecycle()
    val disputes by viewModel.disputes.collectAsStateWithLifecycle()
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()

    val userRegistrations by viewModel.userRegistrations.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val isDeveloperAuthenticated by viewModel.isDeveloperAuthenticated.collectAsStateWithLifecycle()
    val isOnline by viewModel.isOnline.collectAsStateWithLifecycle()
    val authPromptAction by viewModel.authPromptAction.collectAsStateWithLifecycle()
    val pendingApprovalNotice by viewModel.pendingApprovalNotice.collectAsStateWithLifecycle()

    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val selectedScope by viewModel.selectedScope.collectAsStateWithLifecycle()
    val selectedMarket by viewModel.selectedMarket.collectAsStateWithLifecycle()
    val selectedInformalZone by viewModel.selectedInformalZone.collectAsStateWithLifecycle()

    var showCurrencyDialog by remember { mutableStateOf(false) }
    var showDevPinDialog by remember { mutableStateOf(false) }
    var hasCompletedWelcome by remember { mutableStateOf(true) }

    // Back button handling
    BackHandler(enabled = currentScreen !is Screen.Home) {
        viewModel.navigateBack()
    }

    if (!hasCompletedWelcome) {
        WelcomeRoleScreen(
            onSelectRoleAndProceed = { role ->
                viewModel.selectRole(role)
                hasCompletedWelcome = true
            }
        )
        return
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavyBackground),
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            RoleSelectorHeader(
                currentRole = currentRole,
                onRoleSelected = { role ->
                    when (role) {
                        AppRole.ADMIN -> {
                            if (!isDeveloperAuthenticated) {
                                showDevPinDialog = true
                            } else {
                                viewModel.selectRole(AppRole.ADMIN)
                            }
                        }
                        AppRole.VENDEDOR -> {
                            when (val perm = viewModel.checkPermission("VENDER")) {
                                is ActionPermission.RequiresLogin -> viewModel.setAuthPromptAction("VENDER")
                                is ActionPermission.AwaitingApproval -> viewModel.setPendingApprovalNotice(currentUser)
                                is ActionPermission.Allowed -> viewModel.selectRole(AppRole.VENDEDOR)
                            }
                        }
                        AppRole.AFILIADO -> {
                            when (val perm = viewModel.checkPermission("AFILIAR")) {
                                is ActionPermission.RequiresLogin -> viewModel.setAuthPromptAction("AFILIAR")
                                is ActionPermission.AwaitingApproval -> viewModel.setPendingApprovalNotice(currentUser)
                                is ActionPermission.Allowed -> viewModel.selectRole(AppRole.AFILIADO)
                            }
                        }
                        AppRole.COMPRADOR -> {
                            viewModel.selectRole(AppRole.COMPRADOR)
                        }
                    }
                },
                currency = currency,
                onCurrencyClick = { showCurrencyDialog = true },
                isOnline = isOnline,
                isDeveloperAuthenticated = isDeveloperAuthenticated,
                onDeveloperLockClick = { showDevPinDialog = true }
            )
        },
        bottomBar = {
            // Show bottom navigation on main role screens
            if (currentScreen !is Screen.ProductDetail && currentScreen !is Screen.PublishProduct) {
                MacumuBottomBar(
                    currentScreen = currentScreen,
                    cartCount = cartItems.sumOf { it.quantity },
                    onNavigate = { screen ->
                        viewModel.navigateTo(screen)
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkNavyBackground)
                .padding(innerPadding)
        ) {
            when (val screen = currentScreen) {
                is Screen.WelcomeRoleSelect -> {
                    WelcomeRoleScreen(
                        onSelectRoleAndProceed = { role ->
                            viewModel.selectRole(role)
                        }
                    )
                }

                is Screen.Home -> {
                    BuyerHomeScreen(
                        products = filteredProducts,
                        searchQuery = searchQuery,
                        selectedCategory = selectedCategory,
                        selectedScope = selectedScope,
                        selectedMarket = selectedMarket,
                        selectedInformalZone = selectedInformalZone,
                        currency = currency,
                        onSearchChange = { viewModel.setSearchQuery(it) },
                        onCategorySelect = { viewModel.selectCategory(it) },
                        onScopeSelect = { viewModel.selectScope(it) },
                        onMarketSelect = { viewModel.selectMarket(it) },
                        onInformalZoneSelect = { viewModel.selectInformalZone(it) },
                        onClearFilters = { viewModel.clearAllFilters() },
                        onProductClick = { productId ->
                            viewModel.navigateTo(Screen.ProductDetail(productId))
                        },
                        onAddToCart = { product ->
                            when (val perm = viewModel.checkPermission("COMPRAR")) {
                                is ActionPermission.RequiresLogin -> viewModel.setAuthPromptAction("COMPRAR")
                                is ActionPermission.AwaitingApproval -> viewModel.setPendingApprovalNotice(currentUser)
                                is ActionPermission.Allowed -> viewModel.addToCart(product)
                            }
                        }
                    )
                }

                is Screen.ProductDetail -> {
                    val product = products.find { it.id == screen.productId }
                        ?: products.firstOrNull()

                    if (product != null) {
                        ProductDetailScreen(
                            product = product,
                            currency = currency,
                            onBack = { viewModel.navigateBack() },
                            onAddToCart = { prod ->
                                when (val perm = viewModel.checkPermission("COMPRAR")) {
                                    is ActionPermission.RequiresLogin -> viewModel.setAuthPromptAction("COMPRAR")
                                    is ActionPermission.AwaitingApproval -> viewModel.setPendingApprovalNotice(currentUser)
                                    is ActionPermission.Allowed -> viewModel.addToCart(prod)
                                }
                            },
                            onBuyNow = { prod ->
                                when (val perm = viewModel.checkPermission("COMPRAR")) {
                                    is ActionPermission.RequiresLogin -> viewModel.setAuthPromptAction("COMPRAR")
                                    is ActionPermission.AwaitingApproval -> viewModel.setPendingApprovalNotice(currentUser)
                                    is ActionPermission.Allowed -> {
                                        viewModel.addToCart(prod)
                                        viewModel.navigateTo(Screen.Cart)
                                    }
                                }
                            },
                            onGenerateAffiliateLink = { prod, callback ->
                                when (val perm = viewModel.checkPermission("AFILIAR")) {
                                    is ActionPermission.RequiresLogin -> viewModel.setAuthPromptAction("AFILIAR")
                                    is ActionPermission.AwaitingApproval -> viewModel.setPendingApprovalNotice(currentUser)
                                    is ActionPermission.Allowed -> viewModel.generateAffiliateLink(prod, callback)
                                }
                            }
                        )
                    } else {
                        viewModel.navigateBack()
                    }
                }

                is Screen.Cart, is Screen.Checkout -> {
                    CartCheckoutScreen(
                        cartItems = cartItems,
                        currency = currency,
                        onBack = { viewModel.navigateBack() },
                        onUpdateQuantity = { id, qty -> viewModel.updateCartQuantity(id, qty) },
                        onRemoveItem = { id -> viewModel.removeFromCart(id) },
                        onPlaceOrder = { name, phone, province, city, neighborhood, ref, paymentMethod, affilCode, callback ->
                            viewModel.placeOrder(
                                buyerName = name,
                                buyerPhone = phone,
                                province = province,
                                cityOrDistrict = city,
                                neighborhood = neighborhood,
                                referencePoint = ref,
                                paymentMethod = paymentMethod,
                                affiliateCode = affilCode,
                                onOrderSuccess = callback
                            )
                        },
                        onOrderPlaced = { orderCode ->
                            viewModel.navigateTo(Screen.OrderTracking(orderCode))
                        }
                    )
                }

                is Screen.OrderTracking -> {
                    OrderTrackingScreen(
                        orders = orders,
                        currency = currency,
                        onConfirmReception = { orderCode ->
                            viewModel.confirmOrderReception(orderCode)
                        },
                        onOpenDispute = { orderCode, buyer, seller, reason, desc ->
                            viewModel.openDispute(orderCode, buyer, seller, reason, desc) {
                                // Dispute submitted
                            }
                        }
                    )
                }

                is Screen.SellerDashboard -> {
                    SellerDashboardScreen(
                        orders = orders,
                        sellerProducts = products,
                        financialStats = financialStats,
                        currency = currency,
                        onPublishProductClick = {
                            viewModel.navigateTo(Screen.PublishProduct)
                        },
                        onUpdateOrderStatus = { orderCode, status ->
                            viewModel.updateSellerOrderStatus(orderCode, status)
                        },
                        onRequestWithdrawal = { type, name, method, phone, amount ->
                            viewModel.requestWithdrawal(type, name, method, phone, amount) {}
                        }
                    )
                }

                is Screen.PublishProduct -> {
                    PublishProductScreen(
                        onBack = { viewModel.navigateBack() },
                        onPublish = { title, desc, cat, price, promo, stock, sellerType, location, isNat ->
                            viewModel.publishProduct(
                                title = title,
                                description = desc,
                                category = cat,
                                priceMzn = price,
                                promoPriceMzn = promo,
                                stock = stock,
                                sellerType = sellerType,
                                sellerLocation = location,
                                isNational = isNat,
                                onSuccess = {
                                    viewModel.navigateTo(Screen.SellerDashboard)
                                }
                            )
                        }
                    )
                }

                is Screen.AffiliateDashboard -> {
                    AffiliateScreen(
                        affiliateLinks = affiliateLinks,
                        products = products,
                        financialStats = financialStats,
                        currency = currency,
                        onGenerateLinkForProduct = { product ->
                            viewModel.generateAffiliateLink(product) {}
                        },
                        onRecordClick = { linkId ->
                            viewModel.recordAffiliateClick(linkId)
                        },
                        onRequestWithdrawal = { type, name, method, phone, amount ->
                            viewModel.requestWithdrawal(type, name, method, phone, amount) {}
                        }
                    )
                }

                is Screen.AdminDashboard -> {
                    AdminScreen(
                        financialStats = financialStats,
                        disputes = disputes,
                        withdrawals = withdrawals,
                        ledgerEntries = viewModel.ledgerEntries.collectAsStateWithLifecycle().value,
                        products = products,
                        userRegistrations = userRegistrations,
                        currency = currency,
                        onResolveDispute = { id, code, refund, notes ->
                            viewModel.resolveDispute(id, code, refund, notes)
                        },
                        onProcessWithdrawal = { id, approved ->
                            viewModel.processWithdrawal(id, approved)
                        },
                        onApproveRegistration = { id ->
                            viewModel.approveUserRegistration(id)
                        },
                        onRejectRegistration = { id ->
                            viewModel.rejectUserRegistration(id)
                        },
                        onApproveProduct = { id ->
                            viewModel.approveProduct(id)
                        },
                        onRejectProduct = { id ->
                            viewModel.rejectProduct(id)
                        },
                        onExitDeveloperMode = {
                            viewModel.logoutDeveloper()
                        }
                    )
                }

                is Screen.Account -> {
                    AccountScreen(
                        currentRole = currentRole,
                        currency = currency,
                        language = language,
                        notifications = notifications,
                        onSelectRole = { role ->
                            viewModel.selectRole(role)
                        },
                        onSelectCurrency = { newCurr ->
                            viewModel.setCurrency(newCurr)
                        },
                        onSelectLanguage = { newLang ->
                            viewModel.setLanguage(newLang)
                        }
                    )
                }

                is Screen.Notifications -> {
                    AccountScreen(
                        currentRole = currentRole,
                        currency = currency,
                        language = language,
                        notifications = notifications,
                        onSelectRole = { role -> viewModel.selectRole(role) },
                        onSelectCurrency = { viewModel.setCurrency(it) },
                        onSelectLanguage = { viewModel.setLanguage(it) }
                    )
                }
            }
        }
    }

    // Currency Switcher Modal
    if (showCurrencyDialog) {
        AlertDialog(
            onDismissRequest = { showCurrencyDialog = false },
            containerColor = DarkNavyCardElevated,
            title = {
                Text("Selecione a Moeda", color = TextWhite, fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    listOf(
                        "MZN" to "Metical Moçambicano (MZN)",
                        "ZAR" to "Rand Sul-Africano (ZAR)",
                        "USD" to "Dólar Americano (USD)",
                        "EUR" to "Euro Europeu (EUR)"
                    ).forEach { (code, name) ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (currency == code) MacumuOrange else DarkNavyCard,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    viewModel.setCurrency(code)
                                    showCurrencyDialog = false
                                }
                        ) {
                            Text(
                                text = name,
                                color = if (currency == code) Color.White else TextWhite,
                                fontWeight = if (currency == code) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showCurrencyDialog = false }) {
                    Text("Fechar", color = TextMuted)
                }
            }
        )
    }

    // Developer Restricted Access PIN Dialog (Visitors Cannot Access Without PIN)
    if (showDevPinDialog) {
        DeveloperPinDialog(
            onDismiss = { showDevPinDialog = false },
            onVerifyPin = { pin ->
                val success = viewModel.verifyDeveloperPin(pin)
                if (success) {
                    showDevPinDialog = false
                }
                success
            }
        )
    }

    // Account Required Dialog (Triggered when visitor tries to Buy, Sell, or Affiliate)
    if (authPromptAction != null) {
        AuthOrRegisterDialog(
            action = authPromptAction!!,
            existingRegistrations = userRegistrations,
            onDismiss = { viewModel.setAuthPromptAction(null) },
            onRegister = { name, phone, email, role, sellerType, businessName, province ->
                viewModel.registerUser(
                    fullName = name,
                    phoneNumber = phone,
                    email = email,
                    requestedRole = role,
                    sellerType = sellerType,
                    businessName = businessName,
                    province = province,
                    onSuccess = {}
                )
            },
            onLogin = { user ->
                viewModel.loginUser(user)
            }
        )
    }

    // Notice shown when an account is pending developer review
    if (pendingApprovalNotice != null) {
        AwaitingApprovalDialog(
            user = pendingApprovalNotice!!,
            onDismiss = { viewModel.setPendingApprovalNotice(null) }
        )
    }
}

