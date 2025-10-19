package com.lvmh.pocketpet.pantallas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import java.text.SimpleDateFormat
import java.util.*
import com.lvmh.pocketpet.DateBase.MetaAhorro

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Metas() {
    var metas by remember { mutableStateOf(listOf<MetaAhorro>()) }
    var mostrarFormulario by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = {  },
                            modifier = Modifier
                                .size(48.dp)
                                .background(Color(0xFFFFFFFF), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Atrás",
                                tint = Color.Black
                            )
                        }

                        Spacer(Modifier.width(8.dp))
                        Text("Metas de Ahorro", color = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF5E35B1)
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { mostrarFormulario = true },
                containerColor = Color(0xFF5E35B1),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, "Nueva Meta")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (metas.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        modifier = Modifier.size(120.dp),
                        tint = Color(0xFFE0E0E0)
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "No tienes metas aún",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Presiona el botón + para crear tu primera meta de ahorro",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(metas) { meta ->
                        TarjetaMeta(
                            meta = meta,
                            onEliminar = { metas = metas.filter { it.id != meta.id } }
                        )
                    }
                }
            }
        }
    }
    if (mostrarFormulario) {
        FormularioNuevaMeta(
            onDismiss = { mostrarFormulario = false },
            onGuardar = { nuevaMeta ->
                metas = metas + nuevaMeta
                mostrarFormulario = false
            }
        )
    }
}

@Composable
fun TarjetaMeta(meta: MetaAhorro, onEliminar: () -> Unit) {
    val porcentaje = (meta.montoAhorrado / meta.montoTotal * 100f).coerceIn(0f, 100f)

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        meta.titulo,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(meta.emoji, fontSize = 24.sp)
                }
                IconButton(onClick = onEliminar) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Eliminar",
                        tint = Color.Red
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
            CircularProgressIndicator(
                porcentaje = porcentaje,
                montoAhorrado = meta.montoAhorrado,
                montoTotal = meta.montoTotal
            )

            Spacer(Modifier.height(24.dp))

            if (meta.fechaInicio.isNotEmpty() && meta.fechaFin.isNotEmpty()) {
                Text(
                    "${meta.fechaInicio} - ${meta.fechaFin}",
                    fontSize = 13.sp,
                    color = Color.Gray,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(16.dp))
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEDE7F6)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Progreso", fontSize = 14.sp, color = Color.Gray)
                        Text(
                            "${porcentaje.toInt()}%",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF5E35B1)
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { porcentaje / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = Color(0xFF5E35B1),
                        trackColor = Color(0xFFD1C4E9)
                    )
                }
            }
        }
    }
}

@Composable
fun CircularProgressIndicator(
    porcentaje: Float,
    montoAhorrado: Float,
    montoTotal: Float
) {
    val animatedProgress by animateFloatAsState(
        targetValue = porcentaje / 100f,
        animationSpec = tween(durationMillis = 1000),
        label = "progress"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier.size(180.dp)
        ) {
            val strokeWidth = 24.dp.toPx()
            val radius = (size.minDimension - strokeWidth) / 2

            drawArc(
                color = Color(0xFFE0E7FF),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                size = Size(radius * 2, radius * 2),
                topLeft = Offset(
                    (size.width - radius * 2) / 2,
                    (size.height - radius * 2) / 2
                )
            )

            drawArc(
                color = Color(0xFF5E35B1),
                startAngle = -90f,
                sweepAngle = 360f * animatedProgress,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                size = Size(radius * 2, radius * 2),
                topLeft = Offset(
                    (size.width - radius * 2) / 2,
                    (size.height - radius * 2) / 2
                )
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Ahorrado",
                fontSize = 12.sp,
                color = Color.Gray
            )
            Text(
                "S/${"%.0f".format(montoAhorrado)}",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                "Meta S/${"%.0f".format(montoTotal)}",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioNuevaMeta(
    onDismiss: () -> Unit,
    onGuardar: (MetaAhorro) -> Unit
) {
    var titulo by remember { mutableStateOf("") }
    var montoAhorrado by remember { mutableStateOf("") }
    var montoTotal by remember { mutableStateOf("") }
    var fechaInicio by remember { mutableStateOf("") }
    var fechaFin by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Nueva Meta",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, "Cerrar")
                    }
                }

                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = titulo,
                    onValueChange = { titulo = it },
                    label = { Text("¿Que quieres lograr?") },
                    placeholder = { Text("Ejm.Viaje a la playa") },
                    leadingIcon = { Icon(Icons.Default.Star, null) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = montoAhorrado,
                    onValueChange = { montoAhorrado = it.filter { char -> char.isDigit() || char == '.' } },
                    label = { Text("Monto ahorrado*") },
                    placeholder = { Text("0.00") },
                    leadingIcon = { Text("S/", modifier = Modifier.padding(start = 12.dp)) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = montoTotal,
                    onValueChange = { montoTotal = it.filter { char -> char.isDigit() || char == '.' } },
                    label = { Text("Meta total*") },
                    leadingIcon = { Text("S/", modifier = Modifier.padding(start = 12.dp)) },
                    placeholder = { Text("0.00") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = fechaInicio,
                        onValueChange = { fechaInicio = it },
                        label = { Text("Inicio") },
                        placeholder = { Text("DD-MM-YYYY") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = fechaFin,
                        onValueChange = { fechaFin = it },
                        label = { Text("Fin") },
                        placeholder = { Text("DD-MM-YYYY") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(Modifier.height(20.dp))

                Button(
                    onClick = {
                        if (titulo.isNotEmpty() && montoAhorrado.isNotEmpty() && montoTotal.isNotEmpty()) {
                            val meta = MetaAhorro(
                                id = System.currentTimeMillis(),
                                titulo = titulo,
                                montoAhorrado = montoAhorrado.toFloatOrNull() ?: 0f,
                                montoTotal = montoTotal.toFloatOrNull() ?: 1f,
                                fechaInicio = fechaInicio,
                                fechaFin = fechaFin
                            )
                            onGuardar(meta)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF5E35B1)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Guardar Meta", modifier = Modifier.padding(8.dp))
                }
            }
        }
    }
}