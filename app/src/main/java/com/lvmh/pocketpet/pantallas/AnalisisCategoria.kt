package com.lvmh.pocketpet.pantallas

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalisisCategoria() {
    var gastado by remember { mutableStateOf(37.00) }
    var promedioDiario by remember { mutableStateOf(12.00) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(
                            onClick = { },
                            modifier = Modifier
                                .size(48.dp)
                                .background(Color(0xFFFFFFFF), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Atras",
                                tint = Color.Black
                            )
                        }
                        Text("Analisis por categorias")
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Search, contentDescription = "Buscar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFFC107)
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .background(Color(0xFFFFFBF0))
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Categoria", fontWeight = FontWeight.Bold, fontSize = 18.sp)

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFC107)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.Restaurant,
                        contentDescription = null,
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .padding(12.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("COMIDA", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Gastado: S/ ${gastado}", fontSize = 14.sp)
                    Text("(100%)", fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Promedio diario: S/ $promedioDiario", fontSize = 12.sp)
                }
            }

            Text("Comparativa mensual", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            GraficoBarrasMensual1()

            Text("Transacciones de la categoria", fontWeight = FontWeight.Bold, fontSize = 18.sp)

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
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
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFFC107).copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Comida",
                                tint = Color(0xFFFFC107)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Comida",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "Hoy",
                                fontSize = 13.sp,
                                color = Color.Gray
                            )
                        }
                    }
                    Text(
                        text = "-S/.10",
                        color = Color.Red,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }

            Text("Disminucion en Comida", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(Color(0xFFFFC107), CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Bajo de 300 en marzo a 250 en setiembre", fontSize = 14.sp)
            }

            Text("Pico en Comida", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(Color(0xFFFFC107), CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("En Agosto con 300", fontSize = 14.sp)
            }

            Text("Aumento en Comida", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(Color(0xFFFFC107), CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("De 300 en abril a 350 en julio", fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun GraficoBarrasMensual1() {
    val dias = listOf("Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Set", "Oct", "Nov", "Dic")
    val valores = listOf(0.4f, 0.3f, 0.5f, 0.6f, 0.7f, 0.9f, 0.3f, 0.4f, 0.3f, 0.5f, 0.6f, 0.7f)
    val amarilloClaro = Color(0xFFFFF9C4)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            valores.forEach { valor ->
                Box(
                    modifier = Modifier
                        .width(30.dp)
                        .fillMaxHeight(valor)
                        .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                        .background(amarilloClaro)
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            dias.forEach { dia ->
                Text(text = dia, fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}
