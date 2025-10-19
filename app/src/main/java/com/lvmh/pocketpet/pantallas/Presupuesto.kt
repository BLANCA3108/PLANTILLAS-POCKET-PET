package com.lvmh.pocketpet.pantallas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lvmh.pocketpet.DateBase.Presupuesto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Presupuestos(onBackClick: () -> Unit = {}) {
    var presupuestos by remember { mutableStateOf(listOf<Presupuesto>()) }
    var showDialog by remember { mutableStateOf(false) }
    var selectedPresupuesto by remember { mutableStateOf<Presupuesto?>(null) }
    var showGastoDialog by remember { mutableStateOf(false) }

    val totalPresupuesto = presupuestos.sumOf { it.monto }
    val totalGastado = presupuestos.sumOf { it.gastado }
    val totalGanado = totalPresupuesto

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
                                contentDescription = "Atras",
                                tint = Color.Black
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        Text("Presupuesto", color = Color.White)
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Button(
                onClick = { },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF5B6BC4)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Resumen")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                BudgetCard(
                    title = "GASTADO",
                    amount = "-S/${String.format("%.0f", totalGastado)}",
                    subtitle = "Este mes gastado",
                    backgroundColor = Color(0xFF5B6BC4),
                    modifier = Modifier.weight(1f)
                )
                BudgetCard(
                    title = "GANADO",
                    amount = "+S/${String.format("%.0f", totalGanado)}",
                    subtitle = "Este mes ingreso",
                    backgroundColor = Color(0xFF4CAF87),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Presupuesto",
                fontSize = 16.sp,
                color = Color.Gray
            )
            Text(
                text = "S/${String.format("%.0f", totalGastado)}",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = if (totalPresupuesto > 0) (totalGastado / totalPresupuesto).toFloat().coerceIn(0f, 1f) else 0f,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = Color(0xFF4CAF87),
                trackColor = Color.LightGray
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "S/${String.format("%.0f", totalGastado)}", fontSize = 14.sp)
                Text(text = "S/${String.format("%.0f", totalPresupuesto)}", fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            presupuestos.forEach { presupuesto ->
                PresupuestoItem(
                    presupuesto = presupuesto,
                    onClick = {
                        selectedPresupuesto = presupuesto
                        showGastoDialog = true
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }

    if (showDialog) {
        NuevoPresupuestoDialog(
            onDismiss = { showDialog = false },
            onConfirm = { nombre, monto ->
                presupuestos = presupuestos + Presupuesto(
                    id = presupuestos.size + 1,
                    nombre = nombre,
                    monto = monto
                )
                showDialog = false
            }
        )
    }

    if (showGastoDialog && selectedPresupuesto != null) {
        GastoDialog(
            presupuesto = selectedPresupuesto!!,
            onDismiss = { showGastoDialog = false },
            onConfirm = { monto ->
                presupuestos = presupuestos.map { p ->
                    if (p.id == selectedPresupuesto!!.id) {
                        p.copy(gastado = p.gastado + monto)
                    } else {
                        p
                    }
                }
                showGastoDialog = false
            }
        )
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
    onClick: () -> Unit
) {
    val color = when (presupuesto.nombre.lowercase()) {
        "comida" -> Color(0xFFFFB74D)
        "transporte" -> Color(0xFFCDDC39)
        "renta" -> Color(0xFF9C27B0)
        "entretenimiento" -> Color(0xFF00BCD4)
        else -> Color(0xFF2196F3)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = presupuesto.nombre,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = (presupuesto.porcentaje / 100f).coerceIn(0f, 1f),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp),
                color = color,
                trackColor = Color.LightGray
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Quedan: S/${String.format("%.0f", presupuesto.quedan)}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Gastado: S/${String.format("%.0f", presupuesto.gastado)}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "(${presupuesto.porcentaje}%)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "(${100 - presupuesto.porcentaje}%)",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun NuevoPresupuestoDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Double) -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var monto by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuevo Presupuesto") },
        text = {
            Column {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre (ej: Comida, Transporte)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = monto,
                    onValueChange = { monto = it },
                    label = { Text("Monto") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val montoDouble = monto.toDoubleOrNull() ?: 0.0
                    if (nombre.isNotEmpty() && montoDouble > 0) {
                        onConfirm(nombre, montoDouble)
                    }
                }
            ) {
                Text("Agregar")
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
fun GastoDialog(
    presupuesto: Presupuesto,
    onDismiss: () -> Unit,
    onConfirm: (Double) -> Unit
) {
    var monto by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Agregar Gasto - ${presupuesto.nombre}") },
        text = {
            Column {
                Text(
                    text = "Disponible: S/${String.format("%.0f", presupuesto.quedan)}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = monto,
                    onValueChange = { monto = it },
                    label = { Text("Monto gastado") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val montoDouble = monto.toDoubleOrNull() ?: 0.0
                    if (montoDouble > 0) {
                        onConfirm(montoDouble)
                    }
                }
            ) {
                Text("Agregar Gasto")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
