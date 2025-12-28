package com.lvmh.pocketpet.presentacion.pantallas.estadisticas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.lvmh.pocketpet.presentacion.componentes.*
import com.lvmh.pocketpet.presentacion.viewmodels.EstadisticasViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaTendencias(
    viewModel: EstadisticasViewModel,
    alRegresar: () -> Unit
) {
    val estado by viewModel.estado.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.compararPeriodos()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tendencias") },
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Evolución Temporal",
                    style = MaterialTheme.typography.titleLarge
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    VistaGrafico(
                        datos = estado.datosGrafico,
                        tipo = TipoGrafico.LINEA,
                        modificador = Modifier.padding(16.dp)
                    )
                }
            }

            estado.comparacion?.let { comp ->
                item {
                    Text(
                        text = "Comparación con Período Anterior",
                        style = MaterialTheme.typography.titleLarge
                    )
                }

                item {
                    TarjetaComparacion(
                        titulo = "Ingresos",
                        valorActual = comp.periodoActualIngresos,
                        valorAnterior = comp.periodoAnteriorIngresos,
                        cambioPorcentaje = comp.cambioIngresosPorc
                    )
                }

                item {
                    TarjetaComparacion(
                        titulo = "Gastos",
                        valorActual = comp.periodoActualGastos,
                        valorAnterior = comp.periodoAnteriorGastos,
                        cambioPorcentaje = comp.cambioGastosPorc
                    )
                }
            }
        }
    }
}

@Composable
private fun TarjetaComparacion(
    titulo: String,
    valorActual: Double,
    valorAnterior: Double,
    cambioPorcentaje: Double
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
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Actual",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "$${String.format("%.2f", valorActual)}",
                        style = MaterialTheme.typography.titleLarge
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Anterior",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "$${String.format("%.2f", valorAnterior)}",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (cambioPorcentaje >= 0) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                    contentDescription = null,
                    tint = if (cambioPorcentaje >= 0) Color(0xFF10B981) else Color(0xFFEF4444)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${String.format("%.1f", kotlin.math.abs(cambioPorcentaje))}%",
                    style = MaterialTheme.typography.titleMedium,
                    color = if (cambioPorcentaje >= 0) Color(0xFF10B981) else Color(0xFFEF4444)
                )
            }
        }
    }
}