package com.lvmh.pocketpet.presentacion.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lvmh.pocketpet.datos.repositorios.PresupuestoRepository
import com.lvmh.pocketpet.datos.repositorios.MetaRepository
import com.lvmh.pocketpet.dominio.modelos.Presupuesto
import com.lvmh.pocketpet.dominio.modelos.Meta
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import java.util.Calendar
import javax.inject.Inject

data class EstadoPresupuesto(
    val cargando: Boolean = false,
    val presupuestos: List<Presupuesto> = emptyList(),
    val metas: List<Meta> = emptyList(),
    val presupuestoSeleccionado: Presupuesto? = null,
    val metaSeleccionada: Meta? = null,
    val error: String? = null
)

@HiltViewModel
class PresupuestoViewModel @Inject constructor(
    private val presupuestoRepository: PresupuestoRepository,
    private val metaRepository: MetaRepository
) : ViewModel() {

    private val _estado = MutableStateFlow(EstadoPresupuesto())
    val estado: StateFlow<EstadoPresupuesto> = _estado.asStateFlow()

    private val _usuarioId = MutableStateFlow("")

    private var observandoPresupuestos = false

    fun inicializarUsuario() {
        val usuarioId = obtenerUsuarioActual()
        if (usuarioId.isNotEmpty()) {
            establecerUsuario(usuarioId)
        }
    }

    private fun obtenerUsuarioActual(): String {
        // TODO: Implementar tu lógica real de autenticación
        return "usuario_demo_123"
    }

    fun establecerUsuario(usuarioId: String) {
        _usuarioId.value = usuarioId
        cargarPresupuestos()
        cargarMetas()
    }

    private fun cargarPresupuestos() {
        if (observandoPresupuestos) return
        observandoPresupuestos = true

        Log.d("PresupuestoVM", "=== INICIANDO CARGA DE PRESUPUESTOS ===")
        Log.d("PresupuestoVM", "Usuario ID: ${_usuarioId.value}")

        viewModelScope.launch {
            _estado.update { it.copy(cargando = true, error = null) }
            try {
                // ✅ El Flow de Firebase emitirá automáticamente cuando haya cambios
                presupuestoRepository.obtenerPorUsuario(_usuarioId.value)
                    .catch { e ->
                        Log.e("PresupuestoVM", "❌ ERROR en Flow: ${e.message}", e)
                        _estado.update { it.copy(error = e.message, cargando = false) }
                    }
                    .collect { presupuestos ->
                        Log.d("PresupuestoVM", "✅ Presupuestos recibidos: ${presupuestos.size}")
                        presupuestos.forEach { p ->
                            Log.d("PresupuestoVM", "  - ${p.categoriaId}: S/${p.monto}")
                        }
                        _estado.update {
                            it.copy(
                                presupuestos = presupuestos,
                                cargando = false,
                                error = null
                            )
                        }
                    }
            } catch (e: Exception) {
                Log.e("PresupuestoVM", "❌ ERROR al cargar presupuestos: ${e.message}", e)
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
            Log.d("PresupuestoVM", "=== INICIANDO CREACIÓN DE PRESUPUESTO ===")
            _estado.update { it.copy(cargando = true, error = null) }

            try {
                // ✅ Obtener mes y año actuales
                val calendar = Calendar.getInstance()
                val mesActual = calendar.get(Calendar.MONTH) + 1 // Enero = 0, por eso +1
                val anioActual = calendar.get(Calendar.YEAR)

                // ✅ IMPORTANTE: Crear presupuesto con TODOS los campos
                val presupuesto = Presupuesto(
                    id = UUID.randomUUID().toString(),
                    usuarioId = _usuarioId.value,
                    categoriaId = categoriaId,
                    monto = monto,
                    gastado = 0.0, // ✅ Siempre iniciar en 0
                    periodo = periodo.lowercase(), // ✅ Firebase espera minúsculas
                    mesInicio = mesActual,
                    anioInicio = anioActual,
                    activo = true,
                    alertaEn = alertaEn,
                    fechaCreacion = System.currentTimeMillis()
                )

                Log.d("PresupuestoVM", "Usuario ID: ${_usuarioId.value}")
                Log.d("PresupuestoVM", "Presupuesto creado: $presupuesto")

                // ✅ Guardar en Firebase (automáticamente actualiza el Flow)
                presupuestoRepository.crear(presupuesto)

                Log.d("PresupuestoVM", "✅ Presupuesto guardado en Firebase exitosamente")

                // ✅ El Flow de Firebase detectará el cambio y actualizará la UI automáticamente
                // No necesitamos actualizar manualmente porque el listener de Firebase lo hace

                _estado.update { it.copy(cargando = false) }

            } catch (e: Exception) {
                Log.e("PresupuestoVM", "❌ ERROR al crear presupuesto: ${e.message}", e)
                _estado.update {
                    it.copy(
                        error = "Error al crear presupuesto: ${e.message}",
                        cargando = false
                    )
                }
            }
        }
    }

    fun actualizarPresupuesto(presupuesto: Presupuesto) {
        viewModelScope.launch {
            _estado.update { it.copy(cargando = true) }
            try {
                presupuestoRepository.actualizar(presupuesto)
                // ✅ Firebase Flow lo detectará automáticamente
                _estado.update { it.copy(cargando = false) }
            } catch (e: Exception) {
                _estado.update {
                    it.copy(
                        error = e.message ?: "Error al actualizar presupuesto",
                        cargando = false
                    )
                }
            }
        }
    }

    fun eliminarPresupuesto(presupuesto: Presupuesto) {
        viewModelScope.launch {
            _estado.update { it.copy(cargando = true) }
            try {
                presupuestoRepository.eliminar(presupuesto)
                // ✅ Firebase Flow lo detectará automáticamente
                _estado.update { it.copy(cargando = false) }
            } catch (e: Exception) {
                _estado.update {
                    it.copy(
                        error = e.message ?: "Error al eliminar presupuesto",
                        cargando = false
                    )
                }
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
            _estado.update { it.copy(cargando = true) }
            try {
                val meta = Meta(
                    id = UUID.randomUUID().toString(),
                    usuarioId = _usuarioId.value,
                    nombre = nombre,
                    descripcion = descripcion,
                    montoObjetivo = montoObjetivo,
                    montoActual = 0.0,
                    fechaLimite = fechaLimite,
                    categoriaId = categoriaId
                )
                metaRepository.crear(meta)
                _estado.update { it.copy(cargando = false) }
            } catch (e: Exception) {
                _estado.update {
                    it.copy(
                        error = e.message ?: "Error al crear meta",
                        cargando = false
                    )
                }
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
            _estado.update { it.copy(cargando = true) }
            try {
                metaRepository.eliminar(meta)
                _estado.update { it.copy(cargando = false) }
            } catch (e: Exception) {
                _estado.update {
                    it.copy(
                        error = e.message ?: "Error al eliminar meta",
                        cargando = false
                    )
                }
            }
        }
    }

    fun seleccionarPresupuesto(presupuesto: Presupuesto) {
        _estado.update { it.copy(presupuestoSeleccionado = presupuesto) }
    }

    fun seleccionarMeta(meta: Meta) {
        _estado.update { it.copy(metaSeleccionada = meta) }
    }

    // ✅ Función para limpiar errores
    fun limpiarError() {
        _estado.update { it.copy(error = null) }
    }
}