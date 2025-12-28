package com.lvmh.pocketpet.pantallas.mascota

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mascotafinanciera.ui.theme.*
import androidx.compose.ui.graphics.graphicsLayer

data class Logro(
    val id: String,
    val titulo: String,
    val descripcion: String,
    val emoji: String,
    val puntos: Int,
    val desbloqueado: Boolean,
    val progreso: Int = 0,
    val progresoMax: Int = 1,
    val categoria: CategoriaLogro,
    val rareza: RarezaLogro
)

enum class CategoriaLogro(val titulo: String, val color: Color) {
    PRINCIPIANTE("Principiante", AzulPastel),
    AHORRO("Ahorro", VerdeMenta),
    DISCIPLINA("Disciplina", MoradoPrincipal),
    MASCOTA("Mascota", RosaPastel),
    ESPECIAL("Especial", AmarilloPastel)
}

enum class RarezaLogro(val titulo: String, val emoji: String, val color: Color) {
    COMUN("Común", "⭐", GrisMedio),
    RARO("Raro", "✨", AzulPastel),
    EPICO("Épico", "💎", MoradoPrincipal),
    LEGENDARIO("Legendario", "👑", AmarilloPastel)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaLogros() {
    var pantalla_seleccionada by remember { mutableStateOf(1) }
    var filtroSeleccionado by remember { mutableStateOf<CategoriaLogro?>(null) }

    val logros = remember {
        listOf(
            Logro(
                id = "primera_transaccion",
                titulo = "¡Primer Paso!",
                descripcion = "Registraste tu primera transacción",
                emoji = "🎯",
                puntos = 50,
                desbloqueado = true,
                categoria = CategoriaLogro.PRINCIPIANTE,
                rareza = RarezaLogro.COMUN
            ),
            Logro(
                id = "mascota_nombre",
                titulo = "Nuevo Amigo",
                descripcion = "Le diste un nombre a tu mascota",
                emoji = "🐾",
                puntos = 30,
                desbloqueado = true,
                categoria = CategoriaLogro.MASCOTA,
                rareza = RarezaLogro.COMUN
            ),
            Logro(
                id = "ahorro_10k",
                titulo = "Ahorrador Novato",
                descripcion = "Ahorraste S/ 10,000",
                emoji = "💰",
                puntos = 100,
                desbloqueado = true,
                categoria = CategoriaLogro.AHORRO,
                rareza = RarezaLogro.RARO
            ),

            Logro(
                id = "racha_7",
                titulo = "Semana Perfecta",
                descripcion = "Registra transacciones 7 días seguidos",
                emoji = "🔥",
                puntos = 150,
                desbloqueado = false,
                progreso = 4,
                progresoMax = 7,
                categoria = CategoriaLogro.DISCIPLINA,
                rareza = RarezaLogro.RARO
            ),
            Logro(
                id = "ahorro_50k",
                titulo = "Ahorrador Experto",
                descripcion = "Ahorra S/ 50,000",
                emoji = "💎",
                puntos = 300,
                desbloqueado = false,
                progreso = 15000,
                progresoMax = 50000,
                categoria = CategoriaLogro.AHORRO,
                rareza = RarezaLogro.EPICO
            ),
            Logro(
                id = "mascota_nivel_10",
                titulo = "Entrenador Experto",
                descripcion = "Tu mascota alcanzó nivel 10",
                emoji = "🏆",
                puntos = 300,
                desbloqueado = false,
                progreso = 5,
                progresoMax = 10,
                categoria = CategoriaLogro.MASCOTA,
                rareza = RarezaLogro.EPICO
            ),
            Logro(
                id = "ahorro_100k",
                titulo = "Maestro del Ahorro",
                descripcion = "¡Ahorraste S/ 100,000!",
                emoji = "👑",
                puntos = 500,
                desbloqueado = false,
                categoria = CategoriaLogro.AHORRO,
                rareza = RarezaLogro.LEGENDARIO
            ),
            Logro(
                id = "racha_30",
                titulo = "Mes Impecable",
                descripcion = "Registra transacciones 30 días seguidos",
                emoji = "⚡",
                puntos = 400,
                desbloqueado = false,
                progreso = 4,
                progresoMax = 30,
                categoria = CategoriaLogro.DISCIPLINA,
                rareza = RarezaLogro.EPICO
            )
        )
    }

    val logrosFiltrados = if (filtroSeleccionado != null) {
        logros.filter { it.categoria == filtroSeleccionado }
    } else {
        logros
    }

    val totalLogros = logros.size
    val logrosDesbloqueados = logros.count { it.desbloqueado }
    val puntosTotal = logros.filter { it.desbloqueado }.sumOf { it.puntos }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Logros 🏆",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AmarilloPastel,
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
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    EstadisticaLogro("🏅", "$logrosDesbloqueados/$totalLogros", "Logros")
                    Divider(
                        modifier = Modifier
                            .height(50.dp)
                            .width(1.dp),
                        color = GrisClaro
                    )
                    EstadisticaLogro("⭐", "$puntosTotal", "Puntos")
                    Divider(
                        modifier = Modifier
                            .height(50.dp)
                            .width(1.dp),
                        color = GrisClaro
                    )
                    EstadisticaLogro("📊", "${(logrosDesbloqueados * 100) / totalLogros}%", "Progreso")
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
                    label = { Text("Todos", fontSize = 13.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MoradoPrincipal,
                        selectedLabelColor = Color.White
                    )
                )

                CategoriaLogro.entries.forEach { categoria ->
                    FilterChip(
                        selected = filtroSeleccionado == categoria,
                        onClick = { filtroSeleccionado = categoria },
                        label = { Text(categoria.titulo, fontSize = 13.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = categoria.color,
                            selectedLabelColor = Color.White
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

                items(logrosFiltrados) { logro ->
                    TarjetaLogro(logro = logro)
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun EstadisticaLogro(emoji: String, valor: String, etiqueta: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = emoji, fontSize = 28.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = valor,
            fontSize = 18.sp,
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

@Composable
fun TarjetaLogro(logro: Logro) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (logro.desbloqueado) Color.White else Color.White.copy(alpha = 0.6f)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (logro.desbloqueado) 4.dp else 2.dp
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .clip(CircleShape)
                    .background(
                        if (logro.desbloqueado)
                            logro.categoria.color.copy(alpha = 0.2f)
                        else
                            GrisClaro
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = logro.emoji,
                    fontSize = 36.sp,
                    modifier = Modifier.alpha(if (logro.desbloqueado) 1f else 0.4f)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = logro.titulo,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (logro.desbloqueado) GrisTexto else GrisMedio
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = logro.rareza.emoji,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = logro.descripcion,
                    fontSize = 13.sp,
                    color = GrisMedio,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (!logro.desbloqueado && logro.progresoMax > 1) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Progreso",
                                fontSize = 11.sp,
                                color = GrisMedio
                            )
                            Text(
                                text = "${logro.progreso}/${logro.progresoMax}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = logro.categoria.color
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = logro.progreso.toFloat() / logro.progresoMax.toFloat(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = logro.categoria.color,
                            trackColor = GrisClaro
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = AmarilloPastel.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "+${logro.puntos} XP",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AmarilloPastel,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = logro.categoria.color.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = logro.categoria.titulo,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = logro.categoria.color,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

fun Modifier.alpha(alpha: Float): Modifier = this.then(
    Modifier.graphicsLayer(alpha = alpha)
)
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewPantallaLogros() {
    MascotaFinancieraTheme {
        PantallaLogros()
    }
}