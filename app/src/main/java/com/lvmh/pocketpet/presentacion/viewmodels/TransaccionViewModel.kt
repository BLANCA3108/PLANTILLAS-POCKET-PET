package com.lvmh.pocketpet.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lvmh.pocketpet.datos.repositorios.TransaccionRepository
import com.lvmh.pocketpet.dominio.modelos.Transaccion
import com.lvmh.pocketpet.dominio.modelos.TipoTransaccion
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransaccionViewModel @Inject constructor(
    private val transaccionRepository: TransaccionRepository
) : ViewModel() {

    // Estado de las transacciones
    private val _transacciones = MutableStateFlow<List<Transaccion>>(emptyList())
    val transacciones: StateFlow<List<Transaccion>> = _transacciones.asStateFlow()

    // Estado de carga
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Estado de error
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // Filtros
    private val _tipoFiltro = MutableStateFlow<TipoTransaccion?>(null)
    val tipoFiltro: StateFlow<TipoTransaccion?> = _tipoFiltro.asStateFlow()

    private val _categoriaFiltro = MutableStateFlow<String?>(null)
    val categoriaFiltro: StateFlow<String?> = _categoriaFiltro.asStateFlow()

    private val _busqueda = MutableStateFlow("")
    val busqueda: StateFlow<String> = _busqueda.asStateFlow()

    // Transacciones filtradas
    val transaccionesFiltradas: StateFlow<List<Transaccion>> = combine(
        _transacciones,
        _tipoFiltro,
        _categoriaFiltro,
        _busqueda
    ) { transacciones, tipo, categoria, busqueda ->
        var resultado = transacciones

        // Filtrar por tipo
        if (tipo != null) {
            resultado = resultado.filter { it.tipo == tipo }
        }

        // Filtrar por categoría
        if (!categoria.isNullOrEmpty()) {
            resultado = resultado.filter { it.categoriaId == categoria }
        }

        // Filtrar por búsqueda
        if (busqueda.isNotEmpty()) {
            resultado = resultado.filter {
                it.descripcion.contains(busqueda, ignoreCase = true) ||
                        it.categoriaNombre.contains(busqueda, ignoreCase = true)
            }
        }

        resultado
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Estadísticas
    private val _totalIngresos = MutableStateFlow(0.0)
    val totalIngresos: StateFlow<Double> = _totalIngresos.asStateFlow()

    private val _totalGastos = MutableStateFlow(0.0)
    val totalGastos: StateFlow<Double> = _totalGastos.asStateFlow()

    private val _balance = MutableStateFlow(0.0)
    val balance: StateFlow<Double> = _balance.asStateFlow()

    // Usuario ID
    private var usuarioId: String = ""

    fun inicializar(userId: String) {
        usuarioId = userId
        cargarTransacciones()
        cargarEstadisticas()
    }

    /**
     * Cargar transacciones
     */
    fun cargarTransacciones() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                transaccionRepository.obtenerTransacciones(usuarioId)
                    .catch { e ->
                        _error.value = "Error al cargar transacciones: ${e.message}"
                    }
                    .collect { transacciones ->
                        _transacciones.value = transacciones
                        _isLoading.value = false
                    }
            } catch (e: Exception) {
                _error.value = "Error inesperado: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    /**
     * Cargar estadísticas (totales)
     */
    private fun cargarEstadisticas() {
        viewModelScope.launch {
            try {
                val ingresos = transaccionRepository.obtenerTotalPorTipo(usuarioId, TipoTransaccion.INGRESO)
                val gastos = transaccionRepository.obtenerTotalPorTipo(usuarioId, TipoTransaccion.GASTO)
                val balance = transaccionRepository.obtenerBalance(usuarioId)

                _totalIngresos.value = ingresos
                _totalGastos.value = gastos
                _balance.value = balance
            } catch (e: Exception) {
                _error.value = "Error al cargar estadísticas: ${e.message}"
            }
        }
    }

    /**
     * Crear transacción
     */
    fun crearTransaccion(
        tipo: TipoTransaccion,
        monto: Double,
        categoriaId: String,
        categoriaNombre: String,
        categoriaEmoji: String,
        descripcion: String,
        fecha: Long = System.currentTimeMillis(),
        metodoPago: String = "Efectivo",
        notas: String = ""
    ) {
        viewModelScope.launch {
            _isLoading.value = true

            val nuevaTransaccion = Transaccion(
                usuarioId = usuarioId,
                tipo = tipo,
                monto = monto,
                categoriaId = categoriaId,
                categoriaNombre = categoriaNombre,
                categoriaEmoji = categoriaEmoji,
                descripcion = descripcion,
                fecha = fecha,
                metodoPago = metodoPago,
                notas = notas
            )

            transaccionRepository.crearTransaccion(nuevaTransaccion)
                .onSuccess {
                    _error.value = null
                    cargarEstadisticas()
                }
                .onFailure { e ->
                    _error.value = "Error al crear transacción: ${e.message}"
                }

            _isLoading.value = false
        }
    }

    /**
     * Actualizar transacción
     */
    fun actualizarTransaccion(transaccion: Transaccion) {
        viewModelScope.launch {
            _isLoading.value = true

            transaccionRepository.actualizarTransaccion(transaccion)
                .onSuccess {
                    _error.value = null
                    cargarEstadisticas()
                }
                .onFailure { e ->
                    _error.value = "Error al actualizar transacción: ${e.message}"
                }

            _isLoading.value = false
        }
    }

    /**
     * Eliminar transacción
     */
    fun eliminarTransaccion(transaccion: Transaccion) {
        viewModelScope.launch {
            _isLoading.value = true

            transaccionRepository.eliminarTransaccion(transaccion)
                .onSuccess {
                    _error.value = null
                    cargarEstadisticas()
                }
                .onFailure { e ->
                    _error.value = "Error al eliminar transacción: ${e.message}"
                }

            _isLoading.value = false
        }
    }

    /**
     * Filtrar por tipo
     */
    fun filtrarPorTipo(tipo: TipoTransaccion?) {
        _tipoFiltro.value = tipo
    }

    /**
     * Filtrar por categoría
     */
    fun filtrarPorCategoria(categoriaId: String?) {
        _categoriaFiltro.value = categoriaId
    }

    /**
     * Buscar transacciones
     */
    fun buscar(texto: String) {
        _busqueda.value = texto
    }

    /**
     * Filtrar por rango de fecha
     */
    fun filtrarPorFecha(fechaInicio: Long, fechaFin: Long) {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                transaccionRepository.obtenerTransaccionesPorRangoFecha(usuarioId, fechaInicio, fechaFin)
                    .catch { e ->
                        _error.value = "Error al filtrar: ${e.message}"
                    }
                    .collect { transacciones ->
                        _transacciones.value = transacciones
                        _isLoading.value = false
                    }
            } catch (e: Exception) {
                _error.value = "Error inesperado: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    /**
     * Limpiar filtros
     */
    fun limpiarFiltros() {
        _tipoFiltro.value = null
        _categoriaFiltro.value = null
        _busqueda.value = ""
        cargarTransacciones()
    }

    /**
     * Limpiar error
     */
    fun limpiarError() {
        _error.value = null
    }
}