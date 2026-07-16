package com.example.AppRazer.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.EaseInCubic
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.AppRazer.presentation.screens.SplashScreen
import com.example.AppRazer.presentation.screens.addresses.AddressesScreen
import com.example.AppRazer.presentation.screens.auth.forgot.ForgotScreen
import com.example.AppRazer.presentation.screens.auth.login.LoginScreen
import com.example.AppRazer.presentation.screens.auth.register.RegisterScreen
import com.example.AppRazer.presentation.screens.cart.CartScreen
import com.example.AppRazer.presentation.screens.cart.CheckoutScreen
import com.example.AppRazer.presentation.screens.home.InicioTienda
import com.example.AppRazer.presentation.screens.laptops.Laptops
import com.example.AppRazer.presentation.screens.movil.SeccionParaMovil
import com.example.AppRazer.presentation.screens.notifications.NotificationsScreen
import com.example.AppRazer.presentation.screens.payment.PaymentMethodsScreen
import com.example.AppRazer.presentation.screens.pc.SeccionParaPC
import com.example.AppRazer.presentation.screens.product.ProductDetailScreen
import com.example.AppRazer.presentation.screens.products.CategoryProductsScreen
import com.example.AppRazer.presentation.screens.profile.OrdersScreen
import com.example.AppRazer.presentation.screens.profile.ProfileScreen
import com.example.AppRazer.presentation.screens.search.SearchScreen
import com.example.AppRazer.presentation.screens.security.SecurityScreen
import com.example.AppRazer.presentation.screens.settings.SettingsScreen
import com.example.AppRazer.presentation.screens.wishlist.WishlistScreen


// Slide desde la derecha (navegación hacia adelante)
private val enterFromRight: AnimatedContentTransitionScope<*>.() -> EnterTransition = {
    slideInHorizontally(
        initialOffsetX = { it },
        animationSpec = tween(350, easing = EaseOutCubic)
    ) + fadeIn(tween(350))
}

// Sale hacia la izquierda
private val exitToLeft: AnimatedContentTransitionScope<*>.() -> ExitTransition = {
    slideOutHorizontally(
        targetOffsetX = { -it / 3 },
        animationSpec = tween(350, easing = EaseOutCubic)
    ) + fadeOut(tween(200))
}

// Entra desde la izquierda (volver atrás)
private val enterFromLeft: AnimatedContentTransitionScope<*>.() -> EnterTransition = {
    slideInHorizontally(
        initialOffsetX = { -it / 3 },
        animationSpec = tween(350, easing = EaseOutCubic)
    ) + fadeIn(tween(350))
}

// Sale hacia la derecha (volver atrás)
private val exitToRight: AnimatedContentTransitionScope<*>.() -> ExitTransition = {
    slideOutHorizontally(
        targetOffsetX = { it },
        animationSpec = tween(350, easing = EaseOutCubic)
    ) + fadeOut(tween(200))
}

// Fade simple para modales y auth
private val fadeEnter: AnimatedContentTransitionScope<*>.() -> EnterTransition = {
    fadeIn(tween(400)) + scaleIn(
        initialScale = 0.95f,
        animationSpec = tween(400, easing = EaseOutCubic)
    )
}

private val fadeExit: AnimatedContentTransitionScope<*>.() -> ExitTransition = {
    fadeOut(tween(300)) + scaleOut(
        targetScale = 0.95f,
        animationSpec = tween(300)
    )
}

// Slide desde abajo para carrito y checkout
private val slideFromBottom: AnimatedContentTransitionScope<*>.() -> EnterTransition = {
    slideInVertically(
        initialOffsetY = { it },
        animationSpec = tween(400, easing = EaseOutCubic)
    ) + fadeIn(tween(400))
}

private val slideToBottom: AnimatedContentTransitionScope<*>.() -> ExitTransition = {
    slideOutVertically(
        targetOffsetY = { it },
        animationSpec = tween(350, easing = EaseInCubic)
    ) + fadeOut(tween(300))
}

@Composable
fun NavGraph() {
    val navController = rememberNavController()


    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        enterTransition = { enterFromRight() },
        exitTransition = { exitToLeft() },
        popEnterTransition = { enterFromLeft() },
        popExitTransition = { exitToRight() }
    ) {
        // ── Inicio ────────────────────────────────────────────────
        composable(
            route = Screen.Inicio.route,
            enterTransition = { fadeEnter() },
            exitTransition = { exitToLeft() },
            popEnterTransition = { enterFromLeft() },
            popExitTransition = { fadeExit() }
        ) {
            InicioTienda(navController)
        }

        // ── Laptops ───────────────────────────────────────────────
        composable(
            route = Screen.Laptops.route,
            enterTransition = { enterFromRight() },
            exitTransition = { exitToLeft() },
            popEnterTransition = { enterFromLeft() },
            popExitTransition = { exitToRight() }
        ) {
            Laptops(navController)
        }

        // ── PC ────────────────────────────────────────────────────
        composable(
            route = Screen.PC.route,
            enterTransition = { enterFromRight() },
            exitTransition = { exitToLeft() },
            popEnterTransition = { enterFromLeft() },
            popExitTransition = { exitToRight() }
        ) {
            SeccionParaPC(navController)

        }

        // ── Móvil ─────────────────────────────────────────────────
        composable(
            route = Screen.Movil.route,
            enterTransition = { enterFromRight() },
            exitTransition = { exitToLeft() },
            popEnterTransition = { enterFromLeft() },
            popExitTransition = { exitToRight() }
        ) {
            SeccionParaMovil(navController)
        }

        // ── Product Detail ────────────────────────────────────────
        composable(
            route = "product/{productId}",
            arguments = listOf(navArgument("productId") { type = NavType.StringType }),
            enterTransition = {
                slideInVertically(
                    initialOffsetY = { it / 2 },
                    animationSpec = tween(400, easing = EaseOutCubic)
                ) + fadeIn(tween(400))
            },
            exitTransition = { fadeExit() },
            popEnterTransition = { fadeEnter() },
            popExitTransition = {
                slideOutVertically(
                    targetOffsetY = { it / 2 },
                    animationSpec = tween(350, easing = EaseInCubic)
                ) + fadeOut(tween(300))
            }
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""
            ProductDetailScreen(navController = navController, productId = productId)
        }

        // ── Cart ──────────────────────────────────────────────────
        composable(
            route = Screen.Cart.route,
            enterTransition = { slideFromBottom() },
            exitTransition = { fadeExit() },
            popEnterTransition = { fadeEnter() },
            popExitTransition = { slideToBottom() }
        ) {
            CartScreen(navController)
        }

        // ── Checkout ──────────────────────────────────────────────
        composable(
            route = Screen.Checkout.route,
            enterTransition = { enterFromRight() },
            exitTransition = { exitToLeft() },
            popEnterTransition = { enterFromLeft() },
            popExitTransition = { exitToRight() }
        ) {
            CheckoutScreen(navController)
        }

        // ── Auth ──────────────────────────────────────────────────
        composable(
            route = Screen.Login.route,
            enterTransition = { fadeEnter() },
            exitTransition = { fadeExit() },
            popEnterTransition = { fadeEnter() },
            popExitTransition = { fadeExit() }
        ) {
            LoginScreen(navController)
        }

        composable(
            route = Screen.Register.route,
            enterTransition = { enterFromRight() },
            exitTransition = { exitToLeft() },
            popEnterTransition = { enterFromLeft() },
            popExitTransition = { exitToRight() }
        ) {
            RegisterScreen(navController)
        }

        composable(
            route = Screen.Forgot.route,
            enterTransition = { enterFromRight() },
            exitTransition = { exitToLeft() },
            popEnterTransition = { enterFromLeft() },
            popExitTransition = { exitToRight() }
        ) {
            ForgotScreen(navController)
        }

        // ── Profile ───────────────────────────────────────────────
        composable(
            route = Screen.Profile.route,
            enterTransition = { fadeEnter() },
            exitTransition = { fadeExit() },
            popEnterTransition = { fadeEnter() },
            popExitTransition = { fadeExit() }
        ) {
            ProfileScreen(navController)
        }

        // ── Orders ────────────────────────────────────────────────
        composable(
            route = Screen.Orders.route,
            enterTransition = { enterFromRight() },
            exitTransition = { exitToLeft() },
            popEnterTransition = { enterFromLeft() },
            popExitTransition = { exitToRight() }
        ) {
            OrdersScreen(navController)
        }
        // ── Splash ────────────────────────────────────────────────
        composable(
            route = Screen.Splash.route,
            enterTransition = { fadeIn(tween(500)) },
            exitTransition = { fadeOut(tween(800)) }
        ) {
            SplashScreen(navController)
        }
        // Search
        composable(
            route = Screen.Search.route,
            enterTransition = { fadeIn(tween(300)) + slideInVertically(tween(300)) },
            exitTransition = { fadeOut(tween(300)) },
            popEnterTransition = { fadeIn(tween(300)) },
            popExitTransition = { slideOutVertically(tween(300)) + fadeOut(tween(300)) }
        ) {
            SearchScreen(navController)
        }

        // ── Wishlist
        composable(
            route = Screen.Wishlist.route,
            enterTransition = { enterFromRight() },
            exitTransition = { exitToLeft() },
            popEnterTransition = { enterFromLeft() },
            popExitTransition = { exitToRight() }
        ) {
            WishlistScreen(navController)
        }
        // ── Category Products (con filtros) ──────────────────────────
        composable(
            route = Screen.CategoryProducts.route,
            arguments = listOf(navArgument("category") { type = NavType.StringType }),
            enterTransition = { enterFromRight() },
            exitTransition = { exitToLeft() },
            popEnterTransition = { enterFromLeft() },
            popExitTransition = { exitToRight() }
        ) {
            CategoryProductsScreen(navController)
        }
        // Settings
        composable(
            route = Screen.Settings.route,
            enterTransition = { enterFromRight() },
            exitTransition = { exitToLeft() },
            popEnterTransition = { enterFromLeft() },
            popExitTransition = { exitToRight() }
        ) {
            SettingsScreen(navController)
        }
        // Security
        composable(
            route = Screen.Security.route,
            enterTransition = { enterFromRight() },
            exitTransition = { exitToLeft() },
            popEnterTransition = { enterFromLeft() },
            popExitTransition = { exitToRight() }
        ) {
            SecurityScreen(navController)
        }
        // Notifications
        composable(
            route = Screen.Notifications.route,
            enterTransition = { enterFromRight() },
            exitTransition = { exitToLeft() },
            popEnterTransition = { enterFromLeft() },
            popExitTransition = { exitToRight() }
        ) {
            NotificationsScreen(navController)
        }
        // Payment Methods
        composable(
            route = Screen.PaymentMethods.route,
            enterTransition = { enterFromRight() },
            exitTransition = { exitToLeft() },
            popEnterTransition = { enterFromLeft() },
            popExitTransition = { exitToRight() }
        ) {
            PaymentMethodsScreen(navController)
        }
        //Adresses
        composable(
            route = Screen.Addresses.route,
            enterTransition = { enterFromRight() },
            exitTransition = { exitToLeft() },
            popEnterTransition = { enterFromLeft() },
            popExitTransition = { exitToRight() }
        ) {
            AddressesScreen(navController)
        }

        // ── Pantallas vacías
        //composable(Screen.GamingZone.route) {}
        //composable(Screen.Mouses.route) {}
        //composable(Screen.Keyboards.route) {}
        composable(Screen.Tienda.route) {}
        composable(Screen.Consola.route) {}
        composable(Screen.EstiloDeVida.route) {}
        composable(Screen.Servicios.route) {}
        composable(Screen.Comunidad.route) {}
        composable(Screen.Asistencia.route) {}
    }
}