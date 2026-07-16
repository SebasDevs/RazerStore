package com.example.AppRazer.presentation.screens.product

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Scale
import com.example.AppRazer.data.model.ProductRepository
import com.example.AppRazer.presentation.navigation.Screen
import com.example.AppRazer.presentation.screens.cart.CartState
import com.example.AppRazer.presentation.screens.home.ProductInfo
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    navController: NavController,
    productId: String,
    viewModel: ProductDetailViewModel = hiltViewModel()
) {
    val product = ProductRepository.getById(productId)
    val relatedProducts = ProductRepository.getRelated(productId)
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val state by viewModel.uiState.collectAsState()
    val haptic = LocalHapticFeedback.current
    var priceExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(state.navigateToCart) {
        if (state.navigateToCart) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            navController.navigate(Screen.Cart.route)
            viewModel.onNavigatedToCart()
        }
    }

    // ── Galería ───────────────────────────────────────────────────
    val images = listOf(
        product?.imageUrl ?: "",
        product?.imageUrl ?: "",
        product?.imageUrl ?: ""
    )
    val pagerState = rememberPagerState { images.size }

    if (product == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Text("Producto no encontrado", color = Color.White)
        }
        return
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp)
        ) {

            // ── TopBar ────────────────────────────────────────────
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black)
                        .statusBarsPadding()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                    }
                    Box {
                        Icon(
                            Icons.Default.ShoppingCart, null, tint = Color.White,
                            modifier = Modifier.clickable { navController.navigate(Screen.Cart.route) }
                        )
                        if (CartState.itemCount > 0) {
                            Badge(
                                modifier = Modifier.align(Alignment.TopEnd),
                                containerColor = Color.Green,
                                contentColor = Color.Black
                            ) {
                                Text("${CartState.itemCount}", fontSize = 10.sp)
                            }
                        }
                    }
                }
            }

            // ── Badge NUEVO ───────────────────────────────────────
            item {
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Box(
                        modifier = Modifier
                            .background(Color.Green, RoundedCornerShape(4.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            "NUEVO",
                            color = Color.Black,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // ── Galería ───────────────────────────────────────────
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                ) {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize()
                    ) { page ->
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(images[page]).crossfade(true).scale(Scale.FILL).build(),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    }
                    IconButton(
                        onClick = {
                            scope.launch {
                                if (pagerState.currentPage > 0)
                                    pagerState.animateScrollToPage(pagerState.currentPage - 1)
                            }
                        },
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .background(Color(0x80000000), RoundedCornerShape(50))
                    ) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, null, tint = Color.White)
                    }
                    IconButton(
                        onClick = {
                            scope.launch {
                                if (pagerState.currentPage < images.size - 1)
                                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        },
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .background(Color(0x80000000), RoundedCornerShape(50))
                    ) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = Color.White)
                    }
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        repeat(images.size) { i ->
                            Box(
                                modifier = Modifier
                                    .padding(3.dp)
                                    .size(8.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(if (pagerState.currentPage == i) Color.White else Color.Gray)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Ver Galería", color = Color.Green, fontSize = 13.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clickable { },
                    textAlign = TextAlign.Center
                )
            }

            // ── Info del producto ─────────────────────────────────
            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        product.title,
                        style = TextStyle(
                            fontSize = 22.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(product.subtitle, style = TextStyle(fontSize = 14.sp, color = Color.Gray))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Más información >", color = Color.Green, fontSize = 13.sp,
                        modifier = Modifier.clickable { })
                }
            }

            // ── Selector de color ─────────────────────────────────
            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Color / Diseño",
                        style = TextStyle(
                            fontSize = 16.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        listOf("Negro", "Mercury").forEach { color ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .border(
                                            2.dp,
                                            if (state.selectedColor == color) Color.Green else Color.DarkGray,
                                            RoundedCornerShape(4.dp)
                                        )
                                        .background(
                                            if (color == "Negro") Color(0xFF1A1A1A) else Color(
                                                0xFFE0E0E0
                                            ),
                                            RoundedCornerShape(4.dp)
                                        )
                                        .clickable { viewModel.selectColor(color) }
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    color,
                                    color = if (state.selectedColor == color) Color.Green else Color.Gray,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
                HorizontalDivider(color = Color(0xFF222222))
            }

            // ── Especificaciones ──────────────────────────────────
            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Características principales",
                        style = TextStyle(
                            fontSize = 16.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    listOf(
                        "✓  ${product.subtitle.replace("\n", " ")}",
                        "✓  Razer Chroma RGB",
                        "✓  Garantía 2 años",
                        "✓  Envío gratis"
                    ).forEach { spec ->
                        Text(
                            spec, color = Color.Gray, fontSize = 14.sp,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Ver Todas Las Especificaciones", color = Color.Green, fontSize = 13.sp,
                        modifier = Modifier.clickable { })
                }
                HorizontalDivider(color = Color(0xFF222222))
            }

            // ── Klarna ────────────────────────────────────────────
            item {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFFFB3C7), RoundedCornerShape(4.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            "Klarna",
                            color = Color.Black,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "3 plazos de ${"%.2f".format(product.priceValue / 3)} € sin intereses (0% TAE)",
                        color = Color.Gray, fontSize = 13.sp
                    )
                }
                HorizontalDivider(color = Color(0xFF222222))
            }

            // ── Productos relacionados ────────────────────────────
            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "MÁS PRODUCTOS QUE TE GUSTARÁN",
                        style = TextStyle(
                            fontSize = 16.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(relatedProducts) { related ->
                            RelatedProductCard(related, navController)
                        }
                    }
                }
            }
        }

        // ── Barra precio fija abajo ───────────────────────────────
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color(0xFF111111))
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        product.price.replace("\n", " "),
                        style = TextStyle(
                            fontSize = 20.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        ),
                        maxLines = if (priceExpanded) 2 else 1,
                        overflow = if (priceExpanded) TextOverflow.Visible else TextOverflow.Ellipsis
                    )
                    Text(
                        if (priceExpanded) "Ver menos ∧" else "Ver más ∨",
                        color = Color.Gray,
                        fontSize = 12.sp,
                        modifier = Modifier.clickable { priceExpanded = !priceExpanded }
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Button(
                    onClick = {
                        viewModel.addToCart(
                            product.id,
                            product.title,
                            product.priceValue,
                            product.imageUrl
                        )
                    },
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (state.addedToCart) Color(0xFF00AA00) else Color.Green
                    ),
                    enabled = !state.isAddingToCart
                ) {
                    if (state.isAddingToCart) {
                        CircularProgressIndicator(
                            color = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Text(
                            text = if (state.addedToCart) "✓ EN EL CARRITO" else "AÑADIR AL CARRITO",
                            color = Color.Black, fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

// ── Tarjeta producto relacionado ──────────────────────────────────
@Composable
fun RelatedProductCard(
    product: ProductInfo,
    navController: NavController,
    wishlistViewModel: com.example.AppRazer.presentation.screens.wishlist.WishlistViewModel = hiltViewModel()
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF111111)),
        modifier = Modifier
            .width(160.dp)
            .clickable {
                navController.navigate(Screen.ProductDetail.createRoute(product.id))
            }
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Box {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(product.imageUrl).crossfade(true).build(),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    contentScale = ContentScale.Crop
                )
                com.example.AppRazer.presentation.components.HeartButton(
                    isFavorite = com.example.AppRazer.presentation.screens.wishlist.WishlistState.isFavorite(
                        product.id
                    ),
                    onClick = {
                        wishlistViewModel.toggleFavorite(
                            com.example.AppRazer.data.remote.firebase.firestore.WishlistItem(
                                id = product.id,
                                name = product.title,
                                price = product.price,
                                priceValue = product.priceValue,
                                imageUrl = product.imageUrl
                            )
                        )
                    },
                    modifier = Modifier
                        .padding(4.dp)
                        .align(Alignment.TopEnd)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                product.title, color = Color.White, fontSize = 12.sp,
                fontWeight = FontWeight.Bold, maxLines = 2
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(product.price.replace("\n", " "), color = Color.Green, fontSize = 12.sp)
        }
    }
}