package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.AppRole
import com.example.ui.theme.DarkNavyBackground
import com.example.ui.theme.DarkNavyCard
import com.example.ui.theme.DarkNavyCardBorder
import com.example.ui.theme.DarkNavyCardElevated
import com.example.ui.theme.EmolaBlue
import com.example.ui.theme.MacumuOrange
import com.example.ui.theme.MpesaGreen
import com.example.ui.theme.TextDarkMuted
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

@Composable
fun WelcomeRoleScreen(
    onSelectRoleAndProceed: (AppRole) -> Unit
) {
    var selectedRole by remember { mutableStateOf(AppRole.COMPRADOR) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavyBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                // Brand Emblem
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(MacumuOrange, Color(0xFFFF954D))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "M",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 32.sp
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "MACUMU GLOBAL",
                    color = TextWhite,
                    fontWeight = FontWeight.Black,
                    fontSize = 22.sp,
                    letterSpacing = 1.sp
                )

                Text(
                    text = "\"Do formal ao informal, tudo num só clique.\"",
                    color = MacumuOrange,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Como pretende utilizar o Macumu Global?",
                    color = TextWhite,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Selecione o seu perfil inicial. Poderá alternar entre comprador, vendedor e afiliado em qualquer momento.",
                    color = TextMuted,
                    fontSize = 11.5.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Role Option Cards
                val options = listOf(
                    Triple(
                        AppRole.COMPRADOR,
                        "A. COMPRADOR",
                        "Comprar produtos locais e globais com garantia de fundos (Escrow) via M-Pesa, e-Mola ou na entrega."
                    ),
                    Triple(
                        AppRole.VENDEDOR,
                        "B. VENDEDOR (Formal & Informal)",
                        "Vender no bazar ou loja online. Sem exigência de NUIT para vendedores informais. Levantamentos directos."
                    ),
                    Triple(
                        AppRole.AFILIADO,
                        "C. AFILIADO",
                        "Gerar links exclusivos, partilhar no WhatsApp e receber comissões automáticas de até 15% por cada venda."
                    )
                )

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    options.forEach { (role, title, desc) ->
                        val isSelected = selectedRole == role
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
                                .fillMaxWidth()
                                .clickable { selectedRole = role }
                                .testTag("welcome_role_${role.name.lowercase()}")
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) MacumuOrange else DarkNavyCardElevated),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = when (role) {
                                            AppRole.COMPRADOR -> Icons.Default.ShoppingBag
                                            AppRole.VENDEDOR -> Icons.Default.Storefront
                                            AppRole.AFILIADO -> Icons.Default.CurrencyExchange
                                            AppRole.ADMIN -> Icons.Default.Security
                                        },
                                        contentDescription = null,
                                        tint = if (isSelected) Color.White else TextMuted,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = title,
                                        color = if (isSelected) MacumuOrange else TextWhite,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.5.sp
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = desc,
                                        color = TextMuted,
                                        fontSize = 11.sp,
                                        lineHeight = 15.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Bottom Continue Button
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = { onSelectRoleAndProceed(selectedRole) },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MacumuOrange),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("welcome_continue_btn")
                ) {
                    Text(
                        text = "Entrar no Macumu Global",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = { onSelectRoleAndProceed(AppRole.ADMIN) }
                ) {
                    Text(
                        text = "Acesso Técnico / Administrador",
                        color = TextDarkMuted,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}
