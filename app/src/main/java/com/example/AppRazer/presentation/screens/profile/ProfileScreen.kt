package com.example.AppRazer.presentation.screens.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.AppRazer.presentation.navigation.Screen

@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    // ── Navegar cuando se cierre sesión ────────────────────────────
    LaunchedEffect(state.loggedOut) {
        if (state.loggedOut) {
            navController.navigate(Screen.Login.route) {
                popUpTo(Screen.Inicio.route) { inclusive = true }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
    ) {
        // ── Header con gradiente ──────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF1A1A1A), Color.Black)
                    )
                )
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.TopStart)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    tint = Color.White
                )
            }

            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1A1A1A))
                        .border(2.dp, Color.Green, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (state.user?.photoUrl != null) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(state.user?.photoUrl).crossfade(true).build(),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            Icons.Default.Person, contentDescription = null,
                            tint = Color.Green, modifier = Modifier.size(50.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = state.user?.displayName ?: "Gamer",
                    style = TextStyle(
                        fontSize = 22.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = state.user?.email ?: "",
                    style = TextStyle(fontSize = 14.sp, color = Color.Gray)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .background(Color(0xFF1A1A1A), RoundedCornerShape(20.dp))
                        .border(1.dp, Color.Green, RoundedCornerShape(20.dp))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text("⚡ RazerStore Rewards", color = Color.Green, fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // ── Estadísticas ──────────────────────────────────────────
        AnimatedVisibility(
            visible = state.visible,
            enter = fadeIn(tween(800)) + expandVertically(tween(800))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatCard("${state.orderCount}", "Pedidos")
                StatCard("0", "Puntos")
                StatCard("0", "Favoritos")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── Opciones del perfil ───────────────────────────────────
        AnimatedVisibility(
            visible = state.visible,
            enter = fadeIn(tween(1000)) + slideInVertically(
                initialOffsetY = { 60 },
                animationSpec = tween(1000)
            )
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    "MI CUENTA", color = Color.Gray, fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                ProfileOption(
                    icon = Icons.Default.ShoppingCart, title = "Mis pedidos",
                    subtitle = "Ver historial de compras"
                ) {
                    navController.navigate(Screen.Orders.route)
                }
                ProfileOption(
                    icon = Icons.Default.Favorite, title = "Favoritos",
                    subtitle = "Productos guardados"
                ) {
                    navController.navigate(Screen.Wishlist.route)
                }
                ProfileOption(
                    icon = Icons.Default.LocationOn, title = "Mis direcciones",
                    subtitle = "Gestionar direcciones de envío"
                ) { navController.navigate(Screen.Addresses.route) }
                ProfileOption(
                    icon = Icons.Default.CreditCard, title = "Métodos de pago",
                    subtitle = "Tarjetas guardadas"
                ) { navController.navigate(Screen.PaymentMethods.route) }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    "CONFIGURACIÓN", color = Color.Gray, fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                ProfileOption(
                    icon = Icons.Default.Notifications, title = "Notificaciones",
                    subtitle = "Gestionar alertas"
                ) { navController.navigate(Screen.Notifications.route) }
                ProfileOption(
                    icon = Icons.Default.Security, title = "Seguridad",
                    subtitle = "Contraseña y privacidad"
                ) { navController.navigate(Screen.Security.route) }
                ProfileOption(
                    icon = Icons.Default.Language, title = "Idioma y región",
                    subtitle = "Español - España"
                ) { navController.navigate(Screen.Settings.route) }

                Spacer(modifier = Modifier.height(24.dp))

                // ── Botón cerrar sesión ───────────────────────────
                var logoutScale by remember { mutableStateOf(1f) }
                val animatedScale by animateFloatAsState(
                    targetValue = logoutScale,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                    label = "logout"
                )

                OutlinedButton(
                    onClick = {
                        logoutScale = 0.95f
                        viewModel.logout()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .scale(animatedScale),
                    shape = RoundedCornerShape(4.dp),
                    border = BorderStroke(1.dp, Color.Red),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.Transparent)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = null,
                        tint = Color.Red
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Cerrar sesión", color = Color.Red, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    "FOR GAMERS. BY GAMERS.", color = Color(0xFF333333),
                    fontSize = 11.sp, modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun StatCard(value: String, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(Color(0xFF111111), RoundedCornerShape(8.dp))
            .padding(horizontal = 24.dp, vertical = 12.dp)
    ) {
        Text(value, color = Color.Green, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text(label, color = Color.Gray, fontSize = 12.sp)
    }
}

@Composable
fun ProfileOption(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    var pressed by remember { mutableStateOf(false) }
    val bgColor by animateColorAsState(
        targetValue = if (pressed) Color(0xFF1A1A1A) else Color.Transparent,
        animationSpec = tween(150),
        label = "bg"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor, RoundedCornerShape(8.dp))
            .clickable {
                pressed = true
                onClick()
            }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(Color(0xFF1A1A1A), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = Color.Green,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Medium)
            Text(subtitle, color = Color.Gray, fontSize = 12.sp)
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
    }

    HorizontalDivider(color = Color(0xFF111111), thickness = 1.dp)
}