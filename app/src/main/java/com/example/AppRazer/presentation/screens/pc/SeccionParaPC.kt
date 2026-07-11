package com.example.AppRazer.presentation.screens.pc

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.example.AppRazer.presentation.navigation.Screen
import com.example.AppRazer.presentation.screens.home.Cabecera

@Composable
fun SeccionParaPC(navController: NavController) {

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.DarkGray
    ) {
        Column {

            Cabecera(navController)

            TopBar()

            ScrollableBoxScreen()

        }

    }
}