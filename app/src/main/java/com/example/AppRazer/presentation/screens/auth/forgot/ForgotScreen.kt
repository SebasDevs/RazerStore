package com.example.AppRazer.presentation.screens.auth.forgot

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.AppRazer.R

@Composable
fun ForgotScreen(
    navController: NavController,
    viewModel: ForgotViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // ── Botón volver
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Volver",
                tint = Color.White,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { navController.popBackStack() }
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        // ── Logo
        Image(
            painter = painterResource(id = R.drawable.logorazer),
            contentDescription = "Razer Logo",
            modifier = Modifier
                .size(80.dp)
                .padding(bottom = 8.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "FOR GAMERS. BY GAMERS.",
            style = TextStyle(
                fontSize = 12.sp, color = Color.Green,
                fontWeight = FontWeight.Bold, letterSpacing = 2.sp
            )
        )

        Spacer(modifier = Modifier.height(40.dp))

        if (!state.emailSent) {
            // ── Vista inicial
            Text(
                text = "¿Olvidaste tu contraseña?",
                style = TextStyle(
                    fontSize = 24.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Introduce tu email y te enviaremos\nun enlace para restablecer tu contraseña.",
                style = TextStyle(fontSize = 14.sp, color = Color.Gray),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = state.email,
                onValueChange = viewModel::onEmailChange,
                label = { Text("Email", color = Color.Gray) },
                leadingIcon = {
                    Icon(Icons.Default.Email, contentDescription = null, tint = Color.Gray)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(4.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Green,
                    unfocusedBorderColor = Color.DarkGray,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color.Green
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true
            )

            if (state.errorMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = state.errorMessage, color = Color.Red, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = viewModel::sendResetEmail,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Green),
                enabled = state.email.isNotEmpty() && !state.isLoading
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        color = Color.Black,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text(
                        text = "ENVIAR EMAIL",
                        color = Color.Black,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }

        } else {
            // ── Vista de éxito
            Text(text = "✅", fontSize = 60.sp)

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Email enviado",
                style = TextStyle(
                    fontSize = 24.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Revisa tu bandeja de entrada en\n${state.email}\ny sigue las instrucciones para restablecer tu contraseña.",
                style = TextStyle(fontSize = 14.sp, color = Color.Gray),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
            ) {
                Text(
                    text = "VOLVER AL LOGIN",
                    color = Color.Black,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "¿No recibiste el email? Reenviar",
                color = Color.Green,
                fontSize = 13.sp,
                modifier = Modifier.clickable { viewModel.resendEmail() }
            )
        }
    }
}