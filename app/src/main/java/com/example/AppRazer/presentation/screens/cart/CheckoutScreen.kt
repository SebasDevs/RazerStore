package com.example.AppRazer.presentation.screens.cart

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
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
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.AppRazer.domain.model.detectCardBrand
import com.example.AppRazer.presentation.navigation.Screen

@Composable
fun CheckoutScreen(
    navController: NavController,
    viewModel: CheckoutViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val total = CartState.total

    if (state.paymentDone) {
        // ── Pantalla de éxito ─────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                val scale by animateFloatAsState(
                    targetValue = if (state.paymentDone) 1f else 0f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                    label = "success"
                )
                Text("✅", fontSize = 80.sp, modifier = Modifier.scale(scale))
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    "¡Pedido confirmado!",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("Recibirás un email con los detalles", color = Color.Gray, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = {
                        navController.navigate(Screen.Inicio.route) {
                            popUpTo(Screen.Inicio.route) { inclusive = true }
                        }
                    },
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Green),
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(52.dp)
                ) {
                    Text("VOLVER A LA TIENDA", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // ── TopBar ────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black)
                .statusBarsPadding()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.White)
            }
            Text("Último paso", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Divider(color = Color(0xFF222222))

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // ── Título ────────────────────────────────────────────
            AnimatedVisibility(
                visible = state.visible,
                enter = fadeIn(tween(500)) + slideInVertically(tween(500))
            ) {
                Text(
                    "¿Cómo quieres pagar?",
                    style = TextStyle(
                        fontSize = 22.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Métodos de pago ───────────────────────────────────
            AnimatedVisibility(
                visible = state.visible,
                enter = fadeIn(tween(700)) + expandVertically(tween(700))
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                    PaymentMethodCard(
                        icon = Icons.Default.CreditCard,
                        title = "Tarjeta de crédito / débito",
                        subtitle = "Visa, Mastercard, Amex",
                        selected = state.selectedMethod == "card",
                        onClick = { viewModel.selectMethod("card") }
                    )

                    // ── Formulario tarjeta ────────────────────────
                    AnimatedVisibility(
                        visible = state.showCardForm && state.selectedMethod == "card",
                        enter = expandVertically(tween(400)) + fadeIn(tween(400)),
                        exit = shrinkVertically(tween(300)) + fadeOut(tween(300))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF111111), RoundedCornerShape(8.dp))
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            CardPreviewFlip(
                                cardNumber = state.cardNumber,
                                cardName = state.cardName,
                                cardExpiry = state.cardExpiry,
                                cardCvv = state.cardCvv,
                                isCvvFocused = state.cvvFocused
                            )

                            OutlinedTextField(
                                value = state.cardNumber,
                                onValueChange = viewModel::onCardNumberChange,
                                label = { Text("Número de tarjeta", color = Color.Gray) },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.CreditCard,
                                        null,
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
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = state.cardName,
                                onValueChange = viewModel::onCardNameChange,
                                label = { Text("Nombre en la tarjeta", color = Color.Gray) },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Person,
                                        null,
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
                                singleLine = true
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                OutlinedTextField(
                                    value = state.cardExpiry,
                                    onValueChange = viewModel::onCardExpiryChange,
                                    label = { Text("MM/AA", color = Color.Gray) },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(4.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color.Green,
                                        unfocusedBorderColor = Color.DarkGray,
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        cursorColor = Color.Green
                                    ),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = state.cardCvv,
                                    onValueChange = viewModel::onCardCvvChange,
                                    label = { Text("CVV", color = Color.Gray) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .onFocusChanged { focusState ->
                                            viewModel.onCvvFocusChanged(
                                                focusState.isFocused
                                            )
                                        },
                                    shape = RoundedCornerShape(4.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color.Green,
                                        unfocusedBorderColor = Color.DarkGray,
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        cursorColor = Color.Green
                                    ),
                                    singleLine = true
                                )
                            }
                        }
                    }

                    PaymentMethodCard(
                        icon = Icons.Default.AccountBalanceWallet,
                        title = "PayPal",
                        subtitle = "Paga con tu cuenta PayPal",
                        selected = state.selectedMethod == "paypal",
                        onClick = { viewModel.selectMethod("paypal") }
                    )

                    PaymentMethodCard(
                        icon = Icons.Default.Phone,
                        title = "Klarna",
                        subtitle = "3 plazos sin intereses",
                        selected = state.selectedMethod == "klarna",
                        onClick = { viewModel.selectMethod("klarna") }
                    )

                    PaymentMethodCard(
                        icon = Icons.Default.Payment,
                        title = "Google Pay",
                        subtitle = "Pago rápido con Google",
                        selected = state.selectedMethod == "gpay",
                        onClick = { viewModel.selectMethod("gpay") }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Cupón ─────────────────────────────────────────────
            AnimatedVisibility(visible = state.visible, enter = fadeIn(tween(900))) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF111111), RoundedCornerShape(8.dp))
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.LocalOffer,
                        null,
                        tint = Color.Green,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    OutlinedTextField(
                        value = state.coupon,
                        onValueChange = viewModel::onCouponChange,
                        placeholder = {
                            Text(
                                "Código de descuento",
                                color = Color.Gray,
                                fontSize = 13.sp
                            )
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(4.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Green,
                            unfocusedBorderColor = Color.DarkGray,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = Color.Green
                        ),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(onClick = viewModel::applyCoupon) {
                        Text(if (state.couponApplied) "✓" else "Aplicar", color = Color.Green)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Datos de entrega ──────────────────────────────────
            AnimatedVisibility(
                visible = state.visible,
                enter = fadeIn(tween(1100)) + slideInVertically(
                    initialOffsetY = { 40 }, animationSpec = tween(1100)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF111111), RoundedCornerShape(8.dp))
                        .padding(16.dp)
                ) {
                    Text(
                        "Datos de entrega",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.LocalShipping,
                            null,
                            tint = Color.Green,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                "Envío estándar",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text("3-5 días hábiles", color = Color.Gray, fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            "Gratis",
                            color = Color.Green,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Divider(color = Color(0xFF222222))
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.LocationOn,
                            null,
                            tint = Color.Green,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Dirección de envío",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text("Añadir dirección", color = Color.Gray, fontSize = 12.sp)
                        }
                        Text(
                            "Cambiar",
                            color = Color.Green,
                            fontSize = 13.sp,
                            modifier = Modifier.clickable { })
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Resumen ───────────────────────────────────────────
            AnimatedVisibility(visible = state.visible, enter = fadeIn(tween(1200))) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF111111), RoundedCornerShape(8.dp))
                        .padding(16.dp)
                ) {
                    Text(
                        "Resumen del pedido",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Subtotal", color = Color.Gray, fontSize = 14.sp)
                        Text("%.2f €".format(total), color = Color.White, fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Envío", color = Color.Gray, fontSize = 14.sp)
                        Text(
                            "Gratis",
                            color = Color.Green,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("IVA (21%)", color = Color.Gray, fontSize = 14.sp)
                        Text("%.2f €".format(total * 0.21), color = Color.White, fontSize = 14.sp)
                    }
                    Divider(
                        color = Color(0xFF333333),
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Total",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "%.2f €".format(total * 1.21),
                            color = Color.Green,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // ── Barra pagar fija abajo ────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF111111))
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Total", color = Color.Gray, fontSize = 12.sp)
                    Text(
                        "%.2f €".format(total * 1.21),
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Button(
                    onClick = { viewModel.pay(total * 1.21) },
                    modifier = Modifier.height(52.dp),
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (state.selectedMethod.isEmpty()) Color.DarkGray else Color.Green
                    ),
                    enabled = state.selectedMethod.isNotEmpty() && !state.isProcessing
                ) {
                    if (state.isProcessing) {
                        CircularProgressIndicator(
                            color = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Procesando...", color = Color.Black, fontWeight = FontWeight.Bold)
                    } else {
                        Text(
                            "PAGAR",
                            color = Color.Black,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PaymentMethodCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val borderColor by animateColorAsState(
        targetValue = if (selected) Color.Green else Color(0xFF333333),
        animationSpec = tween(300),
        label = "border"
    )
    val bgColor by animateColorAsState(
        targetValue = if (selected) Color(0xFF0A1A0A) else Color(0xFF111111),
        animationSpec = tween(300),
        label = "bg"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor, RoundedCornerShape(8.dp))
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(Color(0xFF1A1A1A), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon, contentDescription = null, tint = if (selected) Color.Green else Color.Gray,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Medium)
            Text(subtitle, color = Color.Gray, fontSize = 12.sp)
        }
        Box(
            modifier = Modifier
                .size(20.dp)
                .border(2.dp, if (selected) Color.Green else Color.Gray, CircleShape)
                .padding(3.dp)
                .background(if (selected) Color.Green else Color.Transparent, CircleShape)
        )
    }
}

// ── Tarjeta con flip 3D + detección de marca ──────────────────────
@Composable
fun CardPreviewFlip(
    cardNumber: String,
    cardName: String,
    cardExpiry: String,
    cardCvv: String,
    isCvvFocused: Boolean
) {
    val brand = remember(cardNumber) { detectCardBrand(cardNumber) }

    val rotation by animateFloatAsState(
        targetValue = if (isCvvFocused) 180f else 0f,
        animationSpec = tween(500),
        label = "cardFlip"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
            }
    ) {
        if (rotation <= 90f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        androidx.compose.ui.graphics.Brush.horizontalGradient(
                            listOf(Color(0xFF1A1A2E), Color(0xFF16213E))
                        ),
                        RoundedCornerShape(12.dp)
                    )
                    .padding(16.dp)
            ) {
                Text(
                    text = brand.label,
                    color = Color.Green, fontSize = 14.sp, fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.TopEnd)
                )
                Text(
                    text = if (cardNumber.isEmpty()) "•••• •••• •••• ••••"
                    else cardNumber.chunked(4).joinToString(" "),
                    color = Color.White, fontSize = 18.sp,
                    modifier = Modifier.align(Alignment.BottomStart)
                )
                Text(
                    text = if (cardName.isEmpty()) "NOMBRE APELLIDO" else cardName.uppercase(),
                    color = Color.Gray, fontSize = 12.sp,
                    modifier = Modifier.align(Alignment.TopStart)
                )
                Text(
                    text = if (cardExpiry.isEmpty()) "MM/AA" else cardExpiry,
                    color = Color.Gray, fontSize = 12.sp,
                    modifier = Modifier.align(Alignment.BottomEnd)
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { rotationY = 180f }
                    .background(
                        androidx.compose.ui.graphics.Brush.horizontalGradient(
                            listOf(Color(0xFF16213E), Color(0xFF1A1A2E))
                        ),
                        RoundedCornerShape(12.dp)
                    )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(30.dp)
                        .background(Color.Black)
                        .align(Alignment.TopCenter)
                        .padding(top = 16.dp)
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 16.dp)
                        .width(50.dp)
                        .height(24.dp)
                        .background(Color.White, RoundedCornerShape(2.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (cardCvv.isEmpty()) "•••" else cardCvv,
                        color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}