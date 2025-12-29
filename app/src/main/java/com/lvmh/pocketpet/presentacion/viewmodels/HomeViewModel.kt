package com.lvmh.pocketpet.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lvmh.pocketpet.datos.repositorios.MascotaRepository
import com.lvmh.pocketpet.datos.repositorios.TransaccionRepository
import com.lvmh.pocketpet.datos.repositorios.UsuarioRepository
import com.lvmh.pocketpet.dominio.modelos.Mascota
import com.lvmh.pocketpet.dominio.modelos.Transaccion
import com.lvmh.pocketpet.dominio.modelos.TipoTransaccion
import com.lvmh.pocketpet.dominio.modelos.Usuario
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val transaccionRepository: TransaccionRepository,
    private val usuarioRepository: UsuarioRepository,
    private val mascotaRepository: MascotaRepository
) : ViewModel() {

    // Usuario actual
    private val _usuario = MutableStateFlow<Usuario?>(null)
    val usuario: StateFlow<Usuario?> = _usuario.asStateFlow()

    // Mascota del usuario
    private val _mascota = MutableStateFlow<Mascota?>(null)
    val mascota: StateFlow<Mascota?> = _mascota.asStateFlow()

    // Transacciones
    private val _transacciones = MutableStateFlow<List<Transaccion>>(emptyList())
    val transacciones: StateFlow<List<Transaccion>> = _transacciones.asStateFlow()

    // Últimas 5 transacciones para mostrar en home
    val transaccionesRecientes: StateFlow<List<Transaccion>> = _transacciones
        .map { it.take(5) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Estadísticas financieras
    private val _totalIngresos = MutableStateFlow(0.0)
    val totalIngresos: StateFlow<Double> = _totalIngresos.asStateFlow()

    private val _totalGastos = MutableStateFlow(0.0)
    val totalGastos: StateFlow<Double> = _totalGastos.asStateFlow()

    private val _saldoTotal = MutableStateFlow(0.0)
    val saldoTotal: StateFlow<Double> = _saldoTotal.asStateFlow()

    private val _cantidadTransacciones = MutableStateFlow(0)
    val cantidadTransacciones: StateFlow<Int> = _cantidadTransacciones.asStateFlow()

    // Estados de UI
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _mostrarNotificaciones = MutableStateFlow(false)
    val mostrarNotificaciones: StateFlow<Boolean> = _mostrarNotificaciones.asStateFlow()

    // Usuario ID
    private var usuarioId: String = ""

    /**
     * Inicializar con el ID del usuario
     */
    fun inicializar(userId: String) {
        usuarioId = userId
        cargarDatosCompletos()
    }

    /**
     * Cargar todos los datos necesarios para Home
     */
    private fun cargarDatosCompletos() {
        cargarUsuario()
        cargarMascota()
        cargarTransacciones()
        cargarEstadisticas()
    }

    /**
     * Cargar información del usuario
     */
    private fun cargarUsuario() {
        viewModelScope.launch {
            try {
                usuarioRepository.obtenerUsuario(usuarioId)
                    .catch { e ->
                        _error.value = "Error al cargar usuario: ${e.message}"
                    }
                    .collect { usuario ->
                        _usuario.value = usuario
                    }
            } catch (e: Exception) {
                _error.value = "Error al cargar usuario: ${e.message}"
            }
        }
    }

    /**
     * Cargar mascota
     */
    private fun cargarMascota() {
        viewModelScope.launch {
            try {
                mascotaRepository.obtenerMascota(usuarioId)
                    .catch { e ->
                        // No es crítico si falla, mascota es opcional
                    }
                    .collect { mascota ->
                        _mascota.value = mascota
                    }
            } catch (e: Exception) {
                // Ignorar error de mascota
            }
        }
    }

    /**
     * Cargar transacciones
     */
    private fun cargarTransacciones() {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                transaccionRepository.obtenerTransacciones(usuarioId)
                    .catch { e ->
                        _error.value = "Error al cargar transacciones: ${e.message}"
                    }
                    .collect { transacciones ->
                        _transacciones.value = transacciones
                        _cantidadTransacciones.value = transacciones.size
                        _isLoading.value = false
                    }
            } catch (e: Exception) {
                _error.value = "Error inesperado: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    /**
     * Cargar estadísticas financieras
     */
    private fun cargarEstadisticas() {
        viewModelScope.launch {
            try {
                // Obtener totales
                val ingresos = transaccionRepository.obtenerTotalPorTipo(usuarioId, TipoTransaccion.INGRESO)
                val gastos = transaccionRepository.obtenerTotalPorTipo(usuarioId, TipoTransaccion.GASTO)
                val saldo = transaccionRepository.obtenerBalance(usuarioId)

                _totalIngresos.value = ingresos
                _totalGastos.value = gastos
                _saldoTotal.value = saldo
            } catch (e: Exception) {
                _error.value = "Error al cargar estadísticas: ${e.message}"
            }
        }
    }

    /**
     * Crear nueva transacción desde Home
     */
    fun crearTransaccion(
        tipo: TipoTransaccion,
        monto: Double,
        concepto: String,
        categoriaId: String,
        categoriaNombre: String,
        categoriaEmoji: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true

            val nuevaTransaccion = Transaccion(
                usuarioId = usuarioId,
                tipo = tipo,
                monto = monto,
                descripcion = concepto,
                categoriaId = categoriaId,
                categoriaNombre = categoriaNombre,
                categoriaEmoji = categoriaEmoji,
                fecha = System.currentTimeMillis()
            )

            transaccionRepository.crearTransaccion(nuevaTransaccion)
                .onSuccess {
                    _error.value = null
                    // Las estadísticas se actualizarán automáticamente
                    // por el Flow de transacciones
                    cargarEstadisticas()

                    // Dar experiencia a la mascota por registrar transacción
                    _mascota.value?.let { mascota ->
                        mascotaRepository.ganarExperiencia(mascota.id, 5)
                    }
                }
                .onFailure { e ->
                    _error.value = "Error al crear transacción: ${e.message}"
                }

            _isLoading.value = false
        }
    }

    /**
     * Obtener transacciones filtradas por tipo
     */
    fun obtenerTransaccionesPorTipo(tipo: TipoTransaccion): Flow<List<Transaccion>> {
        return _transacciones.map { transacciones ->
            transacciones.filter { it.tipo == tipo }
        }
    }

    /**
     * Obtener transacciones del mes actual
     */
    fun obtenerTransaccionesMesActual(): Flow<List<Transaccion>> {
        return _transacciones.map { transacciones ->
            val ahora = System.currentTimeMillis()
            val calendar = java.util.Calendar.getInstance()
            calendar.timeInMillis = ahora

            val mesActual = calendar.get(java.util.Calendar.MONTH)
            val anioActual = calendar.get(java.util.Calendar.YEAR)

            transacciones.filter { transaccion ->
                calendar.timeInMillis = transaccion.fecha
                calendar.get(java.util.Calendar.MONTH) == mesActual &&
                        calendar.get(java.util.Calendar.YEAR) == anioActual
            }
        }
    }

    /**
     * Obtener estadísticas del mes actual
     */
    suspend fun obtenerEstadisticasMesActual(): Triple<Double, Double, Double> {
        val ahora = System.currentTimeMillis()
        val calendar = java.util.Calendar.getInstance()
        calendar.timeInMillis = ahora

        // Inicio del mes
        calendar.set(java.util.Calendar.DAY_OF_MONTH, 1)
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
        calendar.set(java.util.Calendar.MINUTE, 0)
        calendar.set(java.util.Calendar.SECOND, 0)
        val inicioMes = calendar.timeInMillis

        // Fin del mes
        calendar.add(java.util.Calendar.MONTH, 1)
        calendar.add(java.util.Calendar.MILLISECOND, -1)
        val finMes = calendar.timeInMillis

        val ingresos = transaccionRepository.obtenerTotalPorTipoYFecha(
            usuarioId,
            TipoTransaccion.INGRESO,
            inicioMes,
            finMes
        )

        val gastos = transaccionRepository.obtenerTotalPorTipoYFecha(
            usuarioId,
            TipoTransaccion.GASTO,
            inicioMes,
            finMes
        )

        val balance = ingresos - gastos

        return Triple(ingresos, gastos, balance)
    }

    /**
     * Toggle notificaciones
     */
    fun toggleNotificaciones() {
        _mostrarNotificaciones.value = !_mostrarNotificaciones.value
    }

    fun ocultarNotificaciones() {
        _mostrarNotificaciones.value = false
    }

    /**
     * Refrescar todos los datos
     */
    fun refrescar() {
        cargarDatosCompletos()
    }

    /**
     * Limpiar error
     */
    fun limpiarError() {
        _error.value = null
    }
}