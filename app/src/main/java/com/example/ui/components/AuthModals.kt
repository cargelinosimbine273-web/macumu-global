package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.VerifiedUser
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.UserRegistrationEntity
import com.example.ui.screens.buyer.MOZAMBIQUE_PROVINCES
import com.example.ui.theme.DangerRed
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

/**
 * Developer Security PIN Dialog:
 * Restricts the developer / admin back-office so visitors cannot access it.
 */
@Composable
fun DeveloperPinDialog(
    onDismiss: () -> Unit,
    onVerifyPin: (String) -> Boolean
) {
    var pin by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkNavyCardElevated,
        icon = {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(MacumuOrange.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = MacumuOrange,
                    modifier = Modifier.size(22.dp)
                )
            }
        },
        title = {
            Text(
                text = "Área Restrita do Desenvolvedor",
                color = TextWhite,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "A consola administrativa ficará indisponível nesta versão de testes. O acesso será reactivado após a implementação de autenticação administrativa segura no servidor.",
                    color = TextMuted,
                    fontSize = 11.5.sp,
                    lineHeight = 16.sp
                )

                OutlinedTextField(
                    value = pin,
                    onValueChange = {
                        pin = it
                        errorMessage = null
                    },
                    label = { Text("Acesso administrativo temporariamente desactivado", fontSize = 11.sp) },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = DarkNavyCard,
                        unfocusedContainerColor = DarkNavyCard,
                        focusedBorderColor = MacumuOrange,
                        unfocusedBorderColor = DarkNavyCardBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedLabelColor = MacumuOrange,
                        unfocusedLabelColor = TextMuted
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("dev_pin_input")
                )

                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = DangerRed,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val success = onVerifyPin(pin)
                    if (!success) {
                        errorMessage = "Acesso administrativo ainda não configurado com segurança."
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MacumuOrange),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("dev_pin_confirm_btn")
            ) {
                Text("Entrar na Consola", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = TextMuted, fontSize = 12.sp)
            }
        }
    )
}

/**
 * Account Required Dialog:
 * Shown when a visitor tries to buy, sell, or affiliate.
 * Lets them either register for developer approval or sign in with an existing account.
 */
@Composable
fun AuthOrRegisterDialog(
    action: String,
    existingRegistrations: List<UserRegistrationEntity>,
    onDismiss: () -> Unit,
    onRegister: (
        name: String,
        phone: String,
        email: String,
        role: String,
        sellerType: String,
        businessName: String,
        province: String
    ) -> Unit,
    onLogin: (UserRegistrationEntity) -> Unit
) {
    var selectedTab by remember { mutableStateOf("REGISTER") } // "REGISTER" or "LOGIN"

    // Form states
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("+258 8") }
    var email by remember { mutableStateOf("") }
    var requestedRole by remember {
        mutableStateOf(
            when (action) {
                "VENDER" -> "VENDEDOR"
                "AFILIAR" -> "AFILIADO"
                else -> "COMPRADOR"
            }
        )
    }
    var sellerType by remember { mutableStateOf("INFORMAL") }
    var businessName by remember { mutableStateOf("") }
    var province by remember { mutableStateOf("Maputo Cidade") }
    var errorText by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkNavyCardElevated,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = MacumuOrange,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = when (action) {
                        "COMPRAR" -> "Conta Necessária para Comprar"
                        "VENDER" -> "Registar como Vendedor"
                        "AFILIAR" -> "Registar como Afiliado"
                        else -> "Identificação de Conta"
                    },
                    color = TextWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Navegue à vontade como visitante! Para realizar transações com garantia de custódia, adesão é necessária e validada pelo Desenvolvedor.",
                    color = TextMuted,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )

                // Tab Switcher: Submeter Adesão vs Iniciar Sessão
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (selectedTab == "REGISTER") MacumuOrange else DarkNavyCard,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedTab = "REGISTER" }
                    ) {
                        Box(modifier = Modifier.padding(vertical = 6.dp), contentAlignment = Alignment.Center) {
                            Text(
                                "Nova Adesão",
                                color = if (selectedTab == "REGISTER") Color.White else TextMuted,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (selectedTab == "LOGIN") MacumuOrange else DarkNavyCard,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedTab = "LOGIN" }
                    ) {
                        Box(modifier = Modifier.padding(vertical = 6.dp), contentAlignment = Alignment.Center) {
                            Text(
                                "Entrar",
                                color = if (selectedTab == "LOGIN") Color.White else TextMuted,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                if (selectedTab == "REGISTER") {
                    // Registration Form
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nome Completo", fontSize = 10.5.sp) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = DarkNavyCard,
                            unfocusedContainerColor = DarkNavyCard,
                            focusedBorderColor = MacumuOrange,
                            unfocusedBorderColor = DarkNavyCardBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("reg_name_input")
                    )

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Contacto Telefónico (M-Pesa / e-Mola)", fontSize = 10.5.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = DarkNavyCard,
                            unfocusedContainerColor = DarkNavyCard,
                            focusedBorderColor = MacumuOrange,
                            unfocusedBorderColor = DarkNavyCardBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("reg_phone_input")
                    )

                    // Role selector in registration
                    Text("Papel Pretendido:", color = TextWhite, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf("COMPRADOR", "VENDEDOR", "AFILIADO").forEach { r ->
                            val isSel = requestedRole == r
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) MacumuOrange.copy(alpha = 0.3f) else DarkNavyCard,
                                border = BorderStroke(1.dp, if (isSel) MacumuOrange else DarkNavyCardBorder),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { requestedRole = r }
                            ) {
                                Box(modifier = Modifier.padding(vertical = 5.dp), contentAlignment = Alignment.Center) {
                                    Text(
                                        text = when (r) {
                                            "COMPRADOR" -> "Comprar"
                                            "VENDEDOR" -> "Vender"
                                            else -> "Afiliar"
                                        },
                                        color = if (isSel) MacumuOrange else TextWhite,
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    if (requestedRole == "VENDEDOR") {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (sellerType == "INFORMAL") Color(0xFF4A2500) else DarkNavyCard,
                                border = BorderStroke(0.5.dp, if (sellerType == "INFORMAL") MacumuOrange else DarkNavyCardBorder),
                                modifier = Modifier.weight(1f).clickable { sellerType = "INFORMAL" }
                            ) {
                                Box(modifier = Modifier.padding(vertical = 4.dp), contentAlignment = Alignment.Center) {
                                    Text("Bazar Informal 🇲🇿", color = TextWhite, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (sellerType == "FORMAL") Color(0xFF0F3254) else DarkNavyCard,
                                border = BorderStroke(0.5.dp, if (sellerType == "FORMAL") EmolaBlue else DarkNavyCardBorder),
                                modifier = Modifier.weight(1f).clickable { sellerType = "FORMAL" }
                            ) {
                                Box(modifier = Modifier.padding(vertical = 4.dp), contentAlignment = Alignment.Center) {
                                    Text("Loja Registada", color = TextWhite, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        OutlinedTextField(
                            value = businessName,
                            onValueChange = { businessName = it },
                            label = { Text("Nome da Loja, Barraca ou Feira", fontSize = 10.5.sp) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = DarkNavyCard,
                                unfocusedContainerColor = DarkNavyCard,
                                focusedBorderColor = MacumuOrange,
                                unfocusedBorderColor = DarkNavyCardBorder,
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite
                            ),
                            modifier = Modifier.fillMaxWidth().testTag("reg_business_input")
                        )
                    }

                    if (errorText != null) {
                        Text(text = errorText!!, color = DangerRed, fontSize = 10.5.sp)
                    }
                } else {
                    // Quick Login with existing accounts
                    Text(
                        text = "Selecione uma conta registada:",
                        color = TextWhite,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold
                    )

                    existingRegistrations.forEach { user ->
                        Card(
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkNavyCard),
                            border = BorderStroke(
                                0.5.dp,
                                if (user.status == "APPROVED") MpesaGreen else EscrowAmber
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onLogin(user) }
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = user.fullName, color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Text(text = "${user.requestedRole} • ${user.phoneNumber}", color = TextMuted, fontSize = 10.sp)
                                }
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = if (user.status == "APPROVED") Color(0xFF0D331A) else Color(0xFF3B2706)
                                ) {
                                    Text(
                                        text = if (user.status == "APPROVED") "Aprovado" else "Aguardando Aval",
                                        color = if (user.status == "APPROVED") MpesaGreen else EscrowAmber,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (selectedTab == "REGISTER") {
                Button(
                    onClick = {
                        if (name.isBlank() || phone.isBlank()) {
                            errorText = "Por favor preencha o Nome e Contacto Telefónico."
                        } else {
                            onRegister(
                                name.trim(),
                                phone.trim(),
                                email.trim(),
                                requestedRole,
                                sellerType,
                                businessName.trim(),
                                province
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MacumuOrange),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("submit_registration_btn")
                ) {
                    Text("Submeter Adesão para Aval", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Continuar a Navegar", color = TextMuted, fontSize = 11.sp)
            }
        }
    )
}

/**
 * Notice shown when the user's account is pending developer approval.
 */
@Composable
fun AwaitingApprovalDialog(
    user: UserRegistrationEntity,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkNavyCardElevated,
        icon = {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(EscrowAmber.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.HourglassTop,
                    contentDescription = null,
                    tint = EscrowAmber,
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        title = {
            Text(
                text = "Adesão em Análise pelo Desenvolvedor",
                color = TextWhite,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Olá, ${user.fullName}!\nO seu pedido de adesão como ${user.requestedRole} foi registado com sucesso no ecossistema Macumu Global.",
                    color = TextWhite,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF261D0A),
                    border = BorderStroke(1.dp, EscrowAmber.copy(alpha = 0.6f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "Status: Aguardando Aval do Desenvolvedor",
                            color = EscrowAmber,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Cada nova inscrição é verificada pelo desenvolvedor para garantir conformidade e segurança nas transações M-Pesa/e-Mola.",
                            color = TextMuted,
                            fontSize = 10.5.sp,
                            lineHeight = 14.sp
                        )
                    }
                }

                Text(
                    text = "Enquanto aguarda a validação, pode continuar a explorar normalmente todos os produtos, feiras e mercados sem restrições.",
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = MacumuOrange),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Continuar a Navegar", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    )
}
