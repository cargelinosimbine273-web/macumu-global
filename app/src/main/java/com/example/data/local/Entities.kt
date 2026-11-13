package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Product in Macumu Global marketplace.
 * Supports both informal sellers (e.g., Mercado do Zimpeto, Estrela, Xipamanine)
 * and formal registered merchants (Lojas Formais).
 */
@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val category: String,
    val priceMzn: Double,
    val promoPriceMzn: Double? = null,
    val stock: Int = 10,
    val sellerId: Long,
    val sellerName: String,
    val sellerType: String, // "INFORMAL" (Bazar/Mercado) or "FORMAL" (Loja Registada)
    val sellerLocation: String, // e.g. "Maputo - Mercado Zimpeto", "Matola", "Beira", "Nampula", "África do Sul"
    val isNational: Boolean = true, // true = Moçambique, false = Global / Internacional
    val rating: Float = 4.8f,
    val reviewCount: Int = 12,
    val imageUrl: String = "",
    val deliveryEstimate: String = "24-48 horas",
    val isApproved: Boolean = true,
    val isFeatured: Boolean = false,
    val marketName: String = "", // e.g. "Maputo Shopping Centre", "Alibaba", "Temu", "Mercado Xipamanine", "Mercado do Waresta"
    val regionZone: String = "", // "SUL", "CENTRO", "NORTE", "INTERNACIONAL"
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Shopping Cart item
 */
@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val productId: Long,
    val productTitle: String,
    val productPriceMzn: Double,
    val productImageUrl: String,
    val sellerName: String,
    val sellerType: String,
    val quantity: Int = 1,
    val addedAt: Long = System.currentTimeMillis()
)

/**
 * Customer Order with Escrow Guarantee (Retenção de Fundos Macumu)
 */
@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val orderCode: String, // e.g., "MC-90823"
    val buyerName: String,
    val buyerPhone: String,
    val province: String, // e.g., "Maputo Cidade", "Sofala", "Nampula"
    val cityOrDistrict: String,
    val neighborhood: String, // Bairro
    val referencePoint: String,
    val itemsSummary: String, // e.g. "2x Capulana Tradicional, 1x Smartphone"
    val subtotalMzn: Double,
    val deliveryFeeMzn: Double,
    val totalMzn: Double,
    val paymentMethod: String, // "M-Pesa", "e-Mola", "Pagamento na Entrega", "Cartão Bancário"
    val paymentStatus: String, // "PENDENTE", "PAGO_RETIDO_ESCROW", "LIBERTADO_VENDEDOR", "REEMBOLSADO"
    val orderStatus: String, // "PAGAMENTO_CONFIRMADO", "EM_PREPARACAO", "EM_TRANSITO", "ENTREGUE", "CONCLUIDO", "EM_DISPUTA"
    val sellerId: Long,
    val sellerName: String,
    val affiliateCode: String? = null,
    val affiliateCommissionMzn: Double = 0.0,
    val platformFeeMzn: Double = 0.0,
    val createdAt: Long = System.currentTimeMillis(),
    val deliveredAt: Long? = null,
    val completedAt: Long? = null
)

/**
 * Financial Double-Entry Ledger (Livro-Razão Financeiro)
 * Records all monetary movements transparently and immutably.
 */
@Entity(tableName = "ledger_entries")
data class LedgerEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val transactionRef: String,
    val account: String, // "ESCROW_CUSTODIA", "CARTEIRA_VENDEDOR", "CARTEIRA_AFILIADO", "COMISSAO_PLATAFORMA"
    val type: String, // "CREDITO", "DEBITO"
    val amountMzn: Double,
    val orderCode: String,
    val description: String,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Payout / Withdrawal Requests (Pedidos de Levantamento para M-Pesa / e-Mola)
 */
@Entity(tableName = "withdrawals")
data class WithdrawalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val beneficiaryType: String, // "VENDEDOR" or "AFILIADO"
    val beneficiaryName: String,
    val method: String, // "M-Pesa", "e-Mola", "Conta Bancária"
    val phoneNumberOrAccount: String,
    val amountMzn: Double,
    val status: String, // "PENDENTE", "APROVADO", "REJEITADO"
    val requestedAt: Long = System.currentTimeMillis(),
    val processedAt: Long? = null
)

/**
 * Affiliate Product Link
 */
@Entity(tableName = "affiliate_links")
data class AffiliateLinkEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val affiliateCode: String, // e.g., "AFIL-MOZ-88"
    val productId: Long,
    val productTitle: String,
    val productPriceMzn: Double,
    val commissionPercent: Double = 8.0, // default 8% commission
    val uniqueUrl: String, // e.g. "https://macumu.co.mz/p/102?ref=AFIL-MOZ-88"
    val clicks: Int = 0,
    val conversions: Int = 0,
    val totalEarnedMzn: Double = 0.0,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Disputes / Reclamações
 */
@Entity(tableName = "disputes")
data class DisputeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val orderCode: String,
    val buyerName: String,
    val sellerName: String,
    val reason: String, // e.g. "Produto não recebido", "Produto diferente do anunciado", "Defeito"
    val description: String,
    val status: String = "EM_ANALISE", // "EM_ANALISE", "RESOLVIDO_REEMBOLSO", "RESOLVIDO_LIBERADO"
    val adminNotes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * System Notification
 */
@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val targetRole: String, // "COMPRADOR", "VENDEDOR", "AFILIADO", "ADMIN"
    val title: String,
    val message: String,
    val relatedOrderCode: String? = null,
    val isRead: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * User Account & Registration with Developer/Admin Approval workflow.
 * Every newly registered user must await the developer's approval before
 * they can sell, buy with escrow, or earn affiliate commissions.
 * Status: "PENDING_APPROVAL", "APPROVED", "REJECTED"
 */
@Entity(tableName = "user_registrations")
data class UserRegistrationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fullName: String,
    val phoneNumber: String, // e.g. "+258 84 123 4567"
    val email: String = "",
    val requestedRole: String, // "COMPRADOR", "VENDEDOR", "AFILIADO"
    val sellerType: String = "", // "INFORMAL" (Bazar/Mercado) or "FORMAL" (Loja Registada) or ""
    val businessName: String = "",
    val province: String = "Maputo Cidade",
    val status: String = "PENDING_APPROVAL", // "PENDING_APPROVAL", "APPROVED", "REJECTED"
    val reviewerNotes: String = "",
    val registeredAt: Long = System.currentTimeMillis(),
    val approvedAt: Long? = null
)

