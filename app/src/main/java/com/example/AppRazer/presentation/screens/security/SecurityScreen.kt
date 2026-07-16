package com.example.AppRazer.presentation.screens.security

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.AppRazer.presentation.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecurityScreen(
    navController: NavController,
    viewModel: SecurityViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    // ── Navegar a Login cuando se elimine la cuenta ──────────────
    LaunchedEffect(state.accountDeleted) {
        if (state.accountDeleted) {
            navController.navigate(Screen.Login.route) {
                popUpTo(Screen.Inicio.route) { inclusive = true }
            }
        }
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color.Black)) {
        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black),
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                }
            },
            title = { Text("Seguridad", color = Color.White, fontWeight = FontWeight.Bold) }
        )

        HorizontalDivider(color = Color(0xFF222222))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            // ══════════════════════════════════════════════════════
            // ── SECCIÓN: Verificación de email ──
            // ══════════════════════════════════════════════════════
            if (state.isEmailUser) {
                Column {
                    Text(
                        "VERIFICACIÓN DE EMAIL",
                        color = Color.Gray,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF111111), RoundedCornerShape(8.dp))
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            if (state.isEmailVerified) Icons.Default.CheckCircle else Icons.Default.Email,
                            null,
                            tint = if (state.isEmailVerified) Color.Green else Color(0xFFFFA726),
                            modifier = Modifier.padding(end = 12.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                state.userEmail,
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                if (state.isEmailVerified) "Email verificado" else "Email sin verificar",
                                color = if (state.isEmailVerified) Color.Green else Color(0xFFFFA726),
                                fontSize = 12.sp
                            )
                        }
                        if (!state.isEmailVerified) {
                            if (state.isSendingVerification) {
                                CircularProgressIndicator(
                                    color = Color.Green,
                                    modifier = Modifier.size(20.dp)
                                )
                            } else if (state.verificationSent) {
                                TextButton(onClick = { viewModel.refreshVerificationStatus() }) {
                                    Text("Ya verifiqué", color = Color.Green, fontSize = 12.sp)
                                }
                            } else {
                                TextButton(onClick = { viewModel.sendVerificationEmail() }) {
                                    Text("Verificar", color = Color.Green, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                    if (state.verificationSent && !state.isEmailVerified) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Revisa tu bandeja de entrada y toca el enlace de verificación",
                            color = Color.Gray, fontSize = 11.sp
                        )
                    }
                }
            }

            // ══════════════════════════════════════════════════════
            // ── SECCIÓN: Contraseña / Info de Google ──
            // ══════════════════════════════════════════════════════
            if (!state.isEmailUser) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF111111), RoundedCornerShape(8.dp))
                        .padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        Icons.Default.Info,
                        null,
                        tint = Color.Green,
                        modifier = Modifier.padding(end = 12.dp)
                    )
                    Column {
                        Text(
                            "Tu cuenta está protegida por Google",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Iniciaste sesión con Google, así que tu contraseña y seguridad se gestionan directamente desde tu cuenta de Google, no desde esta app.",
                            color = Color.Gray, fontSize = 13.sp
                        )
                    }
                }
            } else {
                Column {
                    Text(
                        "CAMBIAR CONTRASEÑA",
                        color = Color.Gray,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    var currentVisible by remember { mutableStateOf(false) }
                    var newVisible by remember { mutableStateOf(false) }

                    val fieldColors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Green,
                        unfocusedBorderColor = Color.DarkGray,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color.Green
                    )

                    OutlinedTextField(
                        value = state.currentPassword,
                        onValueChange = viewModel::onCurrentPasswordChange,
                        label = { Text("Contraseña actual", color = Color.Gray) },
                        leadingIcon = { Icon(Icons.Default.Lock, null, tint = Color.Gray) },
                        trailingIcon = {
                            IconButton(onClick = { currentVisible = !currentVisible }) {
                                Icon(
                                    if (currentVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    null,
                                    tint = Color.Gray
                                )
                            }
                        },
                        visualTransformation = if (currentVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp),
                        colors = fieldColors, singleLine = true
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = state.newPassword,
                        onValueChange = viewModel::onNewPasswordChange,
                        label = { Text("Nueva contraseña", color = Color.Gray) },
                        leadingIcon = { Icon(Icons.Default.Lock, null, tint = Color.Gray) },
                        trailingIcon = {
                            IconButton(onClick = { newVisible = !newVisible }) {
                                Icon(
                                    if (newVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    null,
                                    tint = Color.Gray
                                )
                            }
                        },
                        visualTransformation = if (newVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp),
                        colors = fieldColors, singleLine = true
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = state.confirmPassword,
                        onValueChange = viewModel::onConfirmPasswordChange,
                        label = { Text("Confirmar nueva contraseña", color = Color.Gray) },
                        leadingIcon = { Icon(Icons.Default.Lock, null, tint = Color.Gray) },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp),
                        colors = fieldColors, singleLine = true
                    )

                    if (state.errorMessage.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(state.errorMessage, color = Color.Red, fontSize = 12.sp)
                    }
                    if (state.successMessage.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(state.successMessage, color = Color.Green, fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { viewModel.changePassword() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(4.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Green),
                        enabled = state.currentPassword.isNotBlank() &&
                                state.newPassword.isNotBlank() &&
                                state.confirmPassword.isNotBlank() &&
                                !state.isLoading
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(
                                color = Color.Black,
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Text(
                                "ACTUALIZAR CONTRASEÑA",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            HorizontalDivider(color = Color(0xFF222222))

            // ══════════════════════════════════════════════════════
            // ── SECCIÓN: Eliminar cuenta (zona de peligro) ──
            // ══════════════════════════════════════════════════════
            Column {
                Text(
                    "ZONA DE PELIGRO",
                    color = Color.Red,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "Al eliminar tu cuenta, perderás acceso permanente a tu historial de pedidos, direcciones, métodos de pago y favoritos. Esta acción no se puede deshacer.",
                    color = Color.Gray, fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(
                    onClick = { viewModel.openDeleteDialog() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(4.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.Red)
                ) {
                    Icon(
                        Icons.Default.DeleteForever,
                        null,
                        tint = Color.Red,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("ELIMINAR MI CUENTA", color = Color.Red, fontSize = 13.sp)
                }
            }
        }
    }

    // ── Diálogo de confirmación para eliminar cuenta ──────────────
    if (state.showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.closeDeleteDialog() },
            containerColor = Color(0xFF111111),
            icon = { Icon(Icons.Default.Warning, null, tint = Color.Red) },
            title = {
                Text(
                    "¿Eliminar tu cuenta?",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        "Esta acción es permanente y no se puede deshacer.",
                        color = Color.Gray, fontSize = 13.sp
                    )
                    if (state.isEmailUser) {
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = state.deletePassword,
                            onValueChange = viewModel::onDeletePasswordChange,
                            label = { Text("Confirma tu contraseña", color = Color.Gray) },
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Red,
                                unfocusedBorderColor = Color.DarkGray,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                cursorColor = Color.Red
                            ),
                            singleLine = true
                        )
                    }
                    if (state.deleteError.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(state.deleteError, color = Color.Red, fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.confirmDeleteAccount() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    enabled = (!state.isEmailUser || state.deletePassword.isNotBlank()) && !state.isDeletingAccount
                ) {
                    if (state.isDeletingAccount) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    } else {
                        Text("Eliminar", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.closeDeleteDialog() }) {
                    Text("Cancelar", color = Color.Gray)
                }
            }
        )
    }
}