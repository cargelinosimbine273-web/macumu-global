package com.example.ui.screens.affiliate

import android.content.Intent
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
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Mouse
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.AffiliateLinkEntity
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

@Composable
fun AffiliateScreen(
    affiliateLinks: List<AffiliateLinkEntity>,
    products: List<ProductEntity>,
    financialStats: FinancialStats,
    currency: String,
    onGenerateLinkForProduct: (ProductEntity) -> Unit,
    onRecordClick: (Long) -> Unit,
    onRequestWithdrawal: (type: String, name: String, method: String, phone: String, amount: Double) -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var selectedTab by remember { mutableStateOf("LINKS") }
    var showWithdrawalDialog by remember { mutableStateOf(false) }
    var withdrawalMethod by remember { mutableStateOf("M-Pesa") }
    var withdrawalPhone by remember { mutableStateOf("+258 84 555 7788") }
    var withdrawalAmount by remember { mutableStateOf("") }

    val totalClicks = affiliateLinks.sumOf { it.clicks }
    val totalConversions = affiliateLinks.sumOf { it.conversions }
    val totalEarned = affiliateLinks.sumOf { it.totalEarnedMzn }

    val approvedProducts = products.filter { it.isApproved }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavyBackground)
            .padding(horizontal = 14.dp),
        contentPadding = PaddingValues(top = 14.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Affiliate Program Header
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF10283C)),
                border = BorderStroke(1.dp, EmolaBlue.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(EmolaBlue.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.TrendingUp, contentDescription = null, tint = EmolaBlue, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Espaço do Afiliado Macumu",
                                color = TextWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "Histórico, Registos & Comissões de Venda",
                                color = EmolaBlue,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Divulgue no WhatsApp, TikTok e redes sociais. Ganhe de 8% a 15% de comissão garantida em cada venda aprovada com levantamento para M-Pesa ou e-Mola.",
                        color = TextMuted,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }
            }
        }

        // Subtabs for Affiliates: LINKS (Histórico), CATALOGO (Produtos Aprovados), CARTEIRA
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(
                    "LINKS" to "Meus Links (${affiliateLinks.size})",
                    "CATALOGO" to "Produtos Aprovados (${approvedProducts.size})",
                    "CARTEIRA" to "Carteira & Ganhos"
                ).forEach { (code, label) ->
                    val isSelected = selectedTab == code
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) MacumuOrange else DarkNavyCard,
                        border = BorderStroke(1.dp, if (isSelected) MacumuOrange else DarkNavyCardBorder),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedTab = code }
                            .testTag("affiliate_tab_${code.lowercase()}")
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
            "LINKS" -> {
                // Quick metrics summary
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Card(
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkNavyCard),
                            border = BorderStroke(1.dp, DarkNavyCardBorder),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Mouse, contentDescription = null, tint = MacumuOrange, modifier = Modifier.size(13.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Cliques", color = TextMuted, fontSize = 10.5.sp)
                                }
                                Text("$totalClicks", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }
                        }

                        Card(
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkNavyCard),
                            border = BorderStroke(1.dp, DarkNavyCardBorder),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.ShoppingBag, contentDescription = null, tint = MpesaGreen, modifier = Modifier.size(13.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Vendas", color = TextMuted, fontSize = 10.5.sp)
                                }
                                Text("$totalConversions", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }
                        }

                        Card(
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkNavyCard),
                            border = BorderStroke(1.dp, DarkNavyCardBorder),
                            modifier = Modifier.weight(1.2f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Payments, contentDescription = null, tint = EscrowAmber, modifier = Modifier.size(13.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Ganhos", color = TextMuted, fontSize = 10.5.sp)
                                }
                                Text(
                                    text = CurrencyUtils.formatPrice(totalEarned, currency),
                                    color = MacumuOrange,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }

                if (affiliateLinks.isEmpty()) {
                    item {
                        Card(
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkNavyCard),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Ainda não gerou nenhum link de divulgação.",
                                    color = TextWhite,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Vá à aba 'Produtos Aprovados' e clique em 'Gerar Link' para partilhar no WhatsApp.",
                                    color = TextMuted,
                                    fontSize = 11.5.sp
                                )
                            }
                        }
                    }
                } else {
                    items(affiliateLinks) { link ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkNavyCard),
                            border = BorderStroke(1.dp, DarkNavyCardBorder),
                            modifier = Modifier.fillMaxWidth().testTag("affiliate_link_${link.id}")
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = link.productTitle,
                                        color = TextWhite,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        maxLines = 1,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = MpesaGreen.copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            text = "${link.commissionPercent}% comissão",
                                            color = MpesaGreen,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = link.uniqueUrl,
                                    color = EmolaBlue,
                                    fontSize = 11.sp
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${link.clicks} cliques • ${link.conversions} vendas • Ganho: ${CurrencyUtils.formatPrice(link.totalEarnedMzn, currency)}",
                                        color = TextMuted,
                                        fontSize = 10.5.sp
                                    )

                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        IconButton(
                                            onClick = {
                                                clipboardManager.setText(AnnotatedString(link.uniqueUrl))
                                                onRecordClick(link.id)
                                            },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(Icons.Default.ContentCopy, contentDescription = "Copiar", tint = TextWhite, modifier = Modifier.size(16.dp))
                                        }

                                        Button(
                                            onClick = {
                                                onRecordClick(link.id)
                                                val shareIntent = Intent().apply {
                                                    action = Intent.ACTION_SEND
                                                    putExtra(
                                                        Intent.EXTRA_TEXT,
                                                        "🔥 Grande oportunidade no Macumu Global! Compre '${link.productTitle}' com entrega e pagamento seguro: ${link.uniqueUrl}"
                                                    )
                                                    type = "text/plain"
                                                }
                                                context.startActivity(Intent.createChooser(shareIntent, "Partilhar no WhatsApp"))
                                            },
                                            shape = RoundedCornerShape(8.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = MpesaGreen),
                                            modifier = Modifier.height(32.dp)
                                        ) {
                                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(12.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("WhatsApp", fontSize = 11.sp, color = Color.White)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            "CATALOGO" -> {
                item {
                    Text(
                        text = "Produtos Validados pela Comissão para Divulgação",
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.5.sp
                    )
                }

                if (approvedProducts.isEmpty()) {
                    item {
                        Card(
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkNavyCard),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Nenhum produto aprovado disponível no momento. A comissão de análise está a rever os novos registos de produtos.",
                                color = TextMuted,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                } else {
                    items(approvedProducts) { product ->
                        Card(
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkNavyCardElevated),
                            border = BorderStroke(1.dp, DarkNavyCardBorder),
                            modifier = Modifier.fillMaxWidth().testTag("affiliate_product_${product.id}")
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(product.title, color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 12.5.sp, maxLines = 1)
                                    Text(
                                        text = "Vendedor: ${product.sellerName} • ${product.sellerLocation}",
                                        color = TextMuted,
                                        fontSize = 10.5.sp
                                    )
                                    Text(
                                        text = "Preço: ${CurrencyUtils.formatPrice(product.promoPriceMzn ?: product.priceMzn, currency)} (Comissão estimada: 8% a 15%)",
                                        color = MacumuOrange,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                                OutlinedButton(
                                    onClick = { onGenerateLinkForProduct(product) },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = EmolaBlue),
                                    border = BorderStroke(1.dp, EmolaBlue),
                                    modifier = Modifier.height(34.dp).testTag("quick_affiliate_btn_${product.id}")
                                ) {
                                    Text("Gerar Link", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }

            "CARTEIRA" -> {
                item {
                    Text(
                        text = "Carteira de Comissões do Afiliado",
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F2B1C)),
                        border = BorderStroke(1.dp, MpesaGreen.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Payments, contentDescription = null, tint = MpesaGreen, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Saldo Total de Comissões", color = MpesaGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = CurrencyUtils.formatPrice(financialStats.affiliateBalanceMzn + totalEarned, currency),
                                color = TextWhite,
                                fontWeight = FontWeight.Black,
                                fontSize = 20.sp
                            )
                            Text("Disponível para levantamento imediato via M-Pesa ou e-Mola", color = TextMuted, fontSize = 10.5.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = { showWithdrawalDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = MpesaGreen),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp)
                            .testTag("request_affiliate_withdrawal_btn")
                    ) {
                        Icon(Icons.Default.ArrowDownward, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Levantar Comissões para M-Pesa / e-Mola", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }
    }

    // Affiliate Withdrawal Modal
    if (showWithdrawalDialog) {
        AlertDialog(
            onDismissRequest = { showWithdrawalDialog = false },
            containerColor = DarkNavyCardElevated,
            title = {
                Text("Levantamento de Comissões de Afiliado", color = TextWhite, fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    Text(
                        "Saldo disponível: ${CurrencyUtils.formatPrice(financialStats.affiliateBalanceMzn + totalEarned, currency)}",
                        color = MpesaGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = withdrawalPhone,
                        onValueChange = { withdrawalPhone = it },
                        label = { Text("Número de Telemóvel M-Pesa / e-Mola") },
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
                        placeholder = { Text("Ex: 500") },
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
                        val amount = withdrawalAmount.toDoubleOrNull() ?: 300.0
                        onRequestWithdrawal("AFILIADO", "Afiliado AFIL-MOZ-88", withdrawalMethod, withdrawalPhone, amount)
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
