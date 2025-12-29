package com.lvmh.pocketpet.presentacion.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lvmh.pocketpet.datos.repositorios.TransaccionRepository
import com.lvmh.pocketpet.dominio.casouso.estadisticas.*
import com.lvmh.pocketpet.dominio.modelos.Transaccion
import com.lvmh.pocketpet.dominio.utilidades.AsistenteGraficos
import com.lvmh.pocketpet.dominio.utilidades.PuntoGrafico
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

data class EstadoEstadisticas(
    val cargando: Boolean = false,
    val estadisticas: Estadisticas? = null,
    val reporteMensual: ReporteMensual? = null,
    val comparacion: ComparacionPeriodo? = null,
    val datosGrafico: List<PuntoGrafico> = emptyList(),
    val periodoSeleccionado: String = "mes",
    val error: String? = null
)

@HiltViewModel
class EstadisticasViewModel @Inject constructor(
    private val transaccionRepository: TransaccionRepository
) : ViewModel() {

    private val _estado = MutableStateFlow(EstadoEstadisticas())
    val estado: StateFlow<EstadoEstadisticas> = _estado.asStateFlow()

    private val _usuarioId = MutableStateFlow("")

    // Use cases creados internamente
    private val calcularEstadisticasUseCase = CalcularEstadisticasUseCase(transaccionRepository)
    private val obtenerReporteMensualUseCase = ObtenerReporteMensualUseCase(transaccionRepository)
    private val compararPeriodoUseCase = CompararPeriodoUseCase(transaccionRepository)

    fun establecerUsuario(usuarioId: String) {
        _usuarioId.value = usuarioId
        cargarEstadisticas()
    }

    fun cargarEstadisticas() {
        viewModelScope.launch {
            _estado.update { it.copy(cargando = true, error = null) }
            try {
                val (inicio, fin) = obtenerRangoFechas(_estado.value.periodoSeleccionado)
                val stats = calcularEstadisticasUseCase(_usuarioId.value, inicio, fin)

                // ✅ Obtener transacciones con tipo explícito
                val transacciones: List<Transaccion> = transaccionRepository
                    .obtenerTransaccionesPorRangoFecha(_usuarioId.value, inicio, fin)
                    .first()

                val datosGrafico = when (_estado.value.periodoSeleccionado) {
                    "semana" -> AsistenteGraficos.prepararDatosLinea(transacciones, 7)
                    "mes" -> AsistenteGraficos.prepararDatosLinea(transacciones, 30)
                    "anio" -> AsistenteGraficos.prepararDatosMensuales(transacciones, 12)
                    else -> AsistenteGraficos.prepararDatosBarras(transacciones)
                }

                _estado.update {
                    it.copy(
                        cargando = false,
                        estadisticas = stats,
                        datosGrafico = datosGrafico
                    )
                }
            } catch (e: Exception) {
                _estado.update {
                    it.copy(cargando = false, error = e.message ?: "Error desconocido")
                }
            }
        }
    }

    fun cargarReporteMensual(mes: Int, anio: Int) {
        viewModelScope.launch {
            _estado.update { it.copy(cargando = true, error = null) }
            try {
                val reporte = obtenerReporteMensualUseCase(_usuarioId.value, mes, anio)
                _estado.update { it.copy(cargando = false, reporteMensual = reporte) }
            } catch (e: Exception) {
                _estado.update {
                    it.copy(cargando = false, error = e.message ?: "Error al cargar reporte")
                }
            }
        }
    }

    fun compararPeriodos() {
        viewModelScope.launch {
            _estado.update { it.copy(cargando = true, error = null) }
            try {
                val (inicioActual, finActual) = obtenerRangoFechas(_estado.value.periodoSeleccionado)
                val (inicioAnterior, finAnterior) = obtenerRangoFechasAnterior(_estado.value.periodoSeleccionado)

                val comparacion = compararPeriodoUseCase(
                    _usuarioId.value,
                    inicioActual,
                    finActual,
                    inicioAnterior,
                    finAnterior
                )

                _estado.update { it.copy(cargando = false, comparacion = comparacion) }
            } catch (e: Exception) {
                _estado.update {
                    it.copy(cargando = false, error = e.message ?: "Error al comparar")
                }
            }
        }
    }

    fun cambiarPeriodo(periodo: String) {
        _estado.update { it.copy(periodoSeleccionado = periodo) }
        cargarEstadisticas()
    }

    private fun obtenerRangoFechas(periodo: String): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        val fin = calendar.timeInMillis

        when (periodo) {
            "semana" -> calendar.add(Calendar.DAY_OF_YEAR, -7)
            "mes" -> calendar.add(Calendar.MONTH, -1)
            "anio" -> calendar.add(Calendar.YEAR, -1)
        }

        return Pair(calendar.timeInMillis, fin)
    }

    private fun obtenerRangoFechasAnterior(periodo: String): Pair<Long, Long> {
        val calendar = Calendar.getInstance()

        when (periodo) {
            "semana" -> {
                calendar.add(Calendar.DAY_OF_YEAR, -7)
                val fin = calendar.timeInMillis
                calendar.add(Calendar.DAY_OF_YEAR, -7)
                return Pair(calendar.timeInMillis, fin)
            }
            "mes" -> {
                calendar.add(Calendar.MONTH, -1)
                val fin = calendar.timeInMillis
                calendar.add(Calendar.MONTH, -1)
                return Pair(calendar.timeInMillis, fin)
            }
            "anio" -> {
                calendar.add(Calendar.YEAR, -1)
                val fin = calendar.timeInMillis
                calendar.add(Calendar.YEAR, -1)
                return Pair(calendar.timeInMillis, fin)
            }
        }

        return Pair(0L, 0L)
    }
}