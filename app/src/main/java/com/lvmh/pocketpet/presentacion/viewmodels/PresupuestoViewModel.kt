package com.lvmh.pocketpet.presentacion.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lvmh.pocketpet.datos.repositorios.PresupuestoRepository
import com.lvmh.pocketpet.datos.repositorios.MetaRepository
import com.lvmh.pocketpet.dominio.modelos.Presupuesto
import com.lvmh.pocketpet.dominio.modelos.Meta
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

data class EstadoPresupuesto(
    val cargando: Boolean = false,
    val presupuestos: List<Presupuesto> = emptyList(),
    val metas: List<Meta> = emptyList(),
    val presupuestoSeleccionado: Presupuesto? = null,
    val metaSeleccionada: Meta? = null,
    val error: String? = null
)

class PresupuestoViewModel(
    private val presupuestoRepository: PresupuestoRepository,
    private val metaRepository: MetaRepository
) : ViewModel() {

    private val _estado = MutableStateFlow(EstadoPresupuesto())
    val estado: StateFlow<EstadoPresupuesto> = _estado.asStateFlow()

    private val _usuarioId = MutableStateFlow("")

    fun establecerUsuario(usuarioId: String) {
        _usuarioId.value = usuarioId
        cargarPresupuestos()
        cargarMetas()
    }

    private fun cargarPresupuestos() {
        viewModelScope.launch {
            _estado.update { it.copy(cargando = true, error = null) }
            try {
                presupuestoRepository.obtenerPorUsuario(_usuarioId.value)
                    .catch { e ->
                        _estado.update { it.copy(error = e.message, cargando = false) }
                    }
                    .collect { presupuestos ->
                        _estado.update { it.copy(presupuestos = presupuestos, cargando = false) }
                    }
            } catch (e: Exception) {
                _estado.update {
                    it.copy(cargando = false, error = e.message ?: "Error al cargar presupuestos")
                }
            }
        }
    }

    private fun cargarMetas() {
        viewModelScope.launch {
            try {
                metaRepository.obtenerPorUsuario(_usuarioId.value)
                    .catch { e ->
                        _estado.update { it.copy(error = e.message) }
                    }
                    .collect { metas ->
                        _estado.update { it.copy(metas = metas) }
                    }
            } catch (e: Exception) {
                _estado.update { it.copy(error = e.message ?: "Error al cargar metas") }
            }
        }
    }

    fun crearPresupuesto(
        categoriaId: String,
        monto: Double,
        periodo: String,
        alertaEn: Int
    ) {
        viewModelScope.launch {
            try {
                val presupuesto = Presupuesto(
                    id = UUID.randomUUID().toString(),
                    usuarioId = _usuarioId.value,
                    categoriaId = categoriaId,
                    monto = monto,
                    periodo = periodo,
                    alertaEn = alertaEn
                )
                presupuestoRepository.crear(presupuesto)
            } catch (e: Exception) {
                _estado.update { it.copy(error = e.message ?: "Error al crear presupuesto") }
            }
        }
    }

    fun actualizarPresupuesto(presupuesto: Presupuesto) {
        viewModelScope.launch {
            try {
                presupuestoRepository.actualizar(presupuesto)
            } catch (e: Exception) {
                _estado.update { it.copy(error = e.message ?: "Error al actualizar presupuesto") }
            }
        }
    }

    fun eliminarPresupuesto(presupuesto: Presupuesto) {
        viewModelScope.launch {
            try {
                presupuestoRepository.eliminar(presupuesto)
            } catch (e: Exception) {
                _estado.update { it.copy(error = e.message ?: "Error al eliminar presupuesto") }
            }
        }
    }

    fun crearMeta(
        nombre: String,
        descripcion: String,
        montoObjetivo: Double,
        fechaLimite: Long,
        categoriaId: String
    ) {
        viewModelScope.launch {
            try {
                val meta = Meta(
                    id = UUID.randomUUID().toString(),
                    usuarioId = _usuarioId.value,
                    nombre = nombre,
                    descripcion = descripcion,
                    montoObjetivo = montoObjetivo,
                    fechaLimite = fechaLimite,
                    categoriaId = categoriaId
                )
                metaRepository.crear(meta)
            } catch (e: Exception) {
                _estado.update { it.copy(error = e.message ?: "Error al crear meta") }
            }
        }
    }

    fun agregarMontoMeta(metaId: String, monto: Double) {
        viewModelScope.launch {
            try {
                metaRepository.agregarMonto(metaId, monto)
            } catch (e: Exception) {
                _estado.update { it.copy(error = e.message ?: "Error al agregar monto") }
            }
        }
    }

    fun eliminarMeta(meta: Meta) {
        viewModelScope.launch {
            try {
                metaRepository.eliminar(meta)
            } catch (e: Exception) {
                _estado.update { it.copy(error = e.message ?: "Error al eliminar meta") }
            }
        }
    }

    fun seleccionarPresupuesto(presupuesto: Presupuesto) {
        _estado.update { it.copy(presupuestoSeleccionado = presupuesto) }
    }

    fun seleccionarMeta(meta: Meta) {
        _estado.update { it.copy(metaSeleccionada = meta) }
    }
}
