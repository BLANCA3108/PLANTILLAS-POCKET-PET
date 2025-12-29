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

    // Inicializar con un userId temporal
    LaunchedEffect(Unit) {
        viewModel.inicializar("usuario_temp")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("PocketPet")
                        Icon(
                            Icons.Default.Pets,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                actions = {
                    // Botón para ir a la mascota (método 1)
                    IconButton(onClick = { alNavegar(Routes.MASCOTA) }) {
                        Icon(
                            Icons.Default.Pets,
                            contentDescription = "Mi Mascota",
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        bottomBar = {
            // Barra de navegación inferior (método 2 - recomendado)
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("Inicio") },
                    selected = true,
                    onClick = { /* Ya estamos aquí */ }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Pets, contentDescription = null) },
                    label = { Text("Mascota") },
                    selected = false,
                    onClick = { alNavegar(Routes.MASCOTA) }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.BarChart, contentDescription = null) },
                    label = { Text("Estadísticas") },
                    selected = false,
                    onClick = { alNavegar(Routes.ESTADISTICAS) }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.AccountBalanceWallet, contentDescription = null) },
                    label = { Text("Presupuestos") },
                    selected = false,
                    onClick = { alNavegar(Routes.PRESUPUESTOS) }
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { mostrarDialogoNuevaTransaccion = true },
                containerColor = MaterialTheme.colorScheme.primary
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
                        Icon(
                            Icons.Default.ErrorOutline,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = error ?: "Error desconocido",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge
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
                    // Resumen de balance mejorado
                    TarjetaBalance(
                        balance = balance,
                        totalIngresos = totalIngresos,
                        totalGastos = totalGastos
                    )

                    // Encabezado de transacciones
                    if (transacciones.isNotEmpty()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Transacciones Recientes",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${transacciones.size} total",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Lista de transacciones
                    if (transacciones.isEmpty()) {
                        EstadoVacio()
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(bottom = 16.dp)
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
private fun TarjetaBalance(
    balance: Double,
    totalIngresos: Double,
    totalGastos: Double
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Balance Total",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "$${String.format("%.2f", balance)}",
                style = MaterialTheme.typography.displaySmall,
                color = if (balance >= 0)
                    Color(0xFF10B981)
                else
                    Color(0xFFEF4444)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Divider(
                modifier = Modifier.fillMaxWidth(0.9f),
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.3f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ItemResumen(
                    icono = Icons.Default.TrendingUp,
                    titulo = "Ingresos",
                    monto = totalIngresos,
                    color = Color(0xFF10B981)
                )

                VerticalDivider(
                    modifier = Modifier.height(60.dp),
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.3f)
                )

                ItemResumen(
                    icono = Icons.Default.TrendingDown,
                    titulo = "Gastos",
                    monto = totalGastos,
                    color = Color(0xFFEF4444)
                )
            }
        }
    }
}

@Composable
private fun ItemResumen(
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    titulo: String,
    monto: Double,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icono,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = titulo,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = "$${String.format("%.2f", monto)}",
            style = MaterialTheme.typography.titleMedium,
            color = color
        )
    }
}

@Composable
private fun EstadoVacio() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                Icons.Default.Receipt,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
            Text(
                text = "No hay transacciones",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Presiona el botón + para agregar tu primera transacción",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
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
    var mostrarDialogoConfirmacion by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                // Emoji de categoría con fondo
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = transaccion.categoriaEmoji,
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                }

                Column {
                    Text(
                        text = transaccion.descripcion,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = transaccion.categoriaNombre,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = dateFormat.format(Date(transaccion.fecha)),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
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
                IconButton(
                    onClick = { mostrarDialogoConfirmacion = true },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }

    if (mostrarDialogoConfirmacion) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoConfirmacion = false },
            icon = { Icon(Icons.Default.Warning, contentDescription = null) },
            title = { Text("Confirmar eliminación") },
            text = { Text("¿Estás seguro de que deseas eliminar esta transacción?") },
            confirmButton = {
                Button(
                    onClick = {
                        alEliminar()
                        mostrarDialogoConfirmacion = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoConfirmacion = false }) {
                    Text("Cancelar")
                }
            }
        )
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
    var errorMonto by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = alDismiss,
        icon = { Icon(Icons.Default.Add, contentDescription = null) },
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
                        modifier = Modifier.weight(1f),
                        leadingIcon = if (tipo == TipoTransaccion.GASTO) {
                            { Icon(Icons.Default.Check, contentDescription = null) }
                        } else null
                    )
                    FilterChip(
                        selected = tipo == TipoTransaccion.INGRESO,
                        onClick = { tipo = TipoTransaccion.INGRESO },
                        label = { Text("Ingreso") },
                        modifier = Modifier.weight(1f),
                        leadingIcon = if (tipo == TipoTransaccion.INGRESO) {
                            { Icon(Icons.Default.Check, contentDescription = null) }
                        } else null
                    )
                }

                OutlinedTextField(
                    value = monto,
                    onValueChange = {
                        monto = it
                        errorMonto = it.toDoubleOrNull() == null && it.isNotEmpty()
                    },
                    label = { Text("Monto") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Text("$") },
                    isError = errorMonto,
                    supportingText = if (errorMonto) {
                        { Text("Ingresa un monto válido") }
                    } else null,
                    singleLine = true
                )

                OutlinedTextField(
                    value = categoriaEmoji,
                    onValueChange = { categoriaEmoji = it },
                    label = { Text("Emoji") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = categoriaNombre,
                    onValueChange = { categoriaNombre = it },
                    label = { Text("Categoría") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val montoDouble = monto.toDoubleOrNull() ?: 0.0
                    if (montoDouble > 0) {
                        alConfirmar(tipo, montoDouble, categoriaId, categoriaNombre, categoriaEmoji, descripcion)
                    }
                },
                enabled = monto.toDoubleOrNull() != null && monto.toDoubleOrNull()!! > 0
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