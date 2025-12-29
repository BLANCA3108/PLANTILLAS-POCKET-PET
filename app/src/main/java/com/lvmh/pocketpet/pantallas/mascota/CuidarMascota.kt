package com.lvmh.pocketpet.pantallas.mascota

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lvmh.pocketpet.presentacion.tema.*
import kotlinx.coroutines.delay
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring

data class AccionCuidado(
    val id: String,
    val nombre: String,
    val emoji: String,
    val descripcion: String,
    val mejoraSalud: Int,
    val color: Color,
    val icono: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaCuidarMascota(
onVolver: () -> Unit = {}
) {
    var saludActual by remember { mutableStateOf(75) }
    var mostrarEfecto by remember { mutableStateOf(false) }
    var accionActual by remember { mutableStateOf("") }
    var pantalla_seleccionada by remember { mutableStateOf(1) }

    LaunchedEffect(mostrarEfecto) {
        if (mostrarEfecto) {
            delay(2000)
            mostrarEfecto = false
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "animacion")
    val escala by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "escala"
    )

    val acciones = remember {
        listOf(
            AccionCuidado(
                id = "alimentar",
                nombre = "Alimentar",
                emoji = "🍎",
                descripcion = "Dale comida saludable",
                mejoraSalud = 10,
                color = CoralPastel,
                icono = Icons.Default.Restaurant
            ),
            AccionCuidado(
                id = "jugar",
                nombre = "Jugar",
                emoji = "⚽",
                descripcion = "Divierte a tu mascota",
                mejoraSalud = 8,
                color = AzulPastel,
                icono = Icons.Default.SportsEsports
            ),
            AccionCuidado(
                id = "descansar",
                nombre = "Descansar",
                emoji = "😴",
                descripcion = "Deja que descanse",
                mejoraSalud = 12,
                color = MoradoClaro,
                icono = Icons.Default.Bedtime
            ),
            AccionCuidado(
                id = "limpiar",
                nombre = "Limpiar",
                emoji = "🛁",
                descripcion = "Mantén la higiene",
                mejoraSalud = 7,
                color = TurquesaPastel,
                icono = Icons.Default.CleaningServices
            ),
            AccionCuidado(
                id = "ejercicio",
                nombre = "Ejercicio",
                emoji = "🏃",
                descripcion = "Haz que se ejercite",
                mejoraSalud = 9,
                color = VerdeMenta,
                icono = Icons.Default.DirectionsRun
            ),
            AccionCuidado(
                id = "mimar",
                nombre = "Mimar",
                emoji = "💕",
                descripcion = "Dale cariño y amor",
                mejoraSalud = 15,
                color = RosaPastel,
                icono = Icons.Default.Favorite
            )
        )
    }

    val realizarAccion: (AccionCuidado) -> Unit = { accion ->
        if (saludActual < 100) {
            accionActual = accion.emoji
            mostrarEfecto = true

            val nuevaSalud = (saludActual + accion.mejoraSalud).coerceAtMost(100)
            saludActual = nuevaSalud
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Cuidar Mascota 💚",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = VerdeMenta,
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
        },

    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(FondoApp)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

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
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Salud Actual",
                            fontSize = 14.sp,
                            color = GrisMedio
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$saludActual / 100",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = obtenerColorSalud(saludActual)
                        )
                    }

                    // Mini indicador circular
                    Box(
                        modifier = Modifier.size(60.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            progress = saludActual / 100f,
                            modifier = Modifier.size(60.dp),
                            strokeWidth = 6.dp,
                            color = obtenerColorSalud(saludActual),
                            trackColor = GrisClaro
                        )
                        Text(
                            text = obtenerEmojiMascota(saludActual),
                            fontSize = 24.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier.size(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .scale(escala)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = obtenerEmojiMascota(saludActual),
                        fontSize = 90.sp
                    )
                }

                if (mostrarEfecto) {
                    val escalaEfecto by animateFloatAsState(
                        targetValue = if (mostrarEfecto) 1.2f else 0f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        ),
                        label = "escala_efecto"
                    )

                    Box(
                        modifier = Modifier
                            .size(200.dp)
                            .scale(escalaEfecto),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = accionActual,
                            fontSize = 60.sp
                        )
                    }
                }
            }

            Text(
                text = "Acciones Disponibles",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = GrisTexto,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                acciones.chunked(2).forEach { fila ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        fila.forEach { accion ->
                            TarjetaAccion(
                                accion = accion,
                                onAccion = { realizarAccion(accion) },
                                habilitada = saludActual < 100,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (fila.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (saludActual >= 100) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MoradoPrincipal.copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "🎉", fontSize = 32.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "¡Salud al Máximo!",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MoradoPrincipal
                            )
                            Text(
                                text = "Tu mascota está en perfecto estado",
                                fontSize = 13.sp,
                                color = GrisMedio
                            )
                        }
                    }
                }
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = AzulCielo.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "💡", fontSize = 28.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Tip: Cuida a tu mascota regularmente para mantener tus finanzas saludables",
                            fontSize = 13.sp,
                            color = GrisTexto,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
@Composable
fun TarjetaAccion(
    accion: AccionCuidado,
    onAccion: () -> Unit,
    habilitada: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(140.dp)
            .clickable(enabled = habilitada) { onAccion() },
        colors = CardDefaults.cardColors(
            containerColor = if (habilitada) Color.White else GrisClaro
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (habilitada) 4.dp else 1.dp
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Emoji grande
            Text(
                text = accion.emoji,
                fontSize = 36.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Nombre
            Text(
                text = accion.nombre,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = if (habilitada) GrisTexto else GrisMedio,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Mejora
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = accion.color.copy(alpha = if (habilitada) 0.2f else 0.1f)
            ) {
                Text(
                    text = "+${accion.mejoraSalud} ❤️",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (habilitada) accion.color else GrisMedio,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewPantallaCuidar() {
    MascotaFinancieraTheme {
        PantallaCuidarMascota()
    }
}