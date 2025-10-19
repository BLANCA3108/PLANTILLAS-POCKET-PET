package com.lvmh.pocketpet.pantallas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Proyecciones() {
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
                        Text("Proyecciones", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
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
            Text(
                "Ritmo Actual",
                fontSize = 20.sp,
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
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    GraficoRitmo()
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "\"Si sigues así, a fin de mes tendrás S/.200\".",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEB3B)),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "😊 REALISTA",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "+5% ahorro",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "S/.1250",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    InfoRow(
                        emoji = "⭐",
                        text = "Hoy es el día 10 y has gastado S/.300"
                    )
                    InfoRow(
                        emoji = "⭐",
                        text = "Promedio diario = 300 / 10 = $30"
                    )
                    InfoRow(
                        emoji = "⭐",
                        text = "Mes tiene 30 días → Proyección = S/.30 x 30 = S/.900"
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun InfoRow(emoji: String, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            emoji,
            fontSize = 20.sp,
            modifier = Modifier.padding(end = 8.dp)
        )
        Text(
            text,
            fontSize = 14.sp,
            lineHeight = 20.sp
        )
    }
}

@Composable
fun GraficoRitmo() {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                androidx.compose.foundation.Canvas(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val width = size.width
                    val height = size.height
                    val spacing = width / 6f

                    val datos = listOf(100f, 80f, 90f, 120f, 180f, 220f, 240f)
                    val maxValor = 250f

                    for (i in 0..5) {
                        val y = height * i / 5f
                        drawLine(
                            color = Color.LightGray.copy(alpha = 0.3f),
                            start = Offset(0f, y),
                            end = Offset(width, y),
                            strokeWidth = 1.dp.toPx()
                        )
                    }

                    val pathArea = androidx.compose.ui.graphics.Path()
                    pathArea.moveTo(0f, height)

                    datos.forEachIndexed { index, valor ->
                        val x = spacing * index
                        val y = height - (valor / maxValor * height)
                        if (index == 0) {
                            pathArea.lineTo(x, y)
                        } else {
                            pathArea.lineTo(x, y)
                        }
                    }
                    pathArea.lineTo(width, height)
                    pathArea.close()

                    drawPath(
                        path = pathArea,
                        color = Color(0xFF9C27B0).copy(alpha = 0.2f)
                    )

                    for (i in 0 until datos.size - 1) {
                        val x1 = spacing * i
                        val y1 = height - (datos[i] / maxValor * height)
                        val x2 = spacing * (i + 1)
                        val y2 = height - (datos[i + 1] / maxValor * height)

                        drawLine(
                            color = Color(0xFF9C27B0),
                            start = Offset(x1, y1),
                            end = Offset(x2, y2),
                            strokeWidth = 3.dp.toPx()
                        )
                    }
                    datos.forEachIndexed { index, valor ->
                        val x = spacing * index
                        val y = height - (valor / maxValor * height)

                        drawCircle(
                            color = Color(0xFF9C27B0),
                            radius = 4.dp.toPx(),
                            center = Offset(x, y)
                        )
                    }

                    val startX = spacing * 4
                    val startY = height - (datos[4] / maxValor * height)
                    val endX = spacing * 6
                    val endY = height - (datos[6] / maxValor * height)

                    val dashWidth = 10f
                    val dashSpace = 8f
                    var currentX = startX

                    while (currentX < endX) {
                        val progress = (currentX - startX) / (endX - startX)
                        val currentY = startY + (endY - startY) * progress
                        val nextX = (currentX + dashWidth).coerceAtMost(endX)
                        val nextProgress = (nextX - startX) / (endX - startX)
                        val nextY = startY + (endY - startY) * nextProgress

                        drawLine(
                            color = Color.Gray,
                            start = Offset(currentX, currentY),
                            end = Offset(nextX, nextY),
                            strokeWidth = 2.dp.toPx()
                        )
                        currentX += dashWidth + dashSpace
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(start = 4.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("250", fontSize = 10.sp, color = Color.Gray)
                    Text("200", fontSize = 10.sp, color = Color.Gray)
                    Text("150", fontSize = 10.sp, color = Color.Gray)
                    Text("100", fontSize = 10.sp, color = Color.Gray)
                    Text("50", fontSize = 10.sp, color = Color.Gray)
                    Text("0", fontSize = 10.sp, color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf("Ene", "Feb", "Mar", "Abr", "May", "Jun").forEach { mes ->
                    Text(
                        mes,
                        fontSize = 11.sp,
                        color = Color.Gray,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}