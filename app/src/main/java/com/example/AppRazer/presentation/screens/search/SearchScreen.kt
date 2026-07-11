package com.example.AppRazer.presentation.screens.search

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.AppRazer.data.model.ProductRepository
import com.example.AppRazer.presentation.navigation.Screen
import com.example.AppRazer.presentation.screens.home.ProductInfo
import kotlinx.coroutines.delay

@Composable
fun SearchScreen(navController: NavController) {
    var query by remember { mutableStateOf("") }
    var results by remember { mutableStateOf<List<ProductInfo>>(emptyList()) }
    var recentSearches by remember { mutableStateOf(listOf("Razer Blade", "Cobra Pro", "BlackWidow")) }
    var isSearching by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    // ── Búsqueda con debounce ─────────────────────────────────────
    LaunchedEffect(query) {
        if (query.isEmpty()) {
            results = emptyList()
            isSearching = false
            return@LaunchedEffect
        }
        isSearching = true
        delay(300) // debounce
        results = ProductRepository.search(query)
        isSearching = false
    }

    // ── Focus automático al abrir ─────────────────────────────────
    LaunchedEffect(Unit) {
        delay(200)
        focusRequester.requestFocus()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // ── Barra de búsqueda ─────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF111111))
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, null, tint = Color.White)
            }

            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = {
                    Text("Buscar productos Razer...", color = Color.Gray, fontSize = 14.sp)
                },
                leadingIcon = {
                    Icon(Icons.Default.Search, null, tint = Color.Gray)
                },
                trailingIcon = {
                    AnimatedVisibility(visible = query.isNotEmpty()) {
                        IconButton(onClick = { query = "" }) {
                            Icon(Icons.Default.Close, null, tint = Color.Gray)
                        }
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(focusRequester),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Green,
                    unfocusedBorderColor = Color.DarkGray,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color.Green
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        focusManager.clearFocus()
                        if (query.isNotEmpty() && !recentSearches.contains(query)) {
                            recentSearches = listOf(query) + recentSearches.take(4)
                        }
                    }
                )
            )
        }

        Divider(color = Color(0xFF222222))

        // ── Contenido ─────────────────────────────────────────────
        when {
            // Buscando
            isSearching -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.Green)
                }
            }

            // Sin query — mostrar búsquedas recientes y categorías
            query.isEmpty() -> {
                LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {

                    // Búsquedas recientes
                    item {
                        Text("BÚSQUEDAS RECIENTES", color = Color.Gray, fontSize = 11.sp,
                            fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    items(recentSearches) { search ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { query = search }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.History, null,
                                    tint = Color.Gray, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(search, color = Color.White, fontSize = 15.sp)
                            }
                            Icon(Icons.Default.NorthWest, null,
                                tint = Color.Gray, modifier = Modifier.size(16.dp))
                        }
                        Divider(color = Color(0xFF1A1A1A))
                    }

                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                        Text("CATEGORÍAS POPULARES", color = Color.Gray, fontSize = 11.sp,
                            fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // Categorías
                    val categories = listOf(
                        "Laptops" to Icons.Default.Laptop,
                        "Ratones" to Icons.Default.Mouse,
                        "Teclados" to Icons.Default.Keyboard,
                        "Auriculares" to Icons.Default.Headphones,
                        "Sillas" to Icons.Default.Chair,
                        "Micrófonos" to Icons.Default.Mic
                    )

                    items(categories) { (name, icon) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { query = name }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(Color(0xFF1A1A1A), RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(icon, null, tint = Color.Green, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(name, color = Color.White, fontSize = 15.sp)
                            Spacer(modifier = Modifier.weight(1f))
                            Icon(Icons.Default.ChevronRight, null, tint = Color.Gray)
                        }
                        Divider(color = Color(0xFF1A1A1A))
                    }
                }
            }

            // Sin resultados
            results.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.SearchOff, null,
                            tint = Color.Gray, modifier = Modifier.size(64.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Sin resultados para \"$query\"",
                            color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Intenta con otro término",
                            color = Color.Gray, fontSize = 14.sp)
                    }
                }
            }

            // Resultados
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text("${results.size} resultado${if (results.size != 1) "s" else ""} para \"$query\"",
                            color = Color.Gray, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    items(results, key = { it.id }) { product ->
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn(tween(300)) + slideInVertically(tween(300))
                        ) {
                            SearchResultCard(product, navController)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SearchResultCard(product: ProductInfo, navController: NavController) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF111111)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navController.navigate(Screen.ProductDetail.createRoute(product.id))
            }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagen
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(product.imageUrl).crossfade(true).build(),
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF222222)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Info
            Column(modifier = Modifier.weight(1f)) {
                Text(product.title, color = Color.White, fontSize = 14.sp,
                    fontWeight = FontWeight.Bold, maxLines = 2)
                Spacer(modifier = Modifier.height(4.dp))
                Text(product.subtitle, color = Color.Gray, fontSize = 12.sp, maxLines = 1)
                Spacer(modifier = Modifier.height(8.dp))
                Text(product.price.replace("\n", " "),
                    color = Color.Green, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }

            Icon(Icons.Default.ChevronRight, null, tint = Color.Gray)
        }
    }
}