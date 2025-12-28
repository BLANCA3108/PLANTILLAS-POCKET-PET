package com.lvmh.pocketpet.presentacion.pantallas.estadisticas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.lvmh.pocketpet.presentacion.componentes.TipoGrafico
import com.lvmh.pocketpet.presentacion.componentes.VistaGrafico
import com.lvmh.pocketpet.presentacion.viewmodels.EstadisticasViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaComparativos(
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Comparación Periodo Actual vs Anterior",
                    style = MaterialTheme.typography.titleLarge
                )
            }

            estado.comparacion?.let { comp ->
                item {
                    TarjetaComparativaDetallada(
                        titulo = "Ingresos",
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

                    TarjetaComparativaDetallada(
                        titulo = "Balance",
                        valorActual = balanceActual,
                        valorAnterior = balanceAnterior,
                        cambio = cambioBalance,
                        cambioPorc = if (balanceAnterior != 0.0) (cambioBalance / balanceAnterior) * 100 else 0.0,
                        color = Color(0xFF3B82F6)
                    )
                }
            }

            item {
                Text(
                    text = "Evolución Gráfica",
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
        }
    }
}

@Composable
private fun TarjetaComparativaDetallada(
    titulo: String,
    valorActual: Double,
    valorAnterior: Double,
    cambio: Double,
    cambioPorc: Double,
    color: Color
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

            Spacer(modifier = Modifier.height(16.dp))

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
                        style = MaterialTheme.typography.headlineSmall,
                        color = color
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
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
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
                Text(
                    text = "Cambio",
                    style = MaterialTheme.typography.bodyMedium
                )

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${if (cambio >= 0) "+" else ""}$${String.format("%.2f", cambio)}",
                        style = MaterialTheme.typography.titleMedium,
                        color = if (cambio >= 0) Color(0xFF10B981) else Color(0xFFEF4444)
                    )
                    Text(
                        text = "${if (cambioPorc >= 0) "+" else ""}${String.format("%.1f", cambioPorc)}%",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (cambioPorc >= 0) Color(0xFF10B981) else Color(0xFFEF4444)
                    )
                }
            }
        }
    }
}