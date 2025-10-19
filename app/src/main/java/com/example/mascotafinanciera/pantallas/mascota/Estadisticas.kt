package com.example.mascotafinanciera.pantallas.mascota

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mascotafinanciera.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

data class EstadisticasCompletas(
    val salud: Int = 85,
    val felicidad: Int = 90,
    val disciplina: Int = 75,
    val energia: Int = 80,
    val nivel: Int = 12,
    val experiencia: Int = 850,
    val experienciaMax: Int = 1000,
    val diasConsecutivos: Int = 23,
    val metasCompletadas: Int = 15,
    val metasTotales: Int = 20,
    val ahorroTotal: Double = 2450.50,
    val gastoPromedio: Double = 450.00,
    val mejorRacha: Int = 30,
    val logrosDesbloqueados: Int = 8,
    val logrosTotales: Int = 25
)

data class DatoGrafico(
    val dia: String,
    val valor: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaEstadisticasMascota() {
    val estadisticas = remember { EstadisticasCompletas() }
    val datosGrafico = remember { obtenerDatosGraficoSemana() }

    var tabSeleccionada by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "Estadísticas",
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("📊", fontSize = 20.sp)
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
                    containerColor = MoradoPrincipal,
                    titleContentColor = Color.White
                ),
                actions = {
                    IconButton(onClick = { /* Compartir */ }) {
                        Icon(
                            Icons.Default.Share,
                            contentDescription = "Compartir",
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
            // Nivel y experiencia destacados
            SeccionNivelExperiencia(estadisticas)

            Spacer(modifier = Modifier.height(20.dp))

            // Tabs para diferentes vistas
            TabsEstadisticas(
                tabSeleccionada = tabSeleccionada,
                onTabSeleccionada = { tabSeleccionada = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Contenido según tab seleccionada
            when (tabSeleccionada) {
                0 -> VistaAtributos(estadisticas)
                1 -> VistaProgreso(estadisticas, datosGrafico)
                2 -> VistaLogros(estadisticas)
            }
        }
    }
}

@Composable
fun SeccionNivelExperiencia(stats: EstadisticasCompletas) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
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
                            MoradoClaro
                        )
                    )
                )
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "NIVEL",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.8f),
                        letterSpacing = 1.5.sp
                    )
                    Text(
                        text = "${stats.nivel}",
                        fontSize = 56.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = AmarilloPastel,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Guardián Financiero",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${stats.experiencia} / ${stats.experienciaMax}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .width(120.dp)
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(Color.White.copy(alpha = 0.3f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(stats.experiencia.toFloat() / stats.experienciaMax.toFloat())
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(5.dp))
                                .background(AmarilloPastel)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${((stats.experiencia.toFloat() / stats.experienciaMax.toFloat()) * 100).toInt()}% al siguiente nivel",
                        fontSize = 10.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

@Composable
fun TabsEstadisticas(
    tabSeleccionada: Int,
    onTabSeleccionada: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TabButton("📈", "Atributos", 0, tabSeleccionada, onTabSeleccionada)
        TabButton("📊", "Progreso", 1, tabSeleccionada, onTabSeleccionada)
        TabButton("🏆", "Logros", 2, tabSeleccionada, onTabSeleccionada)
    }
}

@Composable
fun RowScope.TabButton(
    emoji: String,
    texto: String,
    index: Int,
    seleccionada: Int,
    onClick: (Int) -> Unit
) {
    Surface(
        modifier = Modifier.weight(1f),
        shape = RoundedCornerShape(16.dp),
        color = if (seleccionada == index) MoradoPrincipal else Color.White,
        shadowElevation = if (seleccionada == index) 4.dp else 2.dp,
        onClick = { onClick(index) }
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = emoji, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = texto,
                fontSize = 12.sp,
                fontWeight = if (seleccionada == index) FontWeight.Bold else FontWeight.Medium,
                color = if (seleccionada == index) Color.White else GrisTexto
            )
        }
    }
}

@Composable
fun VistaAtributos(stats: EstadisticasCompletas) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Gráfico radial de atributos
        GraficoRadial(
            salud = stats.salud,
            felicidad = stats.felicidad,
            disciplina = stats.disciplina,
            energia = stats.energia
        )

        // Barras de atributos
        TarjetaAtributo(
            icono = Icons.Default.Favorite,
            nombre = "Salud",
            valor = stats.salud,
            color = VerdeMenta,
            descripcion = "Estado general de tu mascota"
        )

        TarjetaAtributo(
            icono = Icons.Default.MoodBad,
            nombre = "Felicidad",
            valor = stats.felicidad,
            color = AmarilloPastel,
            descripcion = "Nivel de satisfacción"
        )

        TarjetaAtributo(
            icono = Icons.Default.School,
            nombre = "Disciplina",
            valor = stats.disciplina,
            color = AzulPastel,
            descripcion = "Constancia en buenos hábitos"
        )

        TarjetaAtributo(
            icono = Icons.Default.Bolt,
            nombre = "Energía",
            valor = stats.energia,
            color = CoralPastel,
            descripcion = "Vitalidad y motivación"
        )
    }
}

@Composable
fun GraficoRadial(
    salud: Int,
    felicidad: Int,
    disciplina: Int,
    energia: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Balance General",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = GrisTexto
            )

            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier.size(220.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val center = Offset(size.width / 2, size.height / 2)
                    val radius = size.minDimension / 2.5f

                    // Líneas de fondo (guías)
                    for (i in 1..4) {
                        val r = radius * (i / 4f)
                        drawCircle(
                            color = GrisClaro,
                            radius = r,
                            center = center,
                            style = Stroke(width = 1.dp.toPx())
                        )
                    }

                    // Líneas de ejes
                    val valores = listOf(salud, felicidad, disciplina, energia)
                    val colores = listOf(VerdeMenta, AmarilloPastel, AzulPastel, CoralPastel)

                    for (i in 0..3) {
                        val angle = (i * 90f - 90f) * (Math.PI / 180f).toFloat()
                        val lineEnd = Offset(
                            center.x + radius * cos(angle),
                            center.y + radius * sin(angle)
                        )
                        drawLine(
                            color = GrisClaro,
                            start = center,
                            end = lineEnd,
                            strokeWidth = 1.dp.toPx()
                        )
                    }

                    // Polígono de valores
                    val puntos = valores.mapIndexed { index, valor ->
                        val angle = (index * 90f - 90f) * (Math.PI / 180f).toFloat()
                        val r = radius * (valor / 100f)
                        Offset(
                            center.x + r * cos(angle),
                            center.y + r * sin(angle)
                        )
                    }

                    // Área rellena
                    for (i in puntos.indices) {
                        val nextIndex = (i + 1) % puntos.size
                        drawLine(
                            color = MoradoPrincipal.copy(alpha = 0.3f),
                            start = puntos[i],
                            end = puntos[nextIndex],
                            strokeWidth = 2.dp.toPx()
                        )
                    }

                    // Puntos en los vértices
                    puntos.forEachIndexed { index, punto ->
                        drawCircle(
                            color = colores[index],
                            radius = 6.dp.toPx(),
                            center = punto
                        )
                    }
                }

                // Puntuación promedio en el centro
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val promedio = (salud + felicidad + disciplina + energia) / 4
                    Text(
                        text = "$promedio",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = MoradoPrincipal
                    )
                    Text(
                        text = "Promedio",
                        fontSize = 12.sp,
                        color = GrisMedio
                    )
                }
            }
        }
    }
}

@Composable
fun TarjetaAtributo(
    icono: ImageVector,
    nombre: String,
    valor: Int,
    color: Color,
    descripcion: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(3.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(color.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            icono,
                            contentDescription = null,
                            tint = color,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = nombre,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = GrisTexto
                        )
                        Text(
                            text = descripcion,
                            fontSize = 11.sp,
                            color = GrisMedio
                        )
                    }
                }

                Text(
                    text = "$valor",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(GrisClaro)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(valor / 100f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(4.dp))
                        .background(color)
                )
            }
        }
    }
}

@Composable
fun VistaProgreso(stats: EstadisticasCompletas, datosGrafico: List<DatoGrafico>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Gráfico de línea semanal
        GraficoSemanal(datosGrafico)

        // Tarjetas de estadísticas
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TarjetaEstadisticaCompacta(
                emoji = "🔥",
                titulo = "Racha Actual",
                valor = "${stats.diasConsecutivos} días",
                color = CoralPastel,
                modifier = Modifier.weight(1f)
            )
            TarjetaEstadisticaCompacta(
                emoji = "🏆",
                titulo = "Mejor Racha",
                valor = "${stats.mejorRacha} días",
                color = AmarilloPastel,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TarjetaEstadisticaCompacta(
                emoji = "🎯",
                titulo = "Metas",
                valor = "${stats.metasCompletadas}/${stats.metasTotales}",
                color = AzulPastel,
                modifier = Modifier.weight(1f)
            )
            TarjetaEstadisticaCompacta(
                emoji = "💰",
                titulo = "Ahorrado",
                valor = "S/ ${String.format("%.0f", stats.ahorroTotal)}",
                color = VerdeMenta,
                modifier = Modifier.weight(1f)
            )
        }

        // Gasto promedio
        TarjetaGastoPromedio(stats.gastoPromedio)
    }
}

@Composable
fun GraficoSemanal(datos: List<DatoGrafico>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "Salud Semanal",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = GrisTexto
            )

            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val maxValor = datos.maxOf { it.valor }.toFloat()
                    val espacioX = size.width / (datos.size - 1)
                    val escalaY = size.height / maxValor

                    // Líneas de fondo
                    for (i in 0..4) {
                        val y = size.height - (size.height * (i / 4f))
                        drawLine(
                            color = GrisClaro,
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            strokeWidth = 1.dp.toPx()
                        )
                    }

                    // Línea del gráfico
                    for (i in 0 until datos.size - 1) {
                        val x1 = i * espacioX
                        val y1 = size.height - (datos[i].valor * escalaY)
                        val x2 = (i + 1) * espacioX
                        val y2 = size.height - (datos[i + 1].valor * escalaY)

                        drawLine(
                            color = VerdeMenta,
                            start = Offset(x1, y1),
                            end = Offset(x2, y2),
                            strokeWidth = 3.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    }

                    // Puntos
                    datos.forEachIndexed { index, dato ->
                        val x = index * espacioX
                        val y = size.height - (dato.valor * escalaY)

                        drawCircle(
                            color = VerdeMenta,
                            radius = 6.dp.toPx(),
                            center = Offset(x, y)
                        )
                        drawCircle(
                            color = Color.White,
                            radius = 3.dp.toPx(),
                            center = Offset(x, y)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Etiquetas de días
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                datos.forEach { dato ->
                    Text(
                        text = dato.dia,
                        fontSize = 11.sp,
                        color = GrisMedio,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun TarjetaEstadisticaCompacta(
    emoji: String,
    titulo: String,
    valor: String,
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = emoji, fontSize = 28.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = valor,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = titulo,
                fontSize = 11.sp,
                color = GrisMedio,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun TarjetaGastoPromedio(gasto: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(3.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(CoralPastel.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "💸", fontSize = 24.sp)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Gasto Promedio Diario",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = GrisTexto
                    )
                    Text(
                        text = "Últimos 7 días",
                        fontSize = 11.sp,
                        color = GrisMedio
                    )
                }
            }
            Text(
                text = "S/ ${String.format("%.2f", gasto)}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = CoralPastel
            )
        }
    }
}

@Composable
fun VistaLogros(stats: EstadisticasCompletas) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Progreso de logros
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(4.dp),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🏆",
                    fontSize = 48.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "${stats.logrosDesbloqueados} / ${stats.logrosTotales}",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MoradoPrincipal
                )
                Text(
                    text = "Logros Desbloqueados",
                    fontSize = 14.sp,
                    color = GrisMedio
                )
                Spacer(modifier = Modifier.height(16.dp))
                LinearProgressIndicator(
                    progress = stats.logrosDesbloqueados.toFloat() / stats.logrosTotales.toFloat(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = AmarilloPastel,
                    trackColor = GrisClaro
                )
            }
        }

        // Categorías de logros
        TarjetaCategoriaLogros("💰", "Ahorro Maestro", 5, 10, VerdeMenta)
        TarjetaCategoriaLogros("🎯", "Cazador de Metas", 3, 8, AzulPastel)
        TarjetaCategoriaLogros("📈", "Racha Imparable", 2, 5, CoralPastel)
        TarjetaCategoriaLogros("⭐", "Especiales", 1, 3, AmarilloPastel)
    }
}

@Composable
fun TarjetaCategoriaLogros(
    emoji: String,
    titulo: String,
    desbloqueados: Int,
    totales: Int,
    color: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(3.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = emoji, fontSize = 24.sp)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = titulo,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = GrisTexto
                    )
                    Text(
                        text = "$desbloqueados de $totales desbloqueados",
                        fontSize = 12.sp,
                        color = GrisMedio
                    )
                }
            }

            Box(
                modifier = Modifier
                    .width(80.dp)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(GrisClaro)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(desbloqueados.toFloat() / totales.toFloat())
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(4.dp))
                        .background(color)
                )
            }
        }
    }
}

fun obtenerDatosGraficoSemana(): List<DatoGrafico> {
    return listOf(
        DatoGrafico("Lun", 75),
        DatoGrafico("Mar", 78),
        DatoGrafico("Mié", 82),
        DatoGrafico("Jue", 80),
        DatoGrafico("Vie", 85),
        DatoGrafico("Sáb", 88),
        DatoGrafico("Dom", 85)
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewEstadisticas() {
    MascotaFinancieraTheme {
        PantallaEstadisticasMascota()
    }
}