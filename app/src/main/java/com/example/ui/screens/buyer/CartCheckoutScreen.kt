package com.example.ui.screens.buyer

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import com.example.data.local.CartItemEntity
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
import com.example.util.CurrencyUtils

val MOZAMBIQUE_PROVINCES = listOf(
    "Maputo Cidade",
    "Maputo Província",
    "Gaza",
    "Inhambane",
    "Sofala",
    "Manica",
    "Tete",
    "Zambézia",
    "Nampula",
    "Cabo Delgado",
    "Niassa"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartCheckoutScreen(
    cartItems: List<CartItemEntity>,
    currency: String,
    onBack: () -> Unit,
    onUpdateQuantity: (Long, Int) -> Unit,
    onRemoveItem: (Long) -> Unit,
    onPlaceOrder: (
        name: String,
        phone: String,
        province: String,
        city: String,
        neighborhood: String,
        reference: String,
        paymentMethod: String,
        affiliateCode: String?,
        callback: (String) -> Unit
    ) -> Unit,
    onOrderPlaced: (String) -> Unit
) {
    var isCheckingOut by remember { mutableStateOf(false) }

    // Checkout Form States
    var buyerName by remember { mutableStateOf("Armando Chissano") }
    var buyerPhone by remember { mutableStateOf("+258 84 123 4567") }
    var selectedProvince by remember { mutableStateOf("Maputo Cidade") }
    var provinceExpanded by remember { mutableStateOf(false) }
    var cityOrDistrict by remember { mutableStateOf("KaMpfumu") }
    var neighborhood by remember { mutableStateOf("Polana Cimento A") }
    var referencePoint by remember { mutableStateOf("Perto do Hospital Central") }
    var affiliateCode by remember { mutableStateOf("AFIL-MOZ-88") }
    var selectedPaymentMethod by remember { mutableStateOf("M-Pesa") }

    val subtotal = cartItems.sumOf { it.productPriceMzn * it.quantity }
    val deliveryFee = if (selectedProvince.contains("Maputo", ignoreCase = true)) 200.0 else 450.0
    val total = subtotal + deliveryFee

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavyBackground)
            .padding(horizontal = 14.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack, modifier = Modifier.testTag("cart_back_btn")) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Voltar",
                        tint = TextWhite
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isCheckingOut) "Finalizar Compra Segura" else "Carrinho de Compras (${cartItems.size})",
                    color = TextWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            }
        }

        if (cartItems.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkNavyCard),
                    modifier = Modifier.fillMaxWidth().padding(top = 40.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "O seu carrinho está vazio",
                            color = TextWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Explore o marketplace e adicione produtos formais ou informais.",
                            color = TextMuted,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onBack,
                            colors = ButtonDefaults.buttonColors(containerColor = MacumuOrange),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Explorar Produtos", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else if (!isCheckingOut) {
            // Cart Items List
            items(cartItems) { item ->
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
                                .size(50.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(DarkNavyCardElevated),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = null,
                                tint = MacumuOrange,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.productTitle,
                                color = TextWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                maxLines = 1
                            )
                            Text(
                                text = "Vendedor: ${item.sellerName} (${item.sellerType})",
                                color = TextMuted,
                                fontSize = 10.5.sp
                            )
                            Text(
                                text = CurrencyUtils.formatPrice(item.productPriceMzn, currency),
                                color = MacumuOrange,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 13.sp
                            )
                        }

                        // Quantity control buttons
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { onUpdateQuantity(item.id, item.quantity - 1) },
                                modifier = Modifier.size(30.dp)
                            ) {
                                Icon(
                                    imageVector = if (item.quantity == 1) Icons.Default.Delete else Icons.Default.Remove,
                                    contentDescription = "Diminuir",
                                    tint = if (item.quantity == 1) Color(0xFFEF4444) else TextWhite,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            Text(
                                text = "${item.quantity}",
                                color = TextWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(horizontal = 6.dp)
                            )

                            IconButton(
                                onClick = { onUpdateQuantity(item.id, item.quantity + 1) },
                                modifier = Modifier.size(30.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Aumentar",
                                    tint = TextWhite,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Summary & Checkout Button
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkNavyCardElevated),
                    border = BorderStroke(1.dp, DarkNavyCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Subtotal", color = TextMuted, fontSize = 13.sp)
                            Text(
                                text = CurrencyUtils.formatPrice(subtotal, currency),
                                color = TextWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Estimativa de Frete", color = TextMuted, fontSize = 13.sp)
                            Text(
                                text = CurrencyUtils.formatPrice(deliveryFee, currency),
                                color = TextWhite,
                                fontSize = 13.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(DarkNavyCardBorder)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text(
                                text = CurrencyUtils.formatPrice(total, currency),
                                color = MacumuOrange,
                                fontWeight = FontWeight.Black,
                                fontSize = 17.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = { isCheckingOut = true },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MacumuOrange),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("proceed_to_checkout_btn")
                        ) {
                            Text(
                                text = "Avançar para o Pagamento",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        } else {
            // CHECKOUT FORM
            item {
                EscrowGuaranteedBanner()
            }

            // Customer Details
            item {
                Text(
                    text = "1. Dados do Comprador",
                    color = TextWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = buyerName,
                    onValueChange = { buyerName = it },
                    label = { Text("Nome Completo") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedBorderColor = MacumuOrange,
                        unfocusedBorderColor = DarkNavyCardBorder
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("checkout_name_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = buyerPhone,
                    onValueChange = { buyerPhone = it },
                    label = { Text("Número de Telemóvel (M-Pesa / e-Mola)") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedBorderColor = MacumuOrange,
                        unfocusedBorderColor = DarkNavyCardBorder
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("checkout_phone_input")
                )
            }

            // Delivery Details
            item {
                Text(
                    text = "2. Endereço de Entrega (Moçambique)",
                    color = TextWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(6.dp))

                // Province Dropdown
                ExposedDropdownMenuBox(
                    expanded = provinceExpanded,
                    onExpandedChange = { provinceExpanded = !provinceExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedProvince,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Província") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = provinceExpanded) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedBorderColor = MacumuOrange,
                            unfocusedBorderColor = DarkNavyCardBorder
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(
                        expanded = provinceExpanded,
                        onDismissRequest = { provinceExpanded = false },
                        modifier = Modifier.background(DarkNavyCard)
                    ) {
                        MOZAMBIQUE_PROVINCES.forEach { province ->
                            DropdownMenuItem(
                                text = { Text(province, color = TextWhite) },
                                onClick = {
                                    selectedProvince = province
                                    provinceExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = cityOrDistrict,
                        onValueChange = { cityOrDistrict = it },
                        label = { Text("Cidade / Distrito") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedBorderColor = MacumuOrange,
                            unfocusedBorderColor = DarkNavyCardBorder
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = neighborhood,
                        onValueChange = { neighborhood = it },
                        label = { Text("Bairro") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedBorderColor = MacumuOrange,
                            unfocusedBorderColor = DarkNavyCardBorder
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = referencePoint,
                    onValueChange = { referencePoint = it },
                    label = { Text("Ponto de Referência") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedBorderColor = MacumuOrange,
                        unfocusedBorderColor = DarkNavyCardBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Payment Methods
            item {
                Text(
                    text = "3. Método de Pagamento",
                    color = TextWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(6.dp))

                val paymentMethods = listOf(
                    Triple("M-Pesa", "Vodacom M-Pesa (Moçambique)", MpesaGreen),
                    Triple("e-Mola", "Movitel e-Mola (Moçambique)", EmolaBlue),
                    Triple("Pagamento na Entrega", "Pagar no acto de recepção", EscrowAmber),
                    Triple("Cartão Bancário", "Visa, Mastercard ou Millennium BIM", Color(0xFFA855F7))
                )

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    paymentMethods.forEach { (method, desc, accentColor) ->
                        val isSelected = selectedPaymentMethod == method
                        Card(
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) Color(0xFF1E283C) else DarkNavyCard
                            ),
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) accentColor else DarkNavyCardBorder
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedPaymentMethod = method }
                                .testTag("payment_opt_${method.lowercase().replace(" ", "_")}")
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { selectedPaymentMethod = method },
                                    colors = RadioButtonDefaults.colors(selectedColor = accentColor)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(
                                        text = method,
                                        color = TextWhite,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                    Text(text = desc, color = TextMuted, fontSize = 10.5.sp)
                                }
                            }
                        }
                    }
                }
            }

            // Affiliate Code (Optional)
            item {
                Text(
                    text = "Código de Afiliado (Opcional)",
                    color = TextMuted,
                    fontSize = 12.sp
                )
                OutlinedTextField(
                    value = affiliateCode,
                    onValueChange = { affiliateCode = it },
                    placeholder = { Text("Ex: AFIL-MOZ-88") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedBorderColor = EmolaBlue,
                        unfocusedBorderColor = DarkNavyCardBorder
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("affiliate_code_input")
                )
            }

            // Final Order Confirmation
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkNavyCardElevated),
                    border = BorderStroke(1.dp, DarkNavyCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Subtotal", color = TextMuted, fontSize = 13.sp)
                            Text(CurrencyUtils.formatPrice(subtotal, currency), color = TextWhite, fontSize = 13.sp)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Frete ($selectedProvince)", color = TextMuted, fontSize = 13.sp)
                            Text(CurrencyUtils.formatPrice(deliveryFee, currency), color = TextWhite, fontSize = 13.sp)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total a Pagar", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text(
                                CurrencyUtils.formatPrice(total, currency),
                                color = MacumuOrange,
                                fontWeight = FontWeight.Black,
                                fontSize = 17.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                onPlaceOrder(
                                    buyerName,
                                    buyerPhone,
                                    selectedProvince,
                                    cityOrDistrict,
                                    neighborhood,
                                    referencePoint,
                                    selectedPaymentMethod,
                                    affiliateCode.ifBlank { null }
                                ) { orderCode ->
                                    onOrderPlaced(orderCode)
                                }
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MacumuOrange),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("confirm_order_btn")
                        ) {
                            Icon(Icons.Default.Security, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Confirmar Encomenda com Garantia",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.5.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
