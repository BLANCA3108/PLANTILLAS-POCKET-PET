package com.lvmh.pocketpet.presentacion.viewmodels

import androidx.lifecycle.ViewModel
import com.lvmh.pocketpet.datos.repositorios.TransactionRepository
import com.lvmh.pocketpet.dominio.casouso.estadisticas.*
import com.lvmh.pocketpet.dominio.utilidades.PuntoGrafico

data class EstadoEstadisticas(
    val cargando: Boolean = false,
    val estadisticas: Estadisticas? = null,
    val reporteMensual: ReporteMensual? = null,
    val comparacion: ComparacionPeriodo? = null,
    val datosGrafico: List<PuntoGrafico> = emptyList(),
    val periodoSeleccionado: String = "mes",
    val error: String? = null
)

class EstadisticasViewModel(
    private val transactionRepository: TransactionRepository
) : ViewModel()
