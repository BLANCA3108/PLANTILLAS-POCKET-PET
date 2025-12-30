package com.example.mascotafinanciera.pantallas.juegos

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mascotafinanciera.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.random.Random

data class Moneda(
    val id: Int,
    val x: Float,
    var y: Float,
    val tipo: TipoMoneda,
    var atrapada: Boolean = false
)

enum class TipoMoneda(val emoji: String, val puntos: Int) {
    BRONCE("🪙", 1),
    PLATA("💰", 3),
    ORO("💎", 5),
    BOMBA("💣", -5)
}

data class EstadoJuegoMonedas(
    val puntuacion: Int = 0,
    val vidas: Int = 3,
    val nivel: Int = 1,
    val tiempoRestante: Int = 30,
    val monedasAtrapadas: Int = 0,
    val juegoActivo: Boolean = false,
    val juegoTerminado: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaAtrapaMonedasJuego(
    onVolver: () -> Unit = {}
) {
    var estadoJuego by remember { mutableStateOf(EstadoJuegoMonedas()) }
    var monedas by remember { mutableStateOf<List<Moneda>>(emptyList()) }
    var siguienteId by remember { mutableStateOf(0) }

    LaunchedEffect(estadoJuego.juegoActivo) {
        if (estadoJuego.juegoActivo && estadoJuego.tiempoRestante > 0) {
            delay(1000)
            estadoJuego = estadoJuego.copy(
                tiempoRestante = estadoJuego.tiempoRestante - 1
            )

            if (estadoJuego.tiempoRestante <= 0 || estadoJuego.vidas <= 0) {
                estadoJuego = estadoJuego.copy(
                    juegoActivo = false,
                    juegoTerminado = true
                )
            }
        }
    }

    LaunchedEffect(estadoJuego.juegoActivo) {
        while (estadoJuego.juegoActivo) {
            delay(1000L / estadoJuego.nivel) // Más rápido en niveles altos

            val tipo = when (Random.nextInt(100)) {
                in 0..59 -> TipoMoneda.BRONCE
                in 60..84 -> TipoMoneda.PLATA
                in 85..94 -> TipoMoneda.ORO
                else -> TipoMoneda.BOMBA
            }

            val nuevaMoneda = Moneda(
                id = siguienteId++,
                x = Random.nextFloat() * 0.8f + 0.1f,
                y = 0f,
                tipo = tipo
            )

            monedas = monedas + nuevaMoneda
        }
    }

    LaunchedEffect(estadoJuego.juegoActivo, monedas.size) {
        while (estadoJuego.juegoActivo) {
            delay(50)
            monedas = monedas.map { moneda ->
                if (!moneda.atrapada) {
                    moneda.copy(y = moneda.y + (0.02f * estadoJuego.nivel))
                } else {
                    moneda
                }
            }.filter { it.y < 1.2f && !it.atrapada }

            // Perder vida si una moneda buena se escapa
            val monedasEscapadas = monedas.filter {
                it.y >= 1.0f && !it.atrapada && it.tipo != TipoMoneda.BOMBA
            }
            if (monedasEscapadas.isNotEmpty()) {
                estadoJuego = estadoJuego.copy(
                    vidas = (estadoJuego.vidas - monedasEscapadas.size).coerceAtLeast(0)
                )
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "Atrapa Monedas",
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("🪙", fontSize = 20.sp)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { /* Volver */ }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = RosaPastel,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            RosaPastelClaro.copy(alpha = 0.3f),
                            AmarilloPastel.copy(alpha = 0.2f)
                        )
                    )
                )
        ) {
            if (!estadoJuego.juegoActivo && !estadoJuego.juegoTerminado) {
                PantallaInicio(
                    onIniciar = {
                        estadoJuego = EstadoJuegoMonedas(juegoActivo = true)
                        monedas = emptyList()
                        siguienteId = 0
                    }
                )
            } else if (estadoJuego.juegoTerminado) {
                PantallaFinJuego(
                    puntuacion = estadoJuego.puntuacion,
                    monedasAtrapadas = estadoJuego.monedasAtrapadas,
                    onReintentar = {
                        estadoJuego = EstadoJuegoMonedas(juegoActivo = true)
                        monedas = emptyList()
                        siguienteId = 0
                    },
                    onSalir = { /* Volver al menú */ }
                )
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    // HUD superior
                    HUDSuperior(estadoJuego)

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        monedas.forEach { moneda ->
                            MonedaCayendo(
                                moneda = moneda,
                                onAtrapada = {
                                    monedas = monedas.map {
                                        if (it.id == moneda.id) it.copy(atrapada = true)
                                        else it
                                    }

                                    when (moneda.tipo) {
                                        TipoMoneda.BOMBA -> {
                                            estadoJuego = estadoJuego.copy(
                                                puntuacion = (estadoJuego.puntuacion + moneda.tipo.puntos).coerceAtLeast(0),
                                                vidas = (estadoJuego.vidas - 1).coerceAtLeast(0)
                                            )
                                        }
                                        else -> {
                                            estadoJuego = estadoJuego.copy(
                                                puntuacion = estadoJuego.puntuacion + moneda.tipo.puntos,
                                                monedasAtrapadas = estadoJuego.monedasAtrapadas + 1,
                                                nivel = (estadoJuego.monedasAtrapadas / 10) + 1
                                            )
                                        }
                                    }
                                }
                            )
                        }

                        Text(
                            text = "¡Toca las monedas! 👆",
                            fontSize = 14.sp,
                            color = GrisMedio,
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HUDSuperior(estado: EstadoJuegoMonedas) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Puntuación
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${estado.puntuacion}",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = AmarilloPastel
                )
                Text(
                    text = "Puntos",
                    fontSize = 11.sp,
                    color = GrisMedio
                )
            }

            // Vidas
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row {
                    repeat(estado.vidas) {
                        Text(text = "❤️", fontSize = 20.sp)
                    }
                    repeat(3 - estado.vidas) {
                        Text(text = "🖤", fontSize = 20.sp)
                    }
                }
                Text(
                    text = "Vidas",
                    fontSize = 11.sp,
                    color = GrisMedio
                )
            }

            // Tiempo
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${estado.tiempoRestante}",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (estado.tiempoRestante <= 10) CoralPastel else VerdeMenta
                )
                Text(
                    text = "Segundos",
                    fontSize = 11.sp,
                    color = GrisMedio
                )
            }

            // Nivel
            Surface(
                shape = CircleShape,
                color = MoradoPrincipal
            ) {
                Text(
                    text = "N${estado.nivel}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
    }
}

@Composable
fun BoxScope.MonedaCayendo(
    moneda: Moneda,
    onAtrapada: () -> Unit
) {
    val scale by remember { mutableStateOf(1f) }
    val rotation by remember { mutableStateOf(Random.nextFloat() * 360f) }

    if (!moneda.atrapada) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.15f)
                .aspectRatio(1f)
                .align(Alignment.TopStart)
                .offset(
                    x = (moneda.x * 300).dp,
                    y = (moneda.y * 600).dp
                )
                .scale(scale)
                .rotate(rotation)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    onAtrapada()
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = moneda.tipo.emoji,
                fontSize = 40.sp
            )
        }
    }
}

@Composable
fun BoxScope.PantallaInicio(onIniciar: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "🪙",
            fontSize = 100.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Atrapa Monedas",
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            color = RosaPastel
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(4.dp),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Cómo Jugar:",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = GrisTexto
                )

                Spacer(modifier = Modifier.height(16.dp))

                InstruccionJuego("🪙", "Toca monedas de bronce", "+1 punto")
                InstruccionJuego("💰", "Toca monedas de plata", "+3 puntos")
                InstruccionJuego("💎", "Toca monedas de oro", "+5 puntos")
                InstruccionJuego("💣", "¡Evita las bombas!", "-5 puntos")

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "• Tienes 3 vidas\n• 30 segundos de tiempo\n• ¡Sube de nivel atrapando más!",
                    fontSize = 12.sp,
                    color = GrisMedio,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onIniciar,
            colors = ButtonDefaults.buttonColors(
                containerColor = RosaPastel
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = "¡INICIAR JUEGO!",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun InstruccionJuego(emoji: String, texto: String, puntos: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = emoji, fontSize = 24.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = texto,
                fontSize = 13.sp,
                color = GrisTexto
            )
        }
        Text(
            text = puntos,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = VerdeMenta
        )
    }
}

@Composable
fun BoxScope.PantallaFinJuego(
    puntuacion: Int,
    monedasAtrapadas: Int,
    onReintentar: () -> Unit,
    onSalir: () -> Unit
) {
    val monedasGanadas = (puntuacion / 2).coerceAtLeast(1)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (puntuacion >= 50) "🎉" else if (puntuacion >= 20) "😊" else "😅",
            fontSize = 80.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "¡Juego Terminado!",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = GrisTexto
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(6.dp),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "$puntuacion",
                    fontSize = 56.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = AmarilloPastel
                )
                Text(
                    text = "Puntos Totales",
                    fontSize = 14.sp,
                    color = GrisMedio
                )

                Spacer(modifier = Modifier.height(20.dp))

                Divider(color = GrisClaro)

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    EstadisticaFinal("🪙", "$monedasAtrapadas", "Atrapadas")
                    EstadisticaFinal("💰", "$monedasGanadas", "Ganadas")
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Mensaje de recompensa
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = VerdeMenta.copy(alpha = 0.2f)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "🎁", fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "¡Has ganado $monedasGanadas monedas!",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = GrisTexto
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Botones
        Button(
            onClick = onReintentar,
            colors = ButtonDefaults.buttonColors(
                containerColor = RosaPastel
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(
                Icons.Default.Refresh,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Jugar de Nuevo",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onSalir,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = GrisMedio
            )
        ) {
            Icon(
                Icons.Default.Home,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Volver al Menú",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun EstadisticaFinal(emoji: String, valor: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = emoji, fontSize = 32.sp)
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = valor,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = GrisTexto
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = GrisMedio
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewAtrapaMonedasJuego() {
    MascotaFinancieraTheme {
        PantallaAtrapaMonedasJuego()
    }
}