package com.lvmh.pocketpet.pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MiPerfil(onBack: () -> Unit) {
    val verdeMenta = Color(0xFF77E76F)
    val azulPrimario = Color(0xFF5E35B1)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color.White, CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Atrás",
                                tint = Color.Black
                            )
                        }

                        Spacer(Modifier.width(12.dp))
                        Text(
                            "MI PERFIL",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = verdeMenta
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
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Avatar
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(Color(0xFFE8D4F8), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Perfil",
                    modifier = Modifier.size(80.dp),
                    tint = azulPrimario
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Título Usuario
            Text(
                text = "USUARIO",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Tarjetas de estadísticas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TarjetaEstadistica("Total\nIngresos", verdeMenta, Modifier.weight(1f))
                TarjetaEstadistica("Total\nGastos", verdeMenta, Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TarjetaEstadistica("Balance\nActual", verdeMenta, Modifier.weight(1f))
                TarjetaEstadistica("Transacciones", verdeMenta, Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Meta del mes
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "META DEL MES",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Barra de progreso
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .background(Color(0xFFE0E0E0), RoundedCornerShape(6.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.65f)
                            .fillMaxHeight()
                            .background(azulPrimario, RoundedCornerShape(6.dp))
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "65% completado",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "S/. 6,500 / S/. 10,000",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun TarjetaEstadistica(texto: String, color: Color, modifier: Modifier = Modifier) {
    Button(
        onClick = { },
        modifier = modifier
            .height(80.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = color
        ),
        shape = RoundedCornerShape(12.dp),
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(
            text = texto,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}
