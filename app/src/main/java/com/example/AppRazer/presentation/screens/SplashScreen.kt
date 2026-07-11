package com.example.AppRazer.presentation.screens

import android.media.MediaPlayer
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.AppRazer.R
import com.example.AppRazer.presentation.navigation.Screen
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext

@Composable
fun SplashScreen(navController: NavController) {

    val context = LocalContext.current

    // ── MediaPlayer controlado manualmente ─────────────────────────
    val mediaPlayer = remember { MediaPlayer.create(context, R.raw.splash_sound) }
    var soundReleased by remember { mutableStateOf(false) } // evita doble release()

    // ── Estados de animación ──────────────────────────────────────
    var startAnimation by remember { mutableStateOf(false) }
    var showText by remember { mutableStateOf(false) }
    var showTagline by remember { mutableStateOf(false) }
    var visibleChars by remember { mutableStateOf(0) }
    val tagline = "FOR GAMERS. BY GAMERS."

    // ── Logo scale y alpha ────────────────────────────────────────
    val logoScale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.3f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "logoScale"
    )

    val logoAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(800),
        label = "logoAlpha"
    )

    // ── Brillo pulsante del logo ──────────────────────────────────
    val glowPulse by rememberInfiniteTransition(label = "glow").animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowPulse"
    )

    // ── Rotación del anillo ───────────────────────────────────────
    val ringRotation by rememberInfiniteTransition(label = "ring").animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing)
        ),
        label = "ringRotation"
    )

    // ── Alpha del texto ───────────────────────────────────────────
    val textAlpha by animateFloatAsState(
        targetValue = if (showText) 1f else 0f,
        animationSpec = tween(600),
        label = "textAlpha"
    )

    // ── Secuencia de animación ────────────────────────────────────
    LaunchedEffect(Unit) {
        delay(200)
        startAnimation = true
        delay(800)
        showText = true
        delay(400)
        showTagline = true

        //  El audio arranca justo cuando empieza a escribirse el tagline
        mediaPlayer.start()

        // Efecto typewriter
        repeat(tagline.length) {
            visibleChars++
            delay(60)
        }
        delay(900)

        // 🔇 Se corta justo antes de navegar a Inicio
        if (!soundReleased) {
            if (mediaPlayer.isPlaying) mediaPlayer.stop()
            mediaPlayer.release()
            soundReleased = true
        }

        // Navegar a inicio
        navController.navigate(Screen.Inicio.route) {
            popUpTo(Screen.Splash.route) { inclusive = true }
        }
    }

    // ── Liberación de seguridad si el usuario sale antes de tiempo ──
    DisposableEffect(Unit) {
        onDispose {
            if (!soundReleased) {
                try {
                    if (mediaPlayer.isPlaying) mediaPlayer.stop()
                    mediaPlayer.release()
                    soundReleased = true
                } catch (e: IllegalStateException) {
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        // ── Fondo con gradiente radial ────────────────────────────
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRadialGlow(this, glowPulse)
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // ── Anillo animado + Logo ─────────────────────────────
            Box(
                modifier = Modifier.size(180.dp),
                contentAlignment = Alignment.Center
            ) {
                // Anillo exterior rotando
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer { rotationZ = ringRotation }
                ) {
                    drawRingWithDots(this, glowPulse)
                }

                // Anillo interior estático
                Canvas(modifier = Modifier.size(140.dp)) {
                    drawCircle(
                        color = Color.Green.copy(alpha = 0.15f * glowPulse),
                        radius = size.minDimension / 2,
                        style = Stroke(width = 1f)
                    )
                }

                // Logo Razer
                Image(
                    painter = painterResource(id = R.drawable.logorazer),
                    contentDescription = null,
                    modifier = Modifier
                        .size(100.dp)
                        .scale(logoScale)
                        .alpha(logoAlpha)
                        .graphicsLayer {
                            // Efecto de brillo en el logo
                            shadowElevation = 20f
                        }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ── Nombre RAZER ──────────────────────────────────────
            Text(
                text = "RAZER",
                style = TextStyle(
                    fontSize = 42.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 12.sp
                ),
                modifier = Modifier.alpha(textAlpha)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // ── Tagline con efecto typewriter ─────────────────────
            if (showTagline) {
                Text(
                    text = tagline.take(visibleChars),
                    style = TextStyle(
                        fontSize = 13.sp,
                        color = Color.Green,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp,
                        textAlign = TextAlign.Center
                    )
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            // ── Barra de carga ────────────────────────────────────
            if (startAnimation) {
                LoadingBar()
            }
        }
    }
}

// ── Barra de carga estilo Razer ───────────────────────────────────
@Composable
fun LoadingBar() {
    val progress by rememberInfiniteTransition(label = "loading").animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Restart
        ),
        label = "progress"
    )

    Box(
        modifier = Modifier
            .width(120.dp)
            .height(2.dp)
            .background(Color(0xFF1A1A1A))
    ) {
        Box(
            modifier = Modifier
                .width(40.dp)
                .height(2.dp)
                .offset(x = (80 * progress).dp)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Green,
                            Color.Transparent
                        )
                    )
                )
        )
    }
}

// ── Dibujar brillo radial de fondo ────────────────────────────────
fun drawRadialGlow(scope: DrawScope, intensity: Float) {
    scope.drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color(0xFF00FF00).copy(alpha = 0.06f * intensity),
                Color.Transparent
            ),
            center = Offset(scope.size.width / 2, scope.size.height / 2),
            radius = scope.size.minDimension * 0.6f
        ),
        radius = scope.size.minDimension * 0.6f
    )
}

// ── Dibujar anillo con puntos ─────────────────────────────────────
fun drawRingWithDots(scope: DrawScope, glowPulse: Float) {
    val center = Offset(scope.size.width / 2, scope.size.height / 2)
    val radius = scope.size.minDimension / 2 - 4f

    // Anillo principal
    scope.drawCircle(
        color = Color.Green.copy(alpha = 0.3f * glowPulse),
        radius = radius,
        style = Stroke(width = 1.5f)
    )

    // Puntos en el anillo
    val dotCount = 8
    repeat(dotCount) { i ->
        val angle = (360f / dotCount) * i
        val rad = Math.toRadians(angle.toDouble())
        val x = center.x + radius * cos(rad).toFloat()
        val y = center.y + radius * sin(rad).toFloat()
        scope.drawCircle(
            color = Color.Green.copy(alpha = if (i % 2 == 0) 0.8f * glowPulse else 0.3f * glowPulse),
            radius = if (i % 2 == 0) 3f else 1.5f,
            center = Offset(x, y)
        )
    }
}