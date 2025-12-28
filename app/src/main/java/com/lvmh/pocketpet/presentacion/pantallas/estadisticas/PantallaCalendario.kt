package com.lvmh.pocketpet.presentacion.pantallas.estadisticas

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaCalendario(
    viewModel: EstadisticasViewModel,
    alRegresar: () -> Unit
) {
    val estado by viewModel.estado.collectAsState()
    var mesActual by remember { mutableStateOf(Calendar.getInstance()) }

    LaunchedEffect(mesActual) {
        viewModel.cargarReporteMensual(
            mesActual.get(Calendar.MONTH) + 1,
            mesActual.get(Calendar.YEAR)
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Calendario") },
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

            estado.reporteMensual?.let { reporte ->
                CalendarioMensual(
                    reporte = reporte,
                    mesActual = mesActual
                )
            } ?: Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
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
            IconButton(onClick = {
                val nuevoMes = mesActual.clone() as Calendar
                nuevoMes.add(Calendar.MONTH, -1)
                alCambiarMes(nuevoMes)
            }) {
                Icon(Icons.Default.ChevronLeft, contentDescription = "Mes anterior")
            }

            Text(
                text = "${nombresMeses[mesActual.get(Calendar.MONTH)]} ${mesActual.get(Calendar.YEAR)}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            IconButton(onClick = {
                val nuevoMes = mesActual.clone() as Calendar
                nuevoMes.add(Calendar.MONTH, 1)
                alCambiarMes(nuevoMes)
            }) {
                Icon(Icons.Default.ChevronRight, contentDescription = "Mes siguiente")
            }
        }
    }
}

@Composable
private fun CalendarioMensual(
    reporte: com.lvmh.pocketpet.dominio.casouso.estadisticas.ReporteMensual,
    mesActual: Calendar
) {
    val diasSemana = listOf("D", "L", "M", "M", "J", "V", "S")

    val primerDia = (mesActual.clone() as Calendar).apply {
        set(Calendar.DAY_OF_MONTH, 1)
    }
    val diasEnMes = mesActual.getActualMaximum(Calendar.DAY_OF_MONTH)
    val primerDiaSemana = primerDia.get(Calendar.DAY_OF_WEEK) - 1

    Column {
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(diasSemana) { dia ->
                Text(
                    text = dia,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            items(primerDiaSemana) {
                Spacer(modifier = Modifier.height(60.dp))
            }

            items((1..diasEnMes).toList()) { dia ->
                val monto = reporte.transaccionesPorDia[dia] ?: 0.0
                CeldaDia(dia = dia, monto = monto)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

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
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Ingresos",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "$${String.format("%.2f", reporte.totalIngresos)}",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF10B981)
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Gastos",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "$${String.format("%.2f", reporte.totalGastos)}",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFFEF4444)
                    )
                }
            }
        }
    }
}

@Composable
private fun CeldaDia(dia: Int, monto: Double) {
    val color = when {
        monto > 0 -> Color(0xFFD1FAE5)
        monto < 0 -> Color(0xFFFEE2E2)
        else -> MaterialTheme.colorScheme.surface
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
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
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            if (monto != 0.0) {
                Text(
                    text = "$${String.format("%.0f", kotlin.math.abs(monto))}",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (monto > 0) Color(0xFF10B981) else Color(0xFFEF4444)
                )
            }
        }
    }
}