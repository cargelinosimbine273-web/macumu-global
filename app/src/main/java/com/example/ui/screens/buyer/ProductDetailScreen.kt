package com.example.ui.screens.buyer

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Brush
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
import com.example.ui.components.EscrowGuaranteedBanner
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
import com.example.ui.theme.VerifiedBadgeColor
import com.example.util.CurrencyUtils

@Composable
fun ProductDetailScreen(
    product: ProductEntity,
    currency: String,
    onBack: () -> Unit,
    onAddToCart: (ProductEntity) -> Unit,
    onBuyNow: (ProductEntity) -> Unit,
    onGenerateAffiliateLink: (ProductEntity, (AffiliateLinkEntity) -> Unit) -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var generatedLink by remember { mutableStateOf<AffiliateLinkEntity?>(null) }
    var showAffiliateSuccess by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavyBackground),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Top App Bar with back button
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("detail_back_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Voltar",
                        tint = TextWhite
                    )
                }

                Text(
                    text = "Detalhes do Produto",
                    color = TextWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                IconButton(
                    onClick = {
                        val shareIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "Veja '${product.title}' no Macumu Global (${CurrencyUtils.formatPrice(product.promoPriceMzn ?: product.priceMzn, currency)}): https://macumu.co.mz/p/${product.id}"
                            )
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Partilhar Produto"))
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Partilhar",
                        tint = MacumuOrange
                    )
                }
            }
        }

        // Product Visual Showcase (Compact for low-end / small screens)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF1B2436), Color(0xFF101624))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Storefront,
                    contentDescription = null,
                    tint = MacumuOrange.copy(alpha = 0.8f),
                    modifier = Modifier.size(64.dp)
                )

                // Badges in Image
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopStart)
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (product.sellerType == "INFORMAL") Color(0xFF5A2A04) else Color(0xFF0F3254),
                        border = BorderStroke(
                            1.dp,
                            if (product.sellerType == "INFORMAL") MacumuOrange else VerifiedBadgeColor
                        )
                    ) {
                        Text(
                            text = if (product.sellerType == "INFORMAL") "Bazar Informal" else "Loja Registada",
                            color = TextWhite,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (product.isNational) Color(0xFF0A3A24) else Color(0xFF332050)
                    ) {
                        Text(
                            text = if (product.isNational) "Moçambique 🇲🇿" else "Internacional 🌍",
                            color = TextWhite,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }
            }
        }

        // Product Info Section
        item {
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                // Category & Rating
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = product.category,
                        color = MacumuOrange,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.5.sp
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = EscrowAmber,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "${product.rating} (${product.reviewCount})",
                            color = TextWhite,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Title
                Text(
                    text = product.title,
                    color = TextWhite,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    lineHeight = 21.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Price Section
                val effectivePrice = product.promoPriceMzn ?: product.priceMzn
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = CurrencyUtils.formatPrice(effectivePrice, currency),
                        color = MacumuOrange,
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp
                    )
                    if (product.promoPriceMzn != null) {
                        Text(
                            text = CurrencyUtils.formatPrice(product.priceMzn, currency),
                            color = TextMuted,
                            fontSize = 13.sp,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Escrow Guarantee Banner
                EscrowGuaranteedBanner()

                Spacer(modifier = Modifier.height(14.dp))

                // Seller Card
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkNavyCard),
                    border = BorderStroke(1.dp, DarkNavyCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(DarkNavyCardElevated),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (product.sellerType == "INFORMAL") Icons.Default.Storefront else Icons.Default.Verified,
                                contentDescription = null,
                                tint = if (product.sellerType == "INFORMAL") MacumuOrange else VerifiedBadgeColor,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = product.sellerName,
                                color = TextWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.5.sp
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = TextMuted,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = if (product.marketName.isNotBlank()) "${product.marketName} • ${product.sellerLocation}" else product.sellerLocation,
                                    color = TextMuted,
                                    fontSize = 11.5.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Delivery & Stock Info
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
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalShipping,
                                contentDescription = null,
                                tint = EmolaBlue,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text("Entrega", color = TextMuted, fontSize = 10.sp)
                                Text(product.deliveryEstimate, color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                            }
                        }
                    }

                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkNavyCard),
                        border = BorderStroke(1.dp, DarkNavyCardBorder),
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = MpesaGreen,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text("Disponibilidade", color = TextMuted, fontSize = 10.sp)
                                Text("${product.stock} em stock", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Description
                Text(
                    text = "Descrição do Produto",
                    color = TextWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = product.description,
                    color = TextMuted,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Affiliate Link Generation Block
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF142438)),
                    border = BorderStroke(1.dp, EmolaBlue.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = null,
                                tint = EmolaBlue,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Programa de Afiliados Macumu",
                                color = TextWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                        Text(
                            text = "Partilhe este produto no WhatsApp e ganhe 8% de comissão por cada venda confirmada.",
                            color = TextMuted,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )

                        if (generatedLink != null) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = DarkNavyCard,
                                border = BorderStroke(1.dp, EmolaBlue),
                                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = generatedLink!!.uniqueUrl,
                                        color = EmolaBlue,
                                        fontSize = 11.sp,
                                        maxLines = 1,
                                        modifier = Modifier.weight(1f)
                                    )
                                    IconButton(
                                        onClick = {
                                            clipboardManager.setText(AnnotatedString(generatedLink!!.uniqueUrl))
                                            showAffiliateSuccess = true
                                        },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ContentCopy,
                                            contentDescription = "Copiar",
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                            if (showAffiliateSuccess) {
                                Text(
                                    text = "Link de afiliado copiado para a área de transferência!",
                                    color = MpesaGreen,
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        } else {
                            OutlinedButton(
                                onClick = {
                                    onGenerateAffiliateLink(product) { link ->
                                        generatedLink = link
                                    }
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = EmolaBlue),
                                border = BorderStroke(1.dp, EmolaBlue),
                                modifier = Modifier.fillMaxWidth().testTag("generate_affiliate_link_btn")
                            ) {
                                Text("Gerar Meu Link de Afiliado Exclusivo", fontSize = 12.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action Buttons: Add to cart & Buy now
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = { onAddToCart(product) },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MacumuOrange),
                        border = BorderStroke(1.5.dp, MacumuOrange),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("detail_add_cart_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Adicionar", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    Button(
                        onClick = { onBuyNow(product) },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MacumuOrange),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("detail_buy_now_btn")
                    ) {
                        Text("Comprar Agora", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}
