package com.example.data.repository

import com.example.data.local.AffiliateDao
import com.example.data.local.AffiliateLinkEntity
import com.example.data.local.AppDatabase
import com.example.data.local.CartDao
import com.example.data.local.CartItemEntity
import com.example.data.local.DisputeDao
import com.example.data.local.DisputeEntity
import com.example.data.local.FinanceDao
import com.example.data.local.LedgerEntryEntity
import com.example.data.local.NotificationDao
import com.example.data.local.NotificationEntity
import com.example.data.local.OrderDao
import com.example.data.local.OrderEntity
import com.example.data.local.ProductDao
import com.example.data.local.ProductEntity
import com.example.data.local.UserRegistrationEntity
import com.example.data.local.WithdrawalEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class MacumuRepository(private val db: AppDatabase) {
    private val productDao: ProductDao = db.productDao()
    private val cartDao: CartDao = db.cartDao()
    private val orderDao: OrderDao = db.orderDao()
    private val financeDao: FinanceDao = db.financeDao()
    private val affiliateDao: AffiliateDao = db.affiliateDao()
    private val disputeDao: DisputeDao = db.disputeDao()
    private val notificationDao: NotificationDao = db.notificationDao()
    private val userRegistrationDao = db.userRegistrationDao()

    // --- Products ---
    fun getAllProducts(): Flow<List<ProductEntity>> = productDao.getAllProducts()
    fun getApprovedProducts(): Flow<List<ProductEntity>> = productDao.getApprovedProducts()
    fun getProductsByScope(isNational: Boolean): Flow<List<ProductEntity>> = productDao.getProductsByScope(isNational)
    fun getSellerProducts(sellerId: Long): Flow<List<ProductEntity>> = productDao.getSellerProducts(sellerId)
    suspend fun getProductById(id: Long): ProductEntity? = productDao.getProductById(id)

    suspend fun createProduct(product: ProductEntity): Long {
        val id = productDao.insertProduct(product)
        notificationDao.insertNotification(
            NotificationEntity(
                targetRole = "ADMIN",
                title = "Novo Produto para Análise da Comissão",
                message = "O vendedor ${product.sellerName} registou '${product.title}'. Aguarda aprovação para a vitrine pública."
            )
        )
        return id
    }

    suspend fun setProductApproval(id: Long, approved: Boolean) {
        productDao.setProductApproval(id, approved)
        val product = productDao.getProductById(id)
        if (product != null && approved) {
            notificationDao.insertNotification(
                NotificationEntity(
                    targetRole = "VENDEDOR",
                    title = "Produto Aprovado na Vitrine!",
                    message = "A comissão de análise aprovou '${product.title}'. O produto já está disponível na vitrine pública do Macumu Global!"
                )
            )
        }
    }

    // --- Cart ---
    fun getCartItems(): Flow<List<CartItemEntity>> = cartDao.getCartItems()

    suspend fun addToCart(product: ProductEntity, quantity: Int = 1) {
        val effectivePrice = product.promoPriceMzn ?: product.priceMzn
        cartDao.insertCartItem(
            CartItemEntity(
                productId = product.id,
                productTitle = product.title,
                productPriceMzn = effectivePrice,
                productImageUrl = product.imageUrl,
                sellerName = product.sellerName,
                sellerType = product.sellerType,
                quantity = quantity
            )
        )
    }

    suspend fun updateCartQuantity(id: Long, quantity: Int) {
        if (quantity <= 0) {
            cartDao.removeCartItem(id)
        } else {
            cartDao.updateQuantity(id, quantity)
        }
    }

    suspend fun removeFromCart(id: Long) = cartDao.removeCartItem(id)
    suspend fun clearCart() = cartDao.clearCart()

    // --- Orders & Escrow System ---
    fun getAllOrders(): Flow<List<OrderEntity>> = orderDao.getAllOrders()
    fun getOrdersForSeller(sellerId: Long): Flow<List<OrderEntity>> = orderDao.getOrdersForSeller(sellerId)

    suspend fun placeOrder(
        buyerName: String,
        buyerPhone: String,
        province: String,
        cityOrDistrict: String,
        neighborhood: String,
        referencePoint: String,
        items: List<CartItemEntity>,
        paymentMethod: String,
        affiliateCode: String? = null
    ): String {
        val subtotal = items.sumOf { it.productPriceMzn * it.quantity }
        val deliveryFee = if (province.contains("Maputo", ignoreCase = true)) 200.0 else 450.0
        val total = subtotal + deliveryFee
        val orderCode = "MC-" + (10000..99999).random()

        val platformFee = subtotal * 0.05 // 5% platform commission
        val affiliateCommission = if (!affiliateCode.isNullOrBlank()) subtotal * 0.08 else 0.0 // 8% affiliate

        val isCashOnDelivery = paymentMethod.contains("Entrega", ignoreCase = true)
        val initialPaymentStatus = if (isCashOnDelivery) "PENDENTE_NA_ENTREGA" else "PAGO_RETIDO_ESCROW"

        val summary = items.joinToString(", ") { "${it.quantity}x ${it.productTitle}" }
        val sellerName = items.firstOrNull()?.sellerName ?: "Vendedor Macumu"
        val sellerId = 101L

        val order = OrderEntity(
            orderCode = orderCode,
            buyerName = buyerName,
            buyerPhone = buyerPhone,
            province = province,
            cityOrDistrict = cityOrDistrict,
            neighborhood = neighborhood,
            referencePoint = referencePoint,
            itemsSummary = summary,
            subtotalMzn = subtotal,
            deliveryFeeMzn = deliveryFee,
            totalMzn = total,
            paymentMethod = paymentMethod,
            paymentStatus = initialPaymentStatus,
            orderStatus = "PAGAMENTO_CONFIRMADO",
            sellerId = sellerId,
            sellerName = sellerName,
            affiliateCode = affiliateCode,
            affiliateCommissionMzn = affiliateCommission,
            platformFeeMzn = platformFee
        )

        orderDao.insertOrder(order)

        // Ledger Entry: Escrow lock
        if (!isCashOnDelivery) {
            financeDao.insertLedgerEntry(
                LedgerEntryEntity(
                    transactionRef = "TX-ESCROW-" + UUID.randomUUID().toString().take(8).uppercase(),
                    account = "ESCROW_CUSTODIA",
                    type = "CREDITO",
                    amountMzn = total,
                    orderCode = orderCode,
                    description = "Retenção de garantia de fundos via $paymentMethod para a encomenda $orderCode"
                )
            )
        }

        // Notifications
        notificationDao.insertNotification(
            NotificationEntity(
                targetRole = "COMPRADOR",
                title = "Encomenda $orderCode Confirmada",
                message = "Pagamento retido com segurança pelo sistema de Custódia Macumu. O vendedor foi notificado.",
                relatedOrderCode = orderCode
            )
        )
        notificationDao.insertNotification(
            NotificationEntity(
                targetRole = "VENDEDOR",
                title = "Nova Encomenda $orderCode Recebida!",
                message = "Total: $total MZN (Fundos garantidos na custódia). Prepare o envio para $neighborhood, $cityOrDistrict.",
                relatedOrderCode = orderCode
            )
        )

        cartDao.clearCart()
        return orderCode
    }

    suspend fun updateSellerOrderStatus(orderCode: String, newStatus: String) {
        val deliveredAt = if (newStatus == "ENTREGUE") System.currentTimeMillis() else null
        orderDao.updateOrderStatus(orderCode, newStatus, deliveredAt)

        val message = when (newStatus) {
            "EM_PREPARACAO" -> "O vendedor começou a preparar o seu pedido."
            "EM_TRANSITO" -> "A sua encomenda saiu para entrega pelo transportador parceiro."
            "ENTREGUE" -> "A sua encomenda foi entregue. Por favor confirme a recepção na app para libertar os fundos ao vendedor."
            else -> "Actualização de status da encomenda: $newStatus"
        }

        notificationDao.insertNotification(
            NotificationEntity(
                targetRole = "COMPRADOR",
                title = "Status da Encomenda $orderCode",
                message = message,
                relatedOrderCode = orderCode
            )
        )
    }

    /**
     * Buyer confirms reception of item:
     * Releases Escrow custody to Seller Wallet and credits Affiliate commission & Platform fee in the Financial Ledger!
     */
    suspend fun confirmOrderReception(orderCode: String) {
        val order = orderDao.getOrderByCode(orderCode) ?: return
        orderDao.updateOrderStatus(orderCode, "CONCLUIDO")
        orderDao.updateOrderPaymentStatus(orderCode, "LIBERTADO_VENDEDOR", System.currentTimeMillis())

        val sellerPayout = order.totalMzn - order.platformFeeMzn - order.affiliateCommissionMzn

        // 1. Debit Escrow Account
        financeDao.insertLedgerEntry(
            LedgerEntryEntity(
                transactionRef = "TX-RELEASE-" + UUID.randomUUID().toString().take(8).uppercase(),
                account = "ESCROW_CUSTODIA",
                type = "DEBITO",
                amountMzn = order.totalMzn,
                orderCode = orderCode,
                description = "Libertação da retenção de custódia após recepção confirmada pelo comprador"
            )
        )

        // 2. Credit Seller Wallet
        financeDao.insertLedgerEntry(
            LedgerEntryEntity(
                transactionRef = "TX-SELLER-" + UUID.randomUUID().toString().take(8).uppercase(),
                account = "CARTEIRA_VENDEDOR",
                type = "CREDITO",
                amountMzn = sellerPayout,
                orderCode = orderCode,
                description = "Crédito líquido na carteira do vendedor por conclusão de entrega"
            )
        )

        // 3. Credit Platform Commission
        financeDao.insertLedgerEntry(
            LedgerEntryEntity(
                transactionRef = "TX-FEE-" + UUID.randomUUID().toString().take(8).uppercase(),
                account = "COMISSAO_PLATAFORMA",
                type = "CREDITO",
                amountMzn = order.platformFeeMzn,
                orderCode = orderCode,
                description = "Taxa de intermediação Macumu Global (5%)"
            )
        )

        // 4. Credit Affiliate if present
        if (!order.affiliateCode.isNullOrBlank() && order.affiliateCommissionMzn > 0) {
            financeDao.insertLedgerEntry(
                LedgerEntryEntity(
                    transactionRef = "TX-AFFIL-" + UUID.randomUUID().toString().take(8).uppercase(),
                    account = "CARTEIRA_AFILIADO",
                    type = "CREDITO",
                    amountMzn = order.affiliateCommissionMzn,
                    orderCode = orderCode,
                    description = "Comissão atribuída ao afiliado ${order.affiliateCode}"
                )
            )
            // Update affiliate stats
            affiliateDao.recordConversion(order.affiliateCode, 1L, order.affiliateCommissionMzn)

            notificationDao.insertNotification(
                NotificationEntity(
                    targetRole = "AFILIADO",
                    title = "Comissão Aprovada!",
                    message = "Ganhou ${order.affiliateCommissionMzn} MZN pela venda atribuída da encomenda $orderCode.",
                    relatedOrderCode = orderCode
                )
            )
        }

        // Notify Seller
        notificationDao.insertNotification(
            NotificationEntity(
                targetRole = "VENDEDOR",
                title = "Fundos Disponíveis!",
                message = "O comprador confirmou a recepção de $orderCode. O valor líquido de $sellerPayout MZN está disponível para levantamento via M-Pesa.",
                relatedOrderCode = orderCode
            )
        )
    }

    // --- Financial Ledger & Withdrawals ---
    fun getAllLedgerEntries(): Flow<List<LedgerEntryEntity>> = financeDao.getAllLedgerEntries()
    fun getAllWithdrawals(): Flow<List<WithdrawalEntity>> = financeDao.getAllWithdrawals()

    suspend fun requestWithdrawal(
        beneficiaryType: String,
        beneficiaryName: String,
        method: String,
        phoneNumberOrAccount: String,
        amountMzn: Double
    ): Long {
        val id = financeDao.insertWithdrawal(
            WithdrawalEntity(
                beneficiaryType = beneficiaryType,
                beneficiaryName = beneficiaryName,
                method = method,
                phoneNumberOrAccount = phoneNumberOrAccount,
                amountMzn = amountMzn,
                status = "PENDENTE"
            )
        )
        notificationDao.insertNotification(
            NotificationEntity(
                targetRole = "ADMIN",
                title = "Novo Pedido de Levantamento",
                message = "$beneficiaryName ($beneficiaryType) solicitou levantamento de $amountMzn MZN via $method ($phoneNumberOrAccount)."
            )
        )
        return id
    }

    suspend fun processWithdrawal(id: Long, approved: Boolean) {
        val status = if (approved) "APROVADO" else "REJEITADO"
        financeDao.updateWithdrawalStatus(id, status)
    }

    // --- Affiliate System ---
    fun getAffiliateLinks(code: String): Flow<List<AffiliateLinkEntity>> = affiliateDao.getLinksForAffiliate(code)

    suspend fun generateAffiliateLink(product: ProductEntity, affiliateCode: String): AffiliateLinkEntity {
        val existing = affiliateDao.getLinkByProductAndCode(product.id, affiliateCode)
        if (existing != null) return existing

        val link = AffiliateLinkEntity(
            affiliateCode = affiliateCode,
            productId = product.id,
            productTitle = product.title,
            productPriceMzn = product.promoPriceMzn ?: product.priceMzn,
            commissionPercent = 8.0,
            uniqueUrl = "https://macumu.co.mz/p/${product.id}?ref=$affiliateCode"
        )
        val id = affiliateDao.insertAffiliateLink(link)
        return link.copy(id = id)
    }

    suspend fun recordAffiliateClick(linkId: Long) {
        affiliateDao.recordClick(linkId)
    }

    // --- Disputes ---
    fun getAllDisputes(): Flow<List<DisputeEntity>> = disputeDao.getAllDisputes()

    suspend fun createDispute(orderCode: String, buyerName: String, sellerName: String, reason: String, description: String) {
        disputeDao.insertDispute(
            DisputeEntity(
                orderCode = orderCode,
                buyerName = buyerName,
                sellerName = sellerName,
                reason = reason,
                description = description,
                status = "EM_ANALISE"
            )
        )
        orderDao.updateOrderStatus(orderCode, "EM_DISPUTA")

        notificationDao.insertNotification(
            NotificationEntity(
                targetRole = "ADMIN",
                title = "Disputa Aberta na Encomenda $orderCode",
                message = "Comprador $buyerName abriu disputa contra $sellerName. Motivo: $reason."
            )
        )
    }

    suspend fun resolveDispute(disputeId: Long, orderCode: String, refundToBuyer: Boolean, adminNotes: String) {
        val status = if (refundToBuyer) "RESOLVIDO_REEMBOLSO" else "RESOLVIDO_LIBERADO"
        disputeDao.resolveDispute(disputeId, status, adminNotes)

        if (refundToBuyer) {
            orderDao.updateOrderPaymentStatus(orderCode, "REEMBOLSADO")
            orderDao.updateOrderStatus(orderCode, "CANCELADO")
            financeDao.insertLedgerEntry(
                LedgerEntryEntity(
                    transactionRef = "TX-REFUND-" + UUID.randomUUID().toString().take(8).uppercase(),
                    account = "ESCROW_CUSTODIA",
                    type = "DEBITO",
                    amountMzn = 0.0, // Reembolso processado
                    orderCode = orderCode,
                    description = "Reembolso ao comprador após decisão de disputa: $adminNotes"
                )
            )
        } else {
            confirmOrderReception(orderCode)
        }
    }

    // --- Notifications ---
    fun getNotificationsForRole(role: String): Flow<List<NotificationEntity>> = notificationDao.getNotificationsForRole(role)
    suspend fun markNotificationAsRead(id: Long) = notificationDao.markAsRead(id)

    // --- User Registrations & Developer Approval ---
    fun getAllUserRegistrations(): Flow<List<UserRegistrationEntity>> = userRegistrationDao.getAllRegistrations()
    fun getPendingUserRegistrations(): Flow<List<UserRegistrationEntity>> = userRegistrationDao.getPendingRegistrations()
    suspend fun getRegistrationByPhone(phone: String): UserRegistrationEntity? = userRegistrationDao.getRegistrationByPhone(phone)
    suspend fun getRegistrationById(id: Long): UserRegistrationEntity? = userRegistrationDao.getRegistrationById(id)

    suspend fun registerUser(
        fullName: String,
        phoneNumber: String,
        email: String,
        requestedRole: String,
        sellerType: String,
        businessName: String,
        province: String
    ): Long {
        val reg = UserRegistrationEntity(
            fullName = fullName,
            phoneNumber = phoneNumber,
            email = email,
            requestedRole = requestedRole,
            sellerType = sellerType,
            businessName = businessName,
            province = province,
            status = "PENDING_APPROVAL"
        )
        val id = userRegistrationDao.insertRegistration(reg)

        // Notify developer / admin back-office of new registration requiring approval
        notificationDao.insertNotification(
            NotificationEntity(
                targetRole = "ADMIN",
                title = "Nova Adesão Aguardando Aval",
                message = "$fullName solicitou adesão como $requestedRole. Validação do Desenvolvedor pendente."
            )
        )
        return id
    }

    suspend fun approveUserRegistration(id: Long, notes: String = "Validado e aprovado pelo Desenvolvedor") {
        userRegistrationDao.updateRegistrationStatus(id, "APPROVED", notes)
        val reg = userRegistrationDao.getRegistrationById(id)
        if (reg != null) {
            notificationDao.insertNotification(
                NotificationEntity(
                    targetRole = reg.requestedRole,
                    title = "Adesão Aprovada pelo Desenvolvedor!",
                    message = "Parabéns ${reg.fullName}! O desenvolvedor aprovou a sua conta. Já pode transacionar no Macumu Global."
                )
            )
        }
    }

    suspend fun rejectUserRegistration(id: Long, notes: String = "Rejeitado na verificação de conformidade") {
        userRegistrationDao.updateRegistrationStatus(id, "REJECTED", notes)
    }
}

