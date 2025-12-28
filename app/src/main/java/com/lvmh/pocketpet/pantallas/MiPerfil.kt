package com.lvmh.pocketpet.pantallas

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MiPerfil(onBack: () -> Unit = {}) {
    // Colores cálidos rosado y morado suave
    val rosadoSuave = Color(0xFFFFB6C1)
    val moradoSuave = Color(0xFFE6B0FF)
    val rosadoClaro = Color(0xFFFFF0F5)
    val moradoClaro = Color(0xFFF8F0FF)

    // Estados
    var totalIngresos by remember { mutableStateOf(15420.50) }
    var totalGastos by remember { mutableStateOf(8920.00) }
    var balanceActual by remember { mutableStateOf(6500.50) }
    var transacciones by remember { mutableStateOf(47) }
    var metaMes by remember { mutableStateOf(10000.0) }
    var progresoMeta by remember { mutableStateOf(6500.0) }

    // Animación de entrada
    var startAnimation by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(100)
        startAnimation = true
    }

    val scale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.95f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        rosadoClaro,
                        moradoClaro
                    )
                )
            )
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = onBack,
                                modifier = Modifier
                                    .size(44.dp)
                                    .shadow(
                                        elevation = 8.dp,
                                        shape = CircleShape
                                    )
                                    .background(Color.White, CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "Volver",
                                    tint = moradoSuave
                                )
                            }
                            Spacer(Modifier.width(12.dp))
                            Text(
                                "👤 Mi Perfil",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 22.sp
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = moradoSuave
                    ),
                    modifier = Modifier.shadow(elevation = 4.dp)
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .scale(scale)
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Avatar con animación
                AvatarAnimado(moradoSuave)

                Spacer(modifier = Modifier.height(24.dp))

                // Nombre de usuario
                Text(
                    text = "Usuario PocketPet",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = moradoSuave
                )

                Text(
                    text = "usuario@pocketpet.com",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Título de estadísticas
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = null,
                        tint = rosadoSuave
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "RESUMEN FINANCIERO",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF555555)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Tarjetas de estadísticas
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TarjetaEstadisticaMejorada(
                        titulo = "Total Ingresos",
                        valor = "S/. ${String.format("%.2f", totalIngresos)}",
                        icono = "💰",
                        color = Color(0xFF4CAF50),
                        modifier = Modifier.weight(1f)
                    )
                    TarjetaEstadisticaMejorada(
                        titulo = "Total Gastos",
                        valor = "S/. ${String.format("%.2f", totalGastos)}",
                        icono = "💸",
                        color = Color(0xFFFF6B6B),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TarjetaEstadisticaMejorada(
                        titulo = "Balance Actual",
                        valor = "S/. ${String.format("%.2f", balanceActual)}",
                        icono = "💳",
                        color = moradoSuave,
                        modifier = Modifier.weight(1f)
                    )
                    TarjetaEstadisticaMejorada(
                        titulo = "Transacciones",
                        valor = "$transacciones",
                        icono = "📊",
                        color = rosadoSuave,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Meta del mes
                MetaDelMesMejorada(
                    progreso = progresoMeta,
                    meta = metaMes,
                    colorProgreso = moradoSuave
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Mascota del usuario
                MascotaUsuario()

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

/**
 * Avatar animado
 */
@Composable
private fun AvatarAnimado(color: Color) {
    val infiniteTransition = rememberInfiniteTransition(label = "avatar")

    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val rotation by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rotation"
    )

    Box(
        modifier = Modifier
            .size(140.dp)
            .scale(scale)
            .rotate(rotation)
            .shadow(
                elevation = 20.dp,
                shape = CircleShape,
                ambientColor = color.copy(alpha = 0.5f)
            )
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White,
                        Color(0xFFF8F0FF)
                    )
                ),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "🐕",
            fontSize = 80.sp
        )
    }
}

/**
 * Tarjeta de estadística mejorada
 */
@Composable
private fun TarjetaEstadisticaMejorada(
    titulo: String,
    valor: String,
    icono: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(110.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(20.dp)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.15f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = icono,
                fontSize = 32.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = titulo,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF555555),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = valor,
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                color = color,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Sección de Meta del Mes mejorada
 */
@Composable
private fun MetaDelMesMejorada(
    progreso: Double,
    meta: Double,
    colorProgreso: Color
) {
    val porcentaje = (progreso / meta * 100).toFloat()

    // Animación de la barra de progreso
    val animatedProgress by animateFloatAsState(
        targetValue = porcentaje / 100f,
        animationSpec = tween(1000, easing = EaseOutCubic),
        label = "progress"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(20.dp)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "META DEL MES",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = colorProgreso
                    )
                }
                Text(
                    text = "${porcentaje.toInt()}%",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = colorProgreso
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Barra de progreso animada
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp)
                    .background(Color(0xFFE0E0E0), RoundedCornerShape(8.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedProgress)
                        .fillMaxHeight()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    colorProgreso,
                                    colorProgreso.copy(alpha = 0.7f)
                                )
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${porcentaje.toInt()}% completado",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray
                )
                Text(
                    text = "S/. ${String.format("%.0f", progreso)} / S/. ${String.format("%.0f", meta)}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF555555)
                )
            }
        }
    }
}

/**
 * Sección de mascota del usuario
 */
@Composable
private fun MascotaUsuario() {
    val infiniteTransition = rememberInfiniteTransition(label = "mascota")

    val bounce by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -10f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bounce"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(20.dp)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    tint = Color(0xFFFF6B6B),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "TU MASCOTA",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF555555)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier.offset(y = bounce.dp)
            ) {
                Text(
                    text = "🐕",
                    fontSize = 80.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Toby",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF555555)
            )

            Text(
                text = "Nivel 5 • Feliz 😊",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Barras de estadísticas de mascota
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                BarraMascota("💚", "Salud", 0.8f, Color(0xFF4CAF50), Modifier.weight(1f))
                BarraMascota("😊", "Felicidad", 0.9f, Color(0xFFFFD700), Modifier.weight(1f))
                BarraMascota("🍖", "Hambre", 0.6f, Color(0xFFFF6B6B), Modifier.weight(1f))
            }
        }
    }
}

/**
 * Barra de estadística de mascota
 */
@Composable
private fun BarraMascota(
    emoji: String,
    titulo: String,
    progreso: Float,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = emoji,
            fontSize = 24.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = titulo,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .background(Color(0xFFE0E0E0), RoundedCornerShape(3.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progreso)
                    .fillMaxHeight()
                    .background(color, RoundedCornerShape(3.dp))
            )
        }
    }
}

// Preview
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewMiPerfil() {
    MiPerfil()
}