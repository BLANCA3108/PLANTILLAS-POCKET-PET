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

@Composable
fun Slide3(onNext: () -> Unit = {}) {
    // Colores del tema - gradiente morado/púrpura para personalización
    val gradientColors = listOf(
        Color(0xFF9C27B0),  // Morado
        Color(0xFF673AB7)   // Púrpura
    )

    // Estados para animaciones
    var startAnimation by remember { mutableStateOf(false) }
    var titleVisible by remember { mutableStateOf(false) }
    var descriptionVisible by remember { mutableStateOf(false) }
    var buttonVisible by remember { mutableStateOf(false) }

    // Animación de escala inicial para el emoji
    val scale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.3f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    // Rotación continua del engranaje
    val infiniteTransition = rememberInfiniteTransition(label = "rotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    // Pulsación sutil del engranaje
    val pulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
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
        delay(800)
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
        // Decoración de fondo - engranajes flotantes
        FloatingGearsSlide3()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Contenedor del emoji con rotación continua
            Box(
                modifier = Modifier
                    .size(220.dp)
                    .scale(scale * pulse)
                    .shadow(
                        elevation = 24.dp,
                        shape = RoundedCornerShape(40.dp),
                        ambientColor = Color(0xFF9C27B0).copy(alpha = 0.5f)
                    )
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.White,
                                Color(0xFFF3E5F5)
                            )
                        ),
                        shape = RoundedCornerShape(40.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "⚙️",
                    fontSize = 110.sp,
                    modifier = Modifier.rotate(rotation)
                )
            }

            Spacer(modifier = Modifier.height(60.dp))

            // Título con animación
            if (titleVisible) {
                AnimatedRollingTextSlide3(
                    text = "Personaliza tu experiencia",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Descripción con animación
            if (descriptionVisible) {
                AnimatedRollingTextSlide3(
                    text = "Cuida de tu mascota financiera.\nEstablece metas de ahorro y\nconfigura categorías personalizadas.",
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
                    text = "Comenzar",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF9C27B0)
                )
            }
        }
    }
}

/**
 * Engranajes flotantes en el fondo para Slide3
 */
@Composable
private fun FloatingGearsSlide3() {
    // Engranaje 1 - Pequeño arriba
    val infiniteTransition1 = rememberInfiniteTransition(label = "gear1")
    val gear1Rotation by infiniteTransition1.animateFloat(
        initialValue = 0f,
        targetValue = -360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "gear1Rotation"
    )

    val gear1Y by infiniteTransition1.animateFloat(
        initialValue = 0f,
        targetValue = 15f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gear1Y"
    )

    Box(
        modifier = Modifier
            .size(70.dp)
            .offset(x = 40.dp, y = (120 + gear1Y).dp)
            .alpha(0.15f)
            .rotate(gear1Rotation)
            .background(
                Color.White,
                CircleShape
            )
    ) {
        Text(
            text = "⚙️",
            fontSize = 35.sp,
            modifier = Modifier.align(Alignment.Center)
        )
    }

    // Engranaje 2 - Mediano lado izquierdo
    val infiniteTransition2 = rememberInfiniteTransition(label = "gear2")
    val gear2Rotation by infiniteTransition2.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "gear2Rotation"
    )

    val gear2Y by infiniteTransition2.animateFloat(
        initialValue = 0f,
        targetValue = -20f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gear2Y"
    )

    Box(
        modifier = Modifier
            .size(90.dp)
            .offset(x = (-35).dp, y = (450 + gear2Y).dp)
            .alpha(0.12f)
            .rotate(gear2Rotation)
            .background(
                Color.White,
                CircleShape
            )
    ) {
        Text(
            text = "⚙️",
            fontSize = 45.sp,
            modifier = Modifier.align(Alignment.Center)
        )
    }

    // Engranaje 3 - Pequeño abajo derecha
    val infiniteTransition3 = rememberInfiniteTransition(label = "gear3")
    val gear3Rotation by infiniteTransition3.animateFloat(
        initialValue = 0f,
        targetValue = -360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "gear3Rotation"
    )

    val gear3Y by infiniteTransition3.animateFloat(
        initialValue = 0f,
        targetValue = 25f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gear3Y"
    )

    Box(
        modifier = Modifier
            .size(80.dp)
            .offset(x = 300.dp, y = (550 + gear3Y).dp)
            .alpha(0.1f)
            .rotate(gear3Rotation)
            .background(
                Color.White,
                CircleShape
            )
    ) {
        Text(
            text = "⚙️",
            fontSize = 40.sp,
            modifier = Modifier.align(Alignment.Center)
        )
    }

    // Estrellitas de personalización
    Box(
        modifier = Modifier
            .size(60.dp)
            .offset(x = 320.dp, y = 200.dp)
            .alpha(0.2f)
    ) {
        Text(
            text = "✨",
            fontSize = 30.sp,
            modifier = Modifier.align(Alignment.Center)
        )
    }

    Box(
        modifier = Modifier
            .size(50.dp)
            .offset(x = 20.dp, y = 600.dp)
            .alpha(0.15f)
    ) {
        Text(
            text = "🎨",
            fontSize = 25.sp,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

/**
 * Función de texto animado para Slide3
 */
@Composable
private fun AnimatedRollingTextSlide3(
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
fun PreviewSlide3() {
    Slide3(onNext = {
        println("Navegando a la pantalla principal...")
    })
}