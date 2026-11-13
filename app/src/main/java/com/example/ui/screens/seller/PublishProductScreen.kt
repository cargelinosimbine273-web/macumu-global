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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Verified
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.buyer.MARKETPLACE_CATEGORIES
import com.example.ui.theme.DarkNavyBackground
import com.example.ui.theme.DarkNavyCard
import com.example.ui.theme.DarkNavyCardBorder
import com.example.ui.theme.DarkNavyCardElevated
import com.example.ui.theme.MacumuOrange
import com.example.ui.theme.MpesaGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.ui.theme.VerifiedBadgeColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublishProductScreen(
    onBack: () -> Unit,
    onPublish: (
        title: String,
        description: String,
        category: String,
        priceMzn: Double,
        promoPriceMzn: Double?,
        stock: Int,
        sellerType: String,
        sellerLocation: String,
        isNational: Boolean
    ) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(MARKETPLACE_CATEGORIES.getOrElse(1) { "Roupas e acessórios" }) }
    var categoryExpanded by remember { mutableStateOf(false) }
    var priceMznText by remember { mutableStateOf("") }
    var promoPriceMznText by remember { mutableStateOf("") }
    var stockText by remember { mutableStateOf("10") }
    var sellerType by remember { mutableStateOf("INFORMAL") } // "INFORMAL" or "FORMAL"
    var sellerLocation by remember { mutableStateOf("Maputo - Mercado Zimpeto") }
    var isNational by remember { mutableStateOf(true) }

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
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Voltar",
                        tint = TextWhite
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = "Publicar Novo Produto",
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                    Text(
                        text = "Inclusão para vendedores formais e informais",
                        color = MacumuOrange,
                        fontSize = 11.5.sp
                    )
                }
            }
        }

        // Seller Type Selector (Informal vs Formal)
        item {
            Text("Tipo de Vendedor", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Informal (Bazar/Banca)
                val isInf = sellerType == "INFORMAL"
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isInf) Color(0xFF4A2508) else DarkNavyCard,
                    border = BorderStroke(1.dp, if (isInf) MacumuOrange else DarkNavyCardBorder),
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            sellerType = "INFORMAL"
                            sellerLocation = "Maputo - Mercado Zimpeto"
                        }
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Storefront, contentDescription = null, tint = MacumuOrange, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Bazar Informal", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Text("Sem NUIT inicial • Mercados e mamãs locais", color = TextMuted, fontSize = 10.sp)
                    }
                }

                // Formal
                val isForm = sellerType == "FORMAL"
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isForm) Color(0xFF0F3254) else DarkNavyCard,
                    border = BorderStroke(1.dp, if (isForm) VerifiedBadgeColor else DarkNavyCardBorder),
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            sellerType = "FORMAL"
                            sellerLocation = "Maputo - Av. Julius Nyerere"
                        }
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Verified, contentDescription = null, tint = VerifiedBadgeColor, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Loja Formal", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Text("Empresa com Alvará • Emissão de Factura", color = TextMuted, fontSize = 10.sp)
                    }
                }
            }
        }

        // Image Placeholder Box
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = DarkNavyCardElevated),
                border = BorderStroke(1.dp, DarkNavyCardBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.AddPhotoAlternate,
                        contentDescription = "Fotografia",
                        tint = MacumuOrange,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Fotografia do Produto (Câmara ou Galeria)",
                        color = TextWhite,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Atribuído automaticamente no catálogo",
                        color = TextMuted,
                        fontSize = 10.sp
                    )
                }
            }
        }

        // Title & Description
        item {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Nome do Produto") },
                placeholder = { Text("Ex: Capulana de Nampula, Telemóvel...") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                    focusedBorderColor = MacumuOrange,
                    unfocusedBorderColor = DarkNavyCardBorder
                ),
                modifier = Modifier.fillMaxWidth().testTag("product_title_input")
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Category Dropdown
            ExposedDropdownMenuBox(
                expanded = categoryExpanded,
                onExpandedChange = { categoryExpanded = !categoryExpanded }
            ) {
                OutlinedTextField(
                    value = selectedCategory,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoria") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedBorderColor = MacumuOrange,
                        unfocusedBorderColor = DarkNavyCardBorder
                    ),
                    modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable)
                )
                ExposedDropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false },
                    modifier = Modifier.background(DarkNavyCard)
                ) {
                    MARKETPLACE_CATEGORIES.filter { it != "Todas" }.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat, color = TextWhite) },
                            onClick = {
                                selectedCategory = cat
                                categoryExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descrição detalhada") },
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

        // Prices & Stock
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = priceMznText,
                    onValueChange = { priceMznText = it },
                    label = { Text("Preço (MZN)") },
                    placeholder = { Text("Ex: 1200") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedBorderColor = MacumuOrange,
                        unfocusedBorderColor = DarkNavyCardBorder
                    ),
                    modifier = Modifier.weight(1f).testTag("product_price_input")
                )

                OutlinedTextField(
                    value = promoPriceMznText,
                    onValueChange = { promoPriceMznText = it },
                    label = { Text("Preço Promocional (Opcional)") },
                    placeholder = { Text("Ex: 990") },
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = stockText,
                    onValueChange = { stockText = it },
                    label = { Text("Stock Disponível") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedBorderColor = MacumuOrange,
                        unfocusedBorderColor = DarkNavyCardBorder
                    ),
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = sellerLocation,
                    onValueChange = { sellerLocation = it },
                    label = { Text("Localização / Mercado") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedBorderColor = MacumuOrange,
                        unfocusedBorderColor = DarkNavyCardBorder
                    ),
                    modifier = Modifier.weight(1.5f)
                )
            }
        }

        // Scope Switch: National vs Global
        item {
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = DarkNavyCard),
                border = BorderStroke(1.dp, DarkNavyCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isNational) "Produto de Moçambique (Nacional)" else "Produto Internacional (Global)",
                            color = TextWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.5.sp
                        )
                        Text(
                            text = if (isNational) "Entrega rápida nacional 24-48h" else "Envio internacional transfronteiriço",
                            color = TextMuted,
                            fontSize = 10.5.sp
                        )
                    }
                    Switch(
                        checked = isNational,
                        onCheckedChange = { isNational = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = MacumuOrange
                        )
                    )
                }
            }
        }

        // Submit Button
        item {
            Button(
                onClick = {
                    val price = priceMznText.toDoubleOrNull() ?: 500.0
                    val promo = promoPriceMznText.toDoubleOrNull()
                    val stock = stockText.toIntOrNull() ?: 10
                    onPublish(
                        if (title.isBlank()) "Novo Produto Macumu" else title,
                        if (description.isBlank()) "Produto de qualidade garantida pelo marketplace Macumu Global." else description,
                        selectedCategory,
                        price,
                        promo,
                        stock,
                        sellerType,
                        sellerLocation,
                        isNational
                    )
                },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MacumuOrange),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("submit_publish_product_btn")
            ) {
                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Publicar no Macumu Global",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}
