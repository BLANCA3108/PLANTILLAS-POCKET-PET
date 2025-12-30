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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.lvmh.pocketpet.presentacion.viewmodels.PresupuestoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPresupuestos(
    viewModel: PresupuestoViewModel,
    onBackClick: () -> Unit,
    alNavegar: (String) -> Unit
) {
    val estado by viewModel.estado.collectAsState()
    var mostrarDialogoNuevo by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        println("🔵 Componiendo PantallaPresupuestos")
        viewModel.inicializarUsuario()

        onDispose {
            println("🔵 Saliendo de PantallaPresupuestos")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Presupuestos") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Regresar",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { mostrarDialogoNuevo = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Nuevo presupuesto")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { mostrarDialogoNuevo = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nuevo presupuesto")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            estado.error?.let { error ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Error, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                        Text(error, color = MaterialTheme.colorScheme.onErrorContainer)
                    }
                }
            }

            estado.mensajeExito?.let { mensaje ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary)
                        Text(mensaje, color = MaterialTheme.colorScheme.onTertiaryContainer)
                    }
                }
            }

            when {
                estado.cargando && estado.presupuestos.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            CircularProgressIndicator()
                            Text("Cargando presupuestos...", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
                estado.presupuestos.isEmpty() -> {
                    EstadoVacioPresupuestos()
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(estado.presupuestos, key = { it.presupuesto.id }) { item ->
                            TarjetaPresupuesto(
                                presupuesto = item.presupuesto,
                                categoriaNombre = item.categoriaNombre,
                                categoriaEmoji = item.categoriaEmoji,
                                alEliminar = { viewModel.eliminarPresupuesto(item.presupuesto.id) }
                            )
                        }
                    }
                }
            }
        }
    }

    if (mostrarDialogoNuevo) {
        DialogoNuevoPresupuesto(
            viewModel = viewModel,
            alDismiss = {
                mostrarDialogoNuevo = false
                viewModel.limpiarMensajes()
            }
        )
    }
}

@Composable
fun TarjetaPresupuesto(
    presupuesto: com.lvmh.pocketpet.dominio.modelos.Presupuesto,
    categoriaNombre: String,
    categoriaEmoji: String,
    alEliminar: () -> Unit
) {
    val progreso = remember(presupuesto.porcentajeGastado) {
        (presupuesto.porcentajeGastado.toFloat() / 100f).coerceIn(0f, 1f)
    }

    val colorProgreso = when {
        presupuesto.porcentajeGastado >= 100 -> Color.Red
        presupuesto.porcentajeGastado >= presupuesto.alertaEn -> Color(0xFFFFA000)
        else -> MaterialTheme.colorScheme.primary
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = categoriaEmoji,
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Column {
                        Text(
                            text = categoriaNombre,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${presupuesto.periodo.uppercase()} • Alerta al ${presupuesto.alertaEn}%",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                IconButton(
                    onClick = alEliminar,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LinearProgressIndicator(
                progress = { progreso },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp),
                color = colorProgreso,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        "Gastado",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "S/. ${String.format("%.2f", presupuesto.gastado)}",
                        style = MaterialTheme.typography.titleMedium,
                        color = if (presupuesto.excedido) Color.Red else MaterialTheme.colorScheme.onSurface
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Progreso",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "${String.format("%.0f", presupuesto.porcentajeGastado)}%",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = colorProgreso
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        "Total",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "S/. ${String.format("%.2f", presupuesto.monto)}",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Disponible: S/. ${String.format("%.2f", presupuesto.disponible)}",
                style = MaterialTheme.typography.bodySmall,
                color = when {
                    presupuesto.excedido -> Color.Red
                    presupuesto.disponible < (presupuesto.monto * 0.2) -> Color(0xFFFFA000)
                    else -> MaterialTheme.colorScheme.primary
                }
            )
        }
    }
}

@Composable
fun DialogoNuevoPresupuesto(
    viewModel: PresupuestoViewModel,
    alDismiss: () -> Unit
) {
    val estado by viewModel.estado.collectAsState()
    var categoriaSeleccionada by remember { mutableStateOf<String?>(null) }
    var monto by remember { mutableStateOf("") }
    var periodoSeleccionado by remember { mutableStateOf("mensual") }
    var alertaEn by remember { mutableStateOf(80) }
    var mostrarSelectorCategoria by remember { mutableStateOf(false) }
    var mostrarDialogoNuevaCategoria by remember { mutableStateOf(false) }

    val categoriasDisponibles = viewModel.obtenerCategoriasParaPresupuesto()
    val categoria = categoriaSeleccionada?.let { id ->
        categoriasDisponibles.find { it.id == id }
    }

    LaunchedEffect(categoriasDisponibles.size) {
        println("🔵 Categorías disponibles: ${categoriasDisponibles.size}")
        categoriasDisponibles.forEach {
            println("   - ${it.emoji} ${it.nombre}")
        }
    }

    AlertDialog(
        onDismissRequest = alDismiss,
        icon = { Icon(Icons.Default.AccountBalance, contentDescription = null) },
        title = { Text("Nuevo Presupuesto") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedCard(
                    onClick = { mostrarSelectorCategoria = true },
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
                                text = categoria?.emoji ?: "📊",
                                style = MaterialTheme.typography.headlineSmall
                            )
                            Column {
                                Text(
                                    "Categoría",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = categoria?.nombre ?: "Seleccionar categoría",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = null)
                    }
                }

                OutlinedTextField(
                    value = monto,
                    onValueChange = { monto = it },
                    label = { Text("Monto del presupuesto") },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Ej: 500.00") },
                    leadingIcon = { Text("S/.", style = MaterialTheme.typography.bodyMedium) },
                    singleLine = true
                )

                Text("Período", style = MaterialTheme.typography.labelMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("semanal", "mensual", "trimestral", "anual").forEach { periodo ->
                        FilterChip(
                            selected = periodoSeleccionado == periodo,
                            onClick = { periodoSeleccionado = periodo },
                            label = { Text(periodo.replaceFirstChar { it.uppercase() }) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Column {
                    Text("Recibir alerta al $alertaEn%", style = MaterialTheme.typography.labelMedium)
                    Slider(
                        value = alertaEn.toFloat(),
                        onValueChange = { alertaEn = it.toInt() },
                        valueRange = 50f..100f,
                        steps = 9
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("50%", style = MaterialTheme.typography.bodySmall)
                        Text("100%", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val montoDouble = monto.toDoubleOrNull()
                    if (montoDouble != null && montoDouble > 0 && categoria != null) {
                        viewModel.crearPresupuesto(
                            categoriaId = categoria.id,
                            monto = montoDouble,
                            periodo = periodoSeleccionado,
                            alertaEn = alertaEn
                        )
                        alDismiss()
                    }
                },
                enabled = monto.toDoubleOrNull() != null &&
                        monto.toDoubleOrNull()!! > 0 &&
                        categoria != null
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

    if (mostrarSelectorCategoria) {
        AlertDialog(
            onDismissRequest = { mostrarSelectorCategoria = false },
            title = { Text("Seleccionar Categoría") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (categoriasDisponibles.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.Category,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("No hay categorías de gastos", style = MaterialTheme.typography.bodyMedium)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Crea una categoría de gasto primero",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = {
                                mostrarSelectorCategoria = false
                                mostrarDialogoNuevaCategoria = true
                            }) {
                                Icon(Icons.Default.Add, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Crear categoría")
                            }
                        }
                    } else {
                        categoriasDisponibles.forEach { cat ->
                            Card(
                                onClick = {
                                    categoriaSeleccionada = cat.id
                                    mostrarSelectorCategoria = false
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (categoriaSeleccionada == cat.id) {
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
                                    Text(cat.emoji, style = MaterialTheme.typography.headlineMedium)
                                    Text(cat.nombre, style = MaterialTheme.typography.titleMedium)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedButton(
                            onClick = {
                                mostrarSelectorCategoria = false
                                mostrarDialogoNuevaCategoria = true
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Nueva categoría")
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { mostrarSelectorCategoria = false }) {
                    Text("Cerrar")
                }
            }
        )
    }

    if (mostrarDialogoNuevaCategoria) {
        DialogoNuevaCategoria(
            onDismiss = { mostrarDialogoNuevaCategoria = false },
            onCreate = { nombre, emoji ->
                viewModel.crearCategoria(
                    nombre = nombre,
                    emoji = emoji,
                    tipo = "GASTO"
                ) { nuevaCategoriaId ->
                    categoriaSeleccionada = nuevaCategoriaId
                    mostrarDialogoNuevaCategoria = false
                }
            }
        )
    }
}

@Composable
fun DialogoNuevaCategoria(
    onDismiss: () -> Unit,
    onCreate: (nombre: String, emoji: String) -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var emojiSeleccionado by remember { mutableStateOf("📊") }

    val emojisDisponibles = listOf(
        "🛒", "🏠", "🚗", "🍔", "🎮", "💰", "📈", "🎁",
        "✈️", "🏥", "📚", "👕", "⚡", "💊", "🎵", "📱",
        "🎬", "🏋️", "🎓", "🔧", "☕", "🌮", "🎨", "💼"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Default.Category, contentDescription = null) },
        title = { Text("Nueva Categoría de Gasto") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre de la categoría") },
                    placeholder = { Text("Ej: Supermercado, Transporte, etc.") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Text("Selecciona un emoji:", style = MaterialTheme.typography.labelMedium)

                LazyColumn(
                    modifier = Modifier.height(200.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items((emojisDisponibles.size + 3) / 4) { rowIndex ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            for (colIndex in 0 until 4) {
                                val index = rowIndex * 4 + colIndex
                                if (index < emojisDisponibles.size) {
                                    val emoji = emojisDisponibles[index]
                                    FilterChip(
                                        selected = emojiSeleccionado == emoji,
                                        onClick = { emojiSeleccionado = emoji },
                                        label = { Text(emoji, style = MaterialTheme.typography.headlineMedium) },
                                        modifier = Modifier.weight(1f)
                                    )
                                } else {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (nombre.isNotBlank()) {
                        onCreate(nombre, emojiSeleccionado)
                    }
                },
                enabled = nombre.isNotBlank()
            ) {
                Text("Crear")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun EstadoVacioPresupuestos() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.AccountBalance,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "No hay presupuestos",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Crea tu primer presupuesto para controlar tus gastos",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}