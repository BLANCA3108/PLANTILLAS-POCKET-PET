package com.lvmh.pocketpet.presentacion.pantallas.estadisticas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lvmh.pocketpet.presentacion.viewmodels.EstadisticasViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaComparativos(
    viewModel: EstadisticasViewModel,
    alRegresar: () -> Unit
) {
    val estado by viewModel.estado.collectAsState()

    // Cargar la comparación al iniciar
    LaunchedEffect(estado.periodoSeleccionado) {
        viewModel.compararPeriodos()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Análisis Comparativo") },
                navigationIcon = {
                    IconButton(onClick = alRegresar) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
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
                        .padding(padding)
                        .padding(16.dp),
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
                            color = MaterialTheme.colorScheme.error
                        )
                        Button(onClick = { viewModel.compararPeriodos() }) {
                            Text("Reintentar")
                        }
                    }
                }
            }

            estado.comparacion != null -> {
                ContenidoComparativo(
                    estado = estado,
                    alCambiarPeriodo = { periodo ->
                        viewModel.cambiarPeriodo(periodo)
                    },
                    modificador = Modifier.padding(padding)
                )
            }

            else -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No hay datos disponibles")
                }
            }
        }
    }
}

@Composable
private fun ContenidoComparativo(
    estado: com.lvmh.pocketpet.presentacion.viewmodels.EstadoEstadisticas,
    alCambiarPeriodo: (String) -> Unit,
    modificador: Modifier = Modifier
) {
    val comp = estado.comparacion ?: return

    LazyColumn(
        modifier = modificador
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        item {
            Text(
                text = "Periodo de Comparación",
                style = MaterialTheme.typography.titleLarge
            )
        }

        item {
            SelectorPeriodo(
                periodoSeleccionado = estado.periodoSeleccionado,
                alSeleccionar = alCambiarPeriodo
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.CompareArrows,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Comparando ${obtenerNombrePeriodo(estado.periodoSeleccionado)} actual vs anterior",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        item {
            Text(
                text = "Comparación Detallada",
                style = MaterialTheme.typography.titleMedium
            )
        }

        item {
            TarjetaComparativaDetallada(
                titulo = "Ingresos",
                icono = Icons.Default.TrendingUp,
                valorActual = comp.periodoActualIngresos,
                valorAnterior = comp.periodoAnteriorIngresos,
                cambio = comp.cambioIngresos,
                cambioPorc = comp.cambioIngresosPorc,
                color = Color(0xFF10B981)
            )
        }

        item {
            TarjetaComparativaDetallada(
                titulo = "Gastos",
                icono = Icons.Default.TrendingDown,
                valorActual = comp.periodoActualGastos,
                valorAnterior = comp.periodoAnteriorGastos,
                cambio = comp.cambioGastos,
                cambioPorc = comp.cambioGastosPorc,
                color = Color(0xFFEF4444)
            )
        }

        item {
            val balanceActual = comp.periodoActualIngresos - comp.periodoActualGastos
            val balanceAnterior = comp.periodoAnteriorIngresos - comp.periodoAnteriorGastos
            val cambioBalance = balanceActual - balanceAnterior
            val cambioBalancePorc = if (balanceAnterior != 0.0) {
                (cambioBalance / kotlin.math.abs(balanceAnterior)) * 100
            } else 0.0

            TarjetaComparativaDetallada(
                titulo = "Balance",
                icono = Icons.Default.AccountBalance,
                valorActual = balanceActual,
                valorAnterior = balanceAnterior,
                cambio = cambioBalance,
                cambioPorc = cambioBalancePorc,
                color = Color(0xFF3B82F6)
            )
        }

        item {
            Text(
                text = "Resumen",
                style = MaterialTheme.typography.titleMedium
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Tu situación financiera ha:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    val balanceActual = comp.periodoActualIngresos - comp.periodoActualGastos
                    val balanceAnterior = comp.periodoAnteriorIngresos - comp.periodoAnteriorGastos
                    val mejoro = balanceActual > balanceAnterior

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            if (mejoro) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                            contentDescription = null,
                            tint = if (mejoro) Color(0xFF10B981) else Color(0xFFEF4444)
                        )
                        Text(
                            text = if (mejoro) "Mejorado" else "Empeorado",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = if (mejoro) Color(0xFF10B981) else Color(0xFFEF4444)
                        )
                    }

                    if (comp.cambioGastosPorc > 10) {
                        Text(
                            "⚠️ Tus gastos aumentaron significativamente",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFD97706)
                        )
                    }

                    if (comp.cambioIngresosPorc < -10) {
                        Text(
                            "⚠️ Tus ingresos disminuyeron considerablemente",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFD97706)
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

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        periodos.forEach { periodo ->
            FilterChip(
                selected = periodo == periodoSeleccionado,
                onClick = { alSeleccionar(periodo) },
                label = { Text(etiquetas[periodo] ?: periodo) },
                modifier = Modifier.weight(1f),
                leadingIcon = if (periodo == periodoSeleccionado) {
                    { Icon(Icons.Default.Check, contentDescription = null, Modifier.size(18.dp)) }
                } else null
            )
        }
    }
}

@Composable
private fun TarjetaComparativaDetallada(
    titulo: String,
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    valorActual: Double,
    valorAnterior: Double,
    cambio: Double,
    cambioPorc: Double,
    color: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                Text(
                    text = titulo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    icono,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Column {
                    Text(
                        text = "Periodo Actual",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "S/. ${String.format("%.2f", valorActual)}",
                        style = MaterialTheme.typography.headlineSmall,
                        color = color,
                        fontWeight = FontWeight.Bold
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Periodo Anterior",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "S/. ${String.format("%.2f", valorAnterior)}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column {
                    Text(
                        text = "Cambio",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            if (cambio >= 0) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                            contentDescription = null,
                            tint = if (cambio >= 0) Color(0xFF10B981) else Color(0xFFEF4444),
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "S/. ${String.format("%.2f", kotlin.math.abs(cambio))}",
                            style = MaterialTheme.typography.titleLarge,
                            color = if (cambio >= 0) Color(0xFF10B981) else Color(0xFFEF4444),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (cambioPorc >= 0)
                            Color(0xFFD1FAE5) else Color(0xFFFEE2E2)
                    )
                ) {
                    Text(
                        text = "${if (cambioPorc >= 0) "+" else ""}${String.format("%.1f", cambioPorc)}%",
                        style = MaterialTheme.typography.titleMedium,
                        color = if (cambioPorc >= 0) Color(0xFF065F46) else Color(0xFF991B1B),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}

private fun obtenerNombrePeriodo(periodo: String): String {
    return when (periodo) {
        "semana" -> "la semana"
        "mes" -> "el mes"
        "anio" -> "el año"
        else -> "el periodo"
    }
}