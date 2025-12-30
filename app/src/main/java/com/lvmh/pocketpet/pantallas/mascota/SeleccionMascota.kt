package com.lvmh.pocketpet.pantallas.mascota

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mascotafinanciera.ui.theme.*

data class MascotaData(
    val nombre: String,
    val emoji: String,
    val descripcion: String
)

@Composable
fun PantallaSeleccionMascota(
    mascotas: List<MascotaData>,
    onMascotaSeleccionada: (MascotaData, String) -> Unit,
    onMascotaSorpresa: (String) -> Unit
) {
    var mascotaSeleccionada by remember { mutableStateOf<MascotaData?>(null) }
    var nombreMascota by remember { mutableStateOf("") }
    var mostrarDialogoNombre by remember { mutableStateOf(false) }
    var esSorpresa by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MoradoPrincipal.copy(alpha = 0.1f),
                        Color.White
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "¡Bienvenido a Pocket Pet! 🎉",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MoradoPrincipal,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Elige tu compañero financiero",
                fontSize = 16.sp,
                color = GrisMedio,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(40.dp))

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                items(mascotas) { mascota ->
                    TarjetaMascota(
                        mascota = mascota,
                        seleccionada = mascotaSeleccionada == mascota,
                        onClick = { mascotaSeleccionada = mascota }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedButton(
                onClick = {
                    esSorpresa = true
                    mostrarDialogoNombre = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = AmarilloPastel.copy(alpha = 0.1f)
                )
            ) {
                Icon(
                    Icons.Default.Casino,
                    contentDescription = null,
                    tint = AmarilloPastel
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "¡Sorpréndeme!",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = GrisTexto
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            AnimatedVisibility(visible = mascotaSeleccionada != null) {
                Button(
                    onClick = {
                        esSorpresa = false
                        mostrarDialogoNombre = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MoradoPrincipal
                    )
                ) {
                    Text(
                        "Continuar",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        if (mostrarDialogoNombre) {
            DialogoNombreMascota(
                nombreActual = nombreMascota,
                onNombreCambiado = { nombreMascota = it },
                onConfirmar = {
                    if (esSorpresa) {
                        onMascotaSorpresa(nombreMascota)
                    } else {
                        mascotaSeleccionada?.let {
                            onMascotaSeleccionada(it, nombreMascota)
                        }
                    }
                    mostrarDialogoNombre = false
                    nombreMascota = ""
                    mascotaSeleccionada = null
                },
                onDismiss = { mostrarDialogoNombre = false }
            )
        }
    }
}

@Composable
fun TarjetaMascota(
    mascota: MascotaData,
    seleccionada: Boolean,
    onClick: () -> Unit
) {
    val escala by animateFloatAsState(
        targetValue = if (seleccionada) 1.1f else 1f,
        label = "escala"
    )

    Card(
        modifier = Modifier
            .width(140.dp)
            .scale(escala)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (seleccionada) MoradoPrincipal.copy(alpha = 0.1f) else Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (seleccionada) 8.dp else 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(
                        if (seleccionada) MoradoPrincipal.copy(alpha = 0.2f)
                        else GrisClaro
                    )
                    .then(
                        if (seleccionada) Modifier.border(
                            3.dp,
                            MoradoPrincipal,
                            CircleShape
                        ) else Modifier
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = mascota.emoji, fontSize = 48.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = mascota.nombre,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (seleccionada) MoradoPrincipal else GrisTexto
            )

            Text(
                text = mascota.descripcion,
                fontSize = 12.sp,
                color = GrisMedio,
                textAlign = TextAlign.Center
            )

            if (seleccionada) {
                Spacer(modifier = Modifier.height(8.dp))
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MoradoPrincipal,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun DialogoNombreMascota(
    nombreActual: String,
    onNombreCambiado: (String) -> Unit,
    onConfirmar: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "¿Cómo se llamará tu mascota?",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            OutlinedTextField(
                value = nombreActual,
                onValueChange = onNombreCambiado,
                label = { Text("Nombre") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    if (nombreActual.isNotBlank()) onConfirmar()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MoradoPrincipal
                )
            ) {
                Text("Confirmar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
