package com.example.mascotafinanciera.pantallas.mascota

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mascotafinanciera.ui.theme.*
/*----MASCOTAAAAAA---*/
data class EstadoMascota(
    val nombre: String = "POCKET PET",
    val salud: Int = 75, // 0-100
    val nivel: Int = 5,
    val experiencia: Int = 650,
    val experienciaMax: Int = 1000
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPrincipalMascota(
    estado: EstadoMascota = EstadoMascota()
) {
    var pantalla_seleccionada by remember { mutableStateOf(1) }
    val infiniteTransition = rememberInfiniteTransition(label = "respiracion")
    val escala by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "escala_respiracion"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "Mi Mascota",
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("🐾", fontSize = 20.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MoradoPrincipal,
                    titleContentColor = Color.White
                ),
                actions = {
                    IconButton(onClick = { /* Notificaciones */ }) {
                        Icon(
                            Icons.Default.Notifications,
                            contentDescription = "Notificaciones",
                            tint = Color.White
                        )
                    }
                    IconButton(onClick = { /* Configuración */ }) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "Configuración",
                            tint = Color.White
                        )
                    }
                }
            )
        },
        bottomBar = {
            BarraNavegacionInferior(
                pantalla_seleccionada = pantalla_seleccionada,
                onPantallaSeleccionada = { pantalla_seleccionada = it }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(FondoApp)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = estado.nombre,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = MoradoPrincipal
                )
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MoradoPrincipal,
                    shadowElevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Nivel ${estado.nivel}",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            BarraExperiencia(estado.experiencia, estado.experienciaMax)
            Spacer(modifier = Modifier.height(32.dp))
            Box(
                modifier = Modifier
                    .size(220.dp)
                    .scale(escala),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(220.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    obtenerColorSalud(estado.salud).copy(alpha = 0.15f),
                                    Color.Transparent
                                )
                            )
                        )
                )
                Box(
                    modifier = Modifier
                        .size(180.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = obtenerEmojiMascota(estado.salud),
                        fontSize = 100.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (estado.salud >= 80) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(5) {
                        Text(
                            text = "✨",
                            fontSize = 20.sp,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            TarjetaSalud(estado.salud)

            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                BotonAccion(
                    icono = Icons.Default.FavoriteBorder,
                    texto = "Cuidar",
                    color = RosaPastel
                )
                BotonAccion(
                    icono = Icons.Default.Star,
                    texto = "Logros",
                    color = AmarilloPastel
                )

                BotonAccion(
                    icono = Icons.Default.ShoppingCart,
                    texto = "Tienda",
                    color = VerdeMenta
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ========== ESTADÍSTICAS RÁPIDAS ==========
            TarjetaEstadisticas()

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
@Composable
fun BarraNavegacionInferior(
    pantalla_seleccionada: Int,
    onPantallaSeleccionada: (Int) -> Unit
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Default.Home,
                    contentDescription = "Inicio",
                    modifier = Modifier.size(26.dp)
                )
            },
            label = { Text("Inicio", fontSize = 12.sp, fontWeight = FontWeight.Medium) },
            selected = pantalla_seleccionada == 0,
            onClick = { onPantallaSeleccionada(0) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = GrisTexto,
                selectedTextColor = GrisTexto,
                indicatorColor = GrisClaro,
                unselectedIconColor = GrisMedio,
                unselectedTextColor = GrisMedio
            )
        )
        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Default.Favorite,
                    contentDescription = "Mascota",
                    modifier = Modifier.size(26.dp)
                )
            },
            label = { Text("Mascota", fontSize = 12.sp, fontWeight = FontWeight.Medium) },
            selected = pantalla_seleccionada == 1,
            onClick = { onPantallaSeleccionada(1) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = RosaPastel,
                selectedTextColor = RosaPastel,
                indicatorColor = RosaPastelClaro.copy(alpha = 0.3f),
                unselectedIconColor = GrisMedio,
                unselectedTextColor = GrisMedio
            )
        )
        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Default.DateRange,
                    contentDescription = "Análisis",
                    modifier = Modifier.size(26.dp)
                )
            },
            label = { Text("Análisis", fontSize = 12.sp, fontWeight = FontWeight.Medium) },
            selected = pantalla_seleccionada == 2,
            onClick = { onPantallaSeleccionada(2) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = GrisTexto,
                selectedTextColor = GrisTexto,
                indicatorColor = GrisClaro,
                unselectedIconColor = GrisMedio,
                unselectedTextColor = GrisMedio
            )
        )
        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Default.MoreVert,
                    contentDescription = "Más",
                    modifier = Modifier.size(26.dp)
                )
            },
            label = { Text("Más", fontSize = 12.sp, fontWeight = FontWeight.Medium) },
            selected = pantalla_seleccionada == 3,
            onClick = { onPantallaSeleccionada(3) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = GrisTexto,
                selectedTextColor = GrisTexto,
                indicatorColor = GrisClaro,
                unselectedIconColor = GrisMedio,
                unselectedTextColor = GrisMedio
            )
        )
    }
}
@Composable
fun BarraExperiencia(actual: Int, maximo: Int) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "✨", fontSize = 18.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Experiencia",
                    fontSize = 15.sp,
                    color = GrisMedio,
                    fontWeight = FontWeight.Medium
                )
            }
            Text(
                text = "$actual / $maximo XP",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MoradoPrincipal
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(14.dp)
                .clip(RoundedCornerShape(7.dp))
                .background(GrisClaro)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(actual.toFloat() / maximo.toFloat())
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(7.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                MoradoPrincipal,
                                MoradoClaro
                            )
                        )
                    )
            )
        }
    }
}
@Composable
fun TarjetaSalud(salud: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(6.dp),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "💚", fontSize = 22.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Estado de Salud",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = GrisTexto
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Barra de salud circular
            Box(contentAlignment = Alignment.Center) {
                // Círculo de fondo
                CircularProgressIndicator(
                    progress = 1f,
                    modifier = Modifier.size(130.dp),
                    strokeWidth = 12.dp,
                    color = GrisClaro,
                    trackColor = Color.Transparent
                )

                // Progreso real
                CircularProgressIndicator(
                    progress = salud / 100f,
                    modifier = Modifier.size(130.dp),
                    strokeWidth = 12.dp,
                    color = obtenerColorSalud(salud),
                    trackColor = Color.Transparent
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$salud",
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        color = obtenerColorSalud(salud)
                    )
                    Text(
                        text = obtenerTextoEstado(salud),
                        fontSize = 16.sp,
                        color = GrisMedio,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
@Composable
fun BotonAccion(
    icono: ImageVector,
    texto: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(85.dp)
    ) {
        FloatingActionButton(
            onClick = { /* Acción */ },
            containerColor = color,
            contentColor = Color.White,
            modifier = Modifier.size(60.dp),
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = 4.dp,
                pressedElevation = 6.dp
            )
        ) {
            Icon(
                icono,
                contentDescription = texto,
                modifier = Modifier.size(26.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = texto,
            fontSize = 12.sp,
            color = GrisTexto,
            fontWeight = FontWeight.Medium
        )
    }
}
@Composable
fun TarjetaEstadisticas() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            ItemEstadistica("💰", "S/ 1,250", "Ahorrado")

            Divider(
                modifier = Modifier
                    .height(60.dp)
                    .width(1.dp),
                color = GrisClaro
            )

            ItemEstadistica("📊", "23 días", "Racha")

            Divider(
                modifier = Modifier
                    .height(60.dp)
                    .width(1.dp),
                color = GrisClaro
            )

            ItemEstadistica("🎯", "3/5", "Metas")
        }
    }
}

@Composable
fun ItemEstadistica(emoji: String, valor: String, etiqueta: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Text(text = emoji, fontSize = 26.sp)
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = valor,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = GrisTexto
        )
        Text(
            text = etiqueta,
            fontSize = 12.sp,
            color = GrisMedio
        )
    }
}
fun obtenerColorSalud(salud: Int): Color {
    return when (salud) {
        in 0..20 -> CoralPastel
        in 21..40 -> AmarilloPastel
        in 41..60 -> AzulPastel
        in 61..80 -> VerdeMenta
        else -> MoradoPrincipal
    }
}

fun obtenerTextoEstado(salud: Int): String {
    return when (salud) {
        in 0..20 -> "Crítico"
        in 21..40 -> "Alerta"
        in 41..60 -> "Estable"
        in 61..80 -> "Saludable"
        else -> "Próspero"
    }
}

fun obtenerEmojiMascota(salud: Int): String {
    return when (salud) {
        in 0..20 -> "🐢"
        in 21..40 -> "🐰"
        in 41..60 -> "🐱"
        in 61..80 -> "🐶"
        else -> "🦄"
    }
}
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewPantallaMascota() {
    MascotaFinancieraTheme {
        PantallaPrincipalMascota()
    }
}