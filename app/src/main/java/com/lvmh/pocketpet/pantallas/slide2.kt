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
import androidx.compose.ui.text.SpanStyle                      // ← AGREGAR
import androidx.compose.ui.text.buildAnnotatedString           // ← AGREGAR
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign                // ← AGREGAR
import androidx.compose.ui.text.withStyle                      // ← AGREGAR
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun Slide2(onNext: () -> Unit = {}) {
    // Colores del tema - gradiente dorado/naranja para finanzas
    val gradientColors = listOf(
        Color(0xFF9C27B0),
        Color(0xFF673AB7)
    )

    // Estados para animaciones
    var startAnimation by remember { mutableStateOf(false) }
    var titleVisible by remember { mutableStateOf(false) }
    var descriptionVisible by remember { mutableStateOf(false) }
    var buttonVisible by remember { mutableStateOf(false) }

    // Animación de caída para el emoji
    val dropOffset by animateFloatAsState(
        targetValue = if (startAnimation) 0f else -500f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "drop"
    )

    // Rotación de la moneda al caer
    val rotation by animateFloatAsState(
        targetValue = if (startAnimation) 0f else -720f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "rotation"
    )

    // Animación de brillo continuo
    val infiniteTransition = rememberInfiniteTransition(label = "shine")
    val shineScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shineScale"
    )

    // Animación del botón
    val buttonScale by animateFloatAsState(
        targetValue = if (buttonVisible) 1f else 0.8f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "buttonScale"
    )

    val buttonAlpha by animateFloatAsState(
        targetValue = if (buttonVisible) 1f else 0f,
        animationSpec = tween(500),
        label = "buttonAlpha"
    )

    // Secuencia de animaciones
    LaunchedEffect(Unit) {
        delay(100)
        startAnimation = true
        delay(1000)
        titleVisible = true
        delay(1500)
        descriptionVisible = true
        delay(1200)
        buttonVisible = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(gradientColors)
            )
    ) {
        // Decoración de fondo - monedas flotantes
        FloatingCoinsSlide2()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Contenedor del emoji con efecto de caída y rotación
            Box(
                modifier = Modifier
                    .size(220.dp)
                    .offset(y = dropOffset.dp)
                    .rotate(rotation)
                    .scale(shineScale)
                    .shadow(
                        elevation = 24.dp,
                        shape = RoundedCornerShape(40.dp),
                        ambientColor = Color(0xFFFFD700).copy(alpha = 0.5f)
                    )
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.White,
                                Color(0xFFFFF8DC)
                            )
                        ),
                        shape = RoundedCornerShape(40.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "💰",
                    fontSize = 110.sp
                )
            }

            Spacer(modifier = Modifier.height(60.dp))

            // Título con animación
            if (titleVisible) {
                AnimatedRollingTextSlide2(
                    text = "Controla tus finanzas",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Descripción con animación
            if (descriptionVisible) {
                AnimatedRollingTextSlide2(
                    text = "Registra ingresos y gastos fácilmente.\nVisualiza tu balance en tiempo real y mantén el control.",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.White.copy(alpha = 0.95f),
                    delayPerChar = 20L
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Botón mejorado
            Button(
                onClick = onNext,
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(56.dp)
                    .scale(buttonScale)
                    .alpha(buttonAlpha)
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(28.dp)
                    ),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White
                ),
                shape = RoundedCornerShape(28.dp),
                enabled = buttonVisible
            ) {
                Text(
                    text = "Siguiente",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF9C27B0)
                )
            }
        }
    }
}

/**
 * Monedas flotantes para Slide2
 */
@Composable
private fun FloatingCoinsSlide2() {
    // Moneda 1
    val infiniteTransition1 = rememberInfiniteTransition(label = "coin1")
    val coin1Y by infiniteTransition1.animateFloat(
        initialValue = 0f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "coin1Y"
    )

    Box(
        modifier = Modifier
            .size(80.dp)
            .offset(x = 30.dp, y = (100 + coin1Y).dp)
            .alpha(0.15f)
            .background(
                Color.White,
                CircleShape
            )
    ) {
        Text(
            text = "💵",
            fontSize = 40.sp,
            modifier = Modifier.align(Alignment.Center)
        )
    }

    // Moneda 2
    val infiniteTransition2 = rememberInfiniteTransition(label = "coin2")
    val coin2Y by infiniteTransition2.animateFloat(
        initialValue = 0f,
        targetValue = -25f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "coin2Y"
    )

    Box(
        modifier = Modifier
            .size(100.dp)
            .offset(x = (-40).dp, y = (500 + coin2Y).dp)
            .alpha(0.12f)
            .background(
                Color.White,
                CircleShape
            )
    ) {
        Text(
            text = "💳",
            fontSize = 50.sp,
            modifier = Modifier.align(Alignment.Center)
        )
    }

    // Moneda 3
    val infiniteTransition3 = rememberInfiniteTransition(label = "coin3")
    val coin3Y by infiniteTransition3.animateFloat(
        initialValue = 0f,
        targetValue = 30f,
        animationSpec = infiniteRepeatable(
            animation = tween(3500, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "coin3Y"
    )

    Box(
        modifier = Modifier
            .size(70.dp)
            .offset(x = 320.dp, y = (300 + coin3Y).dp)
            .alpha(0.1f)
            .background(
                Color.White,
                CircleShape
            )
    ) {
        Text(
            text = "💸",
            fontSize = 35.sp,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

/**
 * Función de texto animado para Slide2
 */
@Composable
private fun AnimatedRollingTextSlide2(
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
fun PreviewSlide2() {
    Slide2(onNext = {
        println("Navegando a Slide 3...")
    })
}