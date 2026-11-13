package com.example.ui.screens.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.DisputeEntity
import com.example.data.local.LedgerEntryEntity
import com.example.data.local.ProductEntity
import com.example.data.local.UserRegistrationEntity
import com.example.data.local.WithdrawalEntity
import com.example.ui.FinancialStats
import com.example.ui.theme.DangerRed
import com.example.ui.theme.DarkNavyBackground
import com.example.ui.theme.DarkNavyCard
import com.example.ui.theme.DarkNavyCardBorder
import com.example.ui.theme.DarkNavyCardElevated
import com.example.ui.theme.EmolaBlue
import com.example.ui.theme.EscrowAmber
import com.example.ui.theme.MacumuOrange
import com.example.ui.theme.MpesaGreen
import com.example.ui.theme.TextDarkMuted
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.util.CurrencyUtils

@Composable
fun AdminScreen(
    financialStats: FinancialStats,
    disputes: List<DisputeEntity>,
    withdrawals: List<WithdrawalEntity>,
    ledgerEntries: List<LedgerEntryEntity>,
    products: List<ProductEntity>,
    userRegistrations: List<UserRegistrationEntity> = emptyList(),
    currency: String,
    onResolveDispute: (disputeId: Long, orderCode: String, refundBuyer: Boolean, notes: String) -> Unit,
    onProcessWithdrawal: (id: Long, approved: Boolean) -> Unit,
    onApproveRegistration: (Long) -> Unit = {},
    onRejectRegistration: (Long) -> Unit = {},
    onApproveProduct: (Long) -> Unit = {},
    onRejectProduct: (Long) -> Unit = {},
    onExitDeveloperMode: () -> Unit = {}
) {
    val pendingCount = userRegistrations.count { it.status == "PENDING_APPROVAL" }
    val pendingProductsCount = products.count { !it.isApproved }
    var selectedTab by remember { mutableStateOf(if (pendingCount > 0) "ADESOES" else if (pendingProductsCount > 0) "PRODUTOS" else "GERAL") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavyBackground)
            .padding(horizontal = 14.dp),
        contentPadding = PaddingValues(top = 14.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Admin Header
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = DarkNavyCardElevated),
                border = BorderStroke(1.dp, DarkNavyCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(MacumuOrange.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = MacumuOrange, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Consola do Desenvolvedor & Admin", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                            Text("Validação de Novos Membros & Custódia", color = TextMuted, fontSize = 10.sp)
                        }
                    }

                    OutlinedButton(
                        onClick = onExitDeveloperMode,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444)),
                        border = BorderStroke(1.dp, Color(0xFFEF4444)),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Text("Sair", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Subtabs
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf(
                    "ADESOES" to "Adesões ($pendingCount)",
                    "PRODUTOS" to "Produtos ($pendingProductsCount)",
                    "GERAL" to "Geral",
                    "DISPUTAS" to "Disputas (${disputes.size})",
                    "LEVANTAMENTOS" to "Saques (${withdrawals.count { it.status == "PENDENTE" }})",
                    "LEDGER" to "Ledger"
                ).forEach { (code, label) ->
                    val isSelected = selectedTab == code
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) MacumuOrange else DarkNavyCard,
                        border = BorderStroke(1.dp, if (isSelected) MacumuOrange else DarkNavyCardBorder),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedTab = code }
                            .testTag("admin_tab_${code.lowercase()}")
                    ) {
                        Box(modifier = Modifier.padding(vertical = 6.dp), contentAlignment = Alignment.Center) {
                            Text(
                                text = label,
                                color = if (isSelected) Color.White else TextMuted,
                                fontSize = 9.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }

        when (selectedTab) {
            "ADESOES" -> {
                // Section: Developer Validation of New Registrations
                item {
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF142438)),
                        border = BorderStroke(1.dp, EmolaBlue.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AdminPanelSettings,
                                    contentDescription = null,
                                    tint = MacumuOrange,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Validação de Adesões pelo Desenvolvedor",
                                    color = TextWhite,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.5.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "Cada utilizador que se inscreve deve receber o aval do desenvolvedor antes de poder comprar com custódia, publicar produtos ou auferir comissões.",
                                color = TextMuted,
                                fontSize = 10.5.sp,
                                lineHeight = 14.sp
                            )
                        }
                    }
                }

                if (userRegistrations.isEmpty()) {
                    item {
                        Card(
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkNavyCard),
                            modifier = Modifier.fillMaxWidth().padding(top = 10.dp)
                        ) {
                            Text(
                                text = "Nenhuma solicitação de adesão no momento.",
                                color = TextMuted,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(14.dp)
                            )
                        }
                    }
                } else {
                    items(userRegistrations) { reg ->
                        Card(
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkNavyCard),
                            border = BorderStroke(
                                1.dp,
                                if (reg.status == "PENDING_APPROVAL") EscrowAmber.copy(alpha = 0.8f) else DarkNavyCardBorder
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = reg.fullName,
                                            color = TextWhite,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                        Text(
                                            text = "Contacto: ${reg.phoneNumber} ${if (reg.email.isNotBlank()) "• ${reg.email}" else ""}",
                                            color = TextMuted,
                                            fontSize = 10.5.sp
                                        )
                                        if (reg.businessName.isNotBlank()) {
                                            Text(
                                                text = "Loja/Bazar: ${reg.businessName} (${reg.province})",
                                                color = MacumuOrange,
                                                fontSize = 10.5.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                        } else {
                                            Text(
                                                text = "Localização: ${reg.province}",
                                                color = TextMuted,
                                                fontSize = 10.sp
                                            )
                                        }
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = when (reg.requestedRole) {
                                            "VENDEDOR" -> if (reg.sellerType == "INFORMAL") Color(0xFF4A2500) else Color(0xFF0F3254)
                                            "AFILIADO" -> Color(0xFF2A1550)
                                            else -> Color(0xFF133220)
                                        }
                                    ) {
                                        Text(
                                            text = when (reg.requestedRole) {
                                                "VENDEDOR" -> if (reg.sellerType == "INFORMAL") "Bazar Informal" else "Loja Formal"
                                                "AFILIADO" -> "Afiliado"
                                                else -> "Comprador"
                                            },
                                            color = TextWhite,
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Status Badge
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = when (reg.status) {
                                            "APPROVED" -> Color(0xFF0D331A)
                                            "REJECTED" -> Color(0xFF3E1212)
                                            else -> Color(0xFF3B2706)
                                        }
                                    ) {
                                        Text(
                                            text = when (reg.status) {
                                                "APPROVED" -> "✅ Aprovado pelo Desenvolvedor"
                                                "REJECTED" -> "❌ Adesão Rejeitada"
                                                else -> "⏳ Aguarda Aval do Desenvolvedor"
                                            },
                                            color = when (reg.status) {
                                                "APPROVED" -> MpesaGreen
                                                "REJECTED" -> DangerRed
                                                else -> EscrowAmber
                                            },
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }

                                    // Action buttons if pending
                                    if (reg.status == "PENDING_APPROVAL") {
                                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            OutlinedButton(
                                                onClick = { onRejectRegistration(reg.id) },
                                                shape = RoundedCornerShape(6.dp),
                                                colors = ButtonDefaults.outlinedButtonColors(contentColor = DangerRed),
                                                border = BorderStroke(0.5.dp, DangerRed),
                                                modifier = Modifier.height(28.dp)
                                            ) {
                                                Text("Recusar", fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                                            }

                                            Button(
                                                onClick = { onApproveRegistration(reg.id) },
                                                shape = RoundedCornerShape(6.dp),
                                                colors = ButtonDefaults.buttonColors(containerColor = MpesaGreen),
                                                modifier = Modifier.height(28.dp).testTag("approve_user_${reg.id}")
                                            ) {
                                                Text("Aprovar", color = Color.White, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            "PRODUTOS" -> {
                // Section: Platform Analysis Commission for Product Showcase Moderation
                item {
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF142438)),
                        border = BorderStroke(1.dp, EmolaBlue.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Storefront,
                                    contentDescription = null,
                                    tint = MacumuOrange,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Comissão de Análise de Produtos",
                                    color = TextWhite,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.5.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "Os produtos registados pelos vendedores (formais ou informais) só vão automaticamente para a vitrine pública após a autorização da comissão de moderação.",
                                color = TextMuted,
                                fontSize = 10.5.sp,
                                lineHeight = 14.sp
                            )
                        }
                    }
                }

                if (products.isEmpty()) {
                    item {
                        Card(
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkNavyCard),
                            modifier = Modifier.fillMaxWidth().padding(top = 10.dp)
                        ) {
                            Text(
                                text = "Nenhum produto cadastrado no catálogo.",
                                color = TextMuted,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(14.dp)
                            )
                        }
                    }
                } else {
                    items(products) { prod ->
                        Card(
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkNavyCard),
                            border = BorderStroke(
                                1.dp,
                                if (!prod.isApproved) EscrowAmber.copy(alpha = 0.8f) else DarkNavyCardBorder
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = prod.title,
                                            color = TextWhite,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.5.sp
                                        )
                                        Text(
                                            text = "Vendedor: ${prod.sellerName} (${prod.sellerType}) • ${prod.sellerLocation}",
                                            color = TextMuted,
                                            fontSize = 10.5.sp
                                        )
                                        Text(
                                            text = "Preço: ${CurrencyUtils.formatPrice(prod.priceMzn, currency)} • Stock: ${prod.stock}",
                                            color = MacumuOrange,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (prod.isApproved) Color(0xFF0D331A) else Color(0xFF3B2706)
                                    ) {
                                        Text(
                                            text = if (prod.isApproved) "✅ Na Vitrine Pública" else "⏳ Em Análise",
                                            color = if (prod.isApproved) MpesaGreen else EscrowAmber,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (!prod.isApproved) {
                                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            OutlinedButton(
                                                onClick = { onRejectProduct(prod.id) },
                                                shape = RoundedCornerShape(6.dp),
                                                colors = ButtonDefaults.outlinedButtonColors(contentColor = DangerRed),
                                                border = BorderStroke(0.5.dp, DangerRed),
                                                modifier = Modifier.height(28.dp)
                                            ) {
                                                Text("Recusar", fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                                            }

                                            Button(
                                                onClick = { onApproveProduct(prod.id) },
                                                shape = RoundedCornerShape(6.dp),
                                                colors = ButtonDefaults.buttonColors(containerColor = MpesaGreen),
                                                modifier = Modifier.height(28.dp).testTag("approve_product_${prod.id}")
                                            ) {
                                                Text("Aprovar p/ Vitrine Pública", color = Color.White, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    } else {
                                        Text(
                                            text = "Aprovado pela Comissão da Plataforma",
                                            color = TextDarkMuted,
                                            fontSize = 10.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            "GERAL" -> {
                // Key Metrics
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Escrow Balance
                            Card(
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF281E0D)),
                                border = BorderStroke(1.dp, EscrowAmber),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Lock, contentDescription = null, tint = EscrowAmber, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Custódia Retida", color = EscrowAmber, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = CurrencyUtils.formatPrice(financialStats.escrowLockedMzn, currency),
                                        color = TextWhite,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 15.sp
                                    )
                                }
                            }

                            // Platform Fees
                            Card(
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F2618)),
                                border = BorderStroke(1.dp, MpesaGreen),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = MpesaGreen, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Receita Macumu (5%)", color = MpesaGreen, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = CurrencyUtils.formatPrice(financialStats.platformCommissionsMzn, currency),
                                        color = TextWhite,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 15.sp
                                    )
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Total GMV
                            Card(
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = DarkNavyCard),
                                border = BorderStroke(1.dp, DarkNavyCardBorder),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("Volume Transaccionado (GMV)", color = TextMuted, fontSize = 10.5.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = CurrencyUtils.formatPrice(financialStats.totalGmvMzn, currency),
                                        color = MacumuOrange,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                            }

                            // Orders Count
                            Card(
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = DarkNavyCard),
                                border = BorderStroke(1.dp, DarkNavyCardBorder),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("Total Encomendas", color = TextMuted, fontSize = 10.5.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "${financialStats.totalOrdersCount} encomendas",
                                        color = TextWhite,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // Recent Activity / Registered Products overview
                item {
                    Text("Catálogo e Vendedores Activos", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }

                items(products.take(4)) { prod ->
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkNavyCard),
                        border = BorderStroke(1.dp, DarkNavyCardBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(prod.title, color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 12.sp, maxLines = 1)
                                Text("${prod.sellerName} (${prod.sellerType}) • ${prod.sellerLocation}", color = TextMuted, fontSize = 10.5.sp)
                            }
                            Text(
                                text = CurrencyUtils.formatPrice(prod.promoPriceMzn ?: prod.priceMzn, currency),
                                color = MacumuOrange,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            "DISPUTAS" -> {
                if (disputes.isEmpty()) {
                    item {
                        Card(
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkNavyCard),
                            modifier = Modifier.fillMaxWidth().padding(top = 20.dp)
                        ) {
                            Text(
                                text = "Nenhuma disputa em aberto. Todos os clientes e vendedores estão em conformidade.",
                                color = TextMuted,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                } else {
                    items(disputes) { dispute ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkNavyCard),
                            border = BorderStroke(1.dp, if (dispute.status == "EM_ANALISE") DangerRed else DarkNavyCardBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Encomenda: ${dispute.orderCode}",
                                        color = TextWhite,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (dispute.status == "EM_ANALISE") DangerRed.copy(alpha = 0.2f) else MpesaGreen.copy(alpha = 0.2f)
                                    ) {
                                        Text(
                                            text = dispute.status,
                                            color = if (dispute.status == "EM_ANALISE") DangerRed else MpesaGreen,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Reclamante: ${dispute.buyerName} vs ${dispute.sellerName}",
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = "Motivo: ${dispute.reason}",
                                    color = TextWhite,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.5.sp
                                )
                                Text(
                                    text = dispute.description,
                                    color = TextDarkMuted,
                                    fontSize = 10.5.sp
                                )

                                if (dispute.status == "EM_ANALISE") {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Button(
                                            onClick = {
                                                onResolveDispute(
                                                    dispute.id,
                                                    dispute.orderCode,
                                                    true,
                                                    "Reembolso total autorizado após mediação Macumu"
                                                )
                                            },
                                            shape = RoundedCornerShape(8.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = DangerRed),
                                            modifier = Modifier.weight(1f).height(34.dp)
                                        ) {
                                            Text("Reembolsar Comprador", fontSize = 10.5.sp)
                                        }

                                        Button(
                                            onClick = {
                                                onResolveDispute(
                                                    dispute.id,
                                                    dispute.orderCode,
                                                    false,
                                                    "Comprovativo de entrega validado. Fundos libertados ao vendedor."
                                                )
                                            },
                                            shape = RoundedCornerShape(8.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = MpesaGreen),
                                            modifier = Modifier.weight(1f).height(34.dp)
                                        ) {
                                            Text("Libertar p/ Vendedor", fontSize = 10.5.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            "LEVANTAMENTOS" -> {
                if (withdrawals.isEmpty()) {
                    item {
                        Card(
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkNavyCard),
                            modifier = Modifier.fillMaxWidth().padding(top = 20.dp)
                        ) {
                            Text(
                                text = "Nenhum pedido de levantamento pendente.",
                                color = TextMuted,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                } else {
                    items(withdrawals) { w ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkNavyCard),
                            border = BorderStroke(1.dp, DarkNavyCardBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "${w.beneficiaryName} (${w.beneficiaryType})",
                                        color = TextWhite,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = CurrencyUtils.formatPrice(w.amountMzn, currency),
                                        color = MacumuOrange,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 14.sp
                                    )
                                }

                                Text(
                                    text = "Canal: ${w.method} • Conta: ${w.phoneNumberOrAccount}",
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                if (w.status == "PENDENTE") {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Button(
                                            onClick = { onProcessWithdrawal(w.id, true) },
                                            shape = RoundedCornerShape(8.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = MpesaGreen),
                                            modifier = Modifier.weight(1f).height(32.dp)
                                        ) {
                                            Text("Aprovar Payout", fontSize = 11.sp)
                                        }

                                        OutlinedButton(
                                            onClick = { onProcessWithdrawal(w.id, false) },
                                            shape = RoundedCornerShape(8.dp),
                                            border = BorderStroke(1.dp, DangerRed),
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = DangerRed),
                                            modifier = Modifier.weight(1f).height(32.dp)
                                        ) {
                                            Text("Recusar", fontSize = 11.sp)
                                        }
                                    }
                                } else {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (w.status == "APROVADO") MpesaGreen.copy(alpha = 0.2f) else DangerRed.copy(alpha = 0.2f)
                                    ) {
                                        Text(
                                            text = "Status: ${w.status}",
                                            color = if (w.status == "APROVADO") MpesaGreen else DangerRed,
                                            fontSize = 10.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            "LEDGER" -> {
                item {
                    Text(
                        text = "Livro-Razão Contabilístico Imutável (Double-Entry Ledger)",
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.5.sp
                    )
                }

                if (ledgerEntries.isEmpty()) {
                    item {
                        Card(
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkNavyCard),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Nenhuma movimentação contabilística ainda.",
                                color = TextMuted,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(14.dp)
                            )
                        }
                    }
                } else {
                    items(ledgerEntries) { entry ->
                        Card(
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkNavyCard),
                            border = BorderStroke(0.5.dp, DarkNavyCardBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = entry.account,
                                            color = when (entry.account) {
                                                "ESCROW_CUSTODIA" -> EscrowAmber
                                                "CARTEIRA_VENDEDOR" -> MpesaGreen
                                                "CARTEIRA_AFILIADO" -> EmolaBlue
                                                else -> MacumuOrange
                                            },
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(text = "• ${entry.type}", color = TextMuted, fontSize = 10.sp)
                                    }
                                    Text(text = entry.description, color = TextWhite, fontSize = 11.sp, maxLines = 2)
                                    Text(text = "Ref: ${entry.transactionRef} | Enc: ${entry.orderCode}", color = TextDarkMuted, fontSize = 9.sp)
                                }

                                Text(
                                    text = "${if (entry.type == "CREDITO") "+" else "-"}${CurrencyUtils.formatPrice(entry.amountMzn, currency)}",
                                    color = if (entry.type == "CREDITO") MpesaGreen else DangerRed,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 12.5.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
