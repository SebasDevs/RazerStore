package com.example.AppRazer.presentation.screens.movil

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.AppRazer.presentation.screens.home.Cabecera

@Composable
fun SeccionParaMovil(navController: NavController) {

    Column {
        Cabecera(navController)
    }
}