package com.lvmh.pocketpet.presentacion.pantallas.estadisticas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lvmh.pocketpet.dominio.modelos.Meta
import com.lvmh.pocketpet.presentacion.componentes.BarraProgresoPersonalizada
import com.lvmh.pocketpet.presentacion.viewmodels.MetaViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaMetas(
    usuarioId: String,
    viewModel: MetaViewModel = hiltViewModel(),
    alRegresar: () -> Unit
) {
    // Configurar usuario cuando la pantalla se carga
    LaunchedEffect(usuarioId) {
        viewModel.configurarUsuario(usuarioId)
    }

    val estado by viewModel.estado.collectAsStateWithLifecycle()

    // Estados locales para los diálogos
    var mostrarDialogoNuevaMeta by remember { mutableStateOf(false) }
    var mostrarDialogoAgregarMonto by remember { mutableStateOf(false) }
    var metaSeleccionada by remember { mutableStateOf<Meta?>(null) }
    var mostrarDialogoEditarMeta by remember { mutableStateOf(false) }

    // Mostrar errores si existen
    estado.error?.let { error ->
        LaunchedEffect(error) {
            // Podrías mostrar un Snackbar aquí
            println("Error en metas: $error")
            // Limpiar el error después de mostrarlo
            viewModel.limpiarError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Metas Financieras") },
                navigationIcon = {
                    IconButton(onClick = alRegresar) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                },
                actions = {
                    // Botón para refrescar
                    IconButton(onClick = { viewModel.refrescarMetas() }) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "Refrescar",
                            tint = if (estado.isLoading) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { mostrarDialogoNuevaMeta = true }
            ) {
                if (estado.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Icon(Icons.Default.Add, contentDescription = "Agregar meta")
                }
            }
        }
    ) { paddingValues ->
        if (estado.isLoading && estado.metas.isEmpty()) {
            // Mostrar loading solo en primera carga
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Cargando metas...")
                }
            }
        } else if (estado.metas.isEmpty()) {
            // Estado vacío
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
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
                    Text(
                        "No hay metas",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Define tus metas financieras",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            // Lista de metas
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val metasActivas = estado.metasActivas
                val metasCompletadas = estado.metasCompletadas

                if (metasActivas.isNotEmpty()) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Metas Activas",
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                text = "${metasActivas.size} meta(s)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    items(metasActivas) { meta ->
                        TarjetaMeta(
                            meta = meta,
                            alClick = {
                                metaSeleccionada = meta
                                mostrarDialogoAgregarMonto = true
                            },
                            alEditar = {
                                metaSeleccionada = meta
                                mostrarDialogoEditarMeta = true
                            },
                            alEliminar = { viewModel.eliminarMeta(meta) },
                            alCompletar = { viewModel.marcarComoCompletada(meta.id) }
                        )
                    }
                }

                if (metasCompletadas.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Metas Completadas",
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                text = "${metasCompletadas.size} meta(s)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    items(metasCompletadas) { meta ->
                        TarjetaMeta(
                            meta = meta,
                            alClick = {
                                metaSeleccionada = meta
                                // Podrías mostrar detalles o reiniciar
                            },
                            alEditar = {
                                metaSeleccionada = meta
                                mostrarDialogoEditarMeta = true
                            },
                            alEliminar = { viewModel.eliminarMeta(meta) },
                            alReiniciar = { viewModel.reiniciarMeta(meta.id) }
                        )
                    }
                }
            }
        }

        // Diálogo para nueva meta
        if (mostrarDialogoNuevaMeta) {
            DialogoNuevaMeta(
                alConfirmar = { nombre, descripcion, monto, fechaLimite, categoria, icono ->
                    viewModel.crearMeta(nombre, descripcion, monto, fechaLimite, categoria, icono)
                    mostrarDialogoNuevaMeta = false
                },
                alDismiss = { mostrarDialogoNuevaMeta = false }
            )
        }

        // Diálogo para agregar monto
        if (mostrarDialogoAgregarMonto && metaSeleccionada != null) {
            DialogoAgregarMonto(
                meta = metaSeleccionada!!,
                alConfirmar = { monto ->
                    viewModel.agregarMontoMeta(metaSeleccionada!!.id, monto)
                    mostrarDialogoAgregarMonto = false
                    metaSeleccionada = null
                },
                alDismiss = {
                    mostrarDialogoAgregarMonto = false
                    metaSeleccionada = null
                }
            )
        }

        // Diálogo para editar meta
        if (mostrarDialogoEditarMeta && metaSeleccionada != null) {
            DialogoEditarMeta(
                meta = metaSeleccionada!!,
                alConfirmar = { metaActualizada ->
                    viewModel.actualizarMeta(metaActualizada)
                    mostrarDialogoEditarMeta = false
                    metaSeleccionada = null
                },
                alDismiss = {
                    mostrarDialogoEditarMeta = false
                    metaSeleccionada = null
                }
            )
        }
    }
}

@Composable
private fun TarjetaMeta(
    meta: Meta,
    alClick: () -> Unit,
    alEditar: () -> Unit,
    alEliminar: () -> Unit,
    alCompletar: (() -> Unit)? = null,
    alReiniciar: (() -> Unit)? = null
) {
    val formatoFecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val porcentaje = meta.porcentajeCompletado.coerceIn(0.0, 100.0)

    Card(
        onClick = alClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (meta.completada)
                Color(0xFFD1FAE5).copy(alpha = 0.8f)
            else
                MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (meta.completada) 0.dp else 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Encabezado con nombre y acciones
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (meta.completada) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = "Completada",
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text(
                            text = meta.nombre,
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    if (meta.descripcion.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = meta.descripcion,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2
                        )
                    }
                }

                // Menú de acciones
                Box {
                    var mostrarMenu by remember { mutableStateOf(false) }

                    IconButton(onClick = { mostrarMenu = true }) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = "Más opciones"
                        )
                    }

                    DropdownMenu(
                        expanded = mostrarMenu,
                        onDismissRequest = { mostrarMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Editar") },
                            onClick = {
                                mostrarMenu = false
                                alEditar()
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Edit, contentDescription = null)
                            }
                        )

                        if (alCompletar != null && !meta.completada) {
                            DropdownMenuItem(
                                text = { Text("Marcar como completada") },
                                onClick = {
                                    mostrarMenu = false
                                    alCompletar()
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Check, contentDescription = null)
                                }
                            )
                        }

                        if (alReiniciar != null && meta.completada) {
                            DropdownMenuItem(
                                text = { Text("Reiniciar meta") },
                                onClick = {
                                    mostrarMenu = false
                                    alReiniciar()
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Replay, contentDescription = null)
                                }
                            )
                        }

                        Divider()

                        DropdownMenuItem(
                            text = { Text("Eliminar", color = MaterialTheme.colorScheme.error) },
                            onClick = {
                                mostrarMenu = false
                                alEliminar()
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Información financiera
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "$${String.format("%.2f", meta.montoActual)}",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Ahorrado",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "$${String.format("%.2f", meta.montoObjetivo)}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Meta",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Barra de progreso
            BarraProgresoPersonalizada(
                progreso = (porcentaje / 100).toFloat(),
                colorProgreso = when {
                    meta.completada -> Color(0xFF10B981)
                    porcentaje >= 100 -> Color(0xFF10B981)
                    porcentaje >= 75 -> Color(0xFFF59E0B)
                    porcentaje >= 50 -> Color(0xFF3B82F6)
                    else -> Color(0xFF6366F1)
                },
                mostrarPorcentaje = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Información adicional
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = when {
                        meta.completada -> "✓ Completada"
                        meta.faltante <= 0 -> "¡Meta alcanzada!"
                        else -> "Faltan $${String.format("%.2f", meta.faltante)}"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = if (meta.completada || meta.faltante <= 0)
                        Color(0xFF10B981)
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.CalendarToday,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = formatoFecha.format(Date(meta.fechaLimite)),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Días restantes (solo para metas activas)
            if (!meta.completada && meta.diasRestantes >= 0) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${meta.diasRestantes} días restantes",
                    style = MaterialTheme.typography.bodySmall,
                    color = when {
                        meta.diasRestantes <= 7 -> Color(0xFFEF4444)
                        meta.diasRestantes <= 30 -> Color(0xFFF59E0B)
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
        }
    }
}

@Composable
private fun DialogoNuevaMeta(
    alConfirmar: (String, String, Double, Long, String, String) -> Unit,
    alDismiss: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var monto by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var iconoUrl by remember { mutableStateOf("") }

    val calendar = Calendar.getInstance()
    calendar.add(Calendar.MONTH, 1)

    AlertDialog(
        onDismissRequest = alDismiss,
        title = { Text("Nueva Meta Financiera") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre de la meta *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = nombre.isEmpty()
                )

                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción (opcional)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )

                OutlinedTextField(
                    value = monto,
                    onValueChange = { monto = it },
                    label = { Text("Monto objetivo *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    prefix = { Text("$") },
                    isError = monto.toDoubleOrNull()?.let { it <= 0 } ?: true
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = categoria,
                        onValueChange = { categoria = it },
                        label = { Text("Categoría") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = iconoUrl,
                        onValueChange = { iconoUrl = it },
                        label = { Text("Ícono") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                // Información adicional
                Text(
                    text = "Fecha límite: ${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(calendar.timeInMillis))}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val montoDouble = monto.toDoubleOrNull() ?: 0.0
                    if (nombre.isNotEmpty() && montoDouble > 0) {
                        alConfirmar(
                            nombre,
                            descripcion,
                            montoDouble,
                            calendar.timeInMillis,
                            categoria,
                            iconoUrl
                        )
                    }
                },
                enabled = nombre.isNotEmpty() && monto.toDoubleOrNull()?.let { it > 0 } ?: false
            ) {
                Text("Crear Meta")
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
    val montoMaximo = meta.faltante

    AlertDialog(
        onDismissRequest = alDismiss,
        title = { Text("Agregar Ahorro") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Información de la meta
                Column {
                    Text(
                        text = meta.nombre,
                        style = MaterialTheme.typography.titleMedium
                    )
                    if (meta.descripcion.isNotEmpty()) {
                        Text(
                            text = meta.descripcion,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Progreso actual
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Progreso:",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "${String.format("%.1f", meta.porcentajeCompletado)}%",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    BarraProgresoPersonalizada(
                        progreso = (meta.porcentajeCompletado / 100).toFloat().coerceIn(0f, 1f),
                        colorProgreso = MaterialTheme.colorScheme.primary,
                        mostrarPorcentaje = false
                    )
                }

                // Montos
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$${String.format("%.2f", meta.montoActual)}",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Ahorrado",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$${String.format("%.2f", meta.montoObjetivo)}",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Objetivo",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$${String.format("%.2f", montoMaximo)}",
                            style = MaterialTheme.typography.titleMedium,
                            color = if (montoMaximo <= 0) Color(0xFF10B981) else Color(0xFFEF4444)
                        )
                        Text(
                            text = "Faltante",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                // Campo para ingresar monto
                OutlinedTextField(
                    value = monto,
                    onValueChange = {
                        val nuevoValor = it.filter { char -> char.isDigit() || char == '.' }
                        if (nuevoValor.count { c -> c == '.' } <= 1) {
                            monto = nuevoValor
                        }
                    },
                    label = { Text("Monto a aportar") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    prefix = { Text("$") },
                    supportingText = {
                        if (montoMaximo > 0) {
                            Text("Máximo: $${String.format("%.2f", montoMaximo)}")
                        }
                    },
                    isError = monto.toDoubleOrNull()?.let {
                        it <= 0 || it > montoMaximo
                    } ?: false
                )

                // Botones de cantidad rápida
                if (montoMaximo > 0) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(10.0, 50.0, 100.0, 500.0).forEach { cantidad ->
                            val cantidadValida = cantidad <= montoMaximo
                            FilterChip(
                                selected = false,
                                onClick = {
                                    if (cantidadValida) {
                                        monto = cantidad.toString()
                                    }
                                },
                                label = { Text("$$cantidad") },
                                enabled = cantidadValida,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val montoDouble = monto.toDoubleOrNull() ?: 0.0
                    if (montoDouble > 0 && montoDouble <= montoMaximo) {
                        alConfirmar(montoDouble)
                    }
                },
                enabled = monto.toDoubleOrNull()?.let {
                    it > 0 && it <= montoMaximo
                } ?: false
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

@Composable
private fun DialogoEditarMeta(
    meta: Meta,
    alConfirmar: (Meta) -> Unit,
    alDismiss: () -> Unit
) {
    var nombre by remember { mutableStateOf(meta.nombre) }
    var descripcion by remember { mutableStateOf(meta.descripcion) }
    var montoObjetivo by remember { mutableStateOf(meta.montoObjetivo.toString()) }
    var categoria by remember { mutableStateOf(meta.categoriaId) }
    var iconoUrl by remember { mutableStateOf(meta.iconoUrl) }

    val formatoFecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    var mostrarSelectorFecha by remember { mutableStateOf(false) }
    var fechaLimite by remember { mutableStateOf(meta.fechaLimite) }

    AlertDialog(
        onDismissRequest = alDismiss,
        title = { Text("Editar Meta") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = nombre.isEmpty()
                )

                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )

                OutlinedTextField(
                    value = montoObjetivo,
                    onValueChange = { montoObjetivo = it },
                    label = { Text("Monto objetivo *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    prefix = { Text("$") },
                    isError = montoObjetivo.toDoubleOrNull()?.let { it <= 0 } ?: true
                )

                OutlinedButton(
                    onClick = { mostrarSelectorFecha = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.CalendarToday, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Fecha límite: ${formatoFecha.format(Date(fechaLimite))}")
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = categoria,
                        onValueChange = { categoria = it },
                        label = { Text("Categoría") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = iconoUrl,
                        onValueChange = { iconoUrl = it },
                        label = { Text("URL del ícono") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                // Información actual
                Text(
                    text = "Progreso actual: $${String.format("%.2f", meta.montoActual)} de $${String.format("%.2f", meta.montoObjetivo)} (${String.format("%.1f", meta.porcentajeCompletado)}%)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val montoDouble = montoObjetivo.toDoubleOrNull() ?: meta.montoObjetivo
                    if (nombre.isNotEmpty() && montoDouble > 0) {
                        val metaActualizada = meta.copy(
                            nombre = nombre,
                            descripcion = descripcion,
                            montoObjetivo = montoDouble,
                            fechaLimite = fechaLimite,
                            categoriaId = categoria,
                            iconoUrl = iconoUrl
                        )
                        alConfirmar(metaActualizada)
                    }
                },
                enabled = nombre.isNotEmpty() && montoObjetivo.toDoubleOrNull()?.let { it > 0 } ?: false
            ) {
                Text("Guardar Cambios")
            }
        },
        dismissButton = {
            TextButton(onClick = alDismiss) {
                Text("Cancelar")
            }
        }
    )

    // Selector de fecha (simplificado)
    if (mostrarSelectorFecha) {
        AlertDialog(
            onDismissRequest = { mostrarSelectorFecha = false },
            title = { Text("Seleccionar fecha límite") },
            text = {
                // Aquí podrías agregar un DatePicker real
                Text("Selecciona una fecha futura para la meta")
            },
            confirmButton = {
                Button(onClick = {
                    // Por ahora, solo agregamos un mes
                    val calendar = Calendar.getInstance()
                    calendar.timeInMillis = fechaLimite
                    calendar.add(Calendar.MONTH, 1)
                    fechaLimite = calendar.timeInMillis
                    mostrarSelectorFecha = false
                }) {
                    Text("Agregar un mes")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarSelectorFecha = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}