package com.lvmh.pocketpet.presentacion.pantallas.estadisticas

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.lvmh.pocketpet.dominio.utilidades.PuntoGrafico
import com.lvmh.pocketpet.presentacion.viewmodels.EstadisticasViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaEstadisticas(
    viewModel: EstadisticasViewModel,
    alNavegar: (String) -> Unit
) {
    val estado by viewModel.estado.collectAsState()

    // ✅ YA NO necesitas LaunchedEffect, el ViewModel se inicializa automáticamente

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Estadísticas") },
                navigationIcon = {
                    IconButton(onClick = { alNavegar("principal") }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        when {
            estado.cargando -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            estado.error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = estado.error ?: "Error desconocido",
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                        Button(onClick = { viewModel.inicializar() }) {
                            Text("Reintentar")
                        }
                    }
                }
            }

            else -> {
                ContenidoEstadisticas(
                    estado = estado,
                    alCambiarPeriodo = { viewModel.cambiarPeriodo(it) },
                    alNavegar = alNavegar,
                    modificador = Modifier.padding(padding)
                )
            }
        }
    }
}

@Composable
private fun ContenidoEstadisticas(
    estado: com.lvmh.pocketpet.presentacion.viewmodels.EstadoEstadisticas,
    alCambiarPeriodo: (String) -> Unit,
    alNavegar: (String) -> Unit,
    modificador: Modifier = Modifier
) {
    LazyColumn(
        modifier = modificador
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        item {
            SelectorPeriodo(
                periodoSeleccionado = estado.periodoSeleccionado,
                alSeleccionar = alCambiarPeriodo
            )
        }

        item {
            Text(
                text = "Resumen ${obtenerNombrePeriodo(estado.periodoSeleccionado)}",
                style = MaterialTheme.typography.titleLarge
            )
        }

        estado.estadisticas?.let { stats ->

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TarjetaEstadistica(
                        titulo = "Ingresos",
                        valor = "S/. ${String.format("%.2f", stats.totalIngresos)}",
                        icono = {
                            Icon(
                                Icons.Default.TrendingUp,
                                contentDescription = null,
                                tint = Color(0xFF10B981)
                            )
                        },
                        colorFondo = Color(0xFFD1FAE5),
                        colorTexto = Color(0xFF065F46),
                        modificador = Modifier.weight(1f)
                    )

                    TarjetaEstadistica(
                        titulo = "Gastos",
                        valor = "S/. ${String.format("%.2f", stats.totalGastos)}",
                        icono = {
                            Icon(
                                Icons.Default.TrendingDown,
                                contentDescription = null,
                                tint = Color(0xFFEF4444)
                            )
                        },
                        colorFondo = Color(0xFFFEE2E2),
                        colorTexto = Color(0xFF991B1B),
                        modificador = Modifier.weight(1f)
                    )
                }
            }

            item {
                TarjetaEstadistica(
                    titulo = "Balance",
                    valor = "S/. ${String.format("%.2f", stats.balance)}",
                    icono = {
                        Icon(
                            Icons.Default.AccountBalance,
                            contentDescription = null,
                            tint = if (stats.balance >= 0)
                                Color(0xFF3B82F6) else Color(0xFFEF4444)
                        )
                    },
                    colorFondo = if (stats.balance >= 0)
                        Color(0xFFDBEAFE) else Color(0xFFFEE2E2),
                    colorTexto = if (stats.balance >= 0)
                        Color(0xFF1E40AF) else Color(0xFF991B1B)
                )
            }

            if (stats.categoriaConMasGastos.isNotBlank() && stats.categoriaConMasGastos != "Sin gastos") {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFEF3C7)
                        )
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
                                    text = "Categoría con más gastos",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFF92400E)
                                )
                                Text(
                                    text = stats.categoriaConMasGastos,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color(0xFF78350F)
                                )
                            }
                            Icon(
                                Icons.Default.Category,
                                contentDescription = null,
                                tint = Color(0xFFD97706),
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = "Gráfico de Gastos",
                style = MaterialTheme.typography.titleLarge
            )
        }

        item {
            if (estado.datosGrafico.isNotEmpty()) {
                GraficoBarras(
                    datos = estado.datosGrafico,
                    modificador = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                )
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.ShowChart,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "No hay transacciones en este período",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GraficoBarras(
    datos: List<PuntoGrafico>,
    modificador: Modifier = Modifier
) {
    Card(
        modifier = modificador,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            val maxValor = datos.maxOfOrNull { it.valor } ?: 1.0

            // Área del gráfico
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                val barWidth = size.width / datos.size
                val barSpacing = barWidth * 0.2f
                val actualBarWidth = barWidth - barSpacing

                datos.forEachIndexed { index, punto ->
                    val barHeight = if (maxValor > 0) {
                        (punto.valor / maxValor * size.height * 0.9).toFloat()
                    } else 0f

                    val x = index * barWidth + barSpacing / 2
                    val y = size.height - barHeight

                    // Dibujar barra
                    drawRect(
                        color = Color(0xFF3B82F6),
                        topLeft = Offset(x, y),
                        size = androidx.compose.ui.geometry.Size(actualBarWidth, barHeight)
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // Etiquetas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                datos.forEach { punto ->
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = punto.etiqueta,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "S/. ${String.format("%.0f", punto.valor)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SelectorPeriodo(
    periodoSeleccionado: String,
    alSeleccionar: (String) -> Unit
) {
    val periodos = listOf("semana", "mes", "anio")
    val etiquetas = mapOf(
        "semana" to "Semana",
        "mes" to "Mes",
        "anio" to "Año"
    )

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(periodos) { periodo ->
            FilterChip(
                selected = periodo == periodoSeleccionado,
                onClick = { alSeleccionar(periodo) },
                label = { Text(etiquetas[periodo] ?: periodo) },
                leadingIcon = if (periodo == periodoSeleccionado) {
                    { Icon(Icons.Default.Check, contentDescription = null, Modifier.size(18.dp)) }
                } else null
            )
        }
    }
}

@Composable
private fun TarjetaEstadistica(
    titulo: String,
    valor: String,
    icono: @Composable () -> Unit,
    colorFondo: Color,
    colorTexto: Color,
    modificador: Modifier = Modifier
) {
    Card(
        modifier = modificador.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = colorFondo
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = titulo,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorTexto
                )
                icono()
            }
            Text(
                text = valor,
                style = MaterialTheme.typography.titleLarge,
                color = colorTexto
            )
        }
    }
}

private fun obtenerNombrePeriodo(periodo: String): String {
    return when (periodo) {
        "semana" -> "de la Semana"
        "mes" -> "del Mes"
        "anio" -> "del Año"
        else -> ""
    }
}