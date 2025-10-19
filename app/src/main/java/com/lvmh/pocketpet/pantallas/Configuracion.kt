package com.lvmh.pocketpet.pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Configuracion(onBack: () -> Unit, onNext: () -> Unit) {
    val verdeMenta = Color(0xFF7FDBCA)
    val azulPrimario = Color(0xFF5E35B1)
    var opcionSeleccionada by remember { mutableStateOf("App") }

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
                        Text(
                            "Configuración",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { opcionSeleccionada = "App" },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (opcionSeleccionada == "App") azulPrimario else Color(0xFFE0E0E0),
                        contentColor = if (opcionSeleccionada == "App") Color.White else Color.Gray
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("App", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
                Button(
                    onClick = { opcionSeleccionada = "Mascota" },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (opcionSeleccionada == "Mascota") azulPrimario else Color(0xFFE0E0E0),
                        contentColor = if (opcionSeleccionada == "Mascota") Color.White else Color.Gray
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Mascota", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (opcionSeleccionada == "App") {
                ContenidoApp(azulPrimario)
            } else {
                ContenidoMascota(azulPrimario)
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onNext,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE0E0E0)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    "Siguiente",
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun ContenidoApp(azulPrimario: Color) {
    var notificaciones by remember { mutableStateOf(true) }
    var recordatorios by remember { mutableStateOf(true) }

    Text(
        text = "GENERAL",
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Black,
        modifier = Modifier
            .background(Color(0xFFE8E8E8), RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .fillMaxWidth()
    )

    Spacer(modifier = Modifier.height(12.dp))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(8.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Notificaciones",
            fontSize = 14.sp,
            color = Color.Black,
            fontWeight = FontWeight.Medium
        )
        Switch(
            checked = notificaciones,
            onCheckedChange = { notificaciones = it },
            modifier = Modifier.scale(0.8f),
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = azulPrimario,
                uncheckedThumbColor = Color.Gray,
                uncheckedTrackColor = Color(0xFFE0E0E0)
            )
        )
    }

    Spacer(modifier = Modifier.height(8.dp))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(8.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Recordatorios",
            fontSize = 14.sp,
            color = Color.Black,
            fontWeight = FontWeight.Medium
        )
        Switch(
            checked = recordatorios,
            onCheckedChange = { recordatorios = it },
            modifier = Modifier.scale(0.8f),
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = azulPrimario,
                uncheckedThumbColor = Color.Gray,
                uncheckedTrackColor = Color(0xFFE0E0E0)
            )
        )
    }

    Spacer(modifier = Modifier.height(24.dp))

    Text(
        text = "SEGURIDAD",
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Black,
        modifier = Modifier
            .background(Color(0xFFE8E8E8), RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .fillMaxWidth()
    )

    Spacer(modifier = Modifier.height(12.dp))

    Text(
        text = "Ayuda",
        fontSize = 14.sp,
        color = Color.Black,
        fontWeight = FontWeight.Medium,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(8.dp))
            .padding(16.dp)
    )

    Spacer(modifier = Modifier.height(8.dp))

    Text(
        text = "Acerca de",
        fontSize = 14.sp,
        color = Color.Black,
        fontWeight = FontWeight.Medium,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(8.dp))
            .padding(16.dp)
    )

    Spacer(modifier = Modifier.height(24.dp))

    Text(
        text = "MONEDA",
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Black,
        modifier = Modifier
            .background(Color(0xFFE8E8E8), RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .fillMaxWidth()
    )

    Spacer(modifier = Modifier.height(12.dp))

    Text(
        text = "Moneda Predeterminada",
        fontSize = 14.sp,
        color = Color.Black,
        fontWeight = FontWeight.Medium,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(8.dp))
            .padding(16.dp)
    )
}

@Composable
fun ContenidoMascota(azulPrimario: Color) {
    var recordatoriosCuidado by remember { mutableStateOf(true) }
    var sonidos by remember { mutableStateOf(true) }
    var animacion by remember { mutableStateOf(false) }

    Text(
        text = "INTERACCIÓN",
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Black,
        modifier = Modifier
            .background(Color(0xFFE8E8E8), RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .fillMaxWidth()
    )

    Spacer(modifier = Modifier.height(12.dp))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(8.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Recordatorio de cuidado",
            fontSize = 14.sp,
            color = Color.Black,
            fontWeight = FontWeight.Medium
        )
        Switch(
            checked = recordatoriosCuidado,
            onCheckedChange = { recordatoriosCuidado = it },
            modifier = Modifier.scale(0.8f),
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = azulPrimario,
                uncheckedThumbColor = Color.Gray,
                uncheckedTrackColor = Color(0xFFE0E0E0)
            )
        )
    }

    Spacer(modifier = Modifier.height(8.dp))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(8.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Sonidos",
            fontSize = 14.sp,
            color = Color.Black,
            fontWeight = FontWeight.Medium
        )
        Switch(
            checked = sonidos,
            onCheckedChange = { sonidos = it },
            modifier = Modifier.scale(0.8f),
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = azulPrimario,
                uncheckedThumbColor = Color.Gray,
                uncheckedTrackColor = Color(0xFFE0E0E0)
            )
        )
    }

    Spacer(modifier = Modifier.height(8.dp))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(8.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Animación",
            fontSize = 14.sp,
            color = Color.Black,
            fontWeight = FontWeight.Medium
        )
        Switch(
            checked = animacion,
            onCheckedChange = { animacion = it },
            modifier = Modifier.scale(0.8f),
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = azulPrimario,
                uncheckedThumbColor = Color.Gray,
                uncheckedTrackColor = Color(0xFFE0E0E0)
            )
        )
    }
}
