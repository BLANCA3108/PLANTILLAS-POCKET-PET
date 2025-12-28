package com.lvmh.pocketpet.pantallas

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun Logo(onNext: () -> Unit = {}) {
    // Colores vibrantes para el logo
    val gradientColors = listOf(
        Color(0xFF00D4FF),  // Cyan brillante
        Color(0xFF0099FF),  // Azul eléctrico
        Color(0xFF667eea),  // Púrpura
        Color(0xFFf093fb)   // Rosa
    )

    // Estados de animación
    var startAnimation by remember { mutableStateOf(false) }
    var showExplosion by remember { mutableStateOf(false) }
    var showDog by remember { mutableStateOf(false) }
    var showTitle by remember { mutableStateOf(false) }
    var showButton by remember { mutableStateOf(false) }

    // EXPLOSIÓN INICIAL - El perrito aparece desde el centro
    val explosionScale by animateFloatAsState(
        targetValue = if (showExplosion) 15f else 0.1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "explosion"
    )

    val explosionAlpha by animateFloatAsState(
        targetValue = if (showExplosion && !showDog) 0.8f else 0f,
        animationSpec = tween(800),
        label = "explosionAlpha"
    )

    // PERRITO - Aparece con super rebote
    val dogScale by animateFloatAsState(
        targetValue = if (showDog) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "dogScale"
    )

    val dogRotation by animateFloatAsState(
        targetValue = if (showDog) 0f else 720f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "dogRotation"
    )

    // Animación continua del perrito - Saltando
    val infiniteTransition = rememberInfiniteTransition(label = "dogJump")
    val dogBounce by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -25f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bounce"
    )

    // Rotación continua sutil
    val dogWiggle by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wiggle"
    )

    // Brillo pulsante del fondo
    val backgroundGlow by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    // TÍTULO - Explota letra por letra
    val titleScale by animateFloatAsState(
        targetValue = if (showTitle) 1f else 0.3f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "titleScale"
    )

    val titleAlpha by animateFloatAsState(
        targetValue = if (showTitle) 1f else 0f,
        animationSpec = tween(500),
        label = "titleAlpha"
    )

    // BOTÓN - Salta desde abajo
    val buttonOffset by animateFloatAsState(
        targetValue = if (showButton) 0f else 200f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "buttonOffset"
    )

    val buttonAlpha by animateFloatAsState(
        targetValue = if (showButton) 1f else 0f,
        animationSpec = tween(600),
        label = "buttonAlpha"
    )

    // SECUENCIA DE ANIMACIÓN ÉPICA
    LaunchedEffect(Unit) {
        delay(300)
        startAnimation = true
        delay(100)
        showExplosion = true
        delay(700)
        showDog = true
        delay(800)
        showTitle = true
        delay(1000)
        showButton = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = gradientColors.map { it.copy(alpha = 0.9f) },
                    radius = 1200f * backgroundGlow
                )
            )
    ) {
        // ONDAS DE CHOQUE de fondo
        if (showExplosion) {
            ShockWaves()
        }

        // PARTÍCULAS EXPLOSIVAS flotando EN TODA LA PANTALLA
        if (showDog) {
            FloatingParticlesFullScreen()
        }

        // RAYOS DE LUZ desde el centro
        if (showExplosion) {
            LightRays(explosionAlpha)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // FLASH DE EXPLOSIÓN
            if (explosionAlpha > 0) {
                Box(
                    modifier = Modifier
                        .size(300.dp)
                        .scale(explosionScale)
                        .alpha(explosionAlpha)
                        .blur(40.dp)
                        .background(
                            Color.White,
                            CircleShape
                        )
                )
            }

            // EL PERRITO SIN FONDO - Solo el emoji
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .offset(y = dogBounce.dp),
                contentAlignment = Alignment.Center
            ) {
                // AURA brillante detrás (más sutil)
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .scale(backgroundGlow)
                        .alpha(0.2f)
                        .blur(50.dp)
                        .background(
                            Color(0xFFFFD700),
                            CircleShape
                        )
                )

                // EL PERRITO con sombra
                Text(
                    text = "🐕",
                    fontSize = 180.sp,
                    modifier = Modifier
                        .scale(dogScale)
                        .rotate(dogRotation + dogWiggle)
                        .shadow(
                            elevation = 30.dp,
                            shape = CircleShape,
                            ambientColor = Color(0xFFFFD700).copy(alpha = 0.6f)
                        )
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // TÍTULO CON EFECTO EXPLOSIVO
            Box(
                modifier = Modifier
                    .scale(titleScale)
                    .alpha(titleAlpha)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Texto con sombra y brillo
                    Text(
                        text = "PocketPet",
                        fontSize = 58.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .shadow(
                                elevation = 15.dp,
                                shape = RoundedCornerShape(8.dp),
                                ambientColor = Color(0xFFFFD700)
                            )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Subtítulo con emojis
                    Text(
                        text = " Tu Mascota Financiera ",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFD700),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(70.dp))

            // BOTÓN ÉPICO CENTRADO Y ESTÉTICO
            Button(
                onClick = onNext,
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(68.dp)
                    .offset(y = buttonOffset.dp)
                    .alpha(buttonAlpha)
                    .shadow(
                        elevation = 20.dp,
                        shape = RoundedCornerShape(34.dp),
                        spotColor = Color(0xFFFFD700).copy(alpha = 0.8f),
                        ambientColor = Color(0xFFf093fb).copy(alpha = 0.6f)
                    ),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White
                ),
                shape = RoundedCornerShape(34.dp),
                enabled = showButton
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "COMENZAR AVENTURA",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF0099FF),
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.width(12.dp))

                }
            }
        }
    }
}

/**
 * Ondas de choque expandiéndose
 */
@Composable
private fun ShockWaves() {
    val waves = remember { listOf(1f, 1.3f, 1.6f) }

    waves.forEach { multiplier ->
        val infiniteTransition = rememberInfiniteTransition(label = "wave$multiplier")

        val scale by infiniteTransition.animateFloat(
            initialValue = 0.5f,
            targetValue = 3f * multiplier,
            animationSpec = infiniteRepeatable(
                animation = tween(2000, easing = EaseOut),
                repeatMode = RepeatMode.Restart
            ),
            label = "waveScale"
        )

        val alpha by infiniteTransition.animateFloat(
            initialValue = 0.6f,
            targetValue = 0f,
            animationSpec = infiniteRepeatable(
                animation = tween(2000, easing = EaseOut),
                repeatMode = RepeatMode.Restart
            ),
            label = "waveAlpha"
        )

        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .scale(scale)
                    .alpha(alpha)
                    .background(
                        Color.White,
                        CircleShape
                    )
            )
        }
    }
}

/**
 * Rayos de luz desde el centro
 */
@Composable
private fun LightRays(alpha: Float) {
    val rays = remember { List(8) { it * 45f } }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        rays.forEach { angle ->
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(500.dp)
                    .rotate(angle)
                    .alpha(alpha * 0.3f)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.White,
                                Color.Transparent
                            )
                        )
                    )
            )
        }
    }
}

/**
 * Partículas explosivas flotando EN TODA LA PANTALLA
 */
@Composable
private fun FloatingParticlesFullScreen() {
    val particles = remember {
        List(25) {  // Más partículas
            FullScreenParticle(
                emoji = listOf("✨", "💫", "⭐", "🌟", "💰", "💵", "💳", "💎", "🐾", "🦴", "🎾").random(),
                startX = Random.nextFloat() * 400f - 200f,  // Distribución más amplia
                startY = Random.nextFloat() * 900f - 450f,
                duration = Random.nextInt(2500, 5000),
                size = Random.nextInt(20, 36)
            )
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        particles.forEach { particle ->
            val infiniteTransition = rememberInfiniteTransition(label = "particle${particle.startX}")

            val offsetY by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = -40f,
                animationSpec = infiniteRepeatable(
                    animation = tween(particle.duration, easing = EaseInOutCubic),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "particleY"
            )

            val offsetX by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = Random.nextFloat() * 30f - 15f,
                animationSpec = infiniteRepeatable(
                    animation = tween(particle.duration + 500, easing = EaseInOutCubic),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "particleX"
            )

            val rotation by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(particle.duration, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                ),
                label = "particleRotation"
            )

            val scale by infiniteTransition.animateFloat(
                initialValue = 0.8f,
                targetValue = 1.2f,
                animationSpec = infiniteRepeatable(
                    animation = tween(particle.duration / 2, easing = EaseInOutCubic),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "particleScale"
            )

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = particle.emoji,
                    fontSize = particle.size.sp,
                    modifier = Modifier
                        .offset(
                            x = (particle.startX + offsetX).dp,
                            y = (particle.startY + offsetY).dp
                        )
                        .rotate(rotation)
                        .scale(scale)
                        .alpha(0.5f)
                )
            }
        }
    }
}

data class FullScreenParticle(
    val emoji: String,
    val startX: Float,
    val startY: Float,
    val duration: Int,
    val size: Int
)

// Preview para visualización
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewLogo() {
    Logo(onNext = {
        println("💥 ¡KABOOM! Iniciando aventura...")
    })
}