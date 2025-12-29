package com.lvmh.pocketpet.presentacion.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lvmh.pocketpet.datos.repositorios.TransaccionRepository
import com.lvmh.pocketpet.dominio.casouso.estadisticas.*
import com.lvmh.pocketpet.dominio.utilidades.PuntoGrafico
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

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
    private val transaccionRepository: TransaccionRepository
) : ViewModel() {

    private val _estado = MutableStateFlow(EstadoEstadisticas())
    val estado: StateFlow<EstadoEstadisticas> = _estado.asStateFlow()

    private val obtenerReporteMensualUseCase =
        ObtenerReporteMensualUseCase(transaccionRepository)

    fun cargarReporteMensual(usuarioId: String, mes: Int, anio: Int) {
        viewModelScope.launch {
            _estado.value = _estado.value.copy(cargando = true)

            try {
                val reporte = obtenerReporteMensualUseCase(
                    usuarioId = usuarioId,
                    mes = mes,
                    anio = anio
                )

                _estado.value = _estado.value.copy(
                    cargando = false,
                    reporteMensual = reporte,
                    error = null
                )
            } catch (e: Exception) {
                _estado.value = _estado.value.copy(
                    cargando = false,
                    error = e.message
                )
            }
        }
    }

    fun compararPeriodos() {
        viewModelScope.launch {
            _estado.value = _estado.value.copy(cargando = true)

            try {
                // TODO: Implementar lógica de comparación cuando se conecte con Room
                // Por ahora, datos de ejemplo para que funcione la UI
                val comparacionEjemplo = ComparacionPeriodo(
                    periodoActualIngresos = 5000.0,
                    periodoAnteriorIngresos = 4500.0,
                    periodoActualGastos = 3000.0,
                    periodoAnteriorGastos = 3200.0,
                    cambioIngresos = 500.0,
                    cambioGastos = -200.0,
                    cambioIngresosPorc = 11.1,
                    cambioGastosPorc = -6.25
                )

                _estado.value = _estado.value.copy(
                    cargando = false,
                    comparacion = comparacionEjemplo,
                    error = null
                )
            } catch (e: Exception) {
                _estado.value = _estado.value.copy(
                    cargando = false,
                    error = e.message
                )
            }
        }
    }
}