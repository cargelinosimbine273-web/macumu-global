package com.example.ui.screens.seller

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.data.local.OrderEntity
import com.example.data.local.ProductEntity
import com.example.ui.FinancialStats
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerDashboardScreen(
    orders: List<OrderEntity>,
    sellerProducts: List<ProductEntity>,
    financialStats: FinancialStats,
    currency: String,
    onPublishProductClick: () -> Unit,
    onUpdateOrderStatus: (orderCode: String, status: String) -> Unit,
    onRequestWithdrawal: (type: String, name: String, method: String, phone: String, amount: Double) -> Unit
) {
    var selectedTab by remember { mutableStateOf("PRODUTOS") }
    var showWithdrawalDialog by remember { mutableStateOf(false) }
    var withdrawalMethod by remember { mutableStateOf("M-Pesa") }
    var withdrawalPhone by remember { mutableStateOf("+258 84 987 6543") }
    var withdrawalAmount by remember { mutableStateOf("") }

    val pendingProductsCount = sellerProducts.count { !it.isApproved }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavyBackground)
            .padding(horizontal = 14.dp),
        contentPadding = PaddingValues(top = 14.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Seller Profile & Mode Header
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = DarkNavyCardElevated),
                border = BorderStroke(1.dp, DarkNavyCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(MacumuOrange.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Storefront,
                                contentDescription = null,
                                tint = MacumuOrange,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Bazar & Loja do Vendedor",
                                color = TextWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "Comércio Formal & Informal Moçambique",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Button(
                        onClick = onPublishProductClick,
                        colors = ButtonDefaults.buttonColors(containerColor = MacumuOrange),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(36.dp).testTag("publish_new_product_btn")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Publicar", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Dedicated Subtabs for Seller: Produtos (Registos), Encomendas (Histórico), Carteira
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(
                    "PRODUTOS" to "Meus Produtos (${sellerProducts.size})",
                    "ENCOMENDAS" to "Histórico de Vendas (${orders.size})",
                    "CARTEIRA" to "Carteira & Levantamentos"
                ).forEach { (code, label) ->
                    val isSelected = selectedTab == code
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) MacumuOrange else DarkNavyCard,
                        border = BorderStroke(1.dp, if (isSelected) MacumuOrange else DarkNavyCardBorder),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedTab = code }
                            .testTag("seller_tab_${code.lowercase()}")
                    ) {
                        Box(
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) Color.White else TextMuted,
                                fontSize = 10.5.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }

        when (selectedTab) {
            "PRODUTOS" -> {
                // Notice banner regarding Commission Analysis
                item {
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF132034)),
                        border = BorderStroke(1.dp, EmolaBlue.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Inventory2,
                                    contentDescription = null,
                                    tint = MacumuOrange,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Comissão de Análise Macumu Global",
                                    color = TextWhite,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.5.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Todo o produto submetido passa pela comissão de análise da plataforma. Assim que a comissão aprova, o produto vai automaticamente para a vitrine pública de compradores nacionais e internacionais.",
                                color = TextMuted,
                                fontSize = 11.sp,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }

                if (sellerProducts.isEmpty()) {
                    item {
                        Card(
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkNavyCard),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Nenhum produto registado ainda.",
                                    color = TextWhite,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Clique em 'Publicar' no topo para registar o seu produto ou lote informal.",
                                    color = TextMuted,
                                    fontSize = 11.5.sp
                                )
                            }
                        }
                    }
                } else {
                    items(sellerProducts) { product ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkNavyCard),
                            border = BorderStroke(
                                1.dp,
                                if (!product.isApproved) EscrowAmber.copy(alpha = 0.8f) else DarkNavyCardBorder
                            ),
                            modifier = Modifier.fillMaxWidth().testTag("seller_product_${product.id}")
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = product.title,
                                            color = TextWhite,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.5.sp
                                        )
                                        Text(
                                            text = "Categoria: ${product.category} • Stock: ${product.stock} unid.",
                                            color = TextMuted,
                                            fontSize = 11.sp
                                        )
                                        Text(
                                            text = "Origem: ${product.sellerLocation} (${if (product.isNational) "Nacional" else "Importação"})",
                                            color = TextDarkMuted,
                                            fontSize = 10.sp
                                        )
                                    }

                                    Text(
                                        text = CurrencyUtils.formatPrice(product.promoPriceMzn ?: product.priceMzn, currency),
                                        color = MacumuOrange,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 14.sp
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Prominent Commission Approval Status Badge
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (product.isApproved) Color(0xFF0D2F18) else Color(0xFF382506),
                                    border = BorderStroke(
                                        1.dp,
                                        if (product.isApproved) MpesaGreen.copy(alpha = 0.5f) else EscrowAmber.copy(alpha = 0.5f)
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = if (product.isApproved) Icons.Default.CheckCircle else Icons.Default.Lock,
                                            contentDescription = null,
                                            tint = if (product.isApproved) MpesaGreen else EscrowAmber,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Column {
                                            Text(
                                                text = if (product.isApproved)
                                                    "✅ Aprovado na Vitrine Pública"
                                                else
                                                    "⏳ Em Análise pela Comissão da Plataforma",
                                                color = if (product.isApproved) MpesaGreen else EscrowAmber,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp
                                            )
                                            Text(
                                                text = if (product.isApproved)
                                                    "Disponível para compra e divulgação de afiliados"
                                                else
                                                    "O produto só irá para a vitrine pública após aprovação da comissão",
                                                color = TextMuted,
                                                fontSize = 9.5.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            "ENCOMENDAS" -> {
                // Orders Management
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Histórico de Encomendas Recebidas",
                            color = TextWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "${orders.size} pedidos",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                }

                if (orders.isEmpty()) {
                    item {
                        Card(
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkNavyCard),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Nenhuma encomenda recebida ainda. Quando um cliente comprar seus produtos aprovados, você poderá atualizar os estados de envio aqui.",
                                color = TextMuted,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                } else {
                    items(orders) { order ->
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
                                        text = "${order.orderCode} • ${order.buyerName}",
                                        color = TextWhite,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = CurrencyUtils.formatPrice(order.totalMzn, currency),
                                        color = MacumuOrange,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }

                                Text(
                                    text = order.itemsSummary,
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = "Destino: ${order.neighborhood}, ${order.cityOrDistrict} (${order.province})",
                                    color = TextDarkMuted,
                                    fontSize = 10.sp
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                // Status Progression Action Buttons
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    if (order.orderStatus == "PAGAMENTO_CONFIRMADO") {
                                        Button(
                                            onClick = { onUpdateOrderStatus(order.orderCode, "EM_PREPARACAO") },
                                            shape = RoundedCornerShape(6.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = EmolaBlue),
                                            modifier = Modifier.weight(1f).height(34.dp)
                                        ) {
                                            Text("Preparar Envio", fontSize = 11.sp)
                                        }
                                    } else if (order.orderStatus == "EM_PREPARACAO") {
                                        Button(
                                            onClick = { onUpdateOrderStatus(order.orderCode, "EM_TRANSITO") },
                                            shape = RoundedCornerShape(6.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = MacumuOrange),
                                            modifier = Modifier.weight(1f).height(34.dp)
                                        ) {
                                            Text("Despachar p/ Transporte", fontSize = 11.sp)
                                        }
                                    } else if (order.orderStatus == "EM_TRANSITO") {
                                        Button(
                                            onClick = { onUpdateOrderStatus(order.orderCode, "ENTREGUE") },
                                            shape = RoundedCornerShape(6.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = MpesaGreen),
                                            modifier = Modifier.weight(1f).height(34.dp)
                                        ) {
                                            Text("Marcar Entregue", fontSize = 11.sp)
                                        }
                                    } else {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = MpesaGreen.copy(alpha = 0.15f),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Box(
                                                modifier = Modifier.padding(6.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = if (order.orderStatus == "CONCLUIDO") "Concluído (Fundos Libertados)" else order.orderStatus,
                                                    color = MpesaGreen,
                                                    fontSize = 10.5.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            "CARTEIRA" -> {
                // Financial Wallet Cards
                item {
                    Text(
                        text = "Carteira Financeira do Vendedor",
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Available Balance
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F2B1C)),
                            border = BorderStroke(1.dp, MpesaGreen.copy(alpha = 0.5f)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Payments, contentDescription = null, tint = MpesaGreen, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Disponível", color = MpesaGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = CurrencyUtils.formatPrice(financialStats.sellerAvailableMzn, currency),
                                    color = TextWhite,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 16.sp
                                )
                                Text("Pronto p/ M-Pesa", color = TextMuted, fontSize = 9.5.sp)
                            }
                        }

                        // Escrow Retained Balance
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF2E220D)),
                            border = BorderStroke(1.dp, EscrowAmber.copy(alpha = 0.5f)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Lock, contentDescription = null, tint = EscrowAmber, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Em Custódia", color = EscrowAmber, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = CurrencyUtils.formatPrice(financialStats.escrowLockedMzn, currency),
                                    color = TextWhite,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 16.sp
                                )
                                Text("Libertado pós entrega", color = TextMuted, fontSize = 9.5.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Withdraw Button
                    Button(
                        onClick = { showWithdrawalDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = MpesaGreen),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp)
                            .testTag("request_seller_withdrawal_btn")
                    ) {
                        Icon(Icons.Default.ArrowDownward, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Pedir Levantamento via M-Pesa / e-Mola",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }

    // Withdrawal Modal
    if (showWithdrawalDialog) {
        AlertDialog(
            onDismissRequest = { showWithdrawalDialog = false },
            containerColor = DarkNavyCardElevated,
            title = {
                Text("Levantamento de Saldo", color = TextWhite, fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    Text(
                        "Saldo disponível: ${CurrencyUtils.formatPrice(financialStats.sellerAvailableMzn, currency)}",
                        color = MpesaGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("M-Pesa", "e-Mola", "Banco").forEach { method ->
                            val isSelected = withdrawalMethod == method
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) MacumuOrange else DarkNavyCard,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { withdrawalMethod = method }
                            ) {
                                Box(modifier = Modifier.padding(8.dp), contentAlignment = Alignment.Center) {
                                    Text(
                                        text = method,
                                        color = if (isSelected) Color.White else TextMuted,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = withdrawalPhone,
                        onValueChange = { withdrawalPhone = it },
                        label = { Text("Número de Telemóvel / Conta") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedBorderColor = MacumuOrange,
                            unfocusedBorderColor = DarkNavyCardBorder
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = withdrawalAmount,
                        onValueChange = { withdrawalAmount = it },
                        label = { Text("Valor a Levantar (MZN)") },
                        placeholder = { Text("Ex: 1500") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedBorderColor = MacumuOrange,
                            unfocusedBorderColor = DarkNavyCardBorder
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = withdrawalAmount.toDoubleOrNull() ?: 500.0
                        onRequestWithdrawal("VENDEDOR", "Mamã Esperança", withdrawalMethod, withdrawalPhone, amount)
                        showWithdrawalDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MpesaGreen)
                ) {
                    Text("Confirmar Levantamento", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showWithdrawalDialog = false }) {
                    Text("Cancelar", color = TextMuted)
                }
            }
        )
    }
}
