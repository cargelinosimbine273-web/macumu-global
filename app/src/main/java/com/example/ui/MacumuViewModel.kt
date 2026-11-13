package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AffiliateLinkEntity
import com.example.data.local.AppDatabase
import com.example.data.local.CartItemEntity
import com.example.data.local.DisputeEntity
import com.example.data.local.LedgerEntryEntity
import com.example.data.local.NotificationEntity
import com.example.data.local.OrderEntity
import com.example.data.local.ProductEntity
import com.example.data.local.WithdrawalEntity
import com.example.data.repository.MacumuRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.data.local.UserRegistrationEntity
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppRole(val label: String, val description: String) {
    COMPRADOR("Comprador", "Explore produtos nacionais e globais com pagamento seguro"),
    VENDEDOR("Vendedor", "Comercialize produtos formais e informais sem taxas ocultas"),
    AFILIADO("Afiliado", "Ganhe até 15% de comissão partilhando links no WhatsApp"),
    ADMIN("Gestão / Desenvolvedor", "Acesso restrito: Validação de adesões e custódia")
}

sealed class ActionPermission {
    data object Allowed : ActionPermission()
    data class RequiresLogin(val action: String, val message: String) : ActionPermission()
    data class AwaitingApproval(val userName: String, val requestedRole: String) : ActionPermission()
}

sealed class Screen {
    data object WelcomeRoleSelect : Screen()
    data object Home : Screen()
    data class ProductDetail(val productId: Long) : Screen()
    data object Cart : Screen()
    data object Checkout : Screen()
    data class OrderTracking(val orderCode: String) : Screen()
    data object SellerDashboard : Screen()
    data object PublishProduct : Screen()
    data object AffiliateDashboard : Screen()
    data object AdminDashboard : Screen()
    data object Account : Screen()
    data object Notifications : Screen()
}

class MacumuViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: MacumuRepository

    init {
        val db = AppDatabase.getDatabase(application)
        repository = MacumuRepository(db)
    }

    // Navigation and Role State
    private val _currentRole = MutableStateFlow(AppRole.COMPRADOR)
    val currentRole: StateFlow<AppRole> = _currentRole.asStateFlow()

    private val _currentScreen = MutableStateFlow<Screen>(Screen.Home)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val screenBackStack = mutableListOf<Screen>()

    // Global Preferences
    private val _currency = MutableStateFlow("MZN")
    val currency: StateFlow<String> = _currency.asStateFlow()

    private val _language = MutableStateFlow("Português")
    val language: StateFlow<String> = _language.asStateFlow()

    // Filters
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("Todas")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _selectedScope = MutableStateFlow("TODOS") // "TODOS", "NACIONAL", "GLOBAL"
    val selectedScope: StateFlow<String> = _selectedScope.asStateFlow()

    private val _selectedMarket = MutableStateFlow<String?>(null)
    val selectedMarket: StateFlow<String?> = _selectedMarket.asStateFlow()

    private val _selectedInformalZone = MutableStateFlow("TODOS") // "TODOS", "SUL", "CENTRO", "NORTE"
    val selectedInformalZone: StateFlow<String> = _selectedInformalZone.asStateFlow()

    // Data Streams from Repository
    val products: StateFlow<List<ProductEntity>> = repository.getAllProducts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val cartItems: StateFlow<List<CartItemEntity>> = repository.getCartItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val orders: StateFlow<List<OrderEntity>> = repository.getAllOrders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val ledgerEntries: StateFlow<List<LedgerEntryEntity>> = repository.getAllLedgerEntries()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val withdrawals: StateFlow<List<WithdrawalEntity>> = repository.getAllWithdrawals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val affiliateLinks: StateFlow<List<AffiliateLinkEntity>> = repository.getAffiliateLinks("AFIL-MOZ-88")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val disputes: StateFlow<List<DisputeEntity>> = repository.getAllDisputes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notifications: StateFlow<List<NotificationEntity>> = repository.getNotificationsForRole("COMPRADOR")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Registrations & Developer Approval Stream
    val userRegistrations: StateFlow<List<UserRegistrationEntity>> = repository.getAllUserRegistrations()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active User (null = Visitante/Guest)
    private val _currentUser = MutableStateFlow<UserRegistrationEntity?>(null)
    val currentUser: StateFlow<UserRegistrationEntity?> = _currentUser.asStateFlow()

    // Developer / Administrator Security Protection
    private val _isDeveloperAuthenticated = MutableStateFlow(false)
    val isDeveloperAuthenticated: StateFlow<Boolean> = _isDeveloperAuthenticated.asStateFlow()

    // 99.5% Online Connectivity Indicator
    private val _isOnline = MutableStateFlow(true)
    val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()

    // Action requiring authentication/approval prompt modal
    private val _authPromptAction = MutableStateFlow<String?>(null)
    val authPromptAction: StateFlow<String?> = _authPromptAction.asStateFlow()

    // Notice when account is pending developer review
    private val _pendingApprovalNotice = MutableStateFlow<UserRegistrationEntity?>(null)
    val pendingApprovalNotice: StateFlow<UserRegistrationEntity?> = _pendingApprovalNotice.asStateFlow()

    // Filtered Products for Buyer Home - Universal search across all items
    val filteredProducts: StateFlow<List<ProductEntity>> = combine(
        products, _searchQuery, _selectedCategory, _selectedScope, _selectedMarket
    ) { list, query, category, scope, market ->
        val cleanQuery = query.trim()
        list.filter { product ->
            val matchesQuery = cleanQuery.isBlank() ||
                    product.title.contains(cleanQuery, ignoreCase = true) ||
                    product.description.contains(cleanQuery, ignoreCase = true) ||
                    product.category.contains(cleanQuery, ignoreCase = true) ||
                    product.sellerLocation.contains(cleanQuery, ignoreCase = true) ||
                    product.sellerName.contains(cleanQuery, ignoreCase = true) ||
                    product.marketName.contains(cleanQuery, ignoreCase = true) ||
                    product.regionZone.contains(cleanQuery, ignoreCase = true)

            // When searching, find across all products unless user specifically locked a category/market
            val matchesCategory = (cleanQuery.isNotBlank() && category == "Todas") ||
                    category == "Todas" || product.category.equals(category, ignoreCase = true)

            val matchesScope = when {
                cleanQuery.isNotBlank() -> true
                scope == "NACIONAL" -> product.isNational
                scope == "GLOBAL" -> !product.isNational
                else -> true
            }

            val matchesMarket = market.isNullOrBlank() ||
                    product.marketName.equals(market, ignoreCase = true) ||
                    product.sellerLocation.contains(market, ignoreCase = true)

            product.isApproved && matchesQuery && matchesCategory && matchesScope && matchesMarket
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Financial Balances Derived from Ledger
    val financialStats: StateFlow<FinancialStats> = ledgerEntries.combine(orders) { entries, allOrders ->
        var escrowLocked = 0.0
        var sellerAvailable = 0.0
        var affiliateBalance = 0.0
        var platformCommissions = 0.0

        for (entry in entries) {
            when (entry.account) {
                "ESCROW_CUSTODIA" -> {
                    if (entry.type == "CREDITO") escrowLocked += entry.amountMzn
                    else escrowLocked -= entry.amountMzn
                }
                "CARTEIRA_VENDEDOR" -> {
                    if (entry.type == "CREDITO") sellerAvailable += entry.amountMzn
                    else sellerAvailable -= entry.amountMzn
                }
                "CARTEIRA_AFILIADO" -> {
                    if (entry.type == "CREDITO") affiliateBalance += entry.amountMzn
                    else affiliateBalance -= entry.amountMzn
                }
                "COMISSAO_PLATAFORMA" -> {
                    if (entry.type == "CREDITO") platformCommissions += entry.amountMzn
                }
            }
        }

        val gmv = allOrders.sumOf { it.totalMzn }
        FinancialStats(
            escrowLockedMzn = escrowLocked.coerceAtLeast(0.0),
            sellerAvailableMzn = sellerAvailable.coerceAtLeast(0.0),
            affiliateBalanceMzn = affiliateBalance.coerceAtLeast(0.0),
            platformCommissionsMzn = platformCommissions.coerceAtLeast(0.0),
            totalGmvMzn = gmv,
            totalOrdersCount = allOrders.size
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), FinancialStats())

    // Navigation Methods
    fun navigateTo(screen: Screen) {
        screenBackStack.add(_currentScreen.value)
        _currentScreen.value = screen
    }

    fun navigateBack(): Boolean {
        if (screenBackStack.isNotEmpty()) {
            _currentScreen.value = screenBackStack.removeAt(screenBackStack.size - 1)
            return true
        }
        if (_currentScreen.value !is Screen.Home) {
            _currentScreen.value = Screen.Home
            return true
        }
        return false
    }

    fun selectRole(role: AppRole) {
        _currentRole.value = role
        when (role) {
            AppRole.COMPRADOR -> _currentScreen.value = Screen.Home
            AppRole.VENDEDOR -> _currentScreen.value = Screen.SellerDashboard
            AppRole.AFILIADO -> _currentScreen.value = Screen.AffiliateDashboard
            AppRole.ADMIN -> _currentScreen.value = Screen.AdminDashboard
        }
    }

    fun setCurrency(newCurrency: String) {
        _currency.value = newCurrency
    }

    fun setLanguage(newLanguage: String) {
        _language.value = newLanguage
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }

    fun selectScope(scope: String) {
        _selectedScope.value = scope
    }

    fun selectMarket(marketName: String?) {
        _selectedMarket.value = marketName
    }

    fun selectInformalZone(zone: String) {
        _selectedInformalZone.value = zone
    }

    fun clearAllFilters() {
        _searchQuery.value = ""
        _selectedCategory.value = "Todas"
        _selectedScope.value = "TODOS"
        _selectedMarket.value = null
        _selectedInformalZone.value = "TODOS"
    }

    // Actions
    fun addToCart(product: ProductEntity, quantity: Int = 1) {
        viewModelScope.launch {
            repository.addToCart(product, quantity)
        }
    }

    fun updateCartQuantity(id: Long, quantity: Int) {
        viewModelScope.launch {
            repository.updateCartQuantity(id, quantity)
        }
    }

    fun removeFromCart(id: Long) {
        viewModelScope.launch {
            repository.removeFromCart(id)
        }
    }

    fun placeOrder(
        buyerName: String,
        buyerPhone: String,
        province: String,
        cityOrDistrict: String,
        neighborhood: String,
        referencePoint: String,
        paymentMethod: String,
        affiliateCode: String?,
        onOrderSuccess: (String) -> Unit
    ) {
        viewModelScope.launch {
            val items = cartItems.value
            if (items.isEmpty()) return@launch
            val orderCode = repository.placeOrder(
                buyerName = buyerName,
                buyerPhone = buyerPhone,
                province = province,
                cityOrDistrict = cityOrDistrict,
                neighborhood = neighborhood,
                referencePoint = referencePoint,
                items = items,
                paymentMethod = paymentMethod,
                affiliateCode = affiliateCode
            )
            onOrderSuccess(orderCode)
        }
    }

    fun confirmOrderReception(orderCode: String) {
        viewModelScope.launch {
            repository.confirmOrderReception(orderCode)
        }
    }

    fun updateSellerOrderStatus(orderCode: String, newStatus: String) {
        viewModelScope.launch {
            repository.updateSellerOrderStatus(orderCode, newStatus)
        }
    }

    fun publishProduct(
        title: String,
        description: String,
        category: String,
        priceMzn: Double,
        promoPriceMzn: Double?,
        stock: Int,
        sellerType: String,
        sellerLocation: String,
        isNational: Boolean,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            repository.createProduct(
                ProductEntity(
                    title = title,
                    description = description,
                    category = category,
                    priceMzn = priceMzn,
                    promoPriceMzn = promoPriceMzn,
                    stock = stock,
                    sellerId = 101L,
                    sellerName = if (sellerType == "INFORMAL") "Bazar & Vendedor Local" else "Loja Oficial Registada",
                    sellerType = sellerType,
                    sellerLocation = sellerLocation,
                    isNational = isNational,
                    isApproved = false // O produto vai para análise da comissão; só após aprovação vai para a vitrine pública
                )
            )
            onSuccess()
        }
    }

    fun approveProduct(productId: Long) {
        viewModelScope.launch {
            repository.setProductApproval(productId, true)
        }
    }

    fun rejectProduct(productId: Long) {
        viewModelScope.launch {
            repository.setProductApproval(productId, false)
        }
    }

    fun requestWithdrawal(
        beneficiaryType: String,
        name: String,
        method: String,
        phoneOrAccount: String,
        amountMzn: Double,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            repository.requestWithdrawal(
                beneficiaryType = beneficiaryType,
                beneficiaryName = name,
                method = method,
                phoneNumberOrAccount = phoneOrAccount,
                amountMzn = amountMzn
            )
            onSuccess()
        }
    }

    fun processWithdrawal(id: Long, approved: Boolean) {
        viewModelScope.launch {
            repository.processWithdrawal(id, approved)
        }
    }

    fun generateAffiliateLink(product: ProductEntity, onGenerated: (AffiliateLinkEntity) -> Unit) {
        viewModelScope.launch {
            val link = repository.generateAffiliateLink(product, "AFIL-MOZ-88")
            onGenerated(link)
        }
    }

    fun recordAffiliateClick(linkId: Long) {
        viewModelScope.launch {
            repository.recordAffiliateClick(linkId)
        }
    }

    fun openDispute(
        orderCode: String,
        buyerName: String,
        sellerName: String,
        reason: String,
        description: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            repository.createDispute(orderCode, buyerName, sellerName, reason, description)
            onSuccess()
        }
    }

    fun resolveDispute(disputeId: Long, orderCode: String, refundToBuyer: Boolean, notes: String) {
        viewModelScope.launch {
            repository.resolveDispute(disputeId, orderCode, refundToBuyer, notes)
        }
    }

    // --- Developer Security & Authentication ---
    /**
     * Administrative access is intentionally disabled in this test build.
     * A PIN embedded in an APK can be extracted and is not a secure access control.
     * Re-enable this only after implementing server-side role checks (Firebase Auth
     * custom claims + Firestore Security Rules or an equivalent trusted backend).
     */
    fun verifyDeveloperPin(pin: String): Boolean {
        _isDeveloperAuthenticated.value = false
        return false
    }

    fun logoutDeveloper() {
        _isDeveloperAuthenticated.value = false
        _currentRole.value = AppRole.COMPRADOR
        _currentScreen.value = Screen.Home
    }

    // --- User Registration & Developer Approval ---
    fun setAuthPromptAction(action: String?) {
        _authPromptAction.value = action
    }

    fun setPendingApprovalNotice(user: UserRegistrationEntity?) {
        _pendingApprovalNotice.value = user
    }

    fun registerUser(
        fullName: String,
        phoneNumber: String,
        email: String,
        requestedRole: String,
        sellerType: String,
        businessName: String,
        province: String,
        onSuccess: (UserRegistrationEntity) -> Unit
    ) {
        viewModelScope.launch {
            val id = repository.registerUser(
                fullName = fullName,
                phoneNumber = phoneNumber,
                email = email,
                requestedRole = requestedRole,
                sellerType = sellerType,
                businessName = businessName,
                province = province
            )
            val newReg = repository.getRegistrationById(id) ?: UserRegistrationEntity(
                id = id,
                fullName = fullName,
                phoneNumber = phoneNumber,
                email = email,
                requestedRole = requestedRole,
                sellerType = sellerType,
                businessName = businessName,
                province = province,
                status = "PENDING_APPROVAL"
            )
            _currentUser.value = newReg
            _pendingApprovalNotice.value = newReg
            _authPromptAction.value = null
            onSuccess(newReg)
        }
    }

    fun loginUser(user: UserRegistrationEntity) {
        _currentUser.value = user
        _authPromptAction.value = null
        if (user.status == "PENDING_APPROVAL") {
            _pendingApprovalNotice.value = user
        }
    }

    fun logoutUser() {
        _currentUser.value = null
        _currentRole.value = AppRole.COMPRADOR
        _currentScreen.value = Screen.Home
    }

    fun approveUserRegistration(id: Long, notes: String = "Validado e aprovado pelo Desenvolvedor") {
        viewModelScope.launch {
            repository.approveUserRegistration(id, notes)
            if (_currentUser.value?.id == id) {
                _currentUser.value = _currentUser.value?.copy(status = "APPROVED", approvedAt = System.currentTimeMillis())
                _pendingApprovalNotice.value = null
            }
        }
    }

    fun rejectUserRegistration(id: Long, notes: String = "Rejeitado na verificação") {
        viewModelScope.launch {
            repository.rejectUserRegistration(id, notes)
            if (_currentUser.value?.id == id) {
                _currentUser.value = _currentUser.value?.copy(status = "REJECTED")
            }
        }
    }

    /**
     * Checks if a user is permitted to perform a critical action:
     * - "COMPRAR"
     * - "VENDER"
     * - "AFILIAR"
     */
    fun checkPermission(action: String): ActionPermission {
        val user = _currentUser.value
        return when {
            user == null -> ActionPermission.RequiresLogin(
                action = action,
                message = when (action) {
                    "COMPRAR" -> "Para adicionar ao carrinho e finalizar compras protegidas por custódia, crie ou inicie sessão na sua conta."
                    "VENDER" -> "Para publicar produtos em mercados formais ou feiras informais, é necessário cadastrar a sua loja/bazar."
                    "AFILIAR" -> "Para gerar links e receber comissões de até 15%, crie a sua conta de afiliado."
                    else -> "É necessário iniciar sessão ou registar a sua conta no Macumu Global."
                }
            )
            user.status == "PENDING_APPROVAL" -> ActionPermission.AwaitingApproval(
                userName = user.fullName,
                requestedRole = user.requestedRole
            )
            else -> ActionPermission.Allowed
        }
    }
}


data class FinancialStats(
    val escrowLockedMzn: Double = 0.0,
    val sellerAvailableMzn: Double = 0.0,
    val affiliateBalanceMzn: Double = 0.0,
    val platformCommissionsMzn: Double = 0.0,
    val totalGmvMzn: Double = 0.0,
    val totalOrdersCount: Int = 0
)
