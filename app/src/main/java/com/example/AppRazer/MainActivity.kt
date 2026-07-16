package com.example.AppRazer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.AppRazer.presentation.AppSettingsViewModel
import com.example.AppRazer.presentation.navigation.NavGraph
import com.example.AppRazer.ui.theme.RazerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RazerTheme {
                val settingsViewModel: AppSettingsViewModel = hiltViewModel()
                val fontScale by settingsViewModel.fontScale.collectAsState()
                val density = LocalDensity.current

                CompositionLocalProvider(
                    LocalDensity provides Density(
                        density = density.density,
                        fontScale = fontScale
                    )
                ) {
                    NavGraph()
                }
            }
        }
    }
}