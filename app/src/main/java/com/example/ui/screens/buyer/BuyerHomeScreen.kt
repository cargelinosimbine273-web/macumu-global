package com.example.ui.screens.buyer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.LocalMall
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Verified
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.ProductEntity
import com.example.data.model.MarketHub
import com.example.data.model.MarketHubCatalog
import com.example.ui.components.EscrowGuaranteedBanner
import com.example.ui.components.MarketplaceProductCard
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

val MARKETPLACE_CATEGORIES = listOf(
    "Todas",
    "Roupas e acessórios",
    "Electrónica",
    "Casa e decoração",
    "Beleza e cuidados pessoais",
    "Veículos",
    "Alimentação",
    "Desporto",
    "Agricultura",
    "Construção",
    "Livros e educação",
    "Brinquedos"
)

@Composable
fun BuyerHomeScreen(
    products: List<ProductEntity>,
    searchQuery: String,
    selectedCategory: String,
    selectedScope: String,
    selectedMarket: String?,
    selectedInformalZone: String,
    currency: String,
    onSearchChange: (String) -> Unit,
    onCategorySelect: (String) -> Unit,
    onScopeSelect: (String) -> Unit,
    onMarketSelect: (String?) -> Unit,
    onInformalZoneSelect: (String) -> Unit,
    onClearFilters: () -> Unit,
    onProductClick: (Long) -> Unit,
    onAddToCart: (ProductEntity) -> Unit
) {
    val hasActiveFilter = !selectedMarket.isNullOrBlank() ||
            searchQuery.isNotBlank() ||
            selectedCategory != "Todas" ||
            selectedScope != "TODOS"

    val displayedInformalMarkets = when (selectedInformalZone) {
        "SUL" -> MarketHubCatalog.informalMarketsSul
        "CENTRO" -> MarketHubCatalog.informalMarketsCentro
        "NORTE" -> MarketHubCatalog.informalMarketsNorte
        else -> MarketHubCatalog.getAllInformalMarkets()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavyBackground)
            .padding(horizontal = 8.dp),
        contentPadding = PaddingValues(top = 6.dp, bottom = 70.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 1. Search Bar - Ultra-Compact Universal Search (Space-Saving)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = DarkNavyCard,
                    border = BorderStroke(
                        1.dp,
                        if (searchQuery.isNotBlank()) MacumuOrange else DarkNavyCardBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(34.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Pesquisar",
                            tint = MacumuOrange,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            if (searchQuery.isEmpty()) {
                                Text(
                                    "Pesquisar produtos, Alibaba, Temu, Maputo Shopping, Xipamanine...",
                                    color = TextDarkMuted,
                                    fontSize = 10.5.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            BasicTextField(
                                value = searchQuery,
                                onValueChange = onSearchChange,
                                singleLine = true,
                                textStyle = TextStyle(
                                    color = TextWhite,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Medium
                                ),
                                cursorBrush = SolidColor(MacumuOrange),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("search_input")
                            )
                        }
                        if (searchQuery.isNotEmpty()) {
                            IconButton(
                                onClick = { onSearchChange("") },
                                modifier = Modifier.size(20.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Limpar",
                                    tint = TextMuted,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }

                if (searchQuery.isNotBlank()) {
                    Text(
                        text = "Achados ${products.size} produto(s) para \"$searchQuery\"",
                        color = MacumuOrange,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
        }

        // 2. Active Filter Banner (if user filtered by market or search)
        if (hasActiveFilter) {
            item {
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E283C)),
                    border = BorderStroke(1.dp, MacumuOrange.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.FilterAlt, contentDescription = null, tint = MacumuOrange, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                val filterDesc = when {
                                    !selectedMarket.isNullOrBlank() -> "Mercado: $selectedMarket"
                                    searchQuery.isNotBlank() -> "Pesquisa: \"$searchQuery\""
                                    selectedCategory != "Todas" -> "Categoria: $selectedCategory"
                                    selectedScope != "TODOS" -> "Âmbito: $selectedScope"
                                    else -> "Filtro activo"
                                }
                                Text(
                                    text = filterDesc,
                                    color = TextWhite,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                                Text(
                                    text = "${products.size} produto(s) correspondente(s)",
                                    color = TextMuted,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        Button(
                            onClick = onClearFilters,
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MacumuOrange),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Text("Ver Todos", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }

        // 3. Hero Promotional Banner
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = DarkNavyCardElevated),
                border = BorderStroke(0.5.dp, DarkNavyCardBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(105.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = R.drawable.macumu_hero_banner_1794386234378),
                        contentDescription = "Macumu Global Banner",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        Color(0xFF090D16).copy(alpha = 0.90f),
                                        Color(0xFF090D16).copy(alpha = 0.45f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )

                    Column(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(10.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = MacumuOrange
                        ) {
                            Text(
                                text = "ECOSSISTEMA INTEGRADO",
                                color = Color.White,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Do formal ao informal,\ntudo num só clique.",
                            color = TextWhite,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.sp,
                            lineHeight = 16.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Maputo Shopping • Alibaba • Temu • Xipamanine",
                            color = MpesaGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp
                        )
                    }
                }
            }
        }

        // 4. Escrow Custody Assurance
        item {
            EscrowGuaranteedBanner()
        }

        // ==================== CARROSSEL 1: MERCADO NACIONAL ====================
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Identifier Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF0F3254)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Storefront,
                                    contentDescription = null,
                                    tint = VerifiedBadgeColor,
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "MERCADO NACIONAL 🇲🇿",
                                color = TextWhite,
                                fontWeight = FontWeight.Black,
                                fontSize = 13.5.sp,
                                letterSpacing = 0.5.sp
                            )
                        }
                        Text(
                            text = "Centros Comerciais, Shoppings e Lojas Oficiais",
                            color = TextMuted,
                            fontSize = 10.5.sp
                        )
                    }

                    if (selectedMarket in MarketHubCatalog.nationalMarkets.map { it.name }) {
                        TextButton(onClick = { onMarketSelect(null) }) {
                            Text("Limpar", color = MacumuOrange, fontSize = 11.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Running Carousel
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(MarketHubCatalog.nationalMarkets) { hub ->
                        MarketHubCard(
                            hub = hub,
                            isSelected = selectedMarket == hub.name,
                            accentColor = VerifiedBadgeColor,
                            onClick = {
                                if (selectedMarket == hub.name) onMarketSelect(null)
                                else onMarketSelect(hub.name)
                            }
                        )
                    }
                }
            }
        }

        // ==================== CARROSSEL 2: MERCADO INTERNACIONAL ====================
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Identifier Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF332050)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Public,
                                    contentDescription = null,
                                    tint = Color(0xFFA855F7),
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "MERCADO INTERNACIONAL 🌍",
                                color = TextWhite,
                                fontWeight = FontWeight.Black,
                                fontSize = 13.5.sp,
                                letterSpacing = 0.5.sp
                            )
                        }
                        Text(
                            text = "Alibaba, Temu Express, AliExpress, Shein & Parceiros Globais",
                            color = TextMuted,
                            fontSize = 10.5.sp
                        )
                    }

                    if (selectedMarket in MarketHubCatalog.internationalMarkets.map { it.name }) {
                        TextButton(onClick = { onMarketSelect(null) }) {
                            Text("Limpar", color = MacumuOrange, fontSize = 11.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Running Carousel
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(MarketHubCatalog.internationalMarkets) { hub ->
                        MarketHubCard(
                            hub = hub,
                            isSelected = selectedMarket == hub.name,
                            accentColor = Color(0xFFA855F7),
                            onClick = {
                                if (selectedMarket == hub.name) onMarketSelect(null)
                                else onMarketSelect(hub.name)
                            }
                        )
                    }
                }
            }
        }

        // ==================== CARROSSEL 3: MERCADOS INFORMAIS (SUL, CENTRO, NORTE) ====================
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Identifier Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF4A2508)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocalMall,
                                    contentDescription = null,
                                    tint = MacumuOrange,
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "MERCADOS INFORMAIS DE MOÇAMBIQUE 🇲🇿",
                                color = TextWhite,
                                fontWeight = FontWeight.Black,
                                fontSize = 13.5.sp,
                                letterSpacing = 0.5.sp
                            )
                        }
                        Text(
                            text = "Bazares Tradicionais, Produtores e Mamãs (Centro, Sul e Norte)",
                            color = MacumuOrange,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    if (selectedMarket in MarketHubCatalog.getAllInformalMarkets().map { it.name }) {
                        TextButton(onClick = { onMarketSelect(null) }) {
                            Text("Limpar", color = MacumuOrange, fontSize = 11.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Regional Zone Sub-tabs (Sul, Centro, Norte, Todos)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val zones = listOf(
                        "TODOS" to "Todos os Mercados",
                        "SUL" to "Sul (Maputo/Gaza/Inhambane)",
                        "CENTRO" to "Centro (Beira/Quelimane/Tete)",
                        "NORTE" to "Norte (Nampula/Pemba/Niassa)"
                    )

                    zones.forEach { (zoneKey, label) ->
                        val isZoneActive = selectedInformalZone == zoneKey
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isZoneActive) MacumuOrange else DarkNavyCard,
                            border = BorderStroke(1.dp, if (isZoneActive) MacumuOrange else DarkNavyCardBorder),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onInformalZoneSelect(zoneKey) }
                                .testTag("zone_tab_${zoneKey.lowercase()}")
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 5.dp, horizontal = 2.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = when (zoneKey) {
                                        "TODOS" -> "Todos"
                                        "SUL" -> "Sul"
                                        "CENTRO" -> "Centro"
                                        "NORTE" -> "Norte"
                                        else -> zoneKey
                                    },
                                    color = if (isZoneActive) Color.White else TextMuted,
                                    fontSize = 10.5.sp,
                                    fontWeight = if (isZoneActive) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Informal Running Carousel
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(displayedInformalMarkets) { hub ->
                        MarketHubCard(
                            hub = hub,
                            isSelected = selectedMarket == hub.name,
                            accentColor = MacumuOrange,
                            onClick = {
                                if (selectedMarket == hub.name) onMarketSelect(null)
                                else onMarketSelect(hub.name)
                            }
                        )
                    }
                }
            }
        }

        // Categories Carousel
        item {
            Column {
                Text(
                    text = "Categorias de Produtos",
                    color = TextWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.5.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(MARKETPLACE_CATEGORIES) { category ->
                        val isSelected = selectedCategory == category
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) MacumuOrange else DarkNavyCard,
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) MacumuOrange else DarkNavyCardBorder
                            ),
                            modifier = Modifier
                                .clickable { onCategorySelect(category) }
                                .testTag("cat_chip_${category.lowercase().replace(" ", "_")}")
                        ) {
                            Text(
                                text = category,
                                color = if (isSelected) Color.White else TextMuted,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }

        // Section Title for Results
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = when {
                            !selectedMarket.isNullOrBlank() -> "Produtos de: $selectedMarket"
                            searchQuery.isNotBlank() -> "Resultados da Pesquisa"
                            selectedCategory != "Todas" -> selectedCategory
                            else -> "Catálogo Geral de Produtos"
                        },
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "${products.size} produto(s) encontrado(s)",
                        color = MacumuOrange,
                        fontSize = 11.sp
                    )
                }

                if (hasActiveFilter) {
                    TextButton(onClick = onClearFilters) {
                        Text("Ver Todos", color = EmolaBlue, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Product Grid List
        if (products.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkNavyCard),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Nenhum produto encontrado",
                            color = TextWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Não encontramos produtos para os critérios actuais.",
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = onClearFilters,
                            colors = ButtonDefaults.buttonColors(containerColor = MacumuOrange),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Limpar Filtros e Ver Todos", color = Color.White, fontSize = 12.sp)
                        }
                    }
                }
            }
        } else {
            val chunkedProducts = products.chunked(2)
            items(chunkedProducts) { pair ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MarketplaceProductCard(
                        product = pair[0],
                        currency = currency,
                        onProductClick = { onProductClick(pair[0].id) },
                        onAddToCart = { onAddToCart(pair[0]) },
                        modifier = Modifier.weight(1f)
                    )

                    if (pair.size > 1) {
                        MarketplaceProductCard(
                            product = pair[1],
                            currency = currency,
                            onProductClick = { onProductClick(pair[1].id) },
                            onAddToCart = { onAddToCart(pair[1]) },
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

/**
 * Reusable Hub Card for National Malls, International Channels, and Informal Bazares
 */
@Composable
fun MarketHubCard(
    hub: MarketHub,
    isSelected: Boolean,
    accentColor: Color,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFF1E283E) else DarkNavyCard
        ),
        border = BorderStroke(
            1.5.dp,
            if (isSelected) MacumuOrange else DarkNavyCardBorder
        ),
        modifier = Modifier
            .width(170.dp)
            .clickable { onClick() }
            .testTag("market_hub_${hub.id}")
    ) {
        Column(
            modifier = Modifier.padding(10.dp)
        ) {
            // Top Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = accentColor.copy(alpha = 0.15f),
                    border = BorderStroke(0.5.dp, accentColor)
                ) {
                    Text(
                        text = hub.badge,
                        color = accentColor,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                    )
                }

                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .clip(CircleShape)
                            .background(MacumuOrange),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(10.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Market Name
            Text(
                text = hub.name,
                color = TextWhite,
                fontWeight = FontWeight.Bold,
                fontSize = 12.5.sp,
                maxLines = 1
            )

            // Subtitle
            Text(
                text = hub.subtitle,
                color = TextMuted,
                fontSize = 9.5.sp,
                maxLines = 2,
                lineHeight = 13.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Location pin
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = TextDarkMuted,
                    modifier = Modifier.size(11.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = hub.location,
                    color = TextDarkMuted,
                    fontSize = 9.sp,
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Estimated Delivery
            Text(
                text = "Entrega: ${hub.estimatedDelivery}",
                color = MpesaGreen,
                fontSize = 8.5.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
