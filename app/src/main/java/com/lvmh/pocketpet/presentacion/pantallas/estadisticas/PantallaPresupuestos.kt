package com.lvmh.pocketpet.presentacion.pantallas.estadisticas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.lvmh.pocketpet.dominio.modelos.Presupuesto
import com.lvmh.pocketpet.presentacion.viewmodels.PresupuestoViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPresupuestos(
    viewModel: PresupuestoViewModel,
    alRegresar: () -> Unit
) {
    val estado by viewModel.estado.collectAsState()
    var mostrarDialogo by remember { mutableStateOf(false) }

    // Inicializar con un userId temporal
    LaunchedEffect(Unit) {
        viewModel.establecerUsuario("usuario_temp")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Presupuestos") },
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
                Icon(Icons.Default.Add, contentDescription = "Agregar presupuesto")
            }
        }
    ) { padding ->
        when {
            estado.cargando && estado.presupuestos.isEmpty() -> {
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
                    Text(
                        text = estado.error ?: "Error desconocido",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            estado.presupuestos.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No hay presupuestos. Agrega uno nuevo.")
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(estado.presupuestos) { presupuesto ->
                        TarjetaPresupuesto(
                            presupuesto = presupuesto,
                            alEliminar = { viewModel.eliminarPresupuesto(presupuesto) }
                        )
                    }
                }
            }
        }

        if (mostrarDialogo) {
            DialogoNuevoPresupuesto(
                alConfirmar = { categoria, monto, periodo, alerta ->
                    viewModel.crearPresupuesto(categoria, monto, periodo, alerta)
                    mostrarDialogo = false
                },
                alDismiss = { mostrarDialogo = false }
            )
        }
    }
}

@Composable
private fun TarjetaPresupuesto(
    presupuesto: Presupuesto,
    alEliminar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (presupuesto.excedido)
                Color(0xFFFEE2E2)
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
                    Text(
                        text = "Categoría: ${presupuesto.categoriaId}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = presupuesto.periodo.replaceFirstChar {
                            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(onClick = alEliminar) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Gastado: $${String.format("%.2f", presupuesto.gastado)} de $${String.format("%.2f", presupuesto.monto)}",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Barra de progreso simple
            LinearProgressIndicator(
                progress = (presupuesto.porcentajeGastado / 100).toFloat().coerceIn(0f, 1f),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = if (presupuesto.excedido)
                    Color(0xFFEF4444)
                else if (presupuesto.porcentajeGastado >= presupuesto.alertaEn)
                    Color(0xFFF59E0B)
                else
                    Color(0xFF10B981),
                trackColor = Color(0xFFE5E7EB)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${presupuesto.porcentajeGastado}%",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (presupuesto.excedido) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "⚠️ Presupuesto excedido por $${String.format("%.2f", presupuesto.gastado - presupuesto.monto)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFEF4444)
                )
            }
        }
    }
}

@Composable
private fun DialogoNuevoPresupuesto(
    alConfirmar: (String, Double, String, Int) -> Unit,
    alDismiss: () -> Unit
) {
    var categoria by remember { mutableStateOf("") }
    var monto by remember { mutableStateOf("") }
    var periodo by remember { mutableStateOf("mensual") }
    var alerta by remember { mutableStateOf("80") }

    AlertDialog(
        onDismissRequest = alDismiss,
        title = { Text("Nuevo Presupuesto") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = categoria,
                    onValueChange = { categoria = it },
                    label = { Text("Categoría ID") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = monto,
                    onValueChange = { monto = it },
                    label = { Text("Monto") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = alerta,
                    onValueChange = { alerta = it },
                    label = { Text("Alerta en (%)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val montoDouble = monto.toDoubleOrNull() ?: 0.0
                    val alertaInt = alerta.toIntOrNull() ?: 80
                    alConfirmar(categoria, montoDouble, periodo, alertaInt)
                }
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