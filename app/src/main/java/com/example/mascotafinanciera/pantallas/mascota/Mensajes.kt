package com.example.mascotafinanciera.pantallas.mascota

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mascotafinanciera.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

data class MensajeMascota(
    val id: String,
    val titulo: String,
    val mensaje: String,
    val emoji: String,
    val tipo: TipoMensaje,
    val fecha: Long,
    val leido: Boolean
)

enum class TipoMensaje(val titulo: String, val color: Color, val icono: String) {
    FELICITACION("Felicitación", VerdeMenta, "🎉"),
    CONSEJO("Consejo", AzulPastel, "💡"),
    ALERTA("Alerta", CoralPastel, "⚠️"),
    MOTIVACION("Motivación", MoradoPrincipal, "💪"),
    RECORDATORIO("Recordatorio", AmarilloPastel, "🔔")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaMensajes() {
    var pantalla_seleccionada by remember { mutableStateOf(1) }
    var filtroSeleccionado by remember { mutableStateOf<TipoMensaje?>(null) }

    // Estado de la mascota
    val saludActual = 75
    val nombreMascota = "FinanPet"

    // Animación de la mascota
    val infiniteTransition = rememberInfiniteTransition(label = "animacion")
    val escala by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "escala"
    )

    // Mensajes de ejemplo
    val mensajes = remember {
        listOf(
            MensajeMascota(
                id = "1",
                titulo = "¡Excelente trabajo!",
                mensaje = "Has ahorrado S/ 150 esta semana. Sigue así y alcanzarás tus metas más rápido de lo que piensas. ¡Estoy orgulloso de ti! 🌟",
                emoji = "🎉",
                tipo = TipoMensaje.FELICITACION,
                fecha = System.currentTimeMillis() - 3600000,
                leido = false
            ),
            MensajeMascota(
                id = "2",
                titulo = "Tip del día",
                mensaje = "¿Sabías que registrar tus gastos diarios te ayuda a tomar mejores decisiones? Intenta hacerlo antes de dormir cada noche.",
                emoji = "💡",
                tipo = TipoMensaje.CONSEJO,
                fecha = System.currentTimeMillis() - 7200000,
                leido = false
            ),
            MensajeMascota(
                id = "3",
                titulo = "¡Cuidado!",
                mensaje = "He notado que tus gastos en entretenimiento aumentaron un 30% esta semana. Quizás sea momento de revisar tu presupuesto.",
                emoji = "⚠️",
                tipo = TipoMensaje.ALERTA,
                fecha = System.currentTimeMillis() - 10800000,
                leido = true
            ),
            MensajeMascota(
                id = "4",
                titulo = "¡Tú puedes!",
                mensaje = "Cada pequeño ahorro cuenta. No te desanimes si no alcanzas tu meta de inmediato. Lo importante es la constancia. ¡Yo creo en ti!",
                emoji = "💪",
                tipo = TipoMensaje.MOTIVACION,
                fecha = System.currentTimeMillis() - 86400000,
                leido = true
            ),
            MensajeMascota(
                id = "5",
                titulo = "Recordatorio",
                mensaje = "Hace 3 días que no registras ninguna transacción. ¿Olvidaste agregar algo? Mantener el registro actualizado te ayuda a tener control total.",
                emoji = "🔔",
                tipo = TipoMensaje.RECORDATORIO,
                fecha = System.currentTimeMillis() - 172800000,
                leido = true
            ),
            MensajeMascota(
                id = "6",
                titulo = "¡Meta alcanzada!",
                mensaje = "¡Wow! Completaste tu meta de ahorro mensual. Esto merece una celebración. ¡Eres increíble! 🎊",
                emoji = "🎯",
                tipo = TipoMensaje.FELICITACION,
                fecha = System.currentTimeMillis() - 259200000,
                leido = true
            )
        )
    }

    // Filtrar mensajes
    val mensajesFiltrados = if (filtroSeleccionado != null) {
        mensajes.filter { it.tipo == filtroSeleccionado }
    } else {
        mensajes
    }

    val mensajesNoLeidos = mensajes.count { !it.leido }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Mensajes 💬",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = RosaPastel,
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
                },
                actions = {
                    // Badge de mensajes no leídos
                    if (mensajesNoLeidos > 0) {
                        BadgedBox(
                            badge = {
                                Badge(
                                    containerColor = CoralPastel,
                                    contentColor = Color.White
                                ) {
                                    Text(
                                        text = "$mensajesNoLeidos",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        ) {
                            IconButton(onClick = { /* Marcar todos como leídos */ }) {
                                Icon(
                                    Icons.Default.MarkEmailRead,
                                    contentDescription = "Marcar leídos",
                                    tint = Color.White
                                )
                            }
                        }
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
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(6.dp),
                shape = RoundedCornerShape(24.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .scale(escala)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        RosaPastel.copy(alpha = 0.2f),
                                        Color.Transparent
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = obtenerEmojiMascota(saludActual),
                            fontSize = 50.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Hola, soy $nombreMascota",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = GrisTexto
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tengo ${mensajesNoLeidos} ${if (mensajesNoLeidos == 1) "mensaje nuevo" else "mensajes nuevos"} para ti 💌",
                            fontSize = 14.sp,
                            color = GrisMedio,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = filtroSeleccionado == null,
                    onClick = { filtroSeleccionado = null },
                    label = { Text("Todos", fontSize = 12.sp) },
                    leadingIcon = {
                        Text("📬", fontSize = 16.sp)
                    }
                )

                TipoMensaje.entries.take(3).forEach { tipo ->
                    FilterChip(
                        selected = filtroSeleccionado == tipo,
                        onClick = { filtroSeleccionado = tipo },
                        label = { Text(tipo.titulo, fontSize = 12.sp) },
                        leadingIcon = {
                            Text(tipo.icono, fontSize = 16.sp)
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = tipo.color.copy(alpha = 0.3f),
                            selectedLabelColor = tipo.color
                        )
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                }

                items(mensajesFiltrados) { mensaje ->
                    TarjetaMensaje(mensaje = mensaje)
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }

                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "✨ Estos son todos tus mensajes ✨",
                            fontSize = 13.sp,
                            color = GrisMedio,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun TarjetaMensaje(mensaje: MensajeMascota) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (mensaje.leido) Color.White else mensaje.tipo.color.copy(alpha = 0.05f)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (mensaje.leido) 2.dp else 4.dp
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(mensaje.tipo.color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = mensaje.emoji,
                    fontSize = 28.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = mensaje.titulo,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = GrisTexto
                    )

                    // Badge de no leído
                    if (!mensaje.leido) {
                        Surface(
                            shape = CircleShape,
                            color = mensaje.tipo.color,
                            modifier = Modifier.size(10.dp)
                        ) {}
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = mensaje.tipo.color.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = mensaje.tipo.titulo,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = mensaje.tipo.color,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    Text(
                        text = formatearFecha(mensaje.fecha),
                        fontSize = 11.sp,
                        color = GrisMedio
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = mensaje.mensaje,
                    fontSize = 13.sp,
                    color = GrisTexto,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

fun formatearFecha(timestamp: Long): String {
    val ahora = System.currentTimeMillis()
    val diferencia = ahora - timestamp

    return when {
        diferencia < 3600000 -> "Hace ${diferencia / 60000} min"
        diferencia < 86400000 -> "Hace ${diferencia / 3600000} hrs"
        diferencia < 172800000 -> "Ayer"
        diferencia < 604800000 -> "Hace ${diferencia / 86400000} días"
        else -> SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(timestamp))
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewPantallaMensajes() {
    MascotaFinancieraTheme {
        PantallaMensajes()
    }
}