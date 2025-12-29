package com.lvmh.pocketpet.pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.lvmh.pocketpet.dominio.modelos.Presupuesto
import com.lvmh.pocketpet.presentacion.viewmodels.PresupuestoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Presupuestos(
    onBackClick: () -> Unit = {},
    viewModel: PresupuestoViewModel = hiltViewModel()
) {
    val estado by viewModel.estado.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var showEliminarDialog by remember { mutableStateOf(false) }
    var presupuestoAEliminar by remember { mutableStateOf<Presupuesto?>(null) }

    // ✅ CORREGIDO: Ya no necesita usuarioId como parámetro
    LaunchedEffect(Unit) {
        viewModel.inicializarUsuario()
    }

    val totalPresupuesto = estado.presupuestos.sumOf { it.monto }
    val totalGastado = estado.presupuestos.sumOf { it.gastado }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { onBackClick() },
                            modifier = Modifier
                                .size(48.dp)
                                .background(Color.White, CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Atrás",
                                tint = Color.Black
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        Text("Presupuestos", color = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF5B6BC4)
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                containerColor = Color(0xFF5B6BC4)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nuevo presupuesto")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Cards de resumen
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    BudgetCard(
                        title = "GASTADO",
                        amount = "-S/${String.format("%.2f", totalGastado)}",
                        subtitle = "Total gastado",
                        backgroundColor = Color(0xFFE57373),
                        modifier = Modifier.weight(1f)
                    )
                    BudgetCard(
                        title = "PRESUPUESTO",
                        amount = "S/${String.format("%.2f", totalPresupuesto)}",
                        subtitle = "Total disponible",
                        backgroundColor = Color(0xFF4CAF87),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Barra de progreso general
                Text(
                    text = "Presupuesto General",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "S/${String.format("%.2f", totalGastado)} de S/${String.format("%.2f", totalPresupuesto)}",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = if (totalPresupuesto > 0)
                        (totalGastado / totalPresupuesto).toFloat().coerceIn(0f, 1f)
                    else 0f,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp),
                    color = if (totalPresupuesto > 0 && totalGastado / totalPresupuesto > 0.8)
                        Color(0xFFE57373)
                    else Color(0xFF4CAF87),
                    trackColor = Color.LightGray
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Lista de presupuestos
                if (estado.presupuestos.isEmpty()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFF5F5F5)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No hay presupuestos",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Toca el botón + para crear uno",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                    }
                } else {
                    Text(
                        text = "Mis Presupuestos",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    estado.presupuestos.forEach { presupuesto ->
                        PresupuestoItem(
                            presupuesto = presupuesto,
                            onDelete = {
                                presupuestoAEliminar = presupuesto
                                showEliminarDialog = true
                            }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }

            // Indicador de carga
            if (estado.cargando) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }

    // Diálogo para crear presupuesto
    if (showDialog) {
        NuevoPresupuestoDialog(
            onDismiss = { showDialog = false },
            onConfirm = { categoriaId, monto, periodo, alertaEn ->
                viewModel.crearPresupuesto(categoriaId, monto, periodo, alertaEn)
                showDialog = false
            }
        )
    }

    // Diálogo de confirmación para eliminar
    if (showEliminarDialog && presupuestoAEliminar != null) {
        AlertDialog(
            onDismissRequest = { showEliminarDialog = false },
            title = { Text("Eliminar Presupuesto") },
            text = { Text("¿Estás seguro de que deseas eliminar este presupuesto?") },
            confirmButton = {
                Button(
                    onClick = {
                        presupuestoAEliminar?.let { viewModel.eliminarPresupuesto(it) }
                        showEliminarDialog = false
                        presupuestoAEliminar = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE57373)
                    )
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showEliminarDialog = false
                    presupuestoAEliminar = null
                }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Mostrar error si existe
    estado.error?.let { error ->
        LaunchedEffect(error) {
            // Aquí podrías mostrar un Snackbar o Toast
        }
    }
}

@Composable
fun BudgetCard(
    title: String,
    amount: String,
    subtitle: String,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = amount,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitle,
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 10.sp
            )
        }
    }
}

@Composable
fun PresupuestoItem(
    presupuesto: Presupuesto,
    onDelete: () -> Unit
) {
    val porcentaje = if (presupuesto.monto > 0) {
        ((presupuesto.gastado / presupuesto.monto) * 100).toInt().coerceIn(0, 100)
    } else 0

    val color = when {
        porcentaje >= 90 -> Color(0xFFE57373)
        porcentaje >= 70 -> Color(0xFFFFB74D)
        else -> Color(0xFF4CAF87)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = presupuesto.categoriaId,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = Color(0xFFE57373)
                    )
                }
            }

            Text(
                text = "Período: ${presupuesto.periodo}",
                fontSize = 12.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = (porcentaje / 100f).coerceIn(0f, 1f),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp),
                color = color,
                trackColor = Color.LightGray
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Gastado",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "S/${String.format("%.2f", presupuesto.gastado)}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Presupuesto",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "S/${String.format("%.2f", presupuesto.monto)}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "$porcentaje% usado",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
                Text(
                    text = "Quedan: S/${String.format("%.2f", presupuesto.monto - presupuesto.gastado)}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun NuevoPresupuestoDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Double, String, Int) -> Unit
) {
    var categoria by remember { mutableStateOf("") }
    var monto by remember { mutableStateOf("") }
    var periodo by remember { mutableStateOf("Mensual") }
    var alertaEn by remember { mutableStateOf("80") }
    var expandedPeriodo by remember { mutableStateOf(false) }

    val periodos = listOf("Semanal", "Mensual", "Trimestral", "Anual")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuevo Presupuesto") },
        text = {
            Column {
                OutlinedTextField(
                    value = categoria,
                    onValueChange = { categoria = it },
                    label = { Text("Categoría") },
                    placeholder = { Text("Ej: Comida, Transporte") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = monto,
                    onValueChange = { if (it.isEmpty() || it.toDoubleOrNull() != null) monto = it },
                    label = { Text("Monto (S/)") },
                    placeholder = { Text("0.00") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                ExposedDropdownMenuBox(
                    expanded = expandedPeriodo,
                    onExpandedChange = { expandedPeriodo = !expandedPeriodo }
                ) {
                    OutlinedTextField(
                        value = periodo,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Período") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedPeriodo) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedPeriodo,
                        onDismissRequest = { expandedPeriodo = false }
                    ) {
                        periodos.forEach { p ->
                            DropdownMenuItem(
                                text = { Text(p) },
                                onClick = {
                                    periodo = p
                                    expandedPeriodo = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = alertaEn,
                    onValueChange = {
                        if (it.isEmpty() || (it.toIntOrNull()?.let { v -> v in 0..100 } == true)) {
                            alertaEn = it
                        }
                    },
                    label = { Text("Alerta en (%)") },
                    placeholder = { Text("80") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val montoDouble = monto.toDoubleOrNull() ?: 0.0
                    val alertaInt = alertaEn.toIntOrNull() ?: 80
                    if (categoria.isNotEmpty() && montoDouble > 0) {
                        onConfirm(categoria, montoDouble, periodo, alertaInt)
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF5B6BC4)
                )
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