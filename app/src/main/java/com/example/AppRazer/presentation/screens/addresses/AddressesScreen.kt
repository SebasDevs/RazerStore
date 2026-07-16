package com.example.AppRazer.presentation.screens.addresses

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.AppRazer.data.remote.firebase.firestore.Address
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressesScreen(
    navController: NavController,
    viewModel: AddressesViewModel = hiltViewModel()
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
            title = { Text("Mis direcciones", color = Color.White, fontWeight = FontWeight.Bold) },
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
        } else if (state.addresses.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Default.LocationOn, null, tint = Color.Gray, modifier = Modifier)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "No tienes direcciones guardadas",
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
                    Text("AÑADIR DIRECCIÓN", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.addresses, key = { it.id }) { address ->
                    AddressCard(
                        address = address,
                        onEdit = { viewModel.openEditForm(address) },
                        onDelete = { viewModel.deleteAddress(address.id) }
                    )
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
            AddressFormContent(
                initial = state.editingAddress,
                onSave = { viewModel.saveAddress(it) },
                onCancel = { viewModel.closeForm() }
            )
        }
    }
}

@Composable
fun AddressCard(address: Address, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF111111)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        address.label.ifEmpty { "Dirección" },
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (address.isDefault) {
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
                Row {
                    IconButton(onClick = onEdit) {
                        Icon(
                            Icons.Default.Edit,
                            null,
                            tint = Color.Gray
                        )
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            Icons.Default.Delete,
                            null,
                            tint = Color.Red
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text("${address.street}, ${address.city}", color = Color.Gray, fontSize = 13.sp)
            Text("${address.province}, ${address.zipCode}", color = Color.Gray, fontSize = 13.sp)
            if (address.phone.isNotEmpty()) {
                Text(address.phone, color = Color.Gray, fontSize = 13.sp)
            }
        }
    }
}

@Composable
fun AddressFormContent(
    initial: Address?,
    onSave: (Address) -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var label by remember { mutableStateOf(initial?.label ?: "") }
    var street by remember { mutableStateOf(initial?.street ?: "") }
    var city by remember { mutableStateOf(initial?.city ?: "") }
    var province by remember { mutableStateOf(initial?.province ?: "") }
    var zipCode by remember { mutableStateOf(initial?.zipCode ?: "") }
    var phone by remember { mutableStateOf(initial?.phone ?: "") }
    var isDefault by remember { mutableStateOf(initial?.isDefault ?: false) }

    var latitude by remember { mutableStateOf(initial?.latitude ?: -12.0464) } // Lima por defecto
    var longitude by remember { mutableStateOf(initial?.longitude ?: -77.0428) }
    var isLocating by remember { mutableStateOf(false) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            scope.launch {
                isLocating = true
                val location =
                    com.example.AppRazer.presentation.components.LocationHelper.getCurrentLocation(
                        context
                    )
                if (location != null) {
                    latitude = location.latitude
                    longitude = location.longitude
                    val geocoded = com.example.AppRazer.presentation.components.LocationHelper
                        .reverseGeocode(context, latitude, longitude)
                    if (geocoded != null) {
                        street = geocoded.street
                        city = geocoded.city
                        province = geocoded.province
                        zipCode = geocoded.zipCode
                    }
                }
                isLocating = false
            }
        }
    }

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
        Text(
            if (initial == null) "Nueva dirección" else "Editar dirección",
            color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold
        )

        // ── Mapa interactivo ───────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .border(1.dp, Color(0xFF222222), RoundedCornerShape(8.dp))
        ) {
            com.example.AppRazer.presentation.components.LocationPickerMap(
                latitude = latitude,
                longitude = longitude,
                onLocationChanged = { lat, lng ->
                    latitude = lat
                    longitude = lng
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

        Text(
            "Mueve el mapa para ajustar el punto exacto",
            color = Color.Gray, fontSize = 11.sp
        )

        OutlinedButton(
            onClick = {
                locationPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(4.dp),
            border = BorderStroke(1.dp, Color.Green),
            enabled = !isLocating
        ) {
            if (isLocating) {
                CircularProgressIndicator(color = Color.Green, modifier = Modifier.size(16.dp))
            } else {
                Icon(
                    Icons.Default.MyLocation,
                    null,
                    tint = Color.Green,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("USAR MI UBICACIÓN ACTUAL", color = Color.Green, fontSize = 13.sp)
            }
        }

        HorizontalDivider(color = Color(0xFF222222))

        OutlinedTextField(
            value = label, onValueChange = { label = it },
            label = { Text("Etiqueta (Casa, Trabajo...)", color = Color.Gray) },
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp),
            colors = fieldColors, singleLine = true
        )
        OutlinedTextField(
            value = street,
            onValueChange = { street = it },
            label = { Text("Calle y número", color = Color.Gray) },
            trailingIcon = {
                IconButton(onClick = {
                    val query = "$street, $city, $province"
                    val location = com.example.AppRazer.presentation.components.LocationHelper
                        .forwardGeocode(context, query)
                    if (location != null) {
                        latitude = location.latitude
                        longitude = location.longitude
                    }
                }) {
                    Icon(Icons.Default.Search, null, tint = Color.Gray)
                }
            },
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp),
            colors = fieldColors, singleLine = true
        )
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = city, onValueChange = { city = it },
                label = { Text("Ciudad", color = Color.Gray) },
                modifier = Modifier.weight(1f), shape = RoundedCornerShape(8.dp),
                colors = fieldColors, singleLine = true
            )
            OutlinedTextField(
                value = zipCode, onValueChange = { zipCode = it },
                label = { Text("Código postal", color = Color.Gray) },
                modifier = Modifier.weight(1f), shape = RoundedCornerShape(8.dp),
                colors = fieldColors, singleLine = true
            )
        }
        OutlinedTextField(
            value = province, onValueChange = { province = it },
            label = { Text("Provincia / Región", color = Color.Gray) },
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp),
            colors = fieldColors, singleLine = true
        )
        OutlinedTextField(
            value = phone, onValueChange = { phone = it },
            label = { Text("Teléfono de contacto", color = Color.Gray) },
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp),
            colors = fieldColors, singleLine = true
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { isDefault = !isDefault }
        ) {
            Checkbox(
                checked = isDefault,
                onCheckedChange = { isDefault = it },
                colors = CheckboxDefaults.colors(checkedColor = Color.Green)
            )
            Text("Establecer como predeterminada", color = Color.White, fontSize = 13.sp)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                onClick = onCancel,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
            ) {
                Text("Cancelar", color = Color.White)
            }
            Button(
                onClick = {
                    onSave(
                        Address(
                            id = initial?.id ?: "",
                            label = label, street = street, city = city,
                            province = province, zipCode = zipCode, phone = phone,
                            isDefault = isDefault,
                            latitude = latitude, longitude = longitude
                        )
                    )
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Green),
                enabled = street.isNotBlank() && city.isNotBlank()
            ) {
                Text("Guardar", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}