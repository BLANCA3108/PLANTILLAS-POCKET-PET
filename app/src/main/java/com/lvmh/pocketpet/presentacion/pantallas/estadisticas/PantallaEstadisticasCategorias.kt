package com.lvmh.pocketpet.presentacion.pantallas.estadisticas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
fun PantallaEstadisticasCategorias(
    viewModel: EstadisticasViewModel,
    alRegresar: () -> Unit
) {
    val estado by viewModel.estado.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Estadísticas por Categoría") },
                navigationIcon = {
                    IconButton(onClick = alRegresar) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Distribución por Categoría",
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
                        tipo = TipoGrafico.PASTEL,
                        modificador = Modifier.padding(16.dp)
                    )
                }
            }

            item {
                Text(
                    text = "Desglose Detallado",
                    style = MaterialTheme.typography.titleLarge
                )
            }

            items(estado.datosGrafico) { punto ->
                TarjetaCategoria(
                    nombre = punto.etiqueta,
                    monto = punto.valor
                )
            }
        }
    }
}

@Composable
private fun TarjetaCategoria(
    nombre: String,
    monto: Double
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = nombre,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "$${String.format("%.2f", monto)}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}