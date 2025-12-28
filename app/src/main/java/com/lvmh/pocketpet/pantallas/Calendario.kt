package com.lvmh.pocketpet.pantallas

import com.lvmh.pocketpet.DateBase.Transaccion
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Calendario() {
    val verde = Color(0xFF4CAF50)

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
    var filtroSeleccionado by remember { mutableStateOf("Todos") }

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
                                tint = verde
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        Text("Calendario", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.DateRange, contentDescription = "calendario", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = verde
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
                    modifier = Modifier.size(20.dp),
                    tint = verde
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    "Calendario",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(12.dp))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Abril", color = verde, fontWeight = FontWeight.Medium, fontSize = 16.sp)
                    Icon(Icons.Default.ArrowDropDown, null, tint = verde, modifier = Modifier.size(20.dp))
                }
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("2025", color = verde, fontWeight = FontWeight.Medium, fontSize = 16.sp)
                    Icon(Icons.Default.ArrowDropDown, null, tint = verde, modifier = Modifier.size(20.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            CalendarioGrid()

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = filtroSeleccionado == "Egresos",
                    onClick = { filtroSeleccionado = "Egresos" },
                    label = { Text("Egresos") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFFFF6B6B),
                        selectedLabelColor = Color.White
                    )
                )
                FilterChip(
                    selected = filtroSeleccionado == "Ingresos",
                    onClick = { filtroSeleccionado = "Ingresos" },
                    label = { Text("Ingresos") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = verde,
                        selectedLabelColor = Color.White
                    )
                )
                FilterChip(
                    selected = filtroSeleccionado == "Todos",
                    onClick = { filtroSeleccionado = "Todos" },
                    label = { Text("Todos") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFFFFEB3B),
                        selectedLabelColor = Color.Black
                    )
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                "Transacciones",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(12.dp))

            val transaccionesFiltradas = transacciones.filter { transaccion ->
                when (filtroSeleccionado) {
                    "Egresos" -> transaccion.tipo == "Gasto"
                    "Ingresos" -> transaccion.tipo == "Ingreso"
                    else -> true
                }
            }

            if (transaccionesFiltradas.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No hay transacciones",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                transaccionesFiltradas.forEach { transaccion ->
                    TransaccionItemCalendario(transaccion)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconButton(
                    onClick = { mostrarFormulario = true },
                    modifier = Modifier
                        .size(56.dp)
                        .background(verde, CircleShape)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Agregar",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Agregar\nTransacción",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    if (mostrarFormulario) {
        FormularioTransaccion(
            onDismiss = { mostrarFormulario = false },
            onGuardar = { tipo, monto, categoria, fecha, descripcion ->
                val nuevaTransaccion = Transaccion(
                    id = System.currentTimeMillis().toString(),
                    tipo = tipo,
                    monto = monto,
                    categoria = categoria,
                    fecha = fecha,
                    descripcion = descripcion
                )
                transacciones = transacciones + nuevaTransaccion
                mostrarFormulario = false
            },
            temaColor = verde
        )
    }
}

@Composable
fun CalendarioGrid() {
    val diasSemana = listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sab", "Dom")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            diasSemana.forEach { dia ->
                Text(
                    dia,
                    fontSize = 11.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        val dias = (1..30).toList()
        val semanas = dias.chunked(7)
        val diaInicio = 3

        val primeraSemana = (1..diaInicio).map { null } + semanas.first().take(7 - diaInicio)

        (primeraSemana + semanas.drop(1).flatten()).chunked(7).forEach { semana ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                semana.forEach { dia ->
                    if (dia == null) {
                        Spacer(modifier = Modifier.weight(1f))
                    } else {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .padding(4.dp)
                                .background(
                                    if (dia == 30) Color(0xFF4CAF50).copy(alpha = 0.2f) else Color.Transparent,
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                dia.toString(),
                                fontSize = 13.sp,
                                fontWeight = if (dia == 30) FontWeight.Bold else FontWeight.Normal,
                                color = if (dia == 30) Color(0xFF4CAF50) else Color.Black,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TransaccionItemCalendario(transaccion: Transaccion) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF4CAF50).copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (transaccion.tipo == "Ingreso") Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                        contentDescription = transaccion.categoria,
                        tint = if (transaccion.tipo == "Ingreso") Color(0xFF4CAF50) else Color(0xFFFF6B6B),
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = transaccion.categoria,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = Color.Black
                    )
                    Text(
                        text = transaccion.fecha,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
            Text(
                text = if (transaccion.tipo == "Ingreso")
                    "+S/ ${transaccion.monto}"
                else
                    "-S/ ${transaccion.monto}",
                color = if (transaccion.tipo == "Ingreso") Color(0xFF4CAF50) else Color(0xFFFF6B6B),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}