package com.lvmh.pocketpet.presentacion.pantallas.estadisticas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.lvmh.pocketpet.presentacion.componentes.*
import com.lvmh.pocketpet.presentacion.viewmodels.EstadisticasViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaEstadisticas(
    viewModel: EstadisticasViewModel,
    alNavegar: (String) -> Unit
) {
    val estado by viewModel.estado.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Estadísticas") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        when {
            estado.cargando -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            estado.error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = estado.error ?: "Error desconocido")
                }
            }

            else -> {
                ContenidoEstadisticas(
                    estado = estado,
                    alCambiarPeriodo = { },
                    alNavegar = alNavegar,
                    modificador = Modifier.padding(padding)
                )
            }
        }
    }
}

@Composable
private fun ContenidoEstadisticas(
    estado: com.lvmh.pocketpet.presentacion.viewmodels.EstadoEstadisticas,
    alCambiarPeriodo: (String) -> Unit,
    alNavegar: (String) -> Unit,
    modificador: Modifier = Modifier
) {
    LazyColumn(
        modifier = modificador
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        item {
            SelectorPeriodo(
                periodoSeleccionado = estado.periodoSeleccionado,
                alSeleccionar = alCambiarPeriodo
            )
        }

        item {
            Text(
                text = "Resumen",
                style = MaterialTheme.typography.titleLarge
            )
        }

        estado.estadisticas?.let { stats ->

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TarjetaEstadistica(
                        titulo = "Ingresos",
                        valor = "$${String.format("%.2f", stats.totalIngresos)}",
                        icono = {
                            Icon(
                                Icons.Default.TrendingUp,
                                contentDescription = null,
                                tint = Color(0xFF10B981)
                            )
                        },
                        colorFondo = Color(0xFFD1FAE5),
                        colorTexto = Color(0xFF065F46),
                        modificador = Modifier.weight(1f)
                    )

                    TarjetaEstadistica(
                        titulo = "Gastos",
                        valor = "$${String.format("%.2f", stats.totalGastos)}",
                        icono = {
                            Icon(
                                Icons.Default.TrendingDown,
                                contentDescription = null,
                                tint = Color(0xFFEF4444)
                            )
                        },
                        colorFondo = Color(0xFFFEE2E2),
                        colorTexto = Color(0xFF991B1B),
                        modificador = Modifier.weight(1f)
                    )
                }
            }

            item {
                TarjetaEstadistica(
                    titulo = "Balance",
                    valor = "$${String.format("%.2f", stats.balance)}",
                    icono = {
                        Icon(
                            Icons.Default.AccountBalance,
                            contentDescription = null,
                            tint = if (stats.balance >= 0)
                                Color(0xFF3B82F6) else Color(0xFFEF4444)
                        )
                    },
                    colorFondo = if (stats.balance >= 0)
                        Color(0xFFDBEAFE) else Color(0xFFFEE2E2),
                    colorTexto = if (stats.balance >= 0)
                        Color(0xFF1E40AF) else Color(0xFF991B1B)
                )
            }
        }

        item {
            Text(
                text = "Gráfico",
                style = MaterialTheme.typography.titleLarge
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                VistaGrafico(
                    datos = estado.datosGrafico,
                    tipo = TipoGrafico.BARRAS,
                    modificador = Modifier.padding(16.dp)
                )
            }
        }

        item {
            Text(
                text = "Acciones Rápidas",
                style = MaterialTheme.typography.titleLarge
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                OutlinedButton(
                    onClick = { alNavegar("categorias_estadisticas") },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Category, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Por Categoría")
                }

                OutlinedButton(
                    onClick = { alNavegar("tendencias") },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.ShowChart, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Tendencias")
                }
            }
        }
    }
}

@Composable
private fun SelectorPeriodo(
    periodoSeleccionado: String,
    alSeleccionar: (String) -> Unit
) {
    val periodos = listOf("semana", "mes", "anio")
    val etiquetas = mapOf(
        "semana" to "Semana",
        "mes" to "Mes",
        "anio" to "Año"
    )

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(periodos) { periodo ->
            FilterChip(
                selected = periodo == periodoSeleccionado,
                onClick = { alSeleccionar(periodo) },
                label = { Text(etiquetas[periodo] ?: periodo) }
            )
        }
    }
}
