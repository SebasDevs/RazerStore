package com.example.AppRazer.presentation.screens.payment

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.AppRazer.data.remote.firebase.firestore.PaymentMethod
import com.example.AppRazer.presentation.screens.cart.CardPreviewFlip

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentMethodsScreen(
    navController: NavController,
    viewModel: PaymentMethodsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black),
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                }
            },
            title = { Text("Métodos de pago", color = Color.White, fontWeight = FontWeight.Bold) },
            actions = {
                IconButton(onClick = { viewModel.openAddForm() }) {
                    Icon(Icons.Default.Add, null, tint = Color.Green)
                }
            }
        )

        HorizontalDivider(color = Color(0xFF222222))

        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color.Green)
            }
        } else if (state.methods.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Default.CreditCard, null, tint = Color.Gray, modifier = Modifier)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "No tienes tarjetas guardadas",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { viewModel.openAddForm() },
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
                ) {
                    Text("AÑADIR TARJETA", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.methods, key = { it.id }) { method ->
                    PaymentMethodListCard(
                        method = method,
                        onDelete = { viewModel.deleteMethod(method.id) })
                }
            }
        }
    }

    if (state.showForm) {
        val sheetState = rememberModalBottomSheetState()
        ModalBottomSheet(
            onDismissRequest = { viewModel.closeForm() },
            sheetState = sheetState,
            containerColor = Color(0xFF111111)
        ) {
            PaymentMethodFormContent(state = state, viewModel = viewModel)
        }
    }
}

@Composable
fun PaymentMethodListCard(method: PaymentMethod, onDelete: () -> Unit) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF111111)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.CreditCard,
                null,
                tint = Color.Green,
                modifier = Modifier.padding(end = 12.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        method.alias,
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (method.isDefault) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .background(
                                    Color(0xFF0A2A0A),
                                    RoundedCornerShape(20.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text("Predeterminada", color = Color.Green, fontSize = 10.sp)
                        }
                    }
                }
                Text("${method.brand} •••• ${method.last4}", color = Color.Gray, fontSize = 13.sp)
                Text("Vence ${method.expiry}", color = Color.Gray, fontSize = 12.sp)
            }
            IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, null, tint = Color.Red) }
        }
    }
}

@Composable
fun PaymentMethodFormContent(
    state: PaymentMethodsUiState,
    viewModel: PaymentMethodsViewModel
) {
    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = Color.Green,
        unfocusedBorderColor = Color.DarkGray,
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White,
        cursorColor = Color.Green
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Nueva tarjeta", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)

        CardPreviewFlip(
            cardNumber = state.formCardNumber,
            cardName = state.formCardName,
            cardExpiry = state.formExpiry,
            cardCvv = state.formCvv,
            isCvvFocused = state.formCvvFocused
        )

        OutlinedTextField(
            value = state.formAlias,
            onValueChange = viewModel::onAliasChange,
            label = { Text("Alias (Mi tarjeta principal...)", color = Color.Gray) },
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp),
            colors = fieldColors, singleLine = true
        )
        OutlinedTextField(
            value = state.formCardNumber,
            onValueChange = viewModel::onCardNumberChange,
            label = { Text("Número de tarjeta", color = Color.Gray) },
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp),
            colors = fieldColors, singleLine = true
        )
        OutlinedTextField(
            value = state.formCardName,
            onValueChange = viewModel::onCardNameChange,
            label = { Text("Nombre en la tarjeta", color = Color.Gray) },
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp),
            colors = fieldColors, singleLine = true
        )
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = state.formExpiry,
                onValueChange = viewModel::onExpiryChange,
                label = { Text("MM/AA", color = Color.Gray) },
                modifier = Modifier.weight(1f), shape = RoundedCornerShape(8.dp),
                colors = fieldColors, singleLine = true
            )
            OutlinedTextField(
                value = state.formCvv,
                onValueChange = viewModel::onCvvChange,
                label = { Text("CVV", color = Color.Gray) },
                modifier = Modifier
                    .weight(1f)
                    .onFocusChanged { viewModel.onCvvFocusChanged(it.isFocused) },
                shape = RoundedCornerShape(8.dp),
                colors = fieldColors, singleLine = true
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { viewModel.onDefaultChange(!state.formIsDefault) }
        ) {
            Checkbox(
                checked = state.formIsDefault,
                onCheckedChange = viewModel::onDefaultChange,
                colors = CheckboxDefaults.colors(checkedColor = Color.Green)
            )
            Text("Establecer como predeterminada", color = Color.White, fontSize = 13.sp)
        }

        Text(
            "Solo guardamos los últimos 4 dígitos y la marca de tu tarjeta. El número completo y CVV nunca se almacenan.",
            color = Color.Gray, fontSize = 11.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                onClick = { viewModel.closeForm() },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
            ) {
                Text("Cancelar", color = Color.White)
            }
            Button(
                onClick = { viewModel.saveMethod() },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Green),
                enabled = state.formCardNumber.length >= 4 && state.formExpiry.isNotBlank()
            ) {
                Text("Guardar", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}