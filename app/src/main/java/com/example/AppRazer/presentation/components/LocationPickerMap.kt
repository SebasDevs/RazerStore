package com.example.AppRazer.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.TilesOverlay

@Composable
fun LocationPickerMap(
    latitude: Double,
    longitude: Double,
    onLocationChanged: (Double, Double) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.height(220.dp)) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                Configuration.getInstance().userAgentValue = ctx.packageName
                MapView(ctx).apply {
                    setTileSource(TileSourceFactory.MAPNIK)
                    setMultiTouchControls(true)
                    controller.setZoom(16.0)
                    controller.setCenter(GeoPoint(latitude, longitude))

                    // ── Filtro de inversión de color = efecto "modo noche" ──
                    overlayManager.tilesOverlay.setColorFilter(TilesOverlay.INVERT_COLORS)

                    addMapListener(object : MapListener {
                        override fun onScroll(event: ScrollEvent?): Boolean {
                            val center = mapCenter
                            onLocationChanged(center.latitude, center.longitude)
                            return true
                        }

                        override fun onZoom(event: ZoomEvent?): Boolean = false
                    })
                }
            },
            update = { mapView ->
                val current = mapView.mapCenter
                if (kotlin.math.abs(current.latitude - latitude) > 0.0005 ||
                    kotlin.math.abs(current.longitude - longitude) > 0.0005
                ) {
                    mapView.controller.setCenter(GeoPoint(latitude, longitude))
                }
            }
        )

        // ── Pin fijo en el centro, estilo Razer ──────────────────
        Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = null,
            tint = Color.Green,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}