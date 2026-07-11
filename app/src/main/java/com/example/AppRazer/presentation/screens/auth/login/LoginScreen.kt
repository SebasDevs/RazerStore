package com.example.AppRazer.presentation.screens.auth.login

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.AppRazer.R
import com.example.AppRazer.presentation.navigation.Screen
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    // ── Navegar cuando el login sea exitoso ────────────────────────
    LaunchedEffect(state.loginSuccess) {
        if (state.loginSuccess) {
            navController.navigate(Screen.Inicio.route) {
                popUpTo(Screen.Login.route) { inclusive = true }
            }
        }
    }

    // ── Google Sign-In launcher ─────────────────────────────────────
    val googleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                viewModel.loginWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                viewModel.setGoogleError("Error Google Sign-In: ${e.message}")
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
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
        Text(
            text = "Iniciar sesión",
            style = TextStyle(fontSize = 24.sp, color = Color.White, fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedButton(
            onClick = { googleLauncher.launch(viewModel.getGoogleSignInIntent()) },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(4.dp),
            border = BorderStroke(1.dp, Color.White),
            colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.Black)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_google),
                    contentDescription = "Google",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Continuar con Google", color = Color.White,
                    fontSize = 14.sp, fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            HorizontalDivider(modifier = Modifier.weight(1f), color = Color.DarkGray)
            Text(text = "  o  ", color = Color.Gray, fontSize = 12.sp)
            HorizontalDivider(modifier = Modifier.weight(1f), color = Color.DarkGray)
        }

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = state.email,
            onValueChange = viewModel::onEmailChange,
            label = { Text("Email", color = Color.Gray) },
            leadingIcon = {
                Icon(
                    Icons.Default.Email,
                    contentDescription = null,
                    tint = Color.Gray
                )
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

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = state.password,
            onValueChange = viewModel::onPasswordChange,
            label = { Text("Contraseña", color = Color.Gray) },
            leadingIcon = {
                Icon(
                    Icons.Default.Lock,
                    contentDescription = null,
                    tint = Color.Gray
                )
            },
            trailingIcon = {
                IconButton(onClick = viewModel::togglePasswordVisibility) {
                    Icon(
                        imageVector = if (state.passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = null, tint = Color.Gray
                    )
                }
            },
            visualTransformation = if (state.passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(4.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Green,
                unfocusedBorderColor = Color.DarkGray,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = Color.Green
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "¿Olvidaste tu contraseña?",
            color = Color.Green, fontSize = 13.sp,
            modifier = Modifier
                .align(Alignment.End)
                .clickable { navController.navigate(Screen.Forgot.route) }
        )

        if (state.errorMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = state.errorMessage, color = Color.Red, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = viewModel::loginWithEmail,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(4.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Green),
            enabled = state.email.isNotEmpty() && state.password.isNotEmpty() && !state.isLoading
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(20.dp))
            } else {
                Text(
                    text = "INICIAR SESIÓN", color = Color.Black,
                    fontSize = 14.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
            Text("¿No tienes cuenta? ", color = Color.Gray, fontSize = 13.sp)
            Text(
                text = "Regístrate",
                color = Color.Green, fontSize = 13.sp, fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { navController.navigate(Screen.Register.route) }
            )
        }
    }
}