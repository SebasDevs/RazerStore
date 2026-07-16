package com.example.AppRazer.presentation.screens.products

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.AppRazer.data.remote.firebase.firestore.WishlistItem
import com.example.AppRazer.presentation.components.HeartButton
import com.example.AppRazer.presentation.navigation.Screen
import com.example.AppRazer.presentation.screens.home.ProductInfo
import com.example.AppRazer.presentation.screens.wishlist.WishlistState
import com.example.AppRazer.presentation.screens.wishlist.WishlistViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryProductsScreen(
    navController: NavController,
    viewModel: CategoryProductsViewModel = hiltViewModel(),
    wishlistViewModel: WishlistViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var showFilters by remember { mutableStateOf(false) }

    val activeFilterCount = listOfNotNull(
        state.searchQuery.takeIf { it.isNotBlank() },
        state.sortOption.takeIf { it != SortOption.NONE },
        (state.minPrice > 0f || state.maxPrice < 10000f).takeIf { it }
    ).size

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
            title = {
                Text(
                    state.category.replaceFirstChar { it.uppercase() },
                    color = Color.White, fontWeight = FontWeight.Bold
                )
            },
            actions = {
                IconButton(onClick = { showFilters = true }) {
                    BadgedBox(
                        badge = {
                            if (activeFilterCount > 0) {
                                Badge(containerColor = Color.Green, contentColor = Color.Black) {
                                    Text("$activeFilterCount")
                                }
                            }
                        }
                    ) {
                        Icon(Icons.Default.FilterList, null, tint = Color.White)
                    }
                }
            }
        )

        HorizontalDivider(color = Color(0xFF222222))

        if (state.filteredProducts.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Default.SearchOff, null, tint = Color.Gray, modifier = Modifier)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Sin resultados",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("Prueba ajustando los filtros", color = Color.Gray, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(24.dp))
                TextButton(onClick = { viewModel.clearFilters() }) {
                    Text("Limpiar filtros", color = Color.Green)
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.filteredProducts, key = { it.id }) { product ->
                    CategoryProductCard(
                        product = product,
                        onClick = { navController.navigate(Screen.ProductDetail.createRoute(product.id)) },
                        isFavorite = WishlistState.isFavorite(product.id),
                        onToggleFavorite = {
                            wishlistViewModel.toggleFavorite(
                                WishlistItem(
                                    id = product.id,
                                    name = product.title,
                                    price = product.price,
                                    priceValue = product.priceValue,
                                    imageUrl = product.imageUrl
                                )
                            )
                        }
                    )
                }
            }
        }
    }

    if (showFilters) {
        val sheetState = rememberModalBottomSheetState()
        ModalBottomSheet(
            onDismissRequest = { showFilters = false },
            sheetState = sheetState,
            containerColor = Color(0xFF111111)
        ) {
            FilterSheetContent(
                state = state,
                onSearchChange = viewModel::onSearchChange,
                onPriceRangeChange = viewModel::onPriceRangeChange,
                onSortChange = viewModel::onSortChange,
                onClear = viewModel::clearFilters,
                onApply = { showFilters = false }
            )
        }
    }
}

@Composable
fun FilterSheetContent(
    state: CategoryProductsUiState,
    onSearchChange: (String) -> Unit,
    onPriceRangeChange: (Float, Float) -> Unit,
    onSortChange: (SortOption) -> Unit,
    onClear: () -> Unit,
    onApply: () -> Unit
) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(20.dp)) {
        Text("Filtros", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(20.dp))

        // ── Búsqueda ────────────────────────────────────────────
        Text("Buscar", color = Color.Gray, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = state.searchQuery,
            onValueChange = onSearchChange,
            placeholder = { Text("Nombre del producto...", color = Color.Gray) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Green,
                unfocusedBorderColor = Color.DarkGray,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = Color.Green
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        // ── Rango de precio ───────────────────────────────────────
        Text("Precio", color = Color.Gray, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "€${state.minPrice.toInt()} — €${state.maxPrice.toInt()}",
            color = Color.White, fontSize = 14.sp
        )
        RangeSlider(
            value = state.minPrice..state.maxPrice,
            onValueChange = { range -> onPriceRangeChange(range.start, range.endInclusive) },
            valueRange = 0f..10000f,
            colors = androidx.compose.material3.SliderDefaults.colors(
                thumbColor = Color.Green,
                activeTrackColor = Color.Green,
                inactiveTrackColor = Color.DarkGray
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ── Ordenar ────────────────────────────────────────────────
        Text("Ordenar por", color = Color.Gray, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        SortOption.entries.forEach { option ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSortChange(option) },
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = state.sortOption == option,
                    onClick = { onSortChange(option) },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = Color.Green,
                        unselectedColor = Color.Gray
                    )
                )
                Text(option.label, color = Color.White, fontSize = 14.sp)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            TextButton(onClick = onClear, modifier = Modifier.weight(1f)) {
                Text("Limpiar", color = Color.Gray)
            }
            Button(
                onClick = onApply,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
            ) {
                Text("Aplicar", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun CategoryProductCard(
    product: ProductInfo,
    onClick: () -> Unit,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF111111)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column {
            Box {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(product.imageUrl).crossfade(true).build(),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                HeartButton(
                    isFavorite = isFavorite,
                    onClick = onToggleFavorite,
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.TopEnd)
                )
            }
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    product.title,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    product.price.replace("\n", " "),
                    color = Color.Green,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}