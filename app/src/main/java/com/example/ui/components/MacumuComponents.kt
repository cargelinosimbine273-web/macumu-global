package com.example.ui.components

import android.app.Activity
import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.LocalMall
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.SyncAlt
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ProductEntity
import com.example.ui.AppRole
import com.example.ui.Screen
import com.example.ui.theme.DangerRed
import com.example.ui.theme.DarkNavyBackground
import com.example.ui.theme.DarkNavySurface
import com.example.ui.theme.DarkNavyCard
import com.example.ui.theme.DarkNavyCardBorder
import com.example.ui.theme.DarkNavyCardElevated
import com.example.ui.theme.EmolaBlue
import com.example.ui.theme.EscrowAmber
import com.example.ui.theme.MacumuOrange
import com.example.ui.theme.MpesaGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.ui.theme.VerifiedBadgeColor
import com.example.util.CurrencyUtils
import com.google.firebase.auth.FirebaseAuth

/**
 * Top role selector with 99.5% Online status indicator and
 * Developer-protected access. Visitors can only see public modes:
 * Comprar, Vender, Afiliados.
 */
@Composable
fun RoleSelectorHeader(
    currentRole: AppRole,
    onRoleSelected: (AppRole) -> Unit,
    currency: String,
    onCurrencyClick: () -> Unit,
    isOnline: Boolean = true,
    isDeveloperAuthenticated: Boolean = false,
    onDeveloperLockClick: () -> Unit = {}
) {
    Surface(
        color = DarkNavyCardElevated,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Logo Oficial MCG com trigger secreto para o desenvolvedor (5 toques)
                    var logoClickCount by remember { mutableStateOf(0) }
                    Surface(
                        shape = RoundedCornerShape(7.dp),
                        color = Color.Transparent,
                        modifier = Modifier
                            .clip(RoundedCornerShape(7.dp))
                            .background(Brush.linearGradient(listOf(MacumuOrange, Color(0xFFFF944D))))
                            .clickable {
                                logoClickCount++
                                if (logoClickCount >= 5) {
                                    logoClickCount = 0
                                    onDeveloperLockClick()
                                }
                            }
                            .testTag("mcg_brand_logo")
                    ) {
                        Text(
                            text = "MCG",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp,
                            letterSpacing = 0.5.sp,
                            modifier = Modifier.padding(horizontal = 5.5.dp, vertical = 3.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "MACUMU GLOBAL",
                                color = TextWhite,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            // 99.5% Online indicator badge
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFF092817),
                                border = BorderStroke(0.5.dp, MpesaGreen.copy(alpha = 0.7f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(5.dp)
                                            .clip(CircleShape)
                                            .background(MpesaGreen)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "99.5% Online",
                                        color = MpesaGreen,
                                        fontSize = 8.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                        Text(
                            text = "Do formal ao informal, tudo num só clique.",
                            color = MacumuOrange,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Currency switcher button
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = DarkNavyCard,
                        border = BorderStroke(0.5.dp, DarkNavyCardBorder),
                        modifier = Modifier
                            .clickable { onCurrencyClick() }
                            .testTag("currency_selector_btn")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CurrencyExchange,
                                contentDescription = "Moeda",
                                tint = MacumuOrange,
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(2.5.dp))
                            Text(
                                text = currency,
                                color = TextWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        }
                    }

                    // Developer button is completely hidden from visitors! Only visible when authenticated.
                    if (isDeveloperAuthenticated) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MacumuOrange.copy(alpha = 0.2f),
                            border = BorderStroke(0.5.dp, MacumuOrange),
                            modifier = Modifier
                                .clickable { onDeveloperLockClick() }
                                .testTag("developer_access_lock_btn")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AdminPanelSettings,
                                    contentDescription = "Consola Dev Ativa",
                                    tint = MacumuOrange,
                                    modifier = Modifier.size(11.dp)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = "Dev Activo",
                                    color = MacumuOrange,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(2.5.dp))

            // Barra de Sessão do Utilizador (Email e Botão de Terminar Sessão com reinício da Activity)
            val auth = FirebaseAuth.getInstance()
            val firebaseUser = auth.currentUser
            val context = LocalContext.current
            val activity = context as? Activity

            Surface(
                shape = RoundedCornerShape(7.dp),
                color = Color(0xFF101B2B),
                border = BorderStroke(0.5.dp, DarkNavyCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 7.dp, vertical = 3.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = null,
                            tint = if (firebaseUser != null) MacumuOrange else TextMuted,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (firebaseUser != null) {
                                firebaseUser.email ?: "Utilizador Conectado"
                            } else {
                                "Modo Visitante"
                            },
                            color = TextWhite,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    if (firebaseUser != null) {
                        Surface(
                            shape = RoundedCornerShape(5.dp),
                            color = DangerRed.copy(alpha = 0.15f),
                            border = BorderStroke(0.5.dp, DangerRed),
                            modifier = Modifier
                                .clickable {
                                    auth.signOut()
                                    val intent = Intent(context, com.example.MainActivity::class.java).apply {
                                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    }
                                    context.startActivity(intent)
                                    activity?.finish()
                                }
                                .testTag("btn_terminar_sessao_header")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ExitToApp,
                                    contentDescription = "Terminar Sessão",
                                    tint = DangerRed,
                                    modifier = Modifier.size(11.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "Terminar Sessão",
                                    color = DangerRed,
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    } else {
                        Surface(
                            shape = RoundedCornerShape(5.dp),
                            color = MacumuOrange,
                            modifier = Modifier
                                .clickable {
                                    val intent = Intent(context, com.example.MainActivity::class.java).apply {
                                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    }
                                    context.startActivity(intent)
                                    activity?.finish()
                                }
                                .testTag("btn_iniciar_sessao_header")
                        ) {
                            Text(
                                text = "Entrar",
                                color = Color.White,
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(3.dp))

            // Role Pills Row: Visitors only see public actions (Comprar, Vender, Afiliados).
            // Gestão/Admin is only shown if the developer has unlocked with PIN.
            val visibleRoles = if (isDeveloperAuthenticated) {
                AppRole.entries.toList()
            } else {
                listOf(AppRole.COMPRADOR, AppRole.VENDEDOR, AppRole.AFILIADO)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                visibleRoles.forEach { role ->
                    val isSelected = role == currentRole
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) MacumuOrange else DarkNavyCard,
                        border = BorderStroke(
                            0.5.dp,
                            if (isSelected) MacumuOrange else DarkNavyCardBorder
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { onRoleSelected(role) }
                            .testTag("role_tab_${role.name.lowercase()}")
                    ) {
                        Box(
                            modifier = Modifier.padding(vertical = 3.5.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = when (role) {
                                    AppRole.COMPRADOR -> "Comprar"
                                    AppRole.VENDEDOR -> "Vender"
                                    AppRole.AFILIADO -> "Afiliados"
                                    AppRole.ADMIN -> "Gestão Dev 🛠️"
                                },
                                color = if (isSelected) Color.White else TextMuted,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Escrow Custody Assurance Badge explaining fund retention (Compact for low-end)
 */
@Composable
fun EscrowGuaranteedBanner(
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2838)),
        border = BorderStroke(0.5.dp, EscrowAmber.copy(alpha = 0.4f)),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(EscrowAmber.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = "Garantia Escrow",
                    tint = EscrowAmber,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Garantia de Pagamento Retido (Escrow)",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
                Text(
                    text = "O pagamento só é libertado ao vendedor após confirmar a recepção.",
                    color = TextMuted,
                    fontSize = 9.sp,
                    lineHeight = 12.sp
                )
            }
        }
    }
}

/**
 * Product Card with formal vs informal seller distinction and location
 */
@Composable
fun MarketplaceProductCard(
    product: ProductEntity,
    currency: String,
    onProductClick: () -> Unit,
    onAddToCart: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = DarkNavyCard),
        border = BorderStroke(0.5.dp, DarkNavyCardBorder),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onProductClick() }
            .testTag("product_card_${product.id}")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Product visual header with badges
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(82.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0xFF222D42),
                                Color(0xFF171E2D)
                            )
                        )
                    )
            ) {
                // Category & Informal/Formal Badge
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Seller Type Badge
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = if (product.sellerType == "INFORMAL") Color(0xFF5A2A04) else Color(0xFF0F3254),
                        border = BorderStroke(
                            0.5.dp,
                            if (product.sellerType == "INFORMAL") MacumuOrange else VerifiedBadgeColor
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (product.sellerType == "INFORMAL") Icons.Default.Storefront else Icons.Default.Verified,
                                contentDescription = null,
                                tint = if (product.sellerType == "INFORMAL") MacumuOrange else VerifiedBadgeColor,
                                modifier = Modifier.size(9.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = if (product.sellerType == "INFORMAL") "Bazar" else "Loja",
                                color = TextWhite,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Scope badge (Nacional vs Global)
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = if (product.isNational) Color(0xFF0A3A24) else Color(0xFF332050),
                        border = BorderStroke(0.5.dp, if (product.isNational) MpesaGreen else Color(0xFFA855F7))
                    ) {
                        Text(
                            text = if (product.isNational) "MZ 🇲🇿" else "Global 🌍",
                            color = TextWhite,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                }

                // Decorative Center icon
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF2D3B55).copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when {
                            product.category.contains("Roupas", true) -> Icons.Default.LocalMall
                            product.category.contains("Electrónica", true) -> Icons.Default.ShoppingBag
                            else -> Icons.Default.Storefront
                        },
                        contentDescription = null,
                        tint = MacumuOrange,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Info details
            Column(modifier = Modifier.padding(6.dp)) {
                Text(
                    text = product.title,
                    color = TextWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 14.sp
                )

                Spacer(modifier = Modifier.height(2.dp))

                // Seller name & location
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(10.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = if (product.marketName.isNotBlank()) "${product.marketName} • ${product.sellerLocation}" else product.sellerLocation,
                        color = TextMuted,
                        fontSize = 8.5.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Price display with discount
                val effectivePrice = product.promoPriceMzn ?: product.priceMzn
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = CurrencyUtils.formatPrice(effectivePrice, currency),
                            color = MacumuOrange,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 11.5.sp
                        )
                        if (product.promoPriceMzn != null) {
                            Text(
                                text = CurrencyUtils.formatPrice(product.priceMzn, currency),
                                color = TextMuted,
                                fontSize = 8.sp,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                                )
                            )
                        }
                    }

                    // Quick Add to cart button
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MacumuOrange,
                        modifier = Modifier
                            .clickable { onAddToCart() }
                            .testTag("add_to_cart_btn_${product.id}")
                    ) {
                        Box(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "Adicionar",
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Standard Bottom Navigation Bar for Buyer Flow (Compact for small screens)
 */
@Composable
fun MacumuBottomBar(
    currentScreen: Screen,
    cartCount: Int,
    onNavigate: (Screen) -> Unit
) {
    NavigationBar(
        containerColor = DarkNavySurface,
        tonalElevation = 2.dp
    ) {
        NavigationBarItem(
            selected = currentScreen is Screen.Home,
            onClick = { onNavigate(Screen.Home) },
            icon = { Icon(Icons.Default.Storefront, contentDescription = "Início", modifier = Modifier.size(20.dp)) },
            label = { Text("Início", fontSize = 9.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MacumuOrange,
                selectedTextColor = MacumuOrange,
                indicatorColor = Color(0xFF351C09),
                unselectedIconColor = TextMuted,
                unselectedTextColor = TextMuted
            ),
            modifier = Modifier.testTag("nav_home")
        )

        NavigationBarItem(
            selected = currentScreen is Screen.OrderTracking,
            onClick = { onNavigate(Screen.OrderTracking("")) },
            icon = { Icon(Icons.Default.LocalMall, contentDescription = "Encomendas") },
            label = { Text("Encomendas", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MacumuOrange,
                selectedTextColor = MacumuOrange,
                indicatorColor = Color(0xFF351C09),
                unselectedIconColor = TextMuted,
                unselectedTextColor = TextMuted
            ),
            modifier = Modifier.testTag("nav_orders")
        )

        NavigationBarItem(
            selected = currentScreen is Screen.Cart || currentScreen is Screen.Checkout,
            onClick = { onNavigate(Screen.Cart) },
            icon = {
                if (cartCount > 0) {
                    BadgedBox(
                        badge = {
                            Badge(containerColor = MacumuOrange) {
                                Text(
                                    text = cartCount.toString(),
                                    color = Color.White,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    ) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = "Carrinho")
                    }
                } else {
                    Icon(Icons.Default.ShoppingCart, contentDescription = "Carrinho")
                }
            },
            label = { Text("Carrinho", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MacumuOrange,
                selectedTextColor = MacumuOrange,
                indicatorColor = Color(0xFF351C09),
                unselectedIconColor = TextMuted,
                unselectedTextColor = TextMuted
            ),
            modifier = Modifier.testTag("nav_cart")
        )

        NavigationBarItem(
            selected = currentScreen is Screen.Account,
            onClick = { onNavigate(Screen.Account) },
            icon = { Icon(Icons.Default.AccountCircle, contentDescription = "Conta") },
            label = { Text("Conta", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MacumuOrange,
                selectedTextColor = MacumuOrange,
                indicatorColor = Color(0xFF351C09),
                unselectedIconColor = TextMuted,
                unselectedTextColor = TextMuted
            ),
            modifier = Modifier.testTag("nav_account")
        )
    }
}
