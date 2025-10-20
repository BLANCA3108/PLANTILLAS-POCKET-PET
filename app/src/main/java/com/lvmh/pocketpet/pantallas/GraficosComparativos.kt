package com.lvmh.pocketpet.pantallas
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GraficosComparativo() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = {},
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color.White, CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = null,
                                tint = Color(0xFF5E35B1)
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Graficos Comparativos",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF5E35B1))
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
            Text(
                "Comparando Agosto VS Setiembre",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    GraficoComparativo()
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(16.dp))
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Comida",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF333333)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "S/ 25.00",
                            fontSize = 18.sp,
                            color = Color(0xFF4CAF50),
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "18/10/2025",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color(0xFFFFC107), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            RecomendacionCard("🎯", "Redujiste gastos en 3 categorias", Color(0xFF00BCD4))
            Spacer(modifier = Modifier.height(12.dp))
            RecomendacionCard("📊", "Tu mejor mes en 6 meses", Color(0xFF00BCD4))
            Spacer(modifier = Modifier.height(12.dp))
            RecomendacionCard("💡", "Superaste tu meta de ahorro", Color(0xFF00BCD4))
        }
    }
}

@Composable
fun GraficoComparativo() {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val width = size.width
                    val height = size.height
                    val spacing = width / 7f
                    val datosAgosto = listOf(50f, 80f, 60f, 90f, 70f, 100f, 85f)
                    val datosSeptiembre = listOf(40f, 70f, 85f, 60f, 80f, 120f, 110f)
                    val maxValor = 180f
                    for (i in 0..5) {
                        val y = height * i / 5f
                        drawLine(
                            color = Color.LightGray.copy(alpha = 0.3f),
                            start = Offset(0f, y),
                            end = Offset(width, y),
                            strokeWidth = 1.dp.toPx()
                        )
                    }
                    val pathAgosto = Path()
                    pathAgosto.moveTo(0f, height)
                    datosAgosto.forEachIndexed { index, valor ->
                        val x = spacing * index
                        val y = height - (valor / maxValor * height)
                        pathAgosto.lineTo(x, y)
                    }
                    pathAgosto.lineTo(spacing * (datosAgosto.size - 1), height)
                    pathAgosto.close()
                    drawPath(pathAgosto, Color(0xFFFF6B9D).copy(alpha = 0.3f))
                    val pathSept = Path()
                    pathSept.moveTo(0f, height)
                    datosSeptiembre.forEachIndexed { index, valor ->
                        val x = spacing * index
                        val y = height - (valor / maxValor * height)
                        pathSept.lineTo(x, y)
                    }
                    pathSept.lineTo(spacing * (datosSeptiembre.size - 1), height)
                    pathSept.close()
                    drawPath(pathSept, Color(0xFF9C88FF).copy(alpha = 0.3f))
                    for (i in 0 until datosAgosto.size - 1) {
                        val x1 = spacing * i
                        val y1 = height - (datosAgosto[i] / maxValor * height)
                        val x2 = spacing * (i + 1)
                        val y2 = height - (datosAgosto[i + 1] / maxValor * height)
                        drawLine(
                            color = Color(0xFFFF6B9D),
                            start = Offset(x1, y1),
                            end = Offset(x2, y2),
                            strokeWidth = 3.dp.toPx()
                        )
                    }
                    for (i in 0 until datosSeptiembre.size - 1) {
                        val x1 = spacing * i
                        val y1 = height - (datosSeptiembre[i] / maxValor * height)
                        val x2 = spacing * (i + 1)
                        val y2 = height - (datosSeptiembre[i + 1] / maxValor * height)
                        drawLine(
                            color = Color(0xFF9C88FF),
                            start = Offset(x1, y1),
                            end = Offset(x2, y2),
                            strokeWidth = 3.dp.toPx()
                        )
                    }
                    datosAgosto.forEachIndexed { index, valor ->
                        val x = spacing * index
                        val y = height - (valor / maxValor * height)
                        drawCircle(Color(0xFFFF6B9D), 4.dp.toPx(), Offset(x, y))
                    }
                    datosSeptiembre.forEachIndexed { index, valor ->
                        val x = spacing * index
                        val y = height - (valor / maxValor * height)
                        drawCircle(Color(0xFF9C88FF), 4.dp.toPx(), Offset(x, y))
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf("Lun", "Mar", "Mie", "Jue", "Vie", "Sab", "Dom").forEach {
                    Text(it, fontSize = 10.sp, color = Color.Gray, textAlign = TextAlign.Center)
                }
            }
        }
    }
}

@Composable
fun RecomendacionCard(emoji: String, texto: String, color: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = color),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(emoji, fontSize = 20.sp)
            Spacer(modifier = Modifier.width(12.dp))
            Text(texto, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.White)
        }
    }
}
