package com.lvmh.pocketpet.presentacion.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lvmh.pocketpet.dominio.modelos.Meta
import com.lvmh.pocketpet.datos.repositorios.MetaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MetaEstado(
    val metas: List<Meta> = emptyList(),
    val metasActivas: List<Meta> = emptyList(),
    val metasCompletadas: List<Meta> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val usuarioId: String? = null
)

@HiltViewModel
class MetaViewModel @Inject constructor(
    private val metaRepository: MetaRepository
) : ViewModel() {

    private val _estado = MutableStateFlow(MetaEstado())
    val estado: StateFlow<MetaEstado> = _estado.asStateFlow()

    private var metasObserverJob: Job? = null
    private var metasActivasObserverJob: Job? = null
    private var metasCompletadasObserverJob: Job? = null

    /**
     * Configura el ViewModel con el usuario actual
     */
    fun configurarUsuario(usuarioId: String) {
        if (_estado.value.usuarioId == usuarioId) return

        _estado.update { it.copy(usuarioId = usuarioId) }

        // Detener observaciones anteriores si existen
        metasObserverJob?.cancel()
        metasActivasObserverJob?.cancel()
        metasCompletadasObserverJob?.cancel()

        // Observar todas las metas
        metasObserverJob = viewModelScope.launch {
            try {
                _estado.update { it.copy(isLoading = true, error = null) }
                metaRepository.obtenerPorUsuario(usuarioId).collectLatest { metas ->
                    _estado.update { it.copy(metas = metas, isLoading = false) }
                }
            } catch (e: Exception) {
                // En caso de error, cargar desde cache
                cargarDesdeCache(usuarioId)
                _estado.update {
                    it.copy(
                        error = "Error al cargar metas: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }

        // Observar metas activas
        metasActivasObserverJob = viewModelScope.launch {
            metaRepository.obtenerActivas(usuarioId).collectLatest { metas ->
                _estado.update { it.copy(metasActivas = metas) }
            }
        }

        // Observar metas completadas
        metasCompletadasObserverJob = viewModelScope.launch {
            metaRepository.obtenerCompletadas(usuarioId).collectLatest { metas ->
                _estado.update { it.copy(metasCompletadas = metas) }
            }
        }
    }

    /**
     * Carga las metas desde la cache local
     */
    private fun cargarDesdeCache(usuarioId: String) {
        viewModelScope.launch {
            try {
                metaRepository.obtenerDesdeCache(usuarioId).collectLatest { metas ->
                    _estado.update { it.copy(metas = metas) }
                }
            } catch (e: Exception) {
                _estado.update {
                    it.copy(error = "Error al cargar desde cache: ${e.message}")
                }
            }
        }
    }

    /**
     * Crea una nueva meta
     */
    fun crearMeta(
        nombre: String,
        descripcion: String = "",
        montoObjetivo: Double,
        fechaLimite: Long,
        categoriaId: String = "",
        iconoUrl: String = ""
    ) {
        val usuarioId = _estado.value.usuarioId ?: return

        viewModelScope.launch {
            try {
                val meta = Meta(
                    id = System.currentTimeMillis().toString(),
                    usuarioId = usuarioId,
                    nombre = nombre,
                    descripcion = descripcion,
                    montoObjetivo = montoObjetivo,
                    montoActual = 0.0,
                    fechaInicio = System.currentTimeMillis(),
                    fechaLimite = fechaLimite,
                    categoriaId = categoriaId,
                    iconoUrl = iconoUrl,
                    completada = false,
                    fechaCompletada = null
                )

                metaRepository.crear(meta)
                _estado.update { it.copy(error = null) }
            } catch (e: Exception) {
                _estado.update {
                    it.copy(error = "Error al crear meta: ${e.message}")
                }
            }
        }
    }

    /**
     * Actualiza una meta existente
     */
    fun actualizarMeta(meta: Meta) {
        viewModelScope.launch {
            try {
                metaRepository.actualizar(meta)
                _estado.update { it.copy(error = null) }
            } catch (e: Exception) {
                _estado.update {
                    it.copy(error = "Error al actualizar meta: ${e.message}")
                }
            }
        }
    }

    /**
     * Elimina una meta
     */
    fun eliminarMeta(meta: Meta) {
        viewModelScope.launch {
            try {
                metaRepository.eliminar(meta)
                _estado.update { it.copy(error = null) }
            } catch (e: Exception) {
                _estado.update {
                    it.copy(error = "Error al eliminar meta: ${e.message}")
                }
            }
        }
    }

    /**
     * Agrega monto a una meta específica
     */
    fun agregarMontoMeta(metaId: String, monto: Double) {
        viewModelScope.launch {
            try {
                metaRepository.agregarMonto(metaId, monto)
                _estado.update { it.copy(error = null) }
            } catch (e: Exception) {
                _estado.update {
                    it.copy(error = "Error al agregar monto: ${e.message}")
                }
            }
        }
    }

    /**
     * Marca una meta como completada
     */
    fun marcarComoCompletada(metaId: String) {
        viewModelScope.launch {
            try {
                val meta = metaRepository.obtenerPorId(metaId)
                meta?.let {
                    val metaActualizada = it.copy(
                        completada = true,
                        fechaCompletada = System.currentTimeMillis(),
                        montoActual = it.montoObjetivo // Asegurar que esté completo
                    )
                    metaRepository.actualizar(metaActualizada)
                }
                _estado.update { it.copy(error = null) }
            } catch (e: Exception) {
                _estado.update {
                    it.copy(error = "Error al marcar como completada: ${e.message}")
                }
            }
        }
    }

    /**
     * Obtiene una meta por su ID
     */
    fun obtenerMetaPorId(metaId: String): Meta? {
        return _estado.value.metas.find { it.id == metaId }
    }

    /**
     * Reinicia una meta completada
     */
    fun reiniciarMeta(metaId: String) {
        viewModelScope.launch {
            try {
                val meta = metaRepository.obtenerPorId(metaId)
                meta?.let {
                    val metaActualizada = it.copy(
                        completada = false,
                        fechaCompletada = null,
                        montoActual = 0.0,
                        fechaInicio = System.currentTimeMillis()
                    )
                    metaRepository.actualizar(metaActualizada)
                }
                _estado.update { it.copy(error = null) }
            } catch (e: Exception) {
                _estado.update {
                    it.copy(error = "Error al reiniciar meta: ${e.message}")
                }
            }
        }
    }

    /**
     * Limpia los errores del estado
     */
    fun limpiarError() {
        _estado.update { it.copy(error = null) }
    }

    /**
     * Actualiza manualmente las metas
     */
    fun refrescarMetas() {
        val usuarioId = _estado.value.usuarioId ?: return
        configurarUsuario(usuarioId)
    }

    /**
     * Cierra las observaciones al destruir el ViewModel
     */
    override fun onCleared() {
        super.onCleared()
        metasObserverJob?.cancel()
        metasActivasObserverJob?.cancel()
        metasCompletadasObserverJob?.cancel()
    }
}