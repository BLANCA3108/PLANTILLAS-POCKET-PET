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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.lvmh.pocketpet.dominio.modelos.Categoria
import com.lvmh.pocketpet.dominio.modelos.Transaccion
import com.lvmh.pocketpet.dominio.modelos.TipoTransaccion
import com.lvmh.pocketpet.dominio.modelos.TipoCategoria
import com.lvmh.pocketpet.dominio.modelos.toTipoTransaccion
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
    var mostrarMenuMas by remember { mutableStateOf(false) }
    var mostrarMenuAnalisis by remember { mutableStateOf(false) }

    // Inicializar con el usuario real - CORREGIDO
    LaunchedEffect(Unit) {
        viewModel.inicializar()  // ← SIN parámetro
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
                    // QUITAMOS el botón "+" de aquí
                    IconButton(onClick = { alNavegar(Routes.MI_PERFIL) }) {
                        Icon(
                            Icons.Default.AccountCircle,
                            contentDescription = "Mi Perfil",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(onClick = { alNavegar(Routes.CALENDARIO) }) {
                        Icon(
                            Icons.Default.CalendarMonth,
                            contentDescription = "Calendario",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        bottomBar = {
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
                    label = { Text("Análisis") },
                    selected = false,
                    onClick = { mostrarMenuAnalisis = true }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.MoreHoriz, contentDescription = null) },
                    label = { Text("Más") },
                    selected = false,
                    onClick = { mostrarMenuMas = true }
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
                    TarjetaBalance(
                        balance = balance,
                        totalIngresos = totalIngresos,
                        totalGastos = totalGastos
                    )

                    if (transacciones.isNotEmpty()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Historial de transacciones",
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

        // Diálogo de nueva transacción
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
                alDismiss = { mostrarDialogoNuevaTransaccion = false },
                alNavegar = alNavegar,
                categorias = listOf(
                    Categoria(
                        id = "cat_1",
                        nombre = "General",
                        emoji = "💰",
                        tipo = TipoCategoria.GASTO
                    ),
                    Categoria(
                        id = "cat_2",
                        nombre = "Comida",
                        emoji = "🍔",
                        tipo = TipoCategoria.GASTO
                    ),
                    Categoria(
                        id = "cat_3",
                        nombre = "Transporte",
                        emoji = "🚗",
                        tipo = TipoCategoria.GASTO
                    ),
                    Categoria(
                        id = "cat_4",
                        nombre = "Entretenimiento",
                        emoji = "🎮",
                        tipo = TipoCategoria.GASTO
                    ),
                    Categoria(
                        id = "cat_5",
                        nombre = "Compras",
                        emoji = "🛒",
                        tipo = TipoCategoria.GASTO
                    ),
                    Categoria(
                        id = "cat_6",
                        nombre = "Servicios",
                        emoji = "💡",
                        tipo = TipoCategoria.GASTO
                    ),
                    Categoria(
                        id = "cat_7",
                        nombre = "Salud",
                        emoji = "🏥",
                        tipo = TipoCategoria.GASTO
                    ),
                    Categoria(
                        id = "cat_8",
                        nombre = "Educación",
                        emoji = "📚",
                        tipo = TipoCategoria.GASTO
                    ),
                    Categoria(
                        id = "cat_9",
                        nombre = "Salario",
                        emoji = "💵",
                        tipo = TipoCategoria.INGRESO
                    ),
                    Categoria(
                        id = "cat_10",
                        nombre = "Freelance",
                        emoji = "💼",
                        tipo = TipoCategoria.INGRESO
                    ),
                    Categoria(
                        id = "cat_11",
                        nombre = "Inversiones",
                        emoji = "📈",
                        tipo = TipoCategoria.INGRESO
                    ),
                    Categoria(
                        id = "cat_12",
                        nombre = "Otros Ingresos",
                        emoji = "💸",
                        tipo = TipoCategoria.INGRESO
                    )
                )
            )
        }

        if (mostrarMenuAnalisis) {
            MenuAnalisis(
                alDismiss = { mostrarMenuAnalisis = false },
                alNavegar = alNavegar
            )
        }

        if (mostrarMenuMas) {
            MenuMas(
                alDismiss = { mostrarMenuMas = false },
                alNavegar = alNavegar
            )
        }
    }
}

@Composable
private fun MenuAnalisis(
    alDismiss: () -> Unit,
    alNavegar: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = alDismiss,
        icon = { Icon(Icons.Default.BarChart, contentDescription = null) },
        title = { Text("Análisis") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Card(
                    onClick = { alNavegar("comparativos"); alDismiss() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Compare, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text("Comparativos", style = MaterialTheme.typography.titleMedium)
                    }
                }
                Card(
                    onClick = { alNavegar(Routes.ESTADISTICAS); alDismiss() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Analytics, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text("Estadísticas", style = MaterialTheme.typography.titleMedium)
                    }
                }
                Card(
                    onClick = { alNavegar(Routes.PRESUPUESTOS); alDismiss() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.AccountBalance, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text("Presupuestos", style = MaterialTheme.typography.titleMedium)
                    }
                }
                Card(
                    onClick = { alNavegar("reportes"); alDismiss() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Description, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text("Reportes", style = MaterialTheme.typography.titleMedium)
                    }
                }
                Card(
                    onClick = { alNavegar("tendencias"); alDismiss() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.TrendingUp, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text("Tendencias", style = MaterialTheme.typography.titleMedium)
                    }
                }
                Card(
                    onClick = { alNavegar("metas"); alDismiss() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Flag, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text("Metas", style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = alDismiss) { Text("Cerrar") } }
    )
}

@Composable
private fun MenuMas(
    alDismiss: () -> Unit,
    alNavegar: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = alDismiss,
        icon = { Icon(Icons.Default.MoreHoriz, contentDescription = null) },
        title = { Text("Más opciones") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Card(
                    onClick = { alNavegar("configuracion"); alDismiss() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text("Configuración", style = MaterialTheme.typography.titleMedium)
                    }
                }
                Card(
                    onClick = { alNavegar("categorias"); alDismiss() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Category, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text("Categorías", style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = alDismiss) { Text("Cerrar") } }
    )
}

@Composable
private fun TarjetaBalance(balance: Double, totalIngresos: Double, totalGastos: Double) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Saldo disponible", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "S/. ${String.format("%.3f", balance)}",
                style = MaterialTheme.typography.displaySmall,
                color = if (balance >= 0) Color(0xFF10B981) else Color(0xFFEF4444)
            )
            Text("Actualización ahora", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(modifier = Modifier.fillMaxWidth(0.9f), color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                ItemResumen(Icons.Default.TrendingUp, "Ingresos", totalIngresos, Color(0xFF10B981))
                VerticalDivider(modifier = Modifier.height(60.dp), color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.3f))
                ItemResumen(Icons.Default.TrendingDown, "Gastos", totalGastos, Color(0xFFEF4444))
            }
        }
    }
}

@Composable
private fun ItemResumen(icono: androidx.compose.ui.graphics.vector.ImageVector, titulo: String, monto: Double, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Icon(imageVector = icono, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
        Text(text = titulo, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimaryContainer)
        Text(text = "$${String.format("%.2f", monto)}", style = MaterialTheme.typography.titleMedium, color = color)
    }
}

@Composable
private fun EstadoVacio() {
    Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Icon(Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(80.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
            Text("No hay transacciones", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("Presiona el botón + para agregar tu primera transacción", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
        }
    }
}

@Composable
private fun TarjetaTransaccion(transaccion: Transaccion, alEliminar: () -> Unit) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    var mostrarDialogoConfirmacion by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = MaterialTheme.shapes.medium, color = MaterialTheme.colorScheme.surface, modifier = Modifier.size(48.dp)) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Text(text = transaccion.categoriaEmoji, style = MaterialTheme.typography.headlineSmall)
                    }
                }
                Column {
                    Text(text = transaccion.descripcion, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                    Text(text = transaccion.categoriaNombre, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = dateFormat.format(Date(transaccion.fecha)), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
                }
            }
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = if (transaccion.tipo == TipoTransaccion.INGRESO) "+$${String.format("%.2f", transaccion.monto)}" else "-$${String.format("%.2f", transaccion.monto)}",
                    style = MaterialTheme.typography.titleLarge,
                    color = if (transaccion.tipo == TipoTransaccion.INGRESO) Color(0xFF10B981) else Color(0xFFEF4444)
                )
                IconButton(onClick = { mostrarDialogoConfirmacion = true }, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(20.dp))
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
                Button(onClick = { alEliminar(); mostrarDialogoConfirmacion = false }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
                    Text("Eliminar")
                }
            },
            dismissButton = { TextButton(onClick = { mostrarDialogoConfirmacion = false }) { Text("Cancelar") } }
        )
    }
}

@Composable
private fun DialogoNuevaTransaccion(
    alConfirmar: (TipoTransaccion, Double, String, String, String, String) -> Unit,
    alDismiss: () -> Unit,
    alNavegar: (String) -> Unit,
    categorias: List<Categoria> = listOf()
) {
    var tipo by remember { mutableStateOf(TipoTransaccion.GASTO) }
    var monto by remember { mutableStateOf("") }
    var categoriaSeleccionada by remember { mutableStateOf<Categoria?>(null) }
    var descripcion by remember { mutableStateOf("") }
    var errorMonto by remember { mutableStateOf(false) }
    var mostrarMenuCategorias by remember { mutableStateOf(false) }

    // ✅ NUEVO: Estado para mostrar información del presupuesto
    var mostrarInfoPresupuesto by remember { mutableStateOf(false) }
    var infoPresupuesto by remember { mutableStateOf("") }

    // Seleccionar primera categoría por defecto según el tipo
    LaunchedEffect(tipo) {
        val categoriasPorTipo = categorias.filter { it.tipo.toTipoTransaccion() == tipo }
        if (categoriaSeleccionada == null || categoriaSeleccionada?.tipo?.toTipoTransaccion() != tipo) {
            categoriaSeleccionada = categoriasPorTipo.firstOrNull()
        }
    }

    AlertDialog(
        onDismissRequest = alDismiss,
        icon = { Icon(Icons.Default.Add, contentDescription = null) },
        title = { Text("Nueva Transacción") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
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

                // Campo de monto
                OutlinedTextField(
                    value = monto,
                    onValueChange = {
                        monto = it
                        errorMonto = it.toDoubleOrNull() == null && it.isNotEmpty()
                    },
                    label = { Text("Monto") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Text("S/.") },
                    isError = errorMonto,
                    supportingText = if (errorMonto) {
                        { Text("Ingresa un monto válido") }
                    } else null,
                    singleLine = true
                )

                // Selector de categoría
                OutlinedCard(
                    onClick = { mostrarMenuCategorias = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = categoriaSeleccionada?.emoji ?: "💰",
                                style = MaterialTheme.typography.headlineSmall
                            )
                            Column {
                                Text(
                                    text = "Categoría",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = categoriaSeleccionada?.nombre ?: "Seleccionar",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                        Icon(
                            Icons.Default.KeyboardArrowDown,
                            contentDescription = null
                        )
                    }
                }

                // ✅ NUEVO: Mostrar información del presupuesto si es gasto
                if (tipo == TipoTransaccion.GASTO && categoriaSeleccionada != null && categoriaSeleccionada!!.presupuestado > 0) {
                    Button(
                        onClick = {
                            mostrarInfoPresupuesto = true
                            infoPresupuesto = "ℹ️ Esta categoría tiene un presupuesto de S/. ${String.format("%.2f", categoriaSeleccionada!!.presupuestado)}. " +
                                    "El gasto se descontará automáticamente."
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    ) {
                        Icon(Icons.Default.Info, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Ver información del presupuesto")
                    }
                }

                // Campo de descripción
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
                    categoriaSeleccionada?.let { cat ->
                        if (montoDouble > 0) {
                            alConfirmar(
                                tipo,
                                montoDouble,
                                cat.id,
                                cat.nombre,
                                cat.emoji,
                                descripcion
                            )
                        }
                    }
                },
                enabled = monto.toDoubleOrNull() != null &&
                        monto.toDoubleOrNull()!! > 0 &&
                        categoriaSeleccionada != null
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

    // ✅ NUEVO: Diálogo para información del presupuesto
    if (mostrarInfoPresupuesto) {
        AlertDialog(
            onDismissRequest = { mostrarInfoPresupuesto = false },
            icon = { Icon(Icons.Default.AccountBalance, contentDescription = null) },
            title = { Text("Presupuesto") },
            text = { Text(infoPresupuesto) },
            confirmButton = {
                TextButton(onClick = { mostrarInfoPresupuesto = false }) {
                    Text("Entendido")
                }
            }
        )
    }

    // Diálogo de selección de categorías
    if (mostrarMenuCategorias) {
        AlertDialog(
            onDismissRequest = { mostrarMenuCategorias = false },
            icon = { Icon(Icons.Default.Category, contentDescription = null) },
            title = { Text("Seleccionar Categoría") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    val categoriasFiltradas = categorias.filter { it.tipo.toTipoTransaccion() == tipo }

                    if (categoriasFiltradas.isEmpty()) {
                        Text(
                            text = "No hay categorías disponibles para ${if (tipo == TipoTransaccion.GASTO) "gastos" else "ingresos"}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(16.dp)
                        )
                    } else {
                        categoriasFiltradas.forEach { categoria ->
                            Card(
                                onClick = {
                                    categoriaSeleccionada = categoria
                                    mostrarMenuCategorias = false
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (categoriaSeleccionada?.id == categoria.id) {
                                        MaterialTheme.colorScheme.primaryContainer
                                    } else {
                                        MaterialTheme.colorScheme.surfaceVariant
                                    }
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = categoria.emoji,
                                        style = MaterialTheme.typography.headlineMedium
                                    )
                                    Text(
                                        text = categoria.nombre,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    if (categoria.presupuestado > 0 && categoria.tipo == TipoCategoria.GASTO) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Badge(
                                            containerColor = MaterialTheme.colorScheme.primary,
                                            contentColor = MaterialTheme.colorScheme.onPrimary
                                        ) {
                                            Text("S/. ${String.format("%.0f", categoria.presupuestado)}")
                                        }
                                    }
                                }
                            }
                        }
                    }

                    OutlinedCard(
                        onClick = {
                            mostrarMenuCategorias = false
                            alDismiss()
                            alNavegar("categorias")
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Crear nueva categoría",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { mostrarMenuCategorias = false }) {
                    Text("Cerrar")
                }
            }
        )
    }
}