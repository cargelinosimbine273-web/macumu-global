package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkNavyBackground
import com.example.ui.theme.DarkNavyCard
import com.example.ui.theme.DarkNavyCardBorder
import com.example.ui.theme.MacumuOrange
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun EcranAutenticacao(
    onAuthSucesso: () -> Unit,
    onContinuarVisitante: (() -> Unit)? = null
) {
    var email by remember { mutableStateOf("") }
    var palavraPasse by remember { mutableStateOf("") }
    var mensagemErro by remember { mutableStateOf<String?>(null) }
    var aCarregar by remember { mutableStateOf(false) }
    var modoRegisto by remember { mutableStateOf(false) }

    val auth = FirebaseAuth.getInstance()

    // Função para guardar ou atualizar os dados do utilizador no Firestore
    fun guardarDadosUtilizador(user: FirebaseUser) {
        val db = FirebaseFirestore.getInstance()
        val dadosUtilizador = hashMapOf(
            "uid" to user.uid,
            "email" to (user.email ?: ""),
            "dataCriacao" to System.currentTimeMillis()
        )

        // Utiliza o UID do Firebase Auth como ID do documento para evitar duplicados
        db.collection("utilizadores")
            .document(user.uid)
            .set(dadosUtilizador)
            .addOnSuccessListener {
                // Perfil guardado com sucesso na nuvem
            }
            .addOnFailureListener { e ->
                // Tratamento de erro opcional
            }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = DarkNavyBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Emblema Oficial MCG
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Brush.linearGradient(listOf(MacumuOrange, Color(0xFFFF944D))))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "MCG",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Macumu Global",
                style = MaterialTheme.typography.headlineLarge,
                color = TextWhite,
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            Text(
                text = "\"Do formal ao informal, tudo num só clique.\"",
                color = MacumuOrange,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            Text(
                text = if (modoRegisto) "Criar nova conta" else "Iniciar sessão",
                style = MaterialTheme.typography.titleMedium,
                color = TextMuted,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Campo de Email
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    mensagemErro = null
                },
                label = { Text("Email") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("auth_email_input"),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = DarkNavyCard,
                    unfocusedContainerColor = DarkNavyCard,
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                    focusedBorderColor = MacumuOrange,
                    unfocusedBorderColor = DarkNavyCardBorder,
                    focusedLabelColor = MacumuOrange,
                    unfocusedLabelColor = TextMuted
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Campo de Palavra-passe
            OutlinedTextField(
                value = palavraPasse,
                onValueChange = {
                    palavraPasse = it
                    mensagemErro = null
                },
                label = { Text("Palavra-passe") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("auth_password_input"),
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = DarkNavyCard,
                    unfocusedContainerColor = DarkNavyCard,
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                    focusedBorderColor = MacumuOrange,
                    unfocusedBorderColor = DarkNavyCardBorder,
                    focusedLabelColor = MacumuOrange,
                    unfocusedLabelColor = TextMuted
                )
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Mensagem de Erro
            if (mensagemErro != null) {
                Text(
                    text = mensagemErro!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 14.dp)
                )
            }

            // Botão Principal (Entrar ou Registar)
            Button(
                onClick = {
                    if (email.isBlank() || palavraPasse.isBlank()) {
                        mensagemErro = "Por favor, preenche todos os campos."
                        return@Button
                    }
                    if (modoRegisto && palavraPasse.length < 6) {
                        mensagemErro = "A palavra-passe deve ter pelo menos 6 caracteres."
                        return@Button
                    }

                    aCarregar = true
                    mensagemErro = null

                    val tarefaAuth = if (modoRegisto) {
                        auth.createUserWithEmailAndPassword(email.trim(), palavraPasse)
                    } else {
                        auth.signInWithEmailAndPassword(email.trim(), palavraPasse)
                    }

                    tarefaAuth.addOnCompleteListener { task ->
                        aCarregar = false
                        if (task.isSuccessful) {
                            val utilizadorAtual = auth.currentUser
                            if (utilizadorAtual != null) {
                                // Guarda os dados no Firestore antes de prosseguir
                                guardarDadosUtilizador(utilizadorAtual)
                            }
                            onAuthSucesso()
                        } else {
                            mensagemErro = task.exception?.localizedMessage ?: "Erro na autenticação."
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .testTag("auth_submit_btn"),
                enabled = !aCarregar,
                colors = ButtonDefaults.buttonColors(containerColor = MacumuOrange),
                shape = RoundedCornerShape(10.dp)
            ) {
                if (aCarregar) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = Color.White
                    )
                } else {
                    Text(
                        text = if (modoRegisto) "Criar Conta" else "Entrar",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Botão para alternar entre Login e Registo
            TextButton(
                onClick = {
                    modoRegisto = !modoRegisto
                    mensagemErro = null
                },
                enabled = !aCarregar
            ) {
                Text(
                    text = if (modoRegisto) {
                        "Já tens conta? Inicia sessão"
                    } else {
                        "Ainda não tens conta? Regista-te"
                    },
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }

            // Opção para Navegar como Visitante (Explorar Mercados)
            if (onContinuarVisitante != null) {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = onContinuarVisitante,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp)
                        .testTag("guest_explore_btn"),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextMuted)
                ) {
                    Text(
                        text = "Continuar como Visitante (Explorar Mercados)",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
