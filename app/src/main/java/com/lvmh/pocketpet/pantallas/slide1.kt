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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun Slide1(onNext: () -> Unit = {}) {
    // Colores del tema - gradiente más vibrante
    val gradientColors = listOf(
        Color(0xFF667eea),
        Color(0xFF764ba2),
        Color(0xFFf093fb)
    )

    // Estados para animaciones
    var startAnimation by remember { mutableStateOf(false) }
    var explodeParticles by remember { mutableStateOf(false) }
    var titleVisible by remember { mutableStateOf(false) }
    var descriptionVisible by remember { mutableStateOf(false) }
    var buttonVisible by remember { mutableStateOf(false) }

    // Animación EXPLOSIVA de entrada - La mascota viene desde arriba
    val dropAndBounce by animateFloatAsState(
        targetValue = if (startAnimation) 0f else -800f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "drop"
    )

    // Rotación mientras cae
    val spinRotation by animateFloatAsState(
        targetValue = if (startAnimation) 0f else -1080f, // 3 vueltas completas
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "spin"
    )

    // Escala de impacto (crece al aterrizar)
    val impactScale by animateFloatAsState(
        targetValue = if (explodeParticles) 1.15f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "impact"
    )

    // Animación continua de respiración
    val infiniteTransition = rememberInfiniteTransition(label = "breathe")
    val breathe by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathe"
    )

    // Brillo pulsante del fondo
    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    // Animación del título - Explota hacia afuera
    val titleScale by animateFloatAsState(
        targetValue = if (titleVisible) 1f else 0.3f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "titleScale"
    )

    val titleAlpha by animateFloatAsState(
        targetValue = if (titleVisible) 1f else 0f,
        animationSpec = tween(600),
        label = "titleAlpha"
    )

    // Animación del botón - Salta hacia arriba
    val buttonOffset by animateFloatAsState(
        targetValue = if (buttonVisible) 0f else 100f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "buttonOffset"
    )

    val buttonAlpha by animateFloatAsState(
        targetValue = if (buttonVisible) 1f else 0f,
        animationSpec = tween(500),
        label = "buttonAlpha"
    )

    // Secuencia de animaciones coordinadas
    LaunchedEffect(Unit) {
        delay(200)
        startAnimation = true
        delay(1200) // Esperar a que aterrice
        explodeParticles = true
        delay(150)
        titleVisible = true
        delay(1000)
        descriptionVisible = true
        delay(800)
        buttonVisible = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = gradientColors,
                    radius = 1500f
                )
            )
    ) {
        // Partículas explosivas de fondo
        if (explodeParticles) {
            ExplosionParticles()
        }

        // Círculos pulsantes de fondo
        PulsatingCircles(glowPulse)

        // Estrellas flotantes
        FloatingStars()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Mascota con SUPER entrada
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .offset(y = dropAndBounce.dp)
                    .rotate(spinRotation)
                    .scale(impactScale * breathe)
                    .shadow(
                        elevation = 30.dp,
                        shape = RoundedCornerShape(50.dp),
                        ambientColor = Color(0xFFf093fb).copy(alpha = 0.6f),
                        spotColor = Color(0xFF667eea).copy(alpha = 0.8f)
                    )
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.White,
                                Color(0xFFF0E6FF),
                                Color(0xFFE1D5FF)
                            )
                        ),
                        shape = RoundedCornerShape(50.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🐾",
                    fontSize = 120.sp
                )
            }

            Spacer(modifier = Modifier.height(70.dp))

            // Título EXPLOSIVO
            Box(
                modifier = Modifier
                    .scale(titleScale)
                    .alpha(titleAlpha)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Texto principal
                    if (titleVisible) {
                        AnimatedExplosiveText(
                            text = "¡Bienvenido a",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Nombre de la app con efecto especial
                        Text(
                            text = "PocketPet!",
                            fontSize = 42.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFFFFD700),
                            modifier = Modifier
                                .shadow(
                                    elevation = 8.dp,
                                    shape = RoundedCornerShape(8.dp),
                                    ambientColor = Color(0xFFFFD700)
                                )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Descripción con animación
            if (descriptionVisible) {
                AnimatedRollingTextSlide1(
                    text = "Tu compañero financiero que crece contigo.\n✨ Gestiona tu dinero mientras cuidas de tu mascota virtual ✨",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.95f),
                    delayPerChar = 15L
                )
            }

            Spacer(modifier = Modifier.height(50.dp))

            // Botón super llamativo
            Button(
                onClick = onNext,
                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .height(60.dp)
                    .offset(y = buttonOffset.dp)
                    .alpha(buttonAlpha)
                    .shadow(
                        elevation = 12.dp,
                        shape = RoundedCornerShape(30.dp),
                        spotColor = Color(0xFFFFD700),
                        ambientColor = Color(0xFFf093fb)
                    ),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White
                ),
                shape = RoundedCornerShape(30.dp),
                enabled = buttonVisible
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Siguiente",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF667eea)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
        }
    }
}

/**
 * Partículas que explotan cuando la mascota aterriza
 */
@Composable
private fun ExplosionParticles() {
    val particles = remember {
        List(20) {
            ParticleState(
                angle = Random.nextFloat() * 360f,
                distance = Random.nextFloat() * 200f + 100f,
                size = Random.nextInt(30, 60),
                emoji = listOf("✨", "💫", "⭐", "🌟", "💝").random()
            )
        }
    }

    particles.forEach { particle ->
        val infiniteTransition = rememberInfiniteTransition(label = "particle${particle.angle}")

        val distance by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = particle.distance,
            animationSpec = infiniteRepeatable(
                animation = tween(1500, easing = EaseOut),
                repeatMode = RepeatMode.Reverse
            ),
            label = "distance"
        )

        val alpha by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 0f,
            animationSpec = infiniteRepeatable(
                animation = tween(1500, easing = EaseOut),
                repeatMode = RepeatMode.Reverse
            ),
            label = "alpha"
        )

        val angleRad = Math.toRadians(particle.angle.toDouble())
        val x = (distance * cos(angleRad)).toFloat()
        val y = (distance * sin(angleRad)).toFloat()

        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = particle.emoji,
                fontSize = particle.size.sp,
                modifier = Modifier
                    .offset(x = x.dp, y = y.dp)
                    .alpha(alpha)
            )
        }
    }
}

data class ParticleState(
    val angle: Float,
    val distance: Float,
    val size: Int,
    val emoji: String
)

/**
 * Círculos pulsantes en el fondo
 */
@Composable
private fun PulsatingCircles(pulse: Float) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Círculo grande
        Box(
            modifier = Modifier
                .size((200 * pulse).dp)
                .align(Alignment.TopStart)
                .offset(x = (-50).dp, y = 100.dp)
                .alpha(0.1f)
                .background(
                    Color(0xFFFFD700),
                    CircleShape
                )
        )

        // Círculo mediano
        Box(
            modifier = Modifier
                .size((150 * pulse).dp)
                .align(Alignment.TopEnd)
                .offset(x = 30.dp, y = 150.dp)
                .alpha(0.12f)
                .background(
                    Color(0xFFf093fb),
                    CircleShape
                )
        )

        // Círculo pequeño
        Box(
            modifier = Modifier
                .size((120 * pulse).dp)
                .align(Alignment.BottomStart)
                .offset(x = 50.dp, y = (-100).dp)
                .alpha(0.15f)
                .background(
                    Color(0xFF667eea),
                    CircleShape
                )
        )
    }
}

/**
 * Estrellas flotantes
 */
@Composable
private fun FloatingStars() {
    val stars = remember {
        listOf(
            StarState(30.dp, 150.dp, 3000),
            StarState(320.dp, 250.dp, 3500),
            StarState(60.dp, 500.dp, 2800),
            StarState(280.dp, 600.dp, 3200),
            StarState(150.dp, 80.dp, 2500)
        )
    }

    stars.forEach { star ->
        val infiniteTransition = rememberInfiniteTransition(label = "star${star.x}")

        val offsetY by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 20f,
            animationSpec = infiniteRepeatable(
                animation = tween(star.duration, easing = EaseInOutCubic),
                repeatMode = RepeatMode.Reverse
            ),
            label = "starY"
        )

        val rotation by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(star.duration * 2, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "starRotation"
        )

        Text(
            text = "⭐",
            fontSize = 25.sp,
            modifier = Modifier
                .offset(x = star.x, y = star.y + offsetY.dp)
                .rotate(rotation)
                .alpha(0.3f)
        )
    }
}

data class StarState(
    val x: androidx.compose.ui.unit.Dp,
    val y: androidx.compose.ui.unit.Dp,
    val duration: Int
)

/**
 * Texto que explota letra por letra
 */
@Composable
private fun AnimatedExplosiveText(
    text: String,
    fontSize: androidx.compose.ui.unit.TextUnit,
    fontWeight: FontWeight,
    color: Color
) {
    var visibleChars by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        while (visibleChars < text.length) {
            delay(50)
            visibleChars++
        }
    }

    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        text.forEachIndexed { index, char ->
            if (index < visibleChars) {
                val scale = remember { Animatable(0f) }
                val rotation = remember { Animatable(-180f) }

                LaunchedEffect(Unit) {
                    scale.animateTo(
                        targetValue = 1f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMedium
                        )
                    )
                    rotation.animateTo(
                        targetValue = 0f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMedium
                        )
                    )
                }

                Text(
                    text = char.toString(),
                    fontSize = fontSize,
                    fontWeight = fontWeight,
                    color = color,
                    modifier = Modifier
                        .scale(scale.value)
                        .rotate(rotation.value)
                )
            }
        }
    }
}

/**
 * Texto animado normal (para la descripción)
 */
@Composable
private fun AnimatedRollingTextSlide1(
    text: String,
    fontSize: androidx.compose.ui.unit.TextUnit,
    fontWeight: FontWeight,
    color: Color,
    delayPerChar: Long = 30L
) {
    var visibleChars by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        while (visibleChars < text.length) {
            delay(delayPerChar)
            visibleChars++
        }
    }

    Text(
        text = buildAnnotatedString {
            text.forEachIndexed { index, char ->
                if (index < visibleChars) {
                    withStyle(
                        style = SpanStyle(
                            color = color,
                            fontSize = fontSize,
                            fontWeight = fontWeight
                        )
                    ) {
                        append(char)
                    }
                }
            }
        },
        textAlign = TextAlign.Center,
        lineHeight = 26.sp,
        modifier = Modifier.padding(horizontal = 16.dp)
    )
}

// Preview para visualización
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewSlide1() {
    Slide1(onNext = {
        println("¡BOOM! Navegando a la siguiente pantalla...")
    })
}