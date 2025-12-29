package com.lvmh.pocketpet.presentacion.pantallas.estadisticas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lvmh.pocketpet.presentacion.viewmodels.EstadisticasViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaReportes(
    viewModel: EstadisticasViewModel,
    alRegresar: () -> Unit
) {
    val estado by viewModel.estado.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reportes") },
                navigationIcon = {
                    IconButton(onClick = alRegresar) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Exportar PDF */ }) {
                        Icon(Icons.Default.Download, contentDescription = "Exportar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Resumen Financiero",
                    style = MaterialTheme.typography.headlineMedium
                )
            }

            estado.estadisticas?.let { stats ->
                item {
                    SeccionReporte(
                        titulo = "Balance General",
                        items = listOf(
                            "Total Ingresos" to "$${String.format("%.2f", stats.totalIngresos)}",
                            "Total Gastos" to "$${String.format("%.2f", stats.totalGastos)}",
                            "Balance" to "$${String.format("%.2f", stats.balance)}",
                            "Promedio Diario" to "$${String.format("%.2f", stats.promedioGastoDiario)}"
                        )
                    )
                }
            }

            estado.reporteMensual?.let { reporte ->
                item {
                    SeccionReporte(
                        titulo = "Gastos por Categoría",
                        items = reporte.gastosPorCategoria.map { (categoria, monto) ->
                            categoria to "$${String.format("%.2f", monto)}"
                        }
                    )
                }

                item {
                    SeccionReporte(
                        titulo = "Ingresos por Categoría",
                        items = reporte.ingresosPorCategoria.map { (categoria, monto) ->
                            categoria to "$${String.format("%.2f", monto)}"
                        }
                    )
                }
            }

            item {
                Button(
                    onClick = { /* Generar PDF */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Download, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Descargar Reporte PDF")
                }
            }
        }
    }
}

@Composable
private fun SeccionReporte(
    titulo: String,
    items: List<Pair<String, String>>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = titulo,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(12.dp))

            items.forEachIndexed { index, (label, valor) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = valor,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                if (index < items.size - 1) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                }
            }
        }
    }
}