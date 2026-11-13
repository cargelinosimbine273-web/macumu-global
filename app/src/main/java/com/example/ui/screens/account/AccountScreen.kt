package com.example.ui.screens.account

import android.app.Activity
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
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.ui.platform.LocalContext
import com.example.ui.theme.DangerRed
import com.google.firebase.auth.FirebaseAuth
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
import com.example.data.local.NotificationEntity
import com.example.ui.AppRole
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

@Composable
fun AccountScreen(
    currentRole: AppRole,
    currency: String,
    language: String,
    notifications: List<NotificationEntity>,
    onSelectRole: (AppRole) -> Unit,
    onSelectCurrency: (String) -> Unit,
    onSelectLanguage: (String) -> Unit
) {
    var showCurrencyDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavyBackground)
            .padding(horizontal = 14.dp),
        contentPadding = PaddingValues(top = 14.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // User Profile Card
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = DarkNavyCardElevated),
                border = BorderStroke(1.dp, DarkNavyCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(MacumuOrange),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "MG",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    val firebaseAuth = FirebaseAuth.getInstance()
                    val currentUserEmail = firebaseAuth.currentUser?.email

                    Column {
                        Text(
                            text = currentUserEmail ?: "Utilizador Macumu Global",
                            color = TextWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                        )
                        Text(
                            text = if (currentUserEmail != null) "Sessão iniciada via Firebase • Moçambique 🇲🇿" else "Modo Visitante • Maputo, Moçambique 🇲🇿",
                            color = TextMuted,
                            fontSize = 11.5.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MacumuOrange.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "Modo Actual: ${currentRole.label}",
                                color = MacumuOrange,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }

        // Quick Switch Roles
        item {
            Text("Alternar Perfil de Acesso", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(6.dp))

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                AppRole.entries.filter { it != AppRole.ADMIN }.forEach { role ->
                    val isSelected = currentRole == role
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) Color(0xFF1E283C) else DarkNavyCard
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) MacumuOrange else DarkNavyCardBorder
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectRole(role) }
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = when (role) {
                                    AppRole.COMPRADOR -> Icons.Default.AccountCircle
                                    AppRole.VENDEDOR -> Icons.Default.Storefront
                                    AppRole.AFILIADO -> Icons.Default.CurrencyExchange
                                    AppRole.ADMIN -> Icons.Default.Security
                                },
                                contentDescription = null,
                                tint = if (isSelected) MacumuOrange else TextMuted,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = role.label,
                                    color = TextWhite,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = role.description,
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Settings (Currency & Language)
        item {
            Text("Preferências Globais", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(6.dp))

            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = DarkNavyCard),
                border = BorderStroke(1.dp, DarkNavyCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    // Currency row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showCurrencyDialog = true }
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CurrencyExchange, contentDescription = null, tint = MacumuOrange, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Moeda da Plataforma", color = TextWhite, fontSize = 13.sp)
                        }
                        Text(
                            text = when (currency) {
                                "ZAR" -> "Rands (ZAR)"
                                "USD" -> "Dólar (USD)"
                                "EUR" -> "Euro (EUR)"
                                else -> "Metical (MZN)"
                            },
                            color = MacumuOrange,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }

                    Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(DarkNavyCardBorder))

                    // Language row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showLanguageDialog = true }
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Language, contentDescription = null, tint = EmolaBlue, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Idioma", color = TextWhite, fontSize = 13.sp)
                        }
                        Text(language, color = EmolaBlue, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(DarkNavyCardBorder))

                    // About Macumu Global row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showAboutDialog = true }
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Help, contentDescription = null, tint = MpesaGreen, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Sobre o Macumu Global", color = TextWhite, fontSize = 13.sp)
                        }
                        Text("v1.0", color = TextMuted, fontSize = 12.sp)
                    }
                }
            }
        }

        // Notification Log
        item {
            Text("Últimas Notificações", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }

        if (notifications.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkNavyCard),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Sem novas notificações no momento.",
                        color = TextMuted,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(14.dp)
                    )
                }
            }
        } else {
            items(notifications) { notif ->
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkNavyCard),
                    border = BorderStroke(0.5.dp, DarkNavyCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Notifications, contentDescription = null, tint = MacumuOrange, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(notif.title, color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text(notif.message, color = TextMuted, fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Seção Terminar Sessão / Sessão Firebase
        item {
            val auth = FirebaseAuth.getInstance()
            val firebaseUser = auth.currentUser
            val context = LocalContext.current
            val activity = context as? Activity

            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = DarkNavyCard),
                border = BorderStroke(1.dp, if (firebaseUser != null) DangerRed.copy(alpha = 0.5f) else DarkNavyCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = if (firebaseUser != null) "Sessão Activa: ${firebaseUser.email}" else "Sessão como Visitante",
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (firebaseUser != null)
                            "Ao terminar sessão, a aplicação reinicia voltando ao ecrã inicial de autenticação."
                        else
                            "Inicie sessão para poder comprar com custódia, publicar produtos ou auferir comissões.",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            auth.signOut()
                            val intent = Intent(context, com.example.MainActivity::class.java).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            }
                            context.startActivity(intent)
                            activity?.finish()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (firebaseUser != null) DangerRed else MacumuOrange
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .testTag("account_logout_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (firebaseUser != null) "Terminar Sessão" else "Iniciar Sessão",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.5.sp
                        )
                    }
                }
            }
        }
    }

    // Currency Selector Dialog
    if (showCurrencyDialog) {
        AlertDialog(
            onDismissRequest = { showCurrencyDialog = false },
            containerColor = DarkNavyCardElevated,
            title = { Text("Selecione a Moeda", color = TextWhite, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(
                        "MZN" to "Metical Moçambicano (MZN)",
                        "ZAR" to "Rand Sul-Africano (ZAR)",
                        "USD" to "Dólar Americano (USD)",
                        "EUR" to "Euro Europeu (EUR)"
                    ).forEach { (code, name) ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (currency == code) MacumuOrange else DarkNavyCard,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSelectCurrency(code)
                                    showCurrencyDialog = false
                                }
                        ) {
                            Text(
                                text = name,
                                color = if (currency == code) Color.White else TextWhite,
                                fontWeight = if (currency == code) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showCurrencyDialog = false }) {
                    Text("Fechar", color = TextMuted)
                }
            }
        )
    }

    // Language Selector Dialog
    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            containerColor = DarkNavyCardElevated,
            title = { Text("Selecione o Idioma", color = TextWhite, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Português", "English", "Français", "Español").forEach { lang ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (language == lang) MacumuOrange else DarkNavyCard,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSelectLanguage(lang)
                                    showLanguageDialog = false
                                }
                        ) {
                            Text(
                                text = lang,
                                color = if (language == lang) Color.White else TextWhite,
                                fontWeight = if (language == lang) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showLanguageDialog = false }) {
                    Text("Fechar", color = TextMuted)
                }
            }
        )
    }

    // About Dialog
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            containerColor = DarkNavyCardElevated,
            title = { Text("MACUMU GLOBAL", color = TextWhite, fontWeight = FontWeight.ExtraBold) },
            text = {
                Column {
                    Text(
                        "\"Do formal ao informal, tudo num só clique.\"",
                        color = MacumuOrange,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        "O Macumu Global é uma plataforma de comércio electrónico e marketplace desenhada para integrar comerciantes informais e empresas formais num único ecossistema digital seguro com garantia de fundos (Escrow) e comissões para afiliados.\n\nComeçando em Moçambique e com arquitectura preparada para os mercados africanos e internacionais.",
                        color = TextMuted,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text("Entendido", color = MacumuOrange, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}
