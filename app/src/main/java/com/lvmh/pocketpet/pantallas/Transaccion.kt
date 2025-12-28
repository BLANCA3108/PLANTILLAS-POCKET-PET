package com.lvmh.pocketpet.pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lvmh.pocketpet.DateBase.Transaccion

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Transaccion(onNext: () -> Unit = {}) {
    val azulPrimario = Color(0xFF5E35B1)
    var mostrarAgregar by remember { mutableStateOf(false) }
    var mostrarDetalles by remember { mutableStateOf(false) }
    var transacciones by remember { mutableStateOf(emptyList<Transaccion>()) }
    var transaccionSeleccionada by remember { mutableStateOf<Transaccion?>(null) }

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
                                tint = Color.Black
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        Text("Transacciones", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = azulPrimario
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { mostrarAgregar = true },
                containerColor = azulPrimario,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, "Agregar Transacción")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F7FA))
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            OutlinedTextField(
                value = "",
                onValueChange = { },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Buscar transacciones...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null)
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.LightGray,
                    focusedBorderColor = azulPrimario
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "FILTROS RÁPIDOS",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = false,
                    onClick = { },
                    label = { Text("Hoy") },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = azulPrimario,
                        labelColor = Color.White
                    )
                )
                FilterChip(
                    selected = false,
                    onClick = { },
                    label = { Text("Esta semana") },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = azulPrimario,
                        labelColor = Color.White
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = false,
                    onClick = { },
                    label = { Text("Personalizado") },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = azulPrimario,
                        labelColor = Color.White
                    )
                )
                FilterChip(
                    selected = false,
                    onClick = { },
                    label = { Text("Este mes") },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = azulPrimario,
                        labelColor = Color.White
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "RANGO DE MONTO",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = false,
                    onClick = { },
                    label = { Text("Mínimo") },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = Color.LightGray,
                        labelColor = Color.Black
                    ),
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = false,
                    onClick = { },
                    label = { Text("Máximo") },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = Color.LightGray,
                        labelColor = Color.Black
                    ),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "CATEGORÍAS",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = false,
                    onClick = { },
                    label = { Text("Restaurantes") },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = azulPrimario,
                        labelColor = Color.White
                    )
                )
                FilterChip(
                    selected = false,
                    onClick = { },
                    label = { Text("Salario") },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = azulPrimario,
                        labelColor = Color.White
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = false,
                    onClick = { },
                    label = { Text("Supermercado") },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = azulPrimario,
                        labelColor = Color.White
                    )
                )
                FilterChip(
                    selected = false,
                    onClick = { },
                    label = { Text("Transporte") },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = azulPrimario,
                        labelColor = Color.White
                    )
                )
            }

            Button(onClick = onNext) {
                Text("Siguiente")
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (transacciones.isEmpty()) {
                Text(
                    text = "No hay transacciones",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(transacciones) { transaccion ->
                        TarjetaTransaccion(
                            transaccion = transaccion,
                            onClick = {
                                transaccionSeleccionada = transaccion
                                mostrarDetalles = true
                            }
                        )
                    }
                }
            }
        }
    }

    if (mostrarAgregar) {
        FormularioTransaccion(
            onDismiss = { mostrarAgregar = false },
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
                mostrarAgregar = false
            },
            temaColor = azulPrimario
        )
    }

    if (mostrarDetalles && transaccionSeleccionada != null) {
        DetallesTransaccion(
            transaccion = transaccionSeleccionada!!,
            onBack = { mostrarDetalles = false },
            onEliminar = {
                transacciones = transacciones.filter { it.id != transaccionSeleccionada!!.id }
                mostrarDetalles = false
            }
        )
    }
}

@Composable
fun TarjetaTransaccion(
    transaccion: Transaccion,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaccion.categoria,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = transaccion.monto,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (transaccion.tipo == "Ingreso") Color(0xFF4CAF50) else Color(0xFFF44336)
                )
            }

            Button(
                onClick = onClick,
                modifier = Modifier.height(36.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF5E35B1)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Ver Detalles", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetallesTransaccion(
    transaccion: Transaccion,
    onBack: () -> Unit,
    onEliminar: () -> Unit
) {
    val azulPrimario = Color(0xFF5E35B1)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = onBack,
                            modifier = Modifier
                                .size(40.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White
                            ),
                            shape = CircleShape,
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Atrás",
                                tint = Color.Black,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Text(
                            "Detalles",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = azulPrimario
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F7FA))
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Transacciones",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(azulPrimario, RoundedCornerShape(12.dp))
                    .padding(16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            CampoDetalle("Monto", transaccion.monto)
            Spacer(modifier = Modifier.height(16.dp))

            CampoDetalle("Fecha", transaccion.fecha)
            Spacer(modifier = Modifier.height(16.dp))

            CampoDetalleConIcono("Categoría", transaccion.categoria)
            Spacer(modifier = Modifier.height(16.dp))

            CampoDetalle("Descripción", transaccion.descripcion)
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Comprobante",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .background(Color(0xFFE8D4F8), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "[Imagen del ticket]",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = azulPrimario
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("EDITAR", fontWeight = FontWeight.Bold, color = Color.White)
                }
                Button(
                    onClick = onEliminar,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = azulPrimario
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("ELIMINAR", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun CampoDetalle(etiqueta: String, valor: String) {
    var texto by remember { mutableStateOf(valor) }

    OutlinedTextField(
        value = texto,
        onValueChange = { texto = it },
        label = { Text(etiqueta) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = Color.LightGray,
            focusedBorderColor = Color(0xFF5E35B1)
        )
    )
}

@Composable
fun CampoDetalleConIcono(etiqueta: String, valor: String) {
    var texto by remember { mutableStateOf(valor) }

    OutlinedTextField(
        value = texto,
        onValueChange = { texto = it },
        label = { Text(etiqueta) },
        modifier = Modifier.fillMaxWidth(),
        trailingIcon = { Icon(Icons.Default.ExpandMore, contentDescription = null) },
        shape = RoundedCornerShape(8.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = Color.LightGray,
            focusedBorderColor = Color(0xFF5E35B1)
        )
    )
}