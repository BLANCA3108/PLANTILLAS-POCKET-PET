package com.lvmh.pocketpet.presentacion.componentes

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun BarraProgresoPersonalizada(
    progreso: Float,
    etiqueta: String = "",
    colorProgreso: Color = MaterialTheme.colorScheme.primary,
    colorFondo: Color = MaterialTheme.colorScheme.surfaceVariant,
    mostrarPorcentaje: Boolean = true,
    modificador: Modifier = Modifier
) {
    val progresoLimitado = progreso.coerceIn(0f, 1f)

    var progresoAnimado by remember { mutableStateOf(0f) }

    LaunchedEffect(progresoLimitado) {
        animate(
            initialValue = progresoAnimado,
            targetValue = progresoLimitado,
            animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
        ) { value, _ ->
            progresoAnimado = value
        }
    }

    Column(modifier = modificador.fillMaxWidth()) {
        if (etiqueta.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = etiqueta,
                    style = MaterialTheme.typography.bodyMedium
                )
                if (mostrarPorcentaje) {
                    Text(
                        text = "${(progresoLimitado * 100).toInt()}%",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = colorProgreso
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(colorFondo)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progresoAnimado)
                    .clip(RoundedCornerShape(6.dp))
                    .background(colorProgreso)
            )
        }
    }
}
