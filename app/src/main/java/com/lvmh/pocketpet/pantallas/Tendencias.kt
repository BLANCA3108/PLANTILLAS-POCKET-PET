package com.lvmh.pocketpet.pantallas

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
fun Tendencias() {
    val amarillo = Color(0xFFFFF200)
    var vistaActual by remember { mutableStateOf("semanal") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Tendencias de Gastos",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = amarillo
                ),
                navigationIcon = {
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.Black
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Search, contentDescription = "Buscar", tint = Color.Black)
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5))
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { vistaActual = "semanal" },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (vistaActual == "semanal") amarillo else Color.LightGray
                    ),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                ) {
                    Text(
                        "Patrón semanal",
                        color = Color.Black,
                        fontWeight = if (vistaActual == "semanal") FontWeight.Bold else FontWeight.Normal
                    )
                }

                Button(
                    onClick = { vistaActual = "mensual" },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (vistaActual == "mensual") amarillo else Color.LightGray
                    ),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                ) {
                    Text(
                        "Patrón mensual",
                        color = Color.Black,
                        fontWeight = if (vistaActual == "mensual") FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    if (vistaActual == "semanal") {
                        GraficoBarrasSemanal()
                    } else {
                        GraficoBarrasMensual()
                    }
                }
            }

            Text("CATEGORÍAS RECURRENTES", fontWeight = FontWeight.Bold, fontSize = 16.sp)

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = amarillo),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CategoriaItem(
                        emoji = "🍔",
                        texto = "Comida: Todos los días"
                    )

                    CategoriaItem(
                        emoji = "🚌",
                        texto = "Transporte: Lun-Vie"
                    )

                    CategoriaItem(
                        emoji = "🎮",
                        texto = "Entretenimiento: Sábados"
                    )
                }
            }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF3EDF7)),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF3EDF7)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Box(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        BadgedBox(
                            badge = {
                                Badge(
                                    containerColor = Color.Red,
                                    modifier = Modifier.offset(x = (-6).dp, y = (6).dp)
                                ) {
                                    Text("5", color = Color.White, fontSize = 10.sp)
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notificaciones",
                                modifier = Modifier.size(35.dp),
                                tint = Color(0xFF5C6BC0)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp)) // espacio entre icono y textos

                        Column {
                            Text(
                                "Comida",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 18.sp // más grande
                            )
                            Text(
                                "Has excedido tu presupuesto",
                                color = Color.Red,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

        }
        }
        }
@Composable
fun GraficoBarrasSemanal() {
    val dias = listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom")
    val valores = listOf(0.4f, 0.3f, 0.5f, 0.6f, 0.7f, 0.9f, 0.3f)
    val amarilloClaro = Color(0xFFFFF9C4)

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            valores.forEachIndexed { index, valor ->
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
            dias.forEachIndexed { index, dia ->
                Text(
                    text = dia,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun GraficoBarrasMensual() {
    val dias = listOf("Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul","Ago","Set","Oct","Nov","Dic")
    val valores = listOf(0.4f, 0.3f, 0.5f, 0.6f, 0.7f, 0.9f, 0.3f,0.4f, 0.3f, 0.5f, 0.6f, 0.7f)
    val amarilloClaro = Color(0xFFFFF9C4)

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            valores.forEachIndexed { index, valor ->
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
            dias.forEachIndexed { index, dia ->
                Text(
                    text = dia,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun CategoriaItem(emoji: String, texto: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = emoji,
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = texto,
            fontSize = 14.sp,
            color = Color.Black
        )
    }
}
