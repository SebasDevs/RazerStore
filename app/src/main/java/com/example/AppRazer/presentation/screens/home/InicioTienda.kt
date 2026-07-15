package com.example.AppRazer.presentation.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Scale
import com.example.AppRazer.R
import com.example.AppRazer.domain.model.CarouselItem
import com.example.AppRazer.presentation.navigation.Screen
import com.example.AppRazer.presentation.screens.cart.CartState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue


data class ProductInfo(
    val id: String,
    val title: String,
    val subtitle: String,
    val extra: String = "",
    val price: String,
    val priceValue: Double,
    val imageUrl: String
)

// ── ProductCard reutilizable con animación
@Composable
fun ProductCard(info: ProductInfo, navController: NavController) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(info.title, style = TextStyle(fontSize = 20.sp, color = Color.White))
            Spacer(modifier = Modifier.height(9.dp))
            Text(info.subtitle, style = TextStyle(fontSize = 14.sp, color = Color.Gray))
            if (info.extra.isNotEmpty()) {
                Spacer(modifier = Modifier.height(9.dp))
                Text(info.extra, style = TextStyle(fontSize = 14.sp, color = Color.White))
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(info.price, style = TextStyle(fontSize = 14.sp, color = Color.White))

        }
        Button(
            onClick = {
                navController.navigate(Screen.ProductDetail.createRoute(info.id))
            },
            shape = CutCornerShape(4.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Green),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Text("VER PRODUCTO", color = Color.Black)
        }
    }
}


@Composable
fun SectionText(text: String, color: Color, size: Int = 20) {
    Text(
        text = text,
        style = TextStyle(fontSize = size.sp, color = color),
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
    )
}


@Composable
fun PagerDots(pagerState: PagerState, count: Int) {
    val scope = rememberCoroutineScope()
    Row(
        Modifier
            .height(50.dp)
            .fillMaxWidth(), horizontalArrangement = Arrangement.Center
    ) {
        repeat(count) { i ->
            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .clip(CircleShape)
                    .size(18.dp)
                    .background(if (pagerState.currentPage == i) Color.Green else Color.White)
                    .clickable { scope.launch { pagerState.animateScrollToPage(i) } }
            )
        }
    }
}


@Composable
fun GenericCarousel(
    products: List<ProductInfo>,
    navController: NavController,
    height: Int = 650,
    paddingH: Int = 25
) {
    val pagerState = rememberPagerState { products.size }
    Column(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = paddingH.dp),
            modifier = Modifier.height(height.dp)
        ) { page ->
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF222222)),
                modifier = Modifier
                    .graphicsLayer {
                        val offset =
                            ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction).absoluteValue
                        lerp(0.75f, 1f, 1f - offset.coerceIn(0f, 1f)).also {
                            scaleX = it; scaleY = it
                        }
                        alpha = lerp(0.6f, 1f, 1f - offset.coerceIn(0f, 1f))
                    }
                    .fillMaxSize()
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(products[page].imageUrl)
                            .crossfade(true)
                            .scale(Scale.FILL)
                            .build(),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(350.dp)
                            .clickable {},
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    ProductCard(products[page], navController = navController)
                }
            }
        }
    }
}


@Composable
fun InicioTienda(navController: NavController) {
    val carouselItems = listOf(
        CarouselItem("Zona de Juego", "") { navController.navigate("gamingZone") },
        CarouselItem("Laptops", "") { navController.navigate("laptops") },
        CarouselItem("Ratones", "") { navController.navigate("Mouses") },
        CarouselItem("Teclados", "") { navController.navigate("keyboards") },
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        item {
            Cabecera(navController)
            CarouselCard(carouselItems)
            Spacer(modifier = Modifier.height(10.dp))
            SectionText("DESCUBRE EL EQUIPO DE\nGAMERS. PARA GAMERS.", Color.Green)
            SectionText(
                "Razer ratones, teclados, auriculares,\nordenadores portátiles y mucho más",
                Color.Gray
            )
            Spacer(modifier = Modifier.height(20.dp))
            SectionText("ÚLTIMOS LANZAMIENTOS", Color.White)
            SectionText("Echa un vistazo a nuestros últimos lanzamientos", Color.Gray)
            Spacer(modifier = Modifier.height(15.dp))
            CarouselCardSegundo(navController)
            Spacer(modifier = Modifier.height(30.dp))
            CarouselCardTercero()
            Spacer(modifier = Modifier.height(5.dp))
            SectionText("SOLO EN RAZER", Color.White)
            SectionText(
                "Explora productos y servicios únicos\nque solo están disponibles en nuestra tienda oficial online",
                Color.Gray
            )
            Spacer(modifier = Modifier.height(10.dp))
            CarouselCardCuarto(navController)
            Spacer(modifier = Modifier.height(20.dp))
            CarouselCardDoble()
            Spacer(modifier = Modifier.height(10.dp))
            SectionText("ÚLTIMA RONDA DE PORTÁTILES", Color.White)
            SectionText(
                "Aprovecha tu última oportunidad para hacerte\ncon una de estas potentes gráficas GeForce RTX 30 y 40",
                Color.Gray
            )
            Spacer(modifier = Modifier.height(5.dp))
            CarouselCardQuinto(navController)
            Spacer(modifier = Modifier.height(5.dp))
            SectionText("ÚLTIMA RONDA DE PERIFÉRICOS", Color.White)
            SectionText(
                "No dejes pasar la última oportunidad de\ncomprar estos clásicos",
                Color.Gray
            )
            Spacer(modifier = Modifier.height(15.dp))
            CarouselCardSexto(navController)
            Spacer(modifier = Modifier.height(20.dp))
            Foot()
        }
    }
}

// ── CarouselCard (iconos pequeños arriba)
@Composable
fun CarouselCard(carouselItems: List<CarouselItem>) {
    val sliderList = listOf(
        "https://i.ibb.co/W0s74zB/zonade.png",
        "https://i.ibb.co/rHPKGs2/laptop.png",
        "https://i.ibb.co/ZBM6xWP/mouse.png",
        "https://i.ibb.co/GpjDZwq/teclado.png",
        "https://i.ibb.co/dc25gsc/auricular.png",
        "https://i.ibb.co/bJCvzfb/camara.png",
        "https://i.ibb.co/D5TbTD9/silla.png",
        "https://i.ibb.co/025K8Tw/mando.png",
        "https://i.ibb.co/TgRnrtQ/swtch.png",
        "https://i.ibb.co/JRv069t/ropa.png"
    )
    val textList = listOf(
        "Zona de Juego", "Laptops", "Ratones", "Teclados",
        "Auriculares", "Camaras", "Sillas", "Mando", "Móvil", "Ropa y Equipo"
    )
    val pagerState = rememberPagerState(initialPage = 1) { sliderList.size }

    Column(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 120.dp),
            modifier = Modifier.height(150.dp)
        ) { page ->
            val item = if (page < carouselItems.size) carouselItems[page] else null
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Black),
                modifier = Modifier
                    .graphicsLayer {
                        val offset =
                            ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction).absoluteValue
                        lerp(0.75f, 1f, 1f - offset.coerceIn(0f, 1f)).also {
                            scaleX = it; scaleY = it
                        }
                        alpha = lerp(0.6f, 1f, 1f - offset.coerceIn(0f, 1f))
                    }
                    .aspectRatio(1f)
                    .height(250.dp)
                    .width(160.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(sliderList[page]).crossfade(true).scale(Scale.FILL).build(),
                        contentDescription = null,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { item?.onClickAction?.invoke() }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        textList[page], color = Color.White,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(bottom = 8.dp)
                    )
                }
            }
        }
    }
}

// ── CarouselCardSegundo
@Composable
fun CarouselCardSegundo(navController: NavController) {
    val products = listOf(
        ProductInfo(
            id = "blade14",
            title = "Nuevo Razer Blade 14",
            subtitle = "Portátil gaming ultrapotente de 14\nAMD Ryzen™ 98945HS + NVIDIA® GeForce RTX™ 40",
            extra = "Disponible en Mercury o negro mate.",
            price = "Desde\n2.499,99 €",
            priceValue = 2499.99,
            imageUrl = "https://i.ibb.co/zNNf0v0/laptop1.png"
        ),
        ProductInfo(
            id = "blade16",
            title = "Nuevo Razer Blade 16",
            subtitle = "Portátil gaming de 16' con pantalla OLED a 240 Hz",
            extra = "Disponible en Mercury o negro mate.",
            price = "Desde\n3.499,99 €",
            priceValue = 3499.99,
            imageUrl = "https://i.ibb.co/3ry5bH3/laptop2.png"
        ),
        ProductInfo(
            id = "blade18",
            title = "Nuevo Razer Blade 18",
            subtitle = "Portátil gaming de 18' con pantalla QHD+ Mini-LED a 240 Hz",
            price = "Desde\n3.499,99 €",
            priceValue = 3499.99,
            imageUrl = "https://i.ibb.co/QFWXw2r/laptop3.png"
        ),
        ProductInfo(
            id = "blackwidow_v4_pro",
            title = "Razer BlackWidow V4 Pro",
            subtitle = "Teclado mecánico gaming con Razer Chroma RGB",
            price = "Desde\n269,49 €",
            priceValue = 269.49,
            imageUrl = "https://i.ibb.co/ckn2X3R/teclado1.png"
        ),
        ProductInfo(
            id = "huntsman_v3",
            title = "Razer Huntsman V3 Pro Tenkeyless",
            subtitle = "Teclado óptico analógico para esports sin teclado numérico",
            price = "Desde\n249,99 €",
            priceValue = 249.99,
            imageUrl = "https://i.ibb.co/RvYySWL/teclado2.png"
        ),
        ProductInfo(
            id = "seiren_v3",
            title = "Razer Seiren V3 CHROMA",
            subtitle = "Micrófono USB RGB con pulsación para silenciar",
            price = "Desde\n159,99 €",
            priceValue = 159.99,
            imageUrl = "https://i.ibb.co/WvgW9NR/microfono.png"
        ),
        ProductInfo(
            id = "cobra_pro",
            title = "Razer Cobra Pro",
            subtitle = "Ratón gaming inalámbrico con Razer Chroma RGB",
            price = "Desde\n149,99 €",
            priceValue = 149.99,
            imageUrl = "https://i.ibb.co/vZpkCH3/mouseblanco.png"
        )
    )
    val pagerState = rememberPagerState { products.size }
    val scope = rememberCoroutineScope()

    LaunchedEffect(pagerState) {
        while (true) {
            delay(3500)
            if (!pagerState.isScrollInProgress) {
                val nextPage = (pagerState.currentPage + 1) % products.size
                pagerState.animateScrollToPage(
                    page = nextPage,
                    animationSpec = tween(
                        durationMillis = 900,
                        easing = androidx.compose.animation.core.EaseInOutCubic
                    )
                )
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize()) {
            HorizontalPager(
                state = pagerState,
                contentPadding = PaddingValues(horizontal = 15.dp),
                modifier = Modifier
                    .height(640.dp)
                    .align(Alignment.Center)
            ) { page ->
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF222222)),
                    modifier = Modifier
                        .graphicsLayer {
                            val offset =
                                ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction).absoluteValue
                            lerp(0.75f, 1f, 1f - offset.coerceIn(0f, 1f)).also {
                                scaleX = it; scaleY = it
                            }
                            alpha = lerp(0.6f, 1f, 1f - offset.coerceIn(0f, 1f))
                        }
                        .fillMaxSize()
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(products[page].imageUrl).crossfade(true).scale(Scale.FILL)
                                .build(),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(350.dp)
                                .clickable {},
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        ProductCard(products[page], navController)
                    }
                }
            }
            IconButton(
                onClick = {
                    scope.launch {
                        if (pagerState.currentPage > 0) pagerState.animateScrollToPage(
                            pagerState.currentPage - 1
                        )
                    }
                },
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(16.dp)
                    .background(Color(0x80000000), CircleShape)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    tint = Color.Green
                )
            }
            IconButton(
                onClick = {
                    scope.launch {
                        if (pagerState.currentPage < products.size - 1) pagerState.animateScrollToPage(
                            pagerState.currentPage + 1
                        )
                    }
                },
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(16.dp)
                    .background(Color(0x80000000), CircleShape)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = Color.Green
                )
            }
        }
        PagerDots(pagerState, products.size)
    }
}

// ── CarouselCardTercero
@Composable
fun CarouselCardTercero(modifier: Modifier = Modifier) {
    val images = listOf(R.drawable.p1, R.drawable.p3, R.drawable.p4)
    val texts = listOf("EDICIÓN BLANCA DE RAZER", "AVIVA LA BATALLA", "GANA LOS RAZER NOMMO V2 PRO")
    val descriptions = listOf(
        "DESCUBRE LA COLECCIÓN",
        "TE ESPERAN CON OFERTAS EXCLUSIVAS",
        "SORTEO DE RAZERSTORE REWARDS"
    )
    val actions = listOf("Aprende más>", "Compra Ahora>", "Compra Ahora>")
    val pagerState = rememberPagerState { images.size }
    val scope = rememberCoroutineScope()

    Column(modifier = modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth()
        ) { page ->
            Column {
                Box {
                    Image(
                        painter = painterResource(id = images[page]), contentDescription = "",
                        modifier = Modifier.fillMaxWidth()
                    )
                    IconButton(
                        onClick = {
                            if (page + 1 < images.size) scope.launch {
                                pagerState.scrollToPage(
                                    page + 1
                                )
                            }
                        },
                        modifier = Modifier
                            .padding(20.dp)
                            .size(48.dp)
                            .align(Alignment.CenterEnd)
                            .clip(CircleShape),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color(
                                0x52373737
                            )
                        )
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            tint = Color.Green
                        )
                    }
                    IconButton(
                        onClick = { if (page - 1 >= 0) scope.launch { pagerState.scrollToPage(page - 1) } },
                        modifier = Modifier
                            .padding(20.dp)
                            .size(48.dp)
                            .align(Alignment.CenterStart)
                            .clip(CircleShape),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color(
                                0x52373737
                            )
                        )
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            tint = Color.Green
                        )
                    }
                }
                Text(
                    texts[page],
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(28.dp)
                )
                Text(
                    descriptions[page],
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray,
                    modifier = Modifier.padding(horizontal = 28.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    actions[page],
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Green,
                    modifier = Modifier.padding(horizontal = 28.dp)
                )
                Spacer(modifier = Modifier.height(15.dp))
                PagerDots(pagerState, images.size)
            }
        }
    }
}

// ── CarouselCardCuarto
@Composable
fun CarouselCardCuarto(navController: NavController) {
    GenericCarousel(
        products = listOf(
            ProductInfo(
                "naga_left", "Razer Naga Left-Handed Edition",
                "Ratón ergonómico para juegos MMO\ndiseñado para usuarios zurdos",
                price = "Desde\n109,99 €", priceValue = 109.99,
                imageUrl = "https://i.ibb.co/FzZw4wk/maus.png"
            ),
            ProductInfo(
                "firefly_v2", "Razer Firefly V2 Pro-Blanca",
                "Alfombrilla totalmente iluminada para ratón gaming RGB",
                price = "Desde\n109,99 €", priceValue = 109.99,
                imageUrl = "https://i.ibb.co/j85gy54/mauspad.png"
            ),
            ProductInfo(
                "blackwidow_naranja", "Razer BlackWidow V4 Pro\n-Switches naranja - US - Negro",
                "Teclado mecánico gaming con Razer Chroma RGB",
                price = "Desde\n269,99 €", priceValue = 269.99,
                imageUrl = "https://i.ibb.co/QcdvPwg/teclado4.png"
            ),
            ProductInfo(
                "blue_screen", "Razer Blue Screen",
                "Pantalla Chroma de fondo plegable para streaming",
                price = "Desde\n179,99 €", priceValue = 179.99,
                imageUrl = "https://i.ibb.co/NxRWKHS/nose.png"
            ),
            ProductInfo(
                "ths_case", "Razer THS Case for Airpods Pro-Mercury",
                "Funda protectora para el estuche de carga de los Airpods Pro",
                price = "Desde\n34,99 €", priceValue = 34.99,
                imageUrl = "https://i.ibb.co/7njLjP5/au.png"
            ),
            ProductInfo(
                "hyperpolling", "Razer HyperPolling Wireless Dongle",
                "Dongle inalámbrico de 9000 Hz",
                price = "Desde\n34,99 €", priceValue = 34.99,
                imageUrl = "https://i.ibb.co/dG6s0J0/tecla.png"
            ),
            ProductInfo(
                "floor_rug", "Team Razer Floor Rug - Quartz",
                "Accesorio para silla y habitación para esports",
                price = "Desde\n89,99 €", priceValue = 89.99,
                imageUrl = "https://i.ibb.co/XsW3wSg/logito.png"
            )
        ), navController = navController
    )
}


// ── CarouselCardQuinto
@Composable
fun CarouselCardQuinto(navController: NavController) {
    GenericCarousel(
        products = listOf(
            ProductInfo(
                "blade15_4070", "Razer Blade 15 - QHD 240 Hz - GeForce RTX 4070 - Negro",
                "Portátil Nvidia GeForce RTX 40 de 15'\nIntel Core i7 13ª gen (14 núcleos)",
                price = "Desde\n2.499,99 €", priceValue = 2499.99,
                imageUrl = "https://i.ibb.co/2F6yxrg/descarga-2-fotor-bg-remover-20240611134245.png"
            ),
            ProductInfo(
                "blade16_4060", "Razer Blade 16 - QHD+ 240 Hz - GeForce RTX 4060 - Negro",
                "Portátil NVIDIA GeForce RTX 40 de 16°\nIntel Core i9 13ª gen (24 núcleos)",
                price = "Desde\n2.599,99 €", priceValue = 2599.99,
                imageUrl = "https://i.ibb.co/H4Qxtn8/https-hybrismediaprod-blob-core-windows-net-sys-master-phoenix-images-container-hfe-h86-947071677238.png"
            ),
            ProductInfo(
                "blade16_4070", "Razer Blade 16 - QHD+ 240 Hz - GeForce RTX 4070 - Negro",
                "Portátil NVIDIA GeForce RTX 40 de 16°\nIntel Core i9 13ª gen (24 núcleos)",
                price = "Desde\n2.799,99 €", priceValue = 2799.99,
                imageUrl = "https://i.ibb.co/K5wPHCR/https-hybrismediaprod-blob-core-windows-net-sys-master-phoenix-images-container-hfe-h86-947071677238.png"
            ),
            ProductInfo(
                "blade16_4080", "Razer Blade 16 - QHD+ 240 Hz - GeForce RTX 4080 - Negro",
                "Portátil NVIDIA GeForce RTX 40 de 16°\nIntel Core i9 13ª gen (24 núcleos)",
                price = "Desde\n3.599,99 €", priceValue = 3599.99,
                imageUrl = "https://i.ibb.co/H4Qxtn8/https-hybrismediaprod-blob-core-windows-net-sys-master-phoenix-images-container-hfe-h86-947071677238.png"
            ),
            ProductInfo(
                "blade16_4080_mercury",
                "Razer Blade 16 - Dual UHD+FHD Mini-LED - GeForce RTX 4080 - Mercury",
                "Portátil NVIDIA GeForce RTX 40 de 16°\nIntel Core i9 13ª gen (24 núcleos)",
                price = "Desde\n3.799,99 €",
                priceValue = 3799.99,
                imageUrl = "https://i.ibb.co/GVrVQCz/descarga-fotor-bg-remover-20240611133343.png"
            ),
            ProductInfo(
                "blade16_4090", "Razer Blade 16 - Dual UHD+FHD Mini-LED - GeForce RTX 4090 - Negro",
                "Portátil NVIDIA GeForce RTX 40 de 16°\nIntel Core i9 13ª gen (24 núcleos)",
                price = "Desde\n4.399,99 €", priceValue = 4399.99,
                imageUrl = "https://i.ibb.co/H4Qxtn8/https-hybrismediaprod-blob-core-windows-net-sys-master-phoenix-images-container-hfe-h86-947071677238.png"
            ),
            ProductInfo(
                "blade18_4060", "Razer Blade 18 - QHD+ 240 Hz GeForce RTX 4060 - Negro",
                "Portátil NVIDIA GeForce RTX 40 de 18°\nIntel Core i9 13ª gen (24 núcleos)",
                price = "Desde\n2.799,99 €", priceValue = 2799.99,
                imageUrl = "https://i.ibb.co/6Jftfyh/https-hybrismediaprod-blob-core-windows-net-sys-master-phoenix-images-container-h57-h84-947071680515.png"
            )
        ), navController = navController
    )
}

// ── CarouselCardSexto
@Composable
fun CarouselCardSexto(navController: NavController) {
    GenericCarousel(
        products = listOf(
            ProductInfo(
                "seiren_v2_pro", "Razer Seiren V2 Pro",
                "Micrófono USB de calidad profesional para streamers",
                price = "Desde\n111,99 €", priceValue = 111.99,
                imageUrl = "https://i.ibb.co/k1RJ12w/descarga-10-fotor-bg-remover-20240611145022.png"
            ),
            ProductInfo(
                "seiren_bt", "Razer Seiren BT",
                "Micrófono Bluetooth para streaming móvil",
                price = "Desde\n76,99 €", priceValue = 76.99,
                imageUrl = "https://i.ibb.co/sm19VPz/descarga-3-fotor-bg-remover-2024061114511.png"
            ),
            ProductInfo(
                "wolverine_v2", "Razer Wolverine V2",
                "Mando de juego con cable para Xbox Series X",
                price = "Desde\n83,99 €", priceValue = 83.99,
                imageUrl = "https://i.ibb.co/MZdnyMV/descarga-4-fotor-bg-remover-20240611145050.png"
            ),
            ProductInfo(
                "kaira_pro", "Razer Kaira Pro HyperSpeed PlayStation Licensed",
                "Auriculares inalámbricos gaming multiplataforma con tecnología háptica",
                price = "Desde\n167,99 €", priceValue = 167.99,
                imageUrl = "https://i.ibb.co/P6YC2cC/descarga-5-fotor-bg-remover-20240611145046.png"
            ),
            ProductInfo(
                "kishi_android", "Razer Kishi for Android (Xbox)",
                "Mando universal para juegos Android",
                price = "Desde\n54,99 €", priceValue = 54.99,
                imageUrl = "https://i.ibb.co/HhK7Sdc/descarga-6-fotor-bg-remover-20240611145042.png"
            ),
            ProductInfo(
                "tomahawk", "Razer Tomahawk Mini-ITX",
                "Chasis mini-ITX para juegos con Razer Chroma RGB",
                price = "Desde\n131,99 €", priceValue = 131.99,
                imageUrl = "https://i.ibb.co/gj86H4G/descarga-7-fotor-bg-remover-20240611145039.png"
            ),
            ProductInfo(
                "iskur_negro", "Razer Iskur - Negro",
                "Silla para gamers con soporte lumbar integrado",
                price = "Desde\n384,99 €", priceValue = 384.99,
                imageUrl = "https://i.ibb.co/8DwynCn/descarga-8-fotor-bg-remover-20240611145035.png"
            ),
            ProductInfo(
                "iskur_xl", "Razer Iskur - Negro / Verde - XL",
                "Silla para gamers con soporte lumbar integrado",
                price = "Desde\n454,99 €", priceValue = 454.99,
                imageUrl = "https://i.ibb.co/WynkZ2D/descarga-9-fotor-bg-remover-20240611145028.png"
            )
        ), navController = navController
    )
}

// ── CarouselCardDoble
@Composable
fun CarouselCardDoble() {
    val sliderList = listOf(
        "https://i.ibb.co/M5pVkJ8/descarga-11.png",
        "https://i.ibb.co/fSQtyFH/descarga-12.png"
    )
    val titles = listOf("RazerStore Rewards", "PROGRAMAS DE COMPRA")
    val pagerState = rememberPagerState { sliderList.size }

    Column(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 65.dp), modifier = Modifier.height(300.dp)
        ) { page ->
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF222222)),
                modifier = Modifier
                    .graphicsLayer {
                        val offset =
                            ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction).absoluteValue
                        lerp(0.75f, 1f, 1f - offset.coerceIn(0f, 1f)).also {
                            scaleX = it; scaleY = it
                        }
                        alpha = lerp(0.6f, 1f, 1f - offset.coerceIn(0f, 1f))
                    }
                    .fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        titles[page],
                        style = TextStyle(fontSize = 20.sp, color = Color.White),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(sliderList[page]).crossfade(true).scale(Scale.FILL).build(),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = 1.dp)
                            .fillMaxHeight()
                            .size(200.dp)
                            .clickable {},
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}

// ── Cabecera
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Cabecera(
    navController: NavController,
    viewModel: CabeceraViewModel = hiltViewModel()
) {
    val options = listOf(
        "Tienda" to Screen.Tienda, "PC" to Screen.PC, "Consola" to Screen.Consola,
        "Móvil" to Screen.Movil, "Estilo de vida" to Screen.EstiloDeVida,
        "Servicios" to Screen.Servicios, "Comunidad" to Screen.Comunidad,
        "Asistencia" to Screen.Asistencia
    )
    var expanded by remember { mutableStateOf(false) }

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Black,
            navigationIconContentColor = Color.White,
            actionIconContentColor = Color.White
        ),
        title = {},
        actions = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(Icons.Default.Menu, contentDescription = null, tint = Color.White)
                }
                AnimatedVisibility(
                    visible = expanded,
                    enter = fadeIn(tween(500)) + expandVertically(tween(500)),
                    exit = fadeOut(tween(300)) + shrinkVertically(tween(300))
                ) {
                    DropdownMenu(
                        expanded = expanded, onDismissRequest = { expanded = false },
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .background(Color.Black)
                    ) {
                        DropdownMenuItem(text = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                                Image(
                                    painterResource(id = R.drawable.logorazer),
                                    contentDescription = null,
                                    modifier = Modifier.size(32.dp)
                                )

                                Icon(
                                    imageVector = Icons.Default.AccountCircle,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clickable {
                                            expanded = false // ← cierra el menú primero
                                            if (viewModel.isLoggedIn) navController.navigate(
                                                Screen.Profile.route
                                            )
                                            else navController.navigate(Screen.Login.route)
                                        }
                                )
                            }
                        }, onClick = { expanded = false })

                        DropdownMenuItem(
                            text = {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color.Gray)
                                        .padding(8.dp)
                                        .clickable {
                                            expanded = false
                                            navController.navigate(Screen.Search.route)
                                        },
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Search,
                                        contentDescription = null,
                                        tint = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        "Buscador.Razer.app",
                                        color = Color.White,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }, onClick = { expanded = false })

                        options.forEach { (text, screen) ->
                            DropdownMenuItem(
                                text = { Text(text, color = Color.White) },
                                onClick = { expanded = false; navController.navigate(screen.route) }
                            )
                        }
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painterResource(id = R.drawable.logorazer), contentDescription = null,
                        modifier = Modifier
                            .size(50.dp)
                            .weight(1f)
                    )
                    // ── Lupa + Carrito con badge ───────────────────────
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Search, contentDescription = "Buscar",
                            tint = Color.White,
                            modifier = Modifier
                                .clickable { navController.navigate(Screen.Search.route) }
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Box {
                            Icon(
                                Icons.Default.ShoppingCart, contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.clickable { navController.navigate(Screen.Cart.route) })
                            if (CartState.itemCount > 0) {
                                Badge(
                                    modifier = Modifier.align(Alignment.TopEnd),
                                    containerColor = Color.Green,
                                    contentColor = Color.Black
                                ) {
                                    Text(
                                        "${CartState.itemCount}",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}

// ── Foot
@Composable
fun Foot() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Image(
            painterResource(id = R.drawable.star), contentDescription = null,
            modifier = Modifier
                .size(128.dp)
                .padding(bottom = 16.dp)
        )
        Text(
            "Equipos y\nrecompensas\nexclusivas Razer",
            color = Color.White, fontSize = 24.sp, textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Accede a equipos Razer de edición limitada\nque solo están disponibles en Razer.com",
            color = Color.Gray, fontSize = 16.sp, textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        listOf(
            "Comprar" to "Texto1", "Explorar" to "Texto2",
            "Asistencia" to "Texto3", "Empresa" to "Texto4"
        ).forEach { (title, content) ->
            Section(title, content)
            HorizontalDivider(
                color = Color.White,
                thickness = 1.dp,
                modifier = Modifier.fillMaxWidth()
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Copyright @ 2024 Razer inc. All Rights Reserved\nTérminos Legales | Políticas de Privacidad\nConfiguración de Cookies",
            color = Color.White, fontSize = 12.sp, textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(color = Color.White, thickness = 1.dp, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Spain (España) Cambiar Ubicación",
            color = Color.Gray,
            fontSize = 12.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "FOR GAMERS. BY GAMERS.",
            color = Color.Green,
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
    }
}

// ── Section
@Composable
fun Section(title: String, content: String) {
    var expanded by remember { mutableStateOf(false) }
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .clickable { expanded = !expanded },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title, color = Color.White, fontSize = 16.sp)
            Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
        }
        if (expanded) {
            Text(
                content, color = Color.Gray, fontSize = 14.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
}