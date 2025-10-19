package com.lvmh.pocketpet.pantallas

import com.lvmh.pocketpet.DateBase.Transaccion
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Calendario() {
    var transacciones by remember {
        mutableStateOf(
            listOf(
                Transaccion(
                    id = "1",
                    tipo = "Ingreso",
                    monto = "4000.00",
                    categoria = "Salario",
                    fecha = "30/04/2025",
                    descripcion = "Salario mensual"
                )
            )
        )
    }
    var mostrarFormulario by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { },
                            modifier = Modifier
                                .size(48.dp)
                                .background(Color.White, CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Atrás",
                                tint = Color(0xFF4CAF50)
                            )
                        }

                        Spacer(Modifier.width(8.dp))
                        Text("Calendario", color = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.DateRange, contentDescription = "calendario", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF4CAF50)
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFFAFAFA))
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.DateRange,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    "Calendario",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Abril ▼", color = Color(0xFF4CAF50), fontWeight = FontWeight.Medium)
                Text("2025 ▼", color = Color(0xFF4CAF50), fontWeight = FontWeight.Medium)
            }

            Spacer(modifier = Modifier.height(12.dp))

            CalendarioGrid()

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6B6B)),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Egresos", fontSize = 12.sp)
                }
                Button(
                    onClick = { },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Ingresos", fontSize = 12.sp)
                }
                Button(
                    onClick = { },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEB3B)),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Todos", fontSize = 12.sp, color = Color.Black)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Transaccion",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            transacciones.forEach { transaccion ->
                TransaccionItem(transaccion)
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconButton(
                    onClick = { mostrarFormulario = true },
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color(0xFF4CAF50), CircleShape)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Agregar",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Agregar\nTransaccion",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
            }
        }
    }

    if (mostrarFormulario) {
        FormularioNuevaTransaccion(
            onDismiss = { mostrarFormulario = false },
            onGuardar = { nuevaTransaccion ->
                transacciones = transacciones + nuevaTransaccion
                mostrarFormulario = false
            }
        )
    }
}

@Composable
fun CalendarioGrid() {
    val diasSemana = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            diasSemana.forEach { dia ->
                Text(
                    dia,
                    fontSize = 11.sp,
                    color = Color.Gray,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        val dias = (1..31).toList()
        val semanas = dias.chunked(7)

        semanas.forEach { semana ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                semana.forEach { dia ->
                    Text(
                        dia.toString(),
                        fontSize = 14.sp,
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 4.dp),
                        textAlign = TextAlign.Center
                    )
                }
                repeat(7 - semana.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun TransaccionItem(transaccion: Transaccion) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF4CAF50).copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = transaccion.categoria,
                    tint = Color(0xFF4CAF50)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = transaccion.categoria,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
                Text(
                    text = transaccion.fecha,
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }
        }
        Text(
            text = if (transaccion.tipo == "Ingreso")
                "S/ ${transaccion.monto}"
            else
                "-S/ ${transaccion.monto}",
            color = if (transaccion.tipo == "Ingreso") Color(0xFF4CAF50) else Color.Red,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioNuevaTransaccion(
    onDismiss: () -> Unit,
    onGuardar: (Transaccion) -> Unit
) {
    var tipo by remember { mutableStateOf("Ingreso") }
    var monto by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Nueva Transacción",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, "Cerrar")
                    }
                }

                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { tipo = "Ingreso" },
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (tipo == "Ingreso") Color(0xFF4CAF50) else Color.LightGray
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Ingreso", fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = { tipo = "Gasto" },
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (tipo == "Gasto") Color(0xFFFF6B6B) else Color.LightGray
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Gasto", fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = monto,
                    onValueChange = { monto = it },
                    label = { Text("Monto") },
                    placeholder = { Text("0.00") },
                    leadingIcon = { Text("S/", modifier = Modifier.padding(start = 12.dp)) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = categoria,
                    onValueChange = { categoria = it },
                    label = { Text("Categoría") },
                    placeholder = { Text("Ej. Salario, Comida") },
                    leadingIcon = { Icon(Icons.Default.Category, null) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = fecha,
                    onValueChange = { fecha = it },
                    label = { Text("Fecha") },
                    placeholder = { Text("DD/MM/YYYY") },
                    leadingIcon = { Icon(Icons.Default.DateRange, null) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción") },
                    placeholder = { Text("Descripción (opcional)") },
                    leadingIcon = { Icon(Icons.Default.NoteAdd, null) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(20.dp))

                Button(
                    onClick = {
                        if (monto.isNotEmpty() && categoria.isNotEmpty() && fecha.isNotEmpty()) {
                            val transaccion = Transaccion(
                                id = System.currentTimeMillis().toString(),
                                tipo = tipo,
                                monto = monto,
                                categoria = categoria,
                                fecha = fecha,
                                descripcion = descripcion
                            )
                            onGuardar(transaccion)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Guardar Transacción", modifier = Modifier.padding(8.dp))
                }
            }
        }
    }
}