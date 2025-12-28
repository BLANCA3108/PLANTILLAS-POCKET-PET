package com.lvmh.pocketpet.pantallas.mascota

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mascotafinanciera.ui.theme.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontStyle

data class NivelMascota(
    val nivel: Int,
    val nombre: String,
    val emoji: String,
    val xpRequerido: Int,
    val desbloqueado: Boolean,
    val recompensa: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaEvolucion() {
    var pantalla_seleccionada by remember { mutableStateOf(1) }

    val nivelActual = 5
    val xpActual = 650
    val xpProximoNivel = 1000
    val saludActual = 75
    val infiniteTransition = rememberInfiniteTransition(label = "respiracion")
    val escala by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "escala"
    )

    val niveles = remember {
        listOf(
            NivelMascota(1, "Huevo", "🥚", 0, true, "Inicio del viaje"),
            NivelMascota(2, "Bebé", "🐣", 100, true, "+10 Salud"),
            NivelMascota(3, "Cachorro", "🐤", 300, true, "+15 Salud"),
            NivelMascota(4, "Joven", "🐥", 600, true, "Logro desbloqueado"),
            NivelMascota(5, "Adulto", "🐦", 1000, true, "+20 Salud"),
            NivelMascota(6, "Experto", "🦅", 1500, false, "Habilidad especial"),
            NivelMascota(7, "Maestro", "🦚", 2200, false, "+30 Salud"),
            NivelMascota(8, "Legendario", "🦄", 3000, false, "Forma legendaria")
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Evolución 🌟",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MoradoPrincipal,
                    titleContentColor = Color.White
                ),
                navigationIcon = {
                    IconButton(onClick = { /* Volver */ }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Volver",
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
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MoradoClaro.copy(alpha = 0.15f),
                            AzulCielo.copy(alpha = 0.1f),
                            Color.White
                        )
                    )
                )
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(8.dp),
                shape = RoundedCornerShape(32.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MoradoPrincipal,
                        shadowElevation = 4.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Nivel $nivelActual",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Box(
                        modifier = Modifier.size(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(200.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(
                                            MoradoPrincipal.copy(alpha = 0.1f),
                                            Color.Transparent
                                        )
                                    )
                                )
                        )

                        // Mascota
                        Box(
                            modifier = Modifier
                                .size(160.dp)
                                .scale(escala)
                                .clip(CircleShape)
                                .background(Color.White)
                                .border(4.dp, MoradoPrincipal.copy(alpha = 0.3f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = obtenerEmojiMascota(saludActual),
                                fontSize = 100.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Nombre del nivel
                    Text(
                        text = niveles[nivelActual - 1].nombre,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MoradoPrincipal
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "\"${obtenerMensajeNivel(nivelActual)}\"",
                        fontSize = 14.sp,
                        color = GrisMedio,
                        textAlign = TextAlign.Center,
                        fontStyle = FontStyle.Italic
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(4.dp),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Próximo Nivel",
                                fontSize = 14.sp,
                                color = GrisMedio
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = niveles[nivelActual].emoji,
                                    fontSize = 24.sp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = niveles[nivelActual].nombre,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GrisTexto
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "$xpActual / $xpProximoNivel",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MoradoPrincipal
                            )
                            Text(
                                text = "XP",
                                fontSize = 12.sp,
                                color = GrisMedio
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Barra de progreso grande
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(16.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(GrisClaro)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(xpActual.toFloat() / xpProximoNivel.toFloat())
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(8.dp))
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

                    Spacer(modifier = Modifier.height(12.dp))

                    // Porcentaje
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Te faltan ${xpProximoNivel - xpActual} XP",
                            fontSize = 13.sp,
                            color = GrisMedio
                        )
                        Text(
                            text = "${(xpActual * 100 / xpProximoNivel)}%",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MoradoPrincipal
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ========== ESTADÍSTICAS ==========
            Text(
                text = "Estadísticas",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = GrisTexto,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                EstadisticaEvolucion(
                    emoji = "💪",
                    valor = "$saludActual/100",
                    etiqueta = "Salud",
                    color = VerdeMenta,
                    modifier = Modifier.weight(1f)
                )
                EstadisticaEvolucion(
                    emoji = "⭐",
                    valor = "$xpActual",
                    etiqueta = "XP Total",
                    color = AmarilloPastel,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                EstadisticaEvolucion(
                    emoji = "🎯",
                    valor = "${niveles.count { it.desbloqueado }}/${niveles.size}",
                    etiqueta = "Niveles",
                    color = AzulPastel,
                    modifier = Modifier.weight(1f)
                )
                EstadisticaEvolucion(
                    emoji = "🏆",
                    valor = "12",
                    etiqueta = "Logros",
                    color = RosaPastel,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ========== LÍNEA DE TIEMPO DE NIVELES ==========
            Text(
                text = "Línea de Evolución",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = GrisTexto,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                items(niveles) { nivel ->
                    TarjetaNivel(
                        nivel = nivel,
                        esActual = nivel.nivel == nivelActual
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun EstadisticaEvolucion(
    emoji: String,
    valor: String,
    etiqueta: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(90.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(3.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = emoji, fontSize = 28.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = valor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = etiqueta,
                fontSize = 11.sp,
                color = GrisMedio
            )
        }
    }
}
@Composable
fun TarjetaNivel(nivel: NivelMascota, esActual: Boolean) {
    Card(
        modifier = Modifier
            .width(120.dp)
            .height(160.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (esActual) MoradoPrincipal.copy(alpha = 0.1f)
            else if (nivel.desbloqueado) Color.White
            else Color.White.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (esActual) 8.dp else if (nivel.desbloqueado) 4.dp else 2.dp
        ),
        shape = RoundedCornerShape(20.dp),
        border = if (esActual) BorderStroke(2.dp, MoradoPrincipal) else null
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Emoji
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(
                        if (nivel.desbloqueado)
                            if (esActual) MoradoPrincipal.copy(alpha = 0.2f)
                            else GrisClaro
                        else Color.Transparent
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = nivel.emoji,
                    fontSize = 36.sp,
                    modifier = Modifier.graphicsLayer(
                        alpha = if (nivel.desbloqueado) 1f else 0.3f
                    )
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Nivel
                Text(
                    text = "Nivel ${nivel.nivel}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (esActual) MoradoPrincipal else if (nivel.desbloqueado) GrisTexto else GrisMedio
                )

                // Nombre
                Text(
                    text = nivel.nombre,
                    fontSize = 14.sp,
                    fontWeight = if (esActual) FontWeight.Bold else FontWeight.Normal,
                    color = if (nivel.desbloqueado) GrisTexto else GrisMedio,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Recompensa
                if (nivel.desbloqueado) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (esActual) MoradoPrincipal.copy(alpha = 0.2f) else AzulCielo.copy(alpha = 0.3f)
                    ) {
                        Text(
                            text = nivel.recompensa,
                            fontSize = 10.sp,
                            color = if (esActual) MoradoPrincipal else GrisMedio,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                            maxLines = 1
                        )
                    }
                } else {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = "Bloqueado",
                        tint = GrisMedio,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

fun obtenerMensajeNivel(nivel: Int): String {
    return when (nivel) {
        1 -> "El comienzo de una gran aventura"
        2 -> "Dando los primeros pasos"
        3 -> "Aprendiendo a gestionar"
        4 -> "Creciendo con disciplina"
        5 -> "Un gestor financiero responsable"
        6 -> "Maestro de las finanzas"
        7 -> "Experto en ahorro e inversión"
        else -> "Leyenda financiera"
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewPantallaEvolucion() {
    MascotaFinancieraTheme {
        PantallaEvolucion()
    }
}