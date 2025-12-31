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
import com.lvmh.pocketpet.viewmodels.TransaccionViewModel
import com.lvmh.pocketpet.presentacion.viewmodels.CategoriaViewModel
import com.lvmh.pocketpet.presentacion.navegacion.Routes
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPrincipal(
    viewModel: TransaccionViewModel,
    categoriaViewModel: CategoriaViewModel,
    alNavegar: (String) -> Unit
) {
    val transacciones by viewModel.transaccionesFiltradas.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val balance by viewModel.balance.collectAsState()
    val totalIngresos by viewModel.totalIngresos.collectAsState()
    val totalGastos by viewModel.totalGastos.collectAsState()
    val estadoCategorias by categoriaViewModel.estado.collectAsState()

    var mostrarDialogoNuevaTransaccion by remember { mutableStateOf(false) }
    var mostrarMenuMas by remember { mutableStateOf(false) }
    var mostrarMenuAnalisis by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.inicializar()
        categoriaViewModel.inicializar()
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
                        Icon(Icons.Default.Pets, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    }
                },
                actions = {
                    IconButton(onClick = { alNavegar(Routes.MI_PERFIL) }) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Mi Perfil", tint = MaterialTheme.colorScheme.onSurface)
                    }
                    IconButton(onClick = { alNavegar(Routes.CALENDARIO) }) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = "Calendario", tint = MaterialTheme.colorScheme.onSurface)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            )
        },
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surfaceVariant) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("Inicio") },
                    selected = true,
                    onClick = {}
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
                Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            error != null -> {
                Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.ErrorOutline, contentDescription = null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.error)
                        Text(text = error ?: "Error desconocido", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyLarge)
                        Button(onClick = { viewModel.limpiarError() }) { Text("Cerrar") }
                    }
                }
            }
            else -> {
                Column(modifier = Modifier.fillMaxSize().padding(padding)) {
                    TarjetaBalance(balance, totalIngresos, totalGastos)
                    if (transacciones.isNotEmpty()) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Historial de transacciones", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                            Text("${transacciones.size} total", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    if (transacciones.isEmpty()) {
                        EstadoVacio()
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(bottom = 16.dp)
                        ) {
                            items(transacciones) { t -> TarjetaTransaccion(t) { viewModel.eliminarTransaccion(t) } }
                        }
                    }
                }
            }
        }

        if (mostrarDialogoNuevaTransaccion) {
            DialogoNuevaTransaccion(
                alConfirmar = { tipo, monto, catId, catNombre, catEmoji, desc ->
                    viewModel.crearTransaccion(tipo, monto, catId, catNombre, catEmoji, desc)
                    mostrarDialogoNuevaTransaccion = false
                },
                alDismiss = { mostrarDialogoNuevaTransaccion = false },
                categoriaViewModel = categoriaViewModel,
                categorias = estadoCategorias.categorias
            )
        }
        if (mostrarMenuAnalisis) MenuAnalisis({ mostrarMenuAnalisis = false }, alNavegar)
        if (mostrarMenuMas) MenuMas({ mostrarMenuMas = false }, alNavegar)
    }
}

@Composable
private fun MenuAnalisis(alDismiss: () -> Unit, alNavegar: (String) -> Unit) {
    AlertDialog(
        onDismissRequest = alDismiss,
        icon = { Icon(Icons.Default.BarChart, contentDescription = null) },
        title = { Text("Análisis") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(
                    Triple("comparativos", Icons.Default.Compare, "Comparativos"),
                    Triple(Routes.ESTADISTICAS, Icons.Default.Analytics, "Estadísticas"),
                    Triple(Routes.PRESUPUESTOS, Icons.Default.AccountBalance, "Presupuestos"),
                    Triple("reportes", Icons.Default.Description, "Reportes"),
                    Triple("tendencias", Icons.Default.TrendingUp, "Tendencias"),
                    Triple("metas", Icons.Default.Flag, "Metas")
                ).forEach { (route, icon, label) ->
                    Card(
                        onClick = { alNavegar(route); alDismiss() },
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Text(label, style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = alDismiss) { Text("Cerrar") } }
    )
}

@Composable
private fun MenuMas(alDismiss: () -> Unit, alNavegar: (String) -> Unit) {
    AlertDialog(
        onDismissRequest = alDismiss,
        icon = { Icon(Icons.Default.MoreHoriz, contentDescription = null) },
        title = { Text("Más opciones") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // 🔥 QUITADO "Categorías" del menú
                Card(
                    onClick = { alNavegar("configuracion"); alDismiss() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Settings, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text("Configuración", style = MaterialTheme.typography.titleMedium)
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
        Column(modifier = Modifier.fillMaxWidth().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Saldo disponible", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
            Spacer(modifier = Modifier.height(4.dp))
            Text("S/. ${String.format("%.2f", balance)}", style = MaterialTheme.typography.displaySmall, color = if (balance >= 0) Color(0xFF10B981) else Color(0xFFEF4444))
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
        Icon(icono, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
        Text(titulo, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimaryContainer)
        Text("S/. ${String.format("%.2f", monto)}", style = MaterialTheme.typography.titleMedium, color = color)
    }
}

@Composable
private fun EstadoVacio() {
    Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Icon(Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(80.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
            Text("No hay transacciones", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("Presiona el botón + para agregar tu primera transacción", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f), textAlign = TextAlign.Center)
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
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = MaterialTheme.shapes.medium, color = MaterialTheme.colorScheme.surface, modifier = Modifier.size(48.dp)) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Text(transaccion.categoriaEmoji, style = MaterialTheme.typography.headlineSmall)
                    }
                }
                Column {
                    Text(transaccion.descripcion, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                    Text(transaccion.categoriaNombre, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(dateFormat.format(Date(transaccion.fecha)), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
                }
            }
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = if (transaccion.tipo == TipoTransaccion.INGRESO) "+S/. ${String.format("%.2f", transaccion.monto)}" else "-S/. ${String.format("%.2f", transaccion.monto)}",
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
    categoriaViewModel: CategoriaViewModel,
    categorias: List<Categoria> = listOf()
) {
    var tipo by remember { mutableStateOf(TipoTransaccion.GASTO) }
    var monto by remember { mutableStateOf("") }
    var categoriaSeleccionada by remember { mutableStateOf<Categoria?>(null) }
    var descripcion by remember { mutableStateOf("") }
    var errorMonto by remember { mutableStateOf(false) }
    var mostrarMenuCategorias by remember { mutableStateOf(false) }
    var mostrarDialogoNuevaCategoria by remember { mutableStateOf(false) }

    LaunchedEffect(tipo, categorias) {
        val cats = categorias.filter {
            (tipo == TipoTransaccion.GASTO && it.tipo == TipoCategoria.GASTO) ||
                    (tipo == TipoTransaccion.INGRESO && it.tipo == TipoCategoria.INGRESO)
        }
        if (categoriaSeleccionada == null ||
            (tipo == TipoTransaccion.GASTO && categoriaSeleccionada?.tipo != TipoCategoria.GASTO) ||
            (tipo == TipoTransaccion.INGRESO && categoriaSeleccionada?.tipo != TipoCategoria.INGRESO)) {
            categoriaSeleccionada = cats.firstOrNull()
        }
    }

    AlertDialog(
        onDismissRequest = alDismiss,
        icon = { Icon(Icons.Default.Add, contentDescription = null) },
        title = { Text("Nueva Transacción") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = tipo == TipoTransaccion.GASTO,
                        onClick = { tipo = TipoTransaccion.GASTO },
                        label = { Text("Gasto") },
                        modifier = Modifier.weight(1f),
                        leadingIcon = if (tipo == TipoTransaccion.GASTO) {{ Icon(Icons.Default.Check, contentDescription = null) }} else null
                    )
                    FilterChip(
                        selected = tipo == TipoTransaccion.INGRESO,
                        onClick = { tipo = TipoTransaccion.INGRESO },
                        label = { Text("Ingreso") },
                        modifier = Modifier.weight(1f),
                        leadingIcon = if (tipo == TipoTransaccion.INGRESO) {{ Icon(Icons.Default.Check, contentDescription = null) }} else null
                    )
                }
                OutlinedTextField(
                    value = monto,
                    onValueChange = { monto = it; errorMonto = it.toDoubleOrNull() == null && it.isNotEmpty() },
                    label = { Text("Monto") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Text("S/.") },
                    isError = errorMonto,
                    supportingText = if (errorMonto) {{ Text("Ingresa un monto válido") }} else null,
                    singleLine = true
                )
                OutlinedCard(onClick = { mostrarMenuCategorias = true }, modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(categoriaSeleccionada?.emoji ?: "💰", style = MaterialTheme.typography.headlineSmall)
                            Column {
                                Text("Categoría", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(categoriaSeleccionada?.nombre ?: "Seleccionar", style = MaterialTheme.typography.bodyLarge)
                            }
                        }
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = null)
                    }
                }
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
                    val m = monto.toDoubleOrNull() ?: 0.0
                    categoriaSeleccionada?.let { cat ->
                        if (m > 0) alConfirmar(tipo, m, cat.id, cat.nombre, cat.emoji, descripcion)
                    }
                },
                enabled = monto.toDoubleOrNull() != null && monto.toDoubleOrNull()!! > 0 && categoriaSeleccionada != null
            ) { Text("Agregar") }
        },
        dismissButton = { TextButton(onClick = alDismiss) { Text("Cancelar") } }
    )

    if (mostrarMenuCategorias) {
        val catsTipo = if (tipo == TipoTransaccion.GASTO) {
            categorias.filter { it.tipo == TipoCategoria.GASTO }
        } else {
            categorias.filter { it.tipo == TipoCategoria.INGRESO }
        }

        AlertDialog(
            onDismissRequest = { mostrarMenuCategorias = false },
            icon = { Icon(Icons.Default.Category, contentDescription = null) },
            title = { Text("Seleccionar Categoría") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (catsTipo.isEmpty()) {
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
                            Text(
                                "No hay categorías de ${if (tipo == TipoTransaccion.GASTO) "gasto" else "ingreso"}",
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Crea una nueva categoría para continuar",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = {
                                mostrarMenuCategorias = false
                                mostrarDialogoNuevaCategoria = true
                            }) {
                                Icon(Icons.Default.Add, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Crear categoría")
                            }
                        }
                    } else {
                        catsTipo.forEach { cat ->
                            Card(
                                onClick = { categoriaSeleccionada = cat; mostrarMenuCategorias = false },
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (categoriaSeleccionada?.id == cat.id) {
                                        MaterialTheme.colorScheme.primaryContainer
                                    } else {
                                        MaterialTheme.colorScheme.surfaceVariant
                                    }
                                )
                            ) {
                                Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Text(cat.emoji, style = MaterialTheme.typography.headlineMedium)
                                    Text(cat.nombre, style = MaterialTheme.typography.titleMedium)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedButton(
                            onClick = {
                                mostrarMenuCategorias = false
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
            confirmButton = { TextButton(onClick = { mostrarMenuCategorias = false }) { Text("Cerrar") } }
        )
    }

    // 🔥 DIÁLOGO PARA CREAR NUEVA CATEGORÍA DESDE TRANSACCIONES
    if (mostrarDialogoNuevaCategoria) {
        DialogoCrearCategoriaTransaccion(
            tipo = if (tipo == TipoTransaccion.GASTO) TipoCategoria.GASTO else TipoCategoria.INGRESO,
            categoriaViewModel = categoriaViewModel,
            alDismiss = { mostrarDialogoNuevaCategoria = false },
            alCreada = { nuevaCategoria ->
                categoriaSeleccionada = nuevaCategoria
                mostrarDialogoNuevaCategoria = false
            }
        )
    }
}

// 🔥 COMPONENTE REUTILIZABLE PARA CREAR CATEGORÍAS DESDE TRANSACCIONES
@Composable
private fun DialogoCrearCategoriaTransaccion(
    tipo: TipoCategoria,
    categoriaViewModel: CategoriaViewModel,
    alDismiss: () -> Unit,
    alCreada: (Categoria) -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var emoji by remember { mutableStateOf("") }
    var cargando by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = alDismiss,
        icon = { Icon(Icons.Default.Add, contentDescription = null) },
        title = { Text("Nueva Categoría de ${if (tipo == TipoCategoria.GASTO) "Gasto" else "Ingreso"}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                if (error != null) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            error!!,
                            modifier = Modifier.padding(12.dp),
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                OutlinedTextField(
                    value = emoji,
                    onValueChange = { if (it.length <= 2) emoji = it },
                    label = { Text("Emoji") },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Ej: 🍔") },
                    singleLine = true
                )

                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Ej: Comida") },
                    singleLine = true
                )

                if (cargando) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    cargando = true
                    error = null
                    categoriaViewModel.crearCategoria(
                        nombre = nombre.trim(),
                        emoji = emoji.trim(),
                        tipo = tipo,
                        onSuccess = { id ->
                            cargando = false
                            val nuevaCategoria = Categoria(
                                id = id,
                                nombre = nombre.trim(),
                                emoji = emoji.trim(),
                                tipo = tipo,
                                presupuestado = 0.0
                            )
                            alCreada(nuevaCategoria)
                        },
                        onError = { errorMsg ->
                            cargando = false
                            error = errorMsg
                        }
                    )
                },
                enabled = nombre.isNotBlank() && emoji.isNotBlank() && !cargando
            ) {
                Text("Crear")
            }
        },
        dismissButton = {
            TextButton(onClick = alDismiss, enabled = !cargando) {
                Text("Cancelar")
            }
        }
    )
}