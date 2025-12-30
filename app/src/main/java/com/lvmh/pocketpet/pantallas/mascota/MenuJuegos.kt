package com.example.mascotafinanciera.pantallas.juegos

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mascotafinanciera.ui.theme.*
import androidx.compose.ui.draw.alpha
data class MiniJuego(
    val id: String,
    val nombre: String,
    val emoji: String,
    val descripcion: String,
    val dificultad: Dificultad,
    val recompensaMin: Int,
    val recompensaMax: Int,
    val color: Color,
    val desbloqueado: Boolean = true,
    val nivelRequerido: Int = 1,
    val vecesJugado: Int = 0,
    val mejorPuntuacion: Int = 0
)

enum class Dificultad(val emoji: String, val texto: String, val color: Color) {
    FACIL("⭐", "Fácil", VerdeMenta),
    MEDIO("⭐⭐", "Medio", AmarilloPastel),
    DIFICIL("⭐⭐⭐", "Difícil", CoralPastel)
}

data class EstadisticasJuegos(
    val monedasGanadas: Int = 350,
    val partidasJugadas: Int = 45,
    val juegoFavorito: String = "Ruleta Financiera"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaMenuJuegos(
    onVolver: () -> Unit = {},
    onJuegoSeleccionado: (String) -> Unit = {}
) {
    var estadisticas by remember { mutableStateOf(EstadisticasJuegos()) }
    var monedasActuales by remember { mutableStateOf(850) }
    var mostrarInfo by remember { mutableStateOf(false) }
    var juegoSeleccionado by remember { mutableStateOf<MiniJuego?>(null) }

    val juegos = remember { obtenerListaJuegos() }

    val infiniteTransition = rememberInfiniteTransition(label = "moneda_rotacion")
    val rotacion by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotacion"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "Mini Juegos",
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("🎮", fontSize = 20.sp)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MoradoPrincipal,
                    titleContentColor = Color.White
                ),
                actions = {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = AmarilloPastel,
                        shadowElevation = 2.dp,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "🪙",
                                fontSize = 18.sp,
                                modifier = Modifier.rotate(rotacion)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "$monedasActuales",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }
                    IconButton(onClick = { mostrarInfo = true }) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = "Información",
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(FondoApp)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 20.dp)
        ) {
            BannerPromocional()
            Spacer(modifier = Modifier.height(16.dp))
            EstadisticasRapidas(estadisticas)
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Juegos Disponibles",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = GrisTexto,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.height(650.dp)
            ) {
                items(juegos) { juego ->
                    TarjetaJuego(
                        juego = juego,
                        onClick = {
                            if (juego.desbloqueado) {
                                juegoSeleccionado = juego
                            }
                        }
                    )
                }
            }
        }
    }

    if (mostrarInfo) {
        DialogoInformacion(
            onDismiss = { mostrarInfo = false }
        )
    }

    juegoSeleccionado?.let { juego ->
        DialogoIniciarJuego(
            juego = juego,
            onDismiss = { juegoSeleccionado = null },
            onIniciar = {
                juegoSeleccionado = null
            }
        )
    }
}

@Composable
fun BannerPromocional() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(6.dp),
        shape = RoundedCornerShape(24.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            MoradoPrincipal,
                            AzulPastel
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "¡Juega y Gana!",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Obtén monedas para tu mascota",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = AmarilloPastel
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "🎁", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Bonus Diario",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                Text(
                    text = "🎮",
                    fontSize = 60.sp
                )
            }
        }
    }
}

@Composable
fun EstadisticasRapidas(stats: EstadisticasJuegos) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TarjetaEstadistica(
            emoji = "🪙",
            valor = "${stats.monedasGanadas}",
            label = "Ganadas",
            color = AmarilloPastel,
            modifier = Modifier.weight(1f)
        )
        TarjetaEstadistica(
            emoji = "🎯",
            valor = "${stats.partidasJugadas}",
            label = "Partidas",
            color = AzulPastel,
            modifier = Modifier.weight(1f)
        )
        TarjetaEstadistica(
            emoji = "⭐",
            valor = stats.juegoFavorito.take(8) + "...",
            label = "Favorito",
            color = RosaPastel,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun TarjetaEstadistica(
    emoji: String,
    valor: String,
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(3.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = emoji, fontSize = 20.sp)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = valor,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = GrisTexto,
                maxLines = 1
            )
            Text(
                text = label,
                fontSize = 11.sp,
                color = GrisMedio
            )
        }
    }
}

@Composable
fun TarjetaJuego(
    juego: MiniJuego,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clickable(enabled = juego.desbloqueado, onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (juego.desbloqueado) Color.White else GrisClaro.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(if (juego.desbloqueado) 4.dp else 2.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Fondo superior con color del juego
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .background(
                        if (juego.desbloqueado) {
                            Brush.verticalGradient(
                                colors = listOf(
                                    juego.color.copy(alpha = 0.7f),
                                    juego.color.copy(alpha = 0.3f)
                                )
                            )
                        } else {
                            Brush.verticalGradient(
                                colors = listOf(
                                    GrisClaro,
                                    Color.Transparent
                                )
                            )
                        }
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Emoji del juego
                Text(
                    text = juego.emoji,
                    fontSize = 48.sp,
                    modifier = Modifier.alpha(if (juego.desbloqueado) 1f else 0.4f)
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = juego.nombre,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (juego.desbloqueado) GrisTexto else GrisMedio,
                        textAlign = TextAlign.Center,
                        maxLines = 1
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = juego.descripcion,
                        fontSize = 11.sp,
                        color = GrisMedio,
                        textAlign = TextAlign.Center,
                        maxLines = 2
                    )
                }

                if (juego.desbloqueado) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = AmarilloPastel.copy(alpha = 0.2f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "🪙", fontSize = 12.sp)
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "${juego.recompensaMin}-${juego.recompensaMax}",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GrisTexto
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = juego.dificultad.color.copy(alpha = 0.2f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = juego.dificultad.emoji,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                        if (juego.vecesJugado > 0) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Mejor: ${juego.mejorPuntuacion} pts | Jugado ${juego.vecesJugado}x",
                                fontSize = 9.sp,
                                color = GrisMedio
                            )
                        }
                    }
                } else {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = CoralPastel.copy(alpha = 0.2f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Lock,
                                contentDescription = null,
                                tint = CoralPastel,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Nivel ${juego.nivelRequerido}",
                                fontSize = 11.sp,
                                color = CoralPastel,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DialogoInformacion(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Text(text = "ℹ️", fontSize = 40.sp) },
        title = {
            Text(
                text = "Sobre los Mini Juegos",
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column {
                Text(
                    text = "• Gana monedas jugando\n• Desbloquea más juegos subiendo de nivel\n• Las recompensas varían según tu desempeño\n• Juega responsablemente",
                    fontSize = 14.sp,
                    color = GrisTexto,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Niveles de Dificultad:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = GrisTexto
                )

                Spacer(modifier = Modifier.height(8.dp))

                Dificultad.entries.forEach { dif ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 2.dp)
                    ) {
                        Text(text = dif.emoji, fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = dif.texto,
                            fontSize = 12.sp,
                            color = dif.color,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MoradoPrincipal
                )
            ) {
                Text("Entendido")
            }
        }
    )
}

@Composable
fun DialogoIniciarJuego(
    juego: MiniJuego,
    onDismiss: () -> Unit,
    onIniciar: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Text(text = juego.emoji, fontSize = 56.sp) },
        title = {
            Text(
                text = juego.nombre,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = juego.descripcion,
                    fontSize = 13.sp,
                    color = GrisMedio,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    InfoJuego("🪙", "${juego.recompensaMin}-${juego.recompensaMax}", "Monedas")
                    InfoJuego(juego.dificultad.emoji, juego.dificultad.texto, "Dificultad")
                }

                if (juego.mejorPuntuacion > 0) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = AmarilloPastel.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "🏆 Mejor Puntuación: ${juego.mejorPuntuacion}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = GrisTexto,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onIniciar,
                colors = ButtonDefaults.buttonColors(
                    containerColor = juego.color
                )
            ) {
                Text("¡Jugar!")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = GrisMedio)
            }
        }
    )
}

@Composable
fun InfoJuego(emoji: String, valor: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = emoji, fontSize = 24.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = valor,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = GrisTexto
        )
        Text(
            text = label,
            fontSize = 10.sp,
            color = GrisMedio
        )
    }
}

fun obtenerListaJuegos(): List<MiniJuego> {
    return listOf(
        MiniJuego(
            id = "ruleta",
            nombre = "Ruleta Financiera",
            emoji = "🎰",
            descripcion = "Gira y gana monedas",
            dificultad = Dificultad.FACIL,
            recompensaMin = 10,
            recompensaMax = 50,
            color = AmarilloPastel,
            desbloqueado = true,
            vecesJugado = 15,
            mejorPuntuacion = 50
        ),
        MiniJuego(
            id = "memoria",
            nombre = "Memoria de Gastos",
            emoji = "🧠",
            descripcion = "Encuentra las parejas",
            dificultad = Dificultad.MEDIO,
            recompensaMin = 20,
            recompensaMax = 60,
            color = AzulPastel,
            desbloqueado = true,
            vecesJugado = 8,
            mejorPuntuacion = 120
        ),
        MiniJuego(
            id = "trivia",
            nombre = "Trivia Financiera",
            emoji = "❓",
            descripcion = "Responde preguntas",
            dificultad = Dificultad.MEDIO,
            recompensaMin = 15,
            recompensaMax = 70,
            color = VerdeMenta,
            desbloqueado = true,
            vecesJugado = 12,
            mejorPuntuacion = 200
        ),
        MiniJuego(
            id = "atrapar",
            nombre = "Atrapa Monedas",
            emoji = "🪙",
            descripcion = "Atrapa todas las monedas",
            dificultad = Dificultad.FACIL,
            recompensaMin = 10,
            recompensaMax = 40,
            color = RosaPastel,
            desbloqueado = true,
            vecesJugado = 20,
            mejorPuntuacion = 85
        ),
        MiniJuego(
            id = "ahorro_rapido",
            nombre = "Ahorro Rápido",
            emoji = "💰",
            descripcion = "Calcula y ahorra rápido",
            dificultad = Dificultad.MEDIO,
            recompensaMin = 25,
            recompensaMax = 80,
            color = VerdeMentaClaro,
            desbloqueado = true,
            nivelRequerido = 3
        ),
        MiniJuego(
            id = "rompecabezas",
            nombre = "Puzzle de Billetes",
            emoji = "🧩",
            descripcion = "Arma el rompecabezas",
            dificultad = Dificultad.DIFICIL,
            recompensaMin = 30,
            recompensaMax = 100,
            color = MoradoClaro,
            desbloqueado = false,
            nivelRequerido = 5
        ),
        MiniJuego(
            id = "carrera",
            nombre = "Carrera de Ahorros",
            emoji = "🏃",
            descripcion = "Corre y esquiva gastos",
            dificultad = Dificultad.MEDIO,
            recompensaMin = 20,
            recompensaMax = 65,
            color = CoralPastel,
            desbloqueado = false,
            nivelRequerido = 4
        ),
        MiniJuego(
            id = "inversiones",
            nombre = "Simulador Pro",
            emoji = "📈",
            descripcion = "Invierte estratégicamente",
            dificultad = Dificultad.DIFICIL,
            recompensaMin = 50,
            recompensaMax = 150,
            color = AzulHeader,
            desbloqueado = false,
            nivelRequerido = 8
        )
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewMenuJuegos() {
    MascotaFinancieraTheme {
        PantallaMenuJuegos()
    }
}