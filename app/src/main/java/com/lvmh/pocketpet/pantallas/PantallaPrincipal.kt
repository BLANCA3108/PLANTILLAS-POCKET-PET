package com.lvmh.pocketpet.presentacion.pantallas

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
import com.lvmh.pocketpet.dominio.modelos.Transaccion
import com.lvmh.pocketpet.dominio.modelos.TipoTransaccion
import com.lvmh.pocketpet.viewmodels.TransaccionViewModel
import com.lvmh.pocketpet.presentacion.navegacion.Routes
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPrincipal(
    viewModel: TransaccionViewModel,
    alNavegar: (String) -> Unit
) {
    // Estados del ViewModel
    val transacciones by viewModel.transaccionesFiltradas.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val balance by viewModel.balance.collectAsState()
    val totalIngresos by viewModel.totalIngresos.collectAsState()
    val totalGastos by viewModel.totalGastos.collectAsState()

    var mostrarDialogoNuevaTransaccion by remember { mutableStateOf(false) }

    // Inicializar con un userId temporal (deberías obtenerlo de Auth)
    LaunchedEffect(Unit) {
        viewModel.inicializar("usuario_temp")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("PocketPet") },
                actions = {
                    IconButton(onClick = { alNavegar(Routes.ESTADISTICAS) }) {
                        Icon(Icons.Default.BarChart, contentDescription = "Estadísticas")
                    }
                    IconButton(onClick = { alNavegar(Routes.PRESUPUESTOS) }) {
                        Icon(Icons.Default.AccountBalanceWallet, contentDescription = "Presupuestos")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { mostrarDialogoNuevaTransaccion = true }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar transacción")
            }
        }
    ) { padding ->
        when {
            isLoading && transacciones.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = error ?: "Error desconocido",
                            color = MaterialTheme.colorScheme.error
                        )
                        Button(onClick = { viewModel.limpiarError() }) {
                            Text("Cerrar")
                        }
                    }
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    // Resumen de balance
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Balance Total",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "$${String.format("%.2f", balance)}",
                                style = MaterialTheme.typography.headlineLarge,
                                color = if (balance >= 0)
                                    Color(0xFF10B981)
                                else
                                    Color(0xFFEF4444)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "Ingresos",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    Text(
                                        text = "$${String.format("%.2f", totalIngresos)}",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = Color(0xFF10B981)
                                    )
                                }

                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "Gastos",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    Text(
                                        text = "$${String.format("%.2f", totalGastos)}",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = Color(0xFFEF4444)
                                    )
                                }
                            }
                        }
                    }

                    // Lista de transacciones
                    if (transacciones.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No hay transacciones. Agrega una nueva.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(transacciones) { transaccion ->
                                TarjetaTransaccion(
                                    transaccion = transaccion,
                                    alEliminar = { viewModel.eliminarTransaccion(transaccion) }
                                )
                            }
                        }
                    }
                }
            }
        }

        if (mostrarDialogoNuevaTransaccion) {
            DialogoNuevaTransaccion(
                alConfirmar = { tipo, monto, categoriaId, categoriaNombre, categoriaEmoji, descripcion ->
                    viewModel.crearTransaccion(
                        tipo = tipo,
                        monto = monto,
                        categoriaId = categoriaId,
                        categoriaNombre = categoriaNombre,
                        categoriaEmoji = categoriaEmoji,
                        descripcion = descripcion
                    )
                    mostrarDialogoNuevaTransaccion = false
                },
                alDismiss = { mostrarDialogoNuevaTransaccion = false }
            )
        }
    }
}

@Composable
private fun TarjetaTransaccion(
    transaccion: Transaccion,
    alEliminar: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

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
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Emoji de categoría
                Text(
                    text = transaccion.categoriaEmoji,
                    style = MaterialTheme.typography.headlineMedium
                )

                Column {
                    Text(
                        text = transaccion.descripcion,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = transaccion.categoriaNombre,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = dateFormat.format(Date(transaccion.fecha)),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = if (transaccion.tipo == TipoTransaccion.INGRESO)
                        "+$${String.format("%.2f", transaccion.monto)}"
                    else
                        "-$${String.format("%.2f", transaccion.monto)}",
                    style = MaterialTheme.typography.titleLarge,
                    color = if (transaccion.tipo == TipoTransaccion.INGRESO)
                        Color(0xFF10B981)
                    else
                        Color(0xFFEF4444)
                )
                IconButton(onClick = alEliminar) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
private fun DialogoNuevaTransaccion(
    alConfirmar: (TipoTransaccion, Double, String, String, String, String) -> Unit,
    alDismiss: () -> Unit
) {
    var tipo by remember { mutableStateOf(TipoTransaccion.GASTO) }
    var monto by remember { mutableStateOf("") }
    var categoriaId by remember { mutableStateOf("cat_1") }
    var categoriaNombre by remember { mutableStateOf("General") }
    var categoriaEmoji by remember { mutableStateOf("💰") }
    var descripcion by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = alDismiss,
        title = { Text("Nueva Transacción") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Selector de tipo
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = tipo == TipoTransaccion.GASTO,
                        onClick = { tipo = TipoTransaccion.GASTO },
                        label = { Text("Gasto") },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = tipo == TipoTransaccion.INGRESO,
                        onClick = { tipo = TipoTransaccion.INGRESO },
                        label = { Text("Ingreso") },
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = monto,
                    onValueChange = { monto = it },
                    label = { Text("Monto") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = categoriaNombre,
                    onValueChange = { categoriaNombre = it },
                    label = { Text("Categoría") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = categoriaEmoji,
                    onValueChange = { categoriaEmoji = it },
                    label = { Text("Emoji") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val montoDouble = monto.toDoubleOrNull() ?: 0.0
                    alConfirmar(tipo, montoDouble, categoriaId, categoriaNombre, categoriaEmoji, descripcion)
                }
            ) {
                Text("Agregar")
            }
        },
        dismissButton = {
            TextButton(onClick = alDismiss) {
                Text("Cancelar")
            }
        }
    )
}