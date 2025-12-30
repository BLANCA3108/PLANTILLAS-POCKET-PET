package com.lvmh.pocketpet.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel para manejar las metas mensuales del usuario
 */
@HiltViewModel
class MetaViewModel @Inject constructor(
    // Aquí inyectarás tu repositorio cuando lo tengas
    // private val metaRepository: MetaRepository
) : ViewModel() {

    // Estado de la meta mensual
    private val _metaMensual = MutableStateFlow(10000.0)
    val metaMensual: StateFlow<Double> = _metaMensual.asStateFlow()

    // Estado del progreso actual
    private val _progresoActual = MutableStateFlow(0.0)
    val progresoActual: StateFlow<Double> = _progresoActual.asStateFlow()

    // Estado del porcentaje de completado
    private val _porcentajeCompletado = MutableStateFlow(0f)
    val porcentajeCompletado: StateFlow<Float> = _porcentajeCompletado.asStateFlow()

    // Estado de carga
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Mes actual de la meta
    private val _mesActual = MutableStateFlow("")
    val mesActual: StateFlow<String> = _mesActual.asStateFlow()

    init {
        obtenerMesActual()
    }

    /**
     * Inicializa el ViewModel con el ID del usuario
     */
    fun inicializar(usuarioId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Aquí cargarías la meta desde tu base de datos
                // val meta = metaRepository.obtenerMetaActual(usuarioId)
                // _metaMensual.value = meta.monto

                // Calcular progreso basado en transacciones del mes
                calcularProgreso(usuarioId)
            } catch (e: Exception) {
                // Manejar errores
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Actualiza la meta mensual
     */
    fun actualizarMeta(nuevaMeta: Double) {
        viewModelScope.launch {
            _metaMensual.value = nuevaMeta
            calcularPorcentaje()

            // Aquí guardarías en la base de datos
            // metaRepository.guardarMeta(usuarioId, nuevaMeta)
        }
    }

    /**
     * Calcula el progreso actual basado en transacciones
     */
    private fun calcularProgreso(usuarioId: String) {
        viewModelScope.launch {
            // Aquí calcularías el progreso real basado en tus transacciones
            // Por ejemplo:
            // val transacciones = transaccionRepository.obtenerTransaccionesMesActual(usuarioId)
            // val totalIngresos = transacciones.filter { it.tipo == INGRESO }.sumOf { it.monto }
            // val totalGastos = transacciones.filter { it.tipo == GASTO }.sumOf { it.monto }
            // _progresoActual.value = totalIngresos - totalGastos

            // Por ahora usamos valor de ejemplo
            _progresoActual.value = 6500.0
            calcularPorcentaje()
        }
    }

    /**
     * Actualiza el progreso actual (llamar cuando se agregue/modifique una transacción)
     */
    fun actualizarProgreso(nuevoProgreso: Double) {
        _progresoActual.value = nuevoProgreso
        calcularPorcentaje()
    }

    /**
     * Calcula el porcentaje de completado
     */
    private fun calcularPorcentaje() {
        val porcentaje = if (_metaMensual.value > 0) {
            ((_progresoActual.value / _metaMensual.value) * 100).toFloat()
        } else {
            0f
        }
        _porcentajeCompletado.value = porcentaje.coerceIn(0f, 100f)
    }

    /**
     * Obtiene el mes actual
     */
    private fun obtenerMesActual() {
        val meses = listOf(
            "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
            "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
        )
        val mesIndex = java.util.Calendar.getInstance().get(java.util.Calendar.MONTH)
        _mesActual.value = meses[mesIndex]
    }

    /**
     * Reinicia la meta (para nuevo mes)
     */
    fun reiniciarMeta() {
        viewModelScope.launch {
            _progresoActual.value = 0.0
            calcularPorcentaje()
            obtenerMesActual()
        }
    }

    /**
     * Verifica si la meta fue alcanzada
     */
    fun metaAlcanzada(): Boolean {
        return _progresoActual.value >= _metaMensual.value
    }

    /**
     * Obtiene el mensaje motivacional según el progreso
     */
    fun obtenerMensajeMotivacional(): String {
        return when {
            _porcentajeCompletado.value >= 100f -> "🎉 ¡Meta alcanzada!"
            _porcentajeCompletado.value >= 70f -> "🔥 ¡Casi lo logras!"
            _porcentajeCompletado.value >= 50f -> "💪 Vas por buen camino"
            _porcentajeCompletado.value >= 25f -> "🎯 Sigue adelante"
            else -> "🌱 ¡Comienza tu camino!"
        }
    }

    /**
     * Obtiene el emoji según el progreso
     */
    fun obtenerEmoji(): String {
        return when {
            _porcentajeCompletado.value >= 100f -> "🎉"
            _porcentajeCompletado.value >= 70f -> "🔥"
            _porcentajeCompletado.value >= 50f -> "💪"
            else -> "🎯"
        }
    }
}