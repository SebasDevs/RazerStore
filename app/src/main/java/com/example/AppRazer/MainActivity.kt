package com.example.AppRazer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.AppRazer.presentation.navigation.NavGraph
import com.example.AppRazer.ui.theme.RazerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RazerTheme {
                NavGraph()
            }
        }
    }
}