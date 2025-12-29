package com.lvmh.pocketpet.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lvmh.pocketpet.datos.repositorios.MascotaRepository
import com.lvmh.pocketpet.datos.repositorios.TransaccionRepository
import com.lvmh.pocketpet.datos.repositorios.UsuarioRepository
import com.lvmh.pocketpet.dominio.modelos.Mascota
import com.lvmh.pocketpet.dominio.modelos.TipoTransaccion
import com.lvmh.pocketpet.dominio.modelos.Usuario
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PerfilViewModel @Inject constructor(
    private val usuarioRepository: UsuarioRepository,
    private val mascotaRepository: MascotaRepository,
    private val transaccionRepository: TransaccionRepository
) : ViewModel() {

    // Estado del usuario
    private val _usuario = MutableStateFlow<Usuario?>(null)
    val usuario: StateFlow<Usuario?> = _usuario.asStateFlow()

    // Estado de la mascota
    private val _mascota = MutableStateFlow<Mascota?>(null)
    val mascota: StateFlow<Mascota?> = _mascota.asStateFlow()

    // Estadísticas financieras
    private val _totalIngresos = MutableStateFlow(0.0)
    val totalIngresos: StateFlow<Double> = _totalIngresos.asStateFlow()

    private val _totalGastos = MutableStateFlow(0.0)
    val totalGastos: StateFlow<Double> = _totalGastos.asStateFlow()

    private val _balance = MutableStateFlow(0.0)
    val balance: StateFlow<Double> = _balance.asStateFlow()

    private val _totalTransacciones = MutableStateFlow(0)
    val totalTransacciones: StateFlow<Int> = _totalTransacciones.asStateFlow()

    // Estado de carga
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Estado de error
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // Usuario ID
    private var usuarioId: String = ""

    fun inicializar(userId: String) {
        usuarioId = userId
        cargarUsuario()
        cargarMascota()
        cargarEstadisticas()
    }

    /**
     * Cargar usuario
     */
    private fun cargarUsuario() {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                usuarioRepository.obtenerUsuario(usuarioId)
                    .catch { e ->
                        _error.value = "Error al cargar usuario: ${e.message}"
                    }
                    .collect { usuario ->
                        _usuario.value = usuario
                        _isLoading.value = false
                    }
            } catch (e: Exception) {
                _error.value = "Error inesperado: ${e.message}"
                _isLoading.value = false
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
                        _error.value = "Error al cargar mascota: ${e.message}"
                    }
                    .collect { mascota ->
                        _mascota.value = mascota
                    }
            } catch (e: Exception) {
                _error.value = "Error al cargar mascota: ${e.message}"
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
                val balance = transaccionRepository.obtenerBalance(usuarioId)

                _totalIngresos.value = ingresos
                _totalGastos.value = gastos
                _balance.value = balance

                // Obtener cantidad de transacciones
                transaccionRepository.obtenerTransacciones(usuarioId)
                    .catch { }
                    .collect { transacciones ->
                        _totalTransacciones.value = transacciones.size
                    }
            } catch (e: Exception) {
                _error.value = "Error al cargar estadísticas: ${e.message}"
            }
        }
    }

    /**
     * Actualizar nombre de usuario
     */
    fun actualizarNombre(nuevoNombre: String) {
        viewModelScope.launch {
            _isLoading.value = true

            usuarioRepository.actualizarNombre(usuarioId, nuevoNombre)
                .onSuccess {
                    _error.value = null
                }
                .onFailure { e ->
                    _error.value = "Error al actualizar nombre: ${e.message}"
                }

            _isLoading.value = false
        }
    }

    /**
     * Actualizar avatar
     */
    fun actualizarAvatar(avatarUrl: String?) {
        viewModelScope.launch {
            _isLoading.value = true

            usuarioRepository.actualizarAvatar(usuarioId, avatarUrl)
                .onSuccess {
                    _error.value = null
                }
                .onFailure { e ->
                    _error.value = "Error al actualizar avatar: ${e.message}"
                }

            _isLoading.value = false
        }
    }

    /**
     * Actualizar usuario completo
     */
    fun actualizarUsuario(usuario: Usuario) {
        viewModelScope.launch {
            _isLoading.value = true

            usuarioRepository.actualizarUsuario(usuario)
                .onSuccess {
                    _error.value = null
                }
                .onFailure { e ->
                    _error.value = "Error al actualizar usuario: ${e.message}"
                }

            _isLoading.value = false
        }
    }

    /**
     * Alimentar mascota
     */
    fun alimentarMascota() {
        viewModelScope.launch {
            val mascotaActual = _mascota.value ?: return@launch

            mascotaRepository.alimentarMascota(mascotaActual.id)
                .onSuccess {
                    // Ganar experiencia por cuidar la mascota
                    mascotaRepository.ganarExperiencia(mascotaActual.id, 10)
                    _error.value = null
                }
                .onFailure { e ->
                    _error.value = "Error al alimentar mascota: ${e.message}"
                }
        }
    }

    /**
     * Interactuar con mascota (aumenta felicidad)
     */
    fun interactuarConMascota() {
        viewModelScope.launch {
            val mascotaActual = _mascota.value ?: return@launch

            val nuevaFelicidad = (mascotaActual.felicidad + 0.1f).coerceAtMost(1.0f)

            mascotaRepository.actualizarEstadisticas(
                mascotaId = mascotaActual.id,
                salud = mascotaActual.salud,
                felicidad = nuevaFelicidad,
                hambre = mascotaActual.hambre
            )
                .onSuccess {
                    // Ganar experiencia por interactuar
                    mascotaRepository.ganarExperiencia(mascotaActual.id, 5)
                    _error.value = null
                }
                .onFailure { e ->
                    _error.value = "Error al interactuar: ${e.message}"
                }
        }
    }

    /**
     * Actualizar mascota
     */
    fun actualizarMascota(mascota: Mascota) {
        viewModelScope.launch {
            _isLoading.value = true

            mascotaRepository.actualizarMascota(mascota)
                .onSuccess {
                    _error.value = null
                }
                .onFailure { e ->
                    _error.value = "Error al actualizar mascota: ${e.message}"
                }

            _isLoading.value = false
        }
    }

    /**
     * Refrescar datos
     */
    fun refrescar() {
        cargarUsuario()
        cargarMascota()
        cargarEstadisticas()
    }

    /**
     * Limpiar error
     */
    fun limpiarError() {
        _error.value = null
    }
}