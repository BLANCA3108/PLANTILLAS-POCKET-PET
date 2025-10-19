package com.example.mascotafinanciera.pantallas.mascota

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mascotafinanciera.ui.theme.*

/**
 * Pantalla de desafíos y retos semanales
 * Muestra retos activos, completados y próximos
 *
 * @author MARICARMEN
 */

// ========== DATOS ==========
data class Desafio(
    val id: String,
    val titulo: String,
    val descripcion: String,
    val emoji: String,
    val recompensaXP: Int,
    val recompensaSalud: Int,
    val progreso: Int,
    val meta: Int,
    val completado: Boolean,
    val dificultad: DificultadDesafio,
    val diasRestantes: Int
)

enum class DificultadDesafio(val titulo: String, val color: Color) {
    FACIL("Fácil", VerdeMenta),
    MEDIO("Medio", AmarilloPastel),
    DIFICIL("Difícil", CoralPastel)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaDesafios() {
    var pantalla_seleccionada by remember { mutableStateOf(1) }
    var tabSeleccionada by remember { mutableStateOf(0) }

    // Desafíos de ejemplo
    val desafios = remember {
        listOf(
            // ACTIVOS
            Desafio(
                id = "ahorro_semanal",
                titulo = "Ahorro Semanal",
                descripcion = "Ahorra al menos S/ 100 esta semana",
                emoji = "💰",
                recompensaXP = 80,
                recompensaSalud = 15,
                progreso = 65,
                meta = 100,
                completado = false,
                dificultad = DificultadDesafio.FACIL,
                diasRestantes = 4
            ),
            Desafio(
                id = "sin_gastos_innecesarios",
                titulo = "Cero Gastos Hormiga",
                descripcion = "No gastes en cosas innecesarias por 3 días",
                emoji = "🚫",
                recompensaXP = 120,
                recompensaSalud = 20,
                progreso = 2,
                meta = 3,
                completado = false,
                dificultad = DificultadDesafio.MEDIO,
                diasRestantes = 5
            ),
            Desafio(
                id = "registro_diario",
                titulo = "Registro Perfecto",
                descripcion = "Registra todas tus transacciones durante 7 días",
                emoji = "📝",
                recompensaXP = 150,
                recompensaSalud = 25,
                progreso = 5,
                meta = 7,
                completado = false,
                dificultad = DificultadDesafio.DIFICIL,
                diasRestantes = 3
            ),

            // COMPLETADOS
            Desafio(
                id = "primera_meta",
                titulo = "Primera Meta Cumplida",
                descripcion = "Alcanza tu primera meta de ahorro",
                emoji = "🎯",
                recompensaXP = 100,
                recompensaSalud = 20,
                progreso = 1,
                meta = 1,
                completado = true,
                dificultad = DificultadDesafio.MEDIO,
                diasRestantes = 0
            ),
            Desafio(
                id = "alimentar_mascota",
                titulo = "Mascota Feliz",
                descripcion = "Cuida a tu mascota 5 veces",
                emoji = "🐾",
                recompensaXP = 60,
                recompensaSalud = 10,
                progreso = 5,
                meta = 5,
                completado = true,
                dificultad = DificultadDesafio.FACIL,
                diasRestantes = 0
            )
        )
    }

    val desafiosActivos = desafios.filter { !it.completado }
    val desafiosCompletados = desafios.filter { it.completado }

    // Estadísticas
    val totalRecompensas = desafiosCompletados.sumOf { it.recompensaXP }
    val desafiosRestantes = desafiosActivos.size

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Desafíos 🎯",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CoralPastel,
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
            // ========== BANNER INFORMATIVO ==========
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MoradoPrincipal.copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Desafíos Semanales",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MoradoPrincipal
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Completa retos para ganar recompensas",
                            fontSize = 13.sp,
                            color = GrisMedio
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(MoradoPrincipal.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🏆", fontSize = 32.sp)
                    }
                }
            }

            // ========== ESTADÍSTICAS ==========
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MiniEstadistica(
                    emoji = "⚡",
                    valor = "$desafiosRestantes",
                    etiqueta = "Activos",
                    color = CoralPastel,
                    modifier = Modifier.weight(1f)
                )
                MiniEstadistica(
                    emoji = "✅",
                    valor = "${desafiosCompletados.size}",
                    etiqueta = "Completados",
                    color = VerdeMenta,
                    modifier = Modifier.weight(1f)
                )
                MiniEstadistica(
                    emoji = "⭐",
                    valor = "$totalRecompensas",
                    etiqueta = "XP Ganado",
                    color = AmarilloPastel,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ========== TABS ==========
            TabRow(
                selectedTabIndex = tabSeleccionada,
                containerColor = Color.White,
                contentColor = MoradoPrincipal,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Tab(
                    selected = tabSeleccionada == 0,
                    onClick = { tabSeleccionada = 0 },
                    text = {
                        Text(
                            "Activos (${desafiosActivos.size})",
                            fontWeight = if (tabSeleccionada == 0) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
                Tab(
                    selected = tabSeleccionada == 1,
                    onClick = { tabSeleccionada = 1 },
                    text = {
                        Text(
                            "Completados (${desafiosCompletados.size})",
                            fontWeight = if (tabSeleccionada == 1) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }

            // ========== LISTA DE DESAFÍOS ==========
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(12.dp))
                }

                if (tabSeleccionada == 0) {
                    items(desafiosActivos) { desafio ->
                        TarjetaDesafio(desafio = desafio)
                    }
                } else {
                    items(desafiosCompletados) { desafio ->
                        TarjetaDesafio(desafio = desafio)
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

// ========== COMPONENTE: MINI ESTADÍSTICA ==========
@Composable
fun MiniEstadistica(
    emoji: String,
    valor: String,
    etiqueta: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(3.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = emoji, fontSize = 24.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = valor,
                fontSize = 18.sp,
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

// ========== COMPONENTE: TARJETA DE DESAFÍO ==========
@Composable
fun TarjetaDesafio(desafio: Desafio) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Emoji e Info
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(desafio.dificultad.color.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = desafio.emoji, fontSize = 32.sp)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = desafio.titulo,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = GrisTexto
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = desafio.dificultad.color.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = desafio.dificultad.titulo,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = desafio.dificultad.color,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                // Días restantes
                if (!desafio.completado) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "${desafio.diasRestantes}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = CoralPastel
                        )
                        Text(
                            text = "días",
                            fontSize = 11.sp,
                            color = GrisMedio
                        )
                    }
                } else {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = "Completado",
                        tint = VerdeMenta,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Descripción
            Text(
                text = desafio.descripcion,
                fontSize = 13.sp,
                color = GrisMedio,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Progreso
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Progreso",
                        fontSize = 12.sp,
                        color = GrisMedio
                    )
                    Text(
                        text = "${desafio.progreso}/${desafio.meta}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (desafio.completado) VerdeMenta else MoradoPrincipal
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = desafio.progreso.toFloat() / desafio.meta.toFloat(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = if (desafio.completado) VerdeMenta else MoradoPrincipal,
                    trackColor = GrisClaro
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Recompensas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                RecompensaChip(
                    icono = Icons.Default.Star,
                    texto = "+${desafio.recompensaXP} XP",
                    color = AmarilloPastel
                )
                RecompensaChip(
                    icono = Icons.Default.Favorite,
                    texto = "+${desafio.recompensaSalud} ❤️",
                    color = RosaPastel
                )
            }
        }
    }
}

// ========== COMPONENTE: CHIP DE RECOMPENSA ==========
@Composable
fun RecompensaChip(icono: ImageVector, texto: String, color: Color) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.2f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icono,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = texto,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

// ========== PREVIEW ==========
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewPantallaDesafios() {
    MascotaFinancieraTheme {
        PantallaDesafios()
    }
}