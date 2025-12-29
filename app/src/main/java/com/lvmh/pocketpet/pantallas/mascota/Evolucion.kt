package com.lvmh.pocketpet.pantallas.mascota
import androidx.compose.foundation.layout.*

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mascotafinanciera.ui.theme.*

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
fun PantallaEvolucion(
    onVolver: () -> Unit = {}
) {
    var pantallaSeleccionada by remember { mutableStateOf(1) }

    val nivelActual = 5
    val xpActual = 650
    val xpProximoNivel = 1000
    val saludActual = 75

    val infiniteTransition = rememberInfiniteTransition(label = "escala")
    val escala by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "anim"
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
                title = { Text("Evolución 🌟", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MoradoPrincipal,
                    titleContentColor = Color.White
                ),
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    Brush.verticalGradient(
                        listOf(
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
                shape = RoundedCornerShape(32.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "Nivel $nivelActual",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MoradoPrincipal
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Box(
                        modifier = Modifier.size(180.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(160.dp)
                                .scale(escala)
                                .clip(CircleShape)
                                .background(Color.White)
                                .border(4.dp, MoradoPrincipal, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = obtenerEmojiMascota(saludActual),
                                fontSize = 90.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = niveles[nivelActual - 1].nombre,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MoradoPrincipal
                    )

                    Text(
                        text = "\"${obtenerMensajeNivel(nivelActual)}\"",
                        fontSize = 14.sp,
                        fontStyle = FontStyle.Italic,
                        color = GrisMedio,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Línea de Evolución",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = GrisTexto,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(niveles) { nivel ->
                    TarjetaNivel(
                        nivel = nivel,
                        esActual = nivel.nivel == nivelActual
                    )
                }
            }
        }
    }
}

@Composable
fun TarjetaNivel(
    nivel: NivelMascota,
    esActual: Boolean
) {
    Card(
        modifier = Modifier
            .width(120.dp)
            .height(160.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(if (esActual) 8.dp else 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(GrisClaro),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = nivel.emoji,
                    fontSize = 32.sp,
                    modifier = Modifier.graphicsLayer(
                        alpha = if (nivel.desbloqueado) 1f else 0.3f
                    )
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Nivel ${nivel.nivel}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = nivel.nombre,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

fun obtenerMensajeNivel(nivel: Int): String =
    when (nivel) {
        1 -> "El comienzo de una gran aventura"
        2 -> "Dando los primeros pasos"
        3 -> "Aprendiendo a gestionar"
        4 -> "Creciendo con disciplina"
        5 -> "Un gestor financiero responsable"
        6 -> "Maestro de las finanzas"
        7 -> "Experto en ahorro e inversión"
        else -> "Leyenda financiera"
    }

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewPantallaEvolucion() {
    MascotaFinancieraTheme {
        PantallaEvolucion()
    }
}
