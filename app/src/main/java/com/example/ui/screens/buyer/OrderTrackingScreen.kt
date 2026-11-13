package com.example.ui.screens.buyer

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.example.ui.components.EscrowGuaranteedBanner
import com.example.ui.theme.DarkNavyBackground
import com.example.ui.theme.DarkNavyCard
import com.example.ui.theme.DarkNavyCardBorder
import com.example.ui.theme.DarkNavyCardElevated
import com.example.ui.theme.DangerRed
import com.example.ui.theme.EmolaBlue
import com.example.ui.theme.EscrowAmber
import com.example.ui.theme.MacumuOrange
import com.example.ui.theme.MpesaGreen
import com.example.ui.theme.TextDarkMuted
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.util.CurrencyUtils

@Composable
fun OrderTrackingScreen(
    orders: List<OrderEntity>,
    currency: String,
    onConfirmReception: (String) -> Unit,
    onOpenDispute: (orderCode: String, buyerName: String, sellerName: String, reason: String, desc: String) -> Unit
) {
    var disputeOrder by remember { mutableStateOf<OrderEntity?>(null) }
    var disputeReason by remember { mutableStateOf("Produto com defeito") }
    var disputeDesc by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavyBackground)
            .padding(horizontal = 14.dp),
        contentPadding = PaddingValues(top = 14.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column {
                Text(
                    text = "Acompanhamento de Encomendas",
                    color = TextWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
                Text(
                    text = "Rastreie envios e controle a libertação de pagamento por Custódia (Escrow)",
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }
        }

        item {
            EscrowGuaranteedBanner()
        }

        if (orders.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkNavyCard),
                    modifier = Modifier.fillMaxWidth().padding(top = 30.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(30.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalShipping,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Ainda não tem encomendas activas",
                            color = TextWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Quando fizer compras no marketplace, poderá acompanhar o progresso aqui.",
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        } else {
            items(orders) { order ->
                OrderTrackingCard(
                    order = order,
                    currency = currency,
                    onConfirmReception = { onConfirmReception(order.orderCode) },
                    onOpenDispute = { disputeOrder = order }
                )
            }
        }
    }

    // Dispute Modal Dialog
    if (disputeOrder != null) {
        AlertDialog(
            onDismissRequest = { disputeOrder = null },
            containerColor = DarkNavyCardElevated,
            title = {
                Text("Abrir Disputa: ${disputeOrder!!.orderCode}", color = TextWhite, fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    Text(
                        "Os fundos em custódia serão congelados enquanto a equipa de moderação do Macumu Global avalia o caso.",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = disputeReason,
                        onValueChange = { disputeReason = it },
                        label = { Text("Motivo da Reclamação") },
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
                        value = disputeDesc,
                        onValueChange = { disputeDesc = it },
                        label = { Text("Descreva detalhadamente o problema") },
                        minLines = 3,
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
                        onOpenDispute(
                            disputeOrder!!.orderCode,
                            disputeOrder!!.buyerName,
                            disputeOrder!!.sellerName,
                            disputeReason,
                            disputeDesc
                        )
                        disputeOrder = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DangerRed)
                ) {
                    Text("Submeter Disputa", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { disputeOrder = null }) {
                    Text("Cancelar", color = TextMuted)
                }
            }
        )
    }
}

@Composable
fun OrderTrackingCard(
    order: OrderEntity,
    currency: String,
    onConfirmReception: () -> Unit,
    onOpenDispute: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkNavyCard),
        border = BorderStroke(1.dp, DarkNavyCardBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: Code & Payment Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = order.orderCode,
                        color = TextWhite,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.5.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "• ${order.paymentMethod}",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }

                // Escrow Status Chip
                val (escrowText, escrowColor) = when (order.paymentStatus) {
                    "PAGO_RETIDO_ESCROW" -> "Retido em Custódia" to EscrowAmber
                    "LIBERTADO_VENDEDOR" -> "Fundos Libertados" to MpesaGreen
                    "REEMBOLSADO" -> "Reembolsado" to DangerRed
                    else -> "Pagamento na Entrega" to EmolaBlue
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = escrowColor.copy(alpha = 0.15f),
                    border = BorderStroke(0.5.dp, escrowColor)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (order.paymentStatus == "LIBERTADO_VENDEDOR") Icons.Default.CheckCircle else Icons.Default.Lock,
                            contentDescription = null,
                            tint = escrowColor,
                            modifier = Modifier.size(10.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = escrowText,
                            color = escrowColor,
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Items summary & Destination
            Text(
                text = order.itemsSummary,
                color = TextWhite,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp
            )
            Text(
                text = "Entrega: ${order.neighborhood}, ${order.cityOrDistrict} (${order.province})",
                color = TextMuted,
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Step Progress Bar
            OrderStatusTimeline(status = order.orderStatus)

            Spacer(modifier = Modifier.height(10.dp))

            // Total Price & Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Valor Total", color = TextMuted, fontSize = 10.sp)
                    Text(
                        text = CurrencyUtils.formatPrice(order.totalMzn, currency),
                        color = MacumuOrange,
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp
                    )
                }

                if (order.orderStatus != "CONCLUIDO" && order.orderStatus != "CANCELADO" && order.orderStatus != "EM_DISPUTA") {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        OutlinedButton(
                            onClick = onOpenDispute,
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, DangerRed.copy(alpha = 0.7f)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = DangerRed),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text("Disputa", fontSize = 11.sp)
                        }

                        Button(
                            onClick = onConfirmReception,
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MpesaGreen),
                            modifier = Modifier.height(36.dp).testTag("confirm_reception_${order.orderCode}")
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Confirmar Recepção", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                        }
                    }
                } else if (order.orderStatus == "CONCLUIDO") {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MpesaGreen.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "Compra Concluída com Sucesso",
                            color = MpesaGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OrderStatusTimeline(status: String) {
    val steps = listOf("Confirmado", "Em Preparação", "Em Trânsito", "Entregue", "Concluído")
    val currentIndex = when (status) {
        "PAGAMENTO_CONFIRMADO" -> 0
        "EM_PREPARACAO" -> 1
        "EM_TRANSITO" -> 2
        "ENTREGUE" -> 3
        "CONCLUIDO" -> 4
        else -> 1
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        steps.forEachIndexed { index, title ->
            val isDone = index <= currentIndex
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(if (isDone) MacumuOrange else Color(0xFF222C3E)),
                    contentAlignment = Alignment.Center
                ) {
                    if (isDone) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = title,
                    color = if (isDone) TextWhite else TextDarkMuted,
                    fontSize = 8.sp,
                    maxLines = 1
                )
            }
        }
    }
}
