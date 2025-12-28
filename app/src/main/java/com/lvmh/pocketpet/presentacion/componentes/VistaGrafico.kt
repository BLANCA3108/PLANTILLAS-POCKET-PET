package com.lvmh.pocketpet.presentacion.componentes

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import com.lvmh.pocketpet.dominio.utilidades.PuntoGrafico

@Composable
fun VistaGrafico(
    datos: List<PuntoGrafico>,
    tipo: TipoGrafico = TipoGrafico.BARRAS,
    modificador: Modifier = Modifier
) {
    if (datos.isEmpty()) {
        Box(
            modifier = modificador
                .fillMaxWidth()
                .height(200.dp)
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(16.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text("No hay datos disponibles", style = MaterialTheme.typography.bodyMedium)
        }
        return
    }

    when (tipo) {
        TipoGrafico.BARRAS -> GraficoBarras(datos, modificador)
        TipoGrafico.LINEA -> GraficoLinea(datos, modificador)
        TipoGrafico.PASTEL -> GraficoPastel(datos, modificador)
    }
}

enum class TipoGrafico {
    BARRAS, LINEA, PASTEL
}

@Composable
private fun GraficoBarras(datos: List<PuntoGrafico>, modificador: Modifier) {
    val valorMaximo = datos.maxOfOrNull { it.valor } ?: 1.0
    val colorBarra = MaterialTheme.colorScheme.primary

    Canvas(
        modifier = modificador
            .fillMaxWidth()
            .height(200.dp)
            .padding(16.dp)
    ) {
        val anchoBarra = size.width / (datos.size * 2)
        val espacioEntre = anchoBarra / 2

        datos.forEachIndexed { index, punto ->
            val alturaBarra = (punto.valor / valorMaximo * size.height).toFloat()
            val x = index * (anchoBarra + espacioEntre) + espacioEntre

            drawRect(
                color = colorBarra,
                topLeft = Offset(x, size.height - alturaBarra),
                size = Size(anchoBarra, alturaBarra)
            )
        }
    }
}

@Composable
private fun GraficoLinea(datos: List<PuntoGrafico>, modificador: Modifier) {
    val valorMaximo = datos.maxOfOrNull { it.valor } ?: 1.0
    val colorLinea = MaterialTheme.colorScheme.primary

    Canvas(
        modifier = modificador
            .fillMaxWidth()
            .height(200.dp)
            .padding(16.dp)
    ) {
        if (datos.size < 2) return@Canvas

        val anchoSegmento = size.width / (datos.size - 1)

        for (i in 0 until datos.size - 1) {
            val punto1 = datos[i]
            val punto2 = datos[i + 1]

            val y1 = size.height - (punto1.valor / valorMaximo * size.height).toFloat()
            val y2 = size.height - (punto2.valor / valorMaximo * size.height).toFloat()

            drawLine(
                color = colorLinea,
                start = Offset(i * anchoSegmento, y1),
                end = Offset((i + 1) * anchoSegmento, y2),
                strokeWidth = 4.dp.toPx()
            )

            drawCircle(
                color = colorLinea,
                radius = 6.dp.toPx(),
                center = Offset(i * anchoSegmento, y1)
            )
        }

        val ultimoPunto = datos.last()
        val ultimoY = size.height - (ultimoPunto.valor / valorMaximo * size.height).toFloat()
        drawCircle(
            color = colorLinea,
            radius = 6.dp.toPx(),
            center = Offset(size.width, ultimoY)
        )
    }
}

@Composable
private fun GraficoPastel(datos: List<PuntoGrafico>, modificador: Modifier) {
    val colores = listOf(
        Color(0xFF6366F1),
        Color(0xFF8B5CF6),
        Color(0xFFEC4899),
        Color(0xFFF59E0B),
        Color(0xFF10B981)
    )

    Canvas(
        modifier = modificador
            .fillMaxWidth()
            .height(200.dp)
            .padding(16.dp)
    ) {
        val radio = size.minDimension / 2
        val centro = Offset(size.width / 2, size.height / 2)

        var anguloInicio = -90f

        datos.forEachIndexed { index, punto ->
            val anguloBarrido = (punto.valor / 100 * 360).toFloat()

            drawArc(
                color = colores[index % colores.size],
                startAngle = anguloInicio,
                sweepAngle = anguloBarrido,
                useCenter = true,
                topLeft = Offset(centro.x - radio, centro.y - radio),
                size = Size(radio * 2, radio * 2)
            )

            anguloInicio += anguloBarrido
        }
    }
}
