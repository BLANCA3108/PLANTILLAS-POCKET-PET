package com.lvmh.pocketpet.presentacion.pantallas.estadisticas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.lvmh.pocketpet.dominio.modelos.Meta
import com.lvmh.pocketpet.presentacion.componentes.BarraProgresoPersonalizada
import com.lvmh.pocketpet.presentacion.viewmodels.PresupuestoViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaMetas(
    viewModel: PresupuestoViewModel,
    alRegresar: () -> Unit
) {
    val estado by viewModel.estado.collectAsState()
    var mostrarDialogo by remember { mutableStateOf(false) }
    var metaSeleccionada by remember { mutableStateOf<Meta?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Metas Financieras") },
                navigationIcon = {
                    IconButton(onClick = alRegresar) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { mostrarDialogo = true }) {
                Icon(Icons.Default.Add, contentDescription = "Agregar meta")
            }
        }
    ) { padding ->
        if (estado.metas.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.Flag,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("No hay metas. Define una nueva.")
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val metasActivas = estado.metas.filter { !it.completada }
                val metasCompletadas = estado.metas.filter { it.completada }

                if (metasActivas.isNotEmpty()) {
                    item {
                        Text(
                            text = "Activas",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }

                    items(metasActivas) { meta ->
                        TarjetaMeta(
                            meta = meta,
                            alClick = { metaSeleccionada = meta },
                            alEliminar = { viewModel.eliminarMeta(meta) }
                        )
                    }
                }

                if (metasCompletadas.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Completadas",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }

                    items(metasCompletadas) { meta ->
                        TarjetaMeta(
                            meta = meta,
                            alClick = { metaSeleccionada = meta },
                            alEliminar = { viewModel.eliminarMeta(meta) }
                        )
                    }
                }
            }
        }

        if (mostrarDialogo) {
            DialogoNuevaMeta(
                alConfirmar = { nombre, descripcion, monto, fechaLimite, categoria ->
                    viewModel.crearMeta(nombre, descripcion, monto, fechaLimite, categoria)
                    mostrarDialogo = false
                },
                alDismiss = { mostrarDialogo = false }
            )
        }

        metaSeleccionada?.let { meta ->
            DialogoAgregarMonto(
                meta = meta,
                alConfirmar = { monto ->
                    viewModel.agregarMontoMeta(meta.id, monto)
                    metaSeleccionada = null
                },
                alDismiss = { metaSeleccionada = null }
            )
        }
    }
}

@Composable
private fun TarjetaMeta(
    meta: Meta,
    alClick: () -> Unit,
    alEliminar: () -> Unit
) {
    val formatoFecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    Card(
        onClick = { if (!meta.completada) alClick() },
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (meta.completada)
                Color(0xFFD1FAE5)
            else
                MaterialTheme.colorScheme.surfaceVariant
        )
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
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (meta.completada) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text(
                            text = meta.nombre,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    if (meta.descripcion.isNotEmpty()) {
                        Text(
                            text = meta.descripcion,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(onClick = alEliminar) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

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
                progreso = (meta.porcentajeCompletado / 100).toFloat().coerceIn(0f, 1f),
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
}