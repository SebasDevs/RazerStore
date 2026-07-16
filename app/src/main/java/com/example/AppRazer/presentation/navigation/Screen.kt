package com.example.AppRazer.presentation.navigation

sealed class Screen(val route: String) {
    object Tienda : Screen("tienda")
    object PC : Screen("pc")
    object Consola : Screen("consola")
    object Movil : Screen("movil")
    object EstiloDeVida : Screen("estilo_de_vida")
    object Servicios : Screen("servicios")
    object Comunidad : Screen("comunidad")
    object Asistencia : Screen("asistencia")

    object Inicio : Screen("inicio")

    object Laptops : Screen("laptops")

    object Forgot : Screen("forgot")

    object Login : Screen("login")

    object Register : Screen("register")

    object Cart : Screen("cart")

    object ProductDetail : Screen("product/{productId}") {
        fun createRoute(productId: String) = "product/$productId"
    }

    object Splash : Screen("splash")
    object Orders : Screen("orders")
    object Checkout : Screen("checkout")

    object Profile : Screen("profile")

    object Search : Screen("search")

    object Wishlist : Screen("wishlist")

    object CategoryProducts : Screen("category/{category}") {
        fun createRoute(category: String) = "category/$category"
    }

    object Security : Screen("security")
    object Settings : Screen("settings")

    object Addresses : Screen("addresses")

    object PaymentMethods : Screen("payment_methods")

    object Notifications : Screen("notifications")
}



