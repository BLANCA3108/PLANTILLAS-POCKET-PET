package com.lvmh.pocketpet.presentacion.pantallas.estadisticas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.lvmh.pocketpet.presentacion.viewmodels.EstadisticasViewModel
import com.lvmh.pocketpet.presentacion.viewmodels.ReporteMensual
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaCalendario(
    viewModel: EstadisticasViewModel,
    alRegresar: () -> Unit
) {
    val estado by viewModel.estado.collectAsState()
    var mesActual by remember { mutableStateOf(Calendar.getInstance()) }

    // Cargar reporte mensual al iniciar y al cambiar el mes
    LaunchedEffect(mesActual) {
        viewModel.cargarReporteMensual(
            mes = mesActual.get(Calendar.MONTH) + 1,
            anio = mesActual.get(Calendar.YEAR)
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Calendario Financiero") },
                navigationIcon = {
                    IconButton(onClick = alRegresar) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {

            SelectorMes(
                mesActual = mesActual,
                alCambiarMes = { mesActual = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            when {
                estado.cargando -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                estado.error != null -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = estado.error ?: "Error desconocido",
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                textAlign = TextAlign.Center
                            )
                            Button(
                                onClick = {
                                    viewModel.cargarReporteMensual(
                                        mes = mesActual.get(Calendar.MONTH) + 1,
                                        anio = mesActual.get(Calendar.YEAR)
                                    )
                                }
                            ) {
                                Text("Reintentar")
                            }
                        }
                    }
                }

                estado.reporteMensual != null -> {
                    CalendarioMensual(
                        reporte = estado.reporteMensual!!,
                        mesActual = mesActual
                    )
                }

                else -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No hay datos disponibles")
                    }
                }
            }
        }
    }
}

@Composable
private fun SelectorMes(
    mesActual: Calendar,
    alCambiarMes: (Calendar) -> Unit
) {
    val nombresMeses = listOf(
        "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
        "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(
                onClick = {
                    val nuevoMes = mesActual.clone() as Calendar
                    nuevoMes.add(Calendar.MONTH, -1)
                    alCambiarMes(nuevoMes)
                }
            ) {
                Icon(Icons.Default.ChevronLeft, contentDescription = "Mes anterior")
            }

            Text(
                text = "${nombresMeses[mesActual.get(Calendar.MONTH)]} ${mesActual.get(Calendar.YEAR)}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            IconButton(
                onClick = {
                    val nuevoMes = mesActual.clone() as Calendar
                    nuevoMes.add(Calendar.MONTH, 1)
                    alCambiarMes(nuevoMes)
                }
            ) {
                Icon(Icons.Default.ChevronRight, contentDescription = "Mes siguiente")
            }
        }
    }
}

@Composable
private fun CalendarioMensual(
    reporte: ReporteMensual,
    mesActual: Calendar
) {
    val diasSemana = listOf("D", "L", "M", "M", "J", "V", "S")

    val primerDiaMes = (mesActual.clone() as Calendar).apply {
        set(Calendar.DAY_OF_MONTH, 1)
    }

    val diasEnMes = mesActual.getActualMaximum(Calendar.DAY_OF_MONTH)
    val primerDiaSemana = primerDiaMes.get(Calendar.DAY_OF_WEEK) - 1

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // Resumen del mes
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Ingresos",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "S/. ${String.format("%.2f", reporte.totalIngresos)}",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF10B981),
                        fontWeight = FontWeight.Bold
                    )
                }

                Divider(
                    modifier = Modifier
                        .height(40.dp)
                        .width(1.dp)
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Gastos",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "S/. ${String.format("%.2f", reporte.totalGastos)}",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFFEF4444),
                        fontWeight = FontWeight.Bold
                    )
                }

                Divider(
                    modifier = Modifier
                        .height(40.dp)
                        .width(1.dp)
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Balance",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "S/. ${String.format("%.2f", reporte.balance)}",
                        style = MaterialTheme.typography.titleMedium,
                        color = if (reporte.balance >= 0) Color(0xFF3B82F6) else Color(0xFFEF4444),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Calendario
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier.padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {

                // Encabezados de días
                items(diasSemana) { dia ->
                    Text(
                        text = dia,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                // Espacios vacíos antes del primer día
                items(primerDiaSemana) {
                    Spacer(modifier = Modifier.height(70.dp))
                }

                // Días del mes
                items((1..diasEnMes).toList()) { dia ->
                    val monto = reporte.transaccionesPorDia[dia] ?: 0.0
                    CeldaDia(dia = dia, monto = monto)
                }
            }
        }

        // Leyenda
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .padding(2.dp)
                    ) {
                        Card(
                            modifier = Modifier.fillMaxSize(),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFD1FAE5)
                            )
                        ) {}
                    }
                    Text(
                        "Ingresos",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .padding(2.dp)
                    ) {
                        Card(
                            modifier = Modifier.fillMaxSize(),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFFEE2E2)
                            )
                        ) {}
                    }
                    Text(
                        "Gastos",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .padding(2.dp)
                    ) {
                        Card(
                            modifier = Modifier.fillMaxSize(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {}
                    }
                    Text(
                        "Sin movimientos",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
private fun CeldaDia(
    dia: Int,
    monto: Double
) {
    val (color, textColor) = when {
        monto > 0 -> Color(0xFFD1FAE5) to Color(0xFF10B981)
        monto < 0 -> Color(0xFFFEE2E2) to Color(0xFFEF4444)
        else -> MaterialTheme.colorScheme.surface to MaterialTheme.colorScheme.onSurface
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = dia.toString(),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyLarge
            )

            if (monto != 0.0) {
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "S/. ${String.format("%.0f", kotlin.math.abs(monto))}",
                    color = textColor,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}