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
<<<<<<< HEAD
=======
        onClick = alClick,
        enabled = !meta.completada, // 👈 AQUÍ LA CLAVE
>>>>>>> 48ca746fe8a4a3ac455baa997861fd65335cb759
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

<<<<<<< HEAD
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
=======
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "$${String.format("%.2f", meta.montoActual)}",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Meta: $${String.format("%.2f", meta.montoObjetivo)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            BarraProgresoPersonalizada(
                progreso = (meta.porcentajeCompletado / 100)
                    .toFloat()
                    .coerceIn(0f, 1f),
                colorProgreso = if (meta.completada)
                    Color(0xFF10B981)
                else
                    Color(0xFF6366F1),
                mostrarPorcentaje = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (meta.completada)
                        "Completada"
                    else
                        "${meta.diasRestantes} días restantes",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = formatoFecha.format(Date(meta.fechaLimite)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}


@Composable
private fun DialogoNuevaMeta(
    alConfirmar: (String, String, Double, Long, String) -> Unit,
    alDismiss: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var monto by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }

    val calendar = Calendar.getInstance()
    calendar.add(Calendar.MONTH, 1)

    AlertDialog(
        onDismissRequest = alDismiss,
        title = { Text("Nueva Meta") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = monto,
                    onValueChange = { monto = it },
                    label = { Text("Monto Objetivo") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = categoria,
                    onValueChange = { categoria = it },
                    label = { Text("Categoría ID") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val montoDouble = monto.toDoubleOrNull() ?: 0.0
                    alConfirmar(nombre, descripcion, montoDouble, calendar.timeInMillis, categoria)
                },
                enabled = nombre.isNotEmpty() && monto.isNotEmpty()
            ) {
                Text("Crear")
            }
        },
        dismissButton = {
            TextButton(onClick = alDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
private fun DialogoAgregarMonto(
    meta: Meta,
    alConfirmar: (Double) -> Unit,
    alDismiss: () -> Unit
) {
    var monto by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = alDismiss,
        title = { Text("Agregar Aporte") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Meta: ${meta.nombre}")
                Text("Progreso: $${String.format("%.2f", meta.montoActual)} / $${String.format("%.2f", meta.montoObjetivo)}")

                OutlinedTextField(
                    value = monto,
                    onValueChange = { monto = it },
                    label = { Text("Monto a aportar") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val montoDouble = monto.toDoubleOrNull() ?: 0.0
                    if (montoDouble > 0) alConfirmar(montoDouble)
                },
                enabled = monto.toDoubleOrNull()?.let { it > 0 } ?: false
            ) {
                Text("Aportar")
            }
        },
        dismissButton = {
            TextButton(onClick = alDismiss) {
                Text("Cancelar")
            }
        }
    )
>>>>>>> 48ca746fe8a4a3ac455baa997861fd65335cb759
}