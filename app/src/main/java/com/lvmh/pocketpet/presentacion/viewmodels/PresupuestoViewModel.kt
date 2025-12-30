package com.lvmh.pocketpet.presentacion.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.lvmh.pocketpet.dominio.modelos.Presupuesto
import com.lvmh.pocketpet.dominio.modelos.TipoCategoria
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import javax.inject.Inject

data class PresupuestoConInfo(
    val presupuesto: Presupuesto,
    val categoriaNombre: String = "",
    val categoriaEmoji: String = ""
)

data class EstadoPresupuesto(
    val presupuestos: List<PresupuestoConInfo> = emptyList(),
    val categorias: List<CategoriaInfo> = emptyList(),
    val cargando: Boolean = false,
    val error: String? = null,
    val mensajeExito: String? = null
)

data class CategoriaInfo(
    val id: String = "",
    val nombre: String = "",
    val emoji: String = "",
    val tipo: String = "GASTO"
)

@HiltViewModel
class PresupuestoViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _estado = MutableStateFlow(EstadoPresupuesto())
    val estado: StateFlow<EstadoPresupuesto> = _estado.asStateFlow()

    private var presupuestoListener: ListenerRegistration? = null
    private var categoriasListener: ListenerRegistration? = null
    private var transaccionesListener: ListenerRegistration? = null
    private var usuarioId: String? = null
    private var listenersActivos = false

    init {
        inicializarUsuario()
    }

    fun inicializarUsuario() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            if (usuarioId != userId) {
                usuarioId = userId
                iniciarListeners(userId)
            } else if (!listenersActivos) {
                iniciarListeners(userId)
            }
        }
    }

    private fun iniciarListeners(userId: String) {
        println("🔵 Iniciando listeners para usuario: $userId")
        listenersActivos = true
        escucharCategorias(userId)
        escucharPresupuestos(userId)
        escucharTransacciones(userId)
    }

    private fun escucharCategorias(userId: String) {
        categoriasListener?.remove()
        println("🔵 Iniciando listener de categorías")

        categoriasListener = firestore
            .collection("usuarios")
            .document(userId)
            .collection("categorias")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    println("❌ ERROR al escuchar categorías: ${error.message}")
                    return@addSnapshotListener
                }

                val categorias = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        CategoriaInfo(
                            id = doc.id,
                            nombre = doc.getString("nombre") ?: "",
                            emoji = doc.getString("emoji") ?: "📊",
                            tipo = doc.getString("tipo") ?: "GASTO"
                        )
                    } catch (e: Exception) {
                        println("❌ Error al parsear categoría: ${e.message}")
                        null
                    }
                } ?: emptyList()

                println("✅ Categorías cargadas: ${categorias.size}")
                categorias.forEach { cat ->
                    println("   - ${cat.emoji} ${cat.nombre} (${cat.tipo})")
                }

                _estado.value = _estado.value.copy(categorias = categorias)

                viewModelScope.launch {
                    recalcularPresupuestos(userId)
                }
            }
    }

    private fun escucharTransacciones(userId: String) {
        transaccionesListener?.remove()
        println("🔵 Iniciando listener de transacciones")

        transaccionesListener = firestore
            .collection("usuarios")
            .document(userId)
            .collection("transacciones")
            .whereEqualTo("tipo", "GASTO")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    println("❌ ERROR al escuchar transacciones: ${error.message}")
                    return@addSnapshotListener
                }

                println("🔵 Transacciones actualizadas, recalculando presupuestos...")
                viewModelScope.launch {
                    recalcularPresupuestos(userId)
                }
            }
    }

    private suspend fun recalcularPresupuestos(userId: String) {
        try {
            println("🔵 Iniciando recalculo de presupuestos")

            val presupuestosSnapshot = firestore
                .collection("usuarios")
                .document(userId)
                .collection("presupuestos")
                .whereEqualTo("activo", true)
                .get()
                .await()

            println("🔵 Presupuestos encontrados: ${presupuestosSnapshot.size()}")

            presupuestosSnapshot.documents.forEach { presupuestoDoc ->
                val categoriaId = presupuestoDoc.getString("categoria_id") ?: return@forEach
                val periodo = presupuestoDoc.getString("periodo") ?: "mensual"
                val mesInicio = (presupuestoDoc.getLong("mes_inicio") ?: 1).toInt()
                val anioInicio = (presupuestoDoc.getLong("anio_inicio") ?: Calendar.getInstance().get(Calendar.YEAR)).toInt()

                val (fechaInicio, fechaFin) = calcularRangoPeriodo(periodo, mesInicio, anioInicio)

                val transaccionesSnapshot = firestore
                    .collection("usuarios")
                    .document(userId)
                    .collection("transacciones")
                    .whereEqualTo("tipo", "GASTO")
                    .whereEqualTo("categoria_id", categoriaId)
                    .whereGreaterThanOrEqualTo("fecha", fechaInicio)
                    .whereLessThanOrEqualTo("fecha", fechaFin)
                    .get()
                    .await()

                val totalGastado = transaccionesSnapshot.documents.sumOf {
                    it.getDouble("monto") ?: 0.0
                }

                presupuestoDoc.reference.update("gastado", totalGastado).await()
                println("✅ Presupuesto ${presupuestoDoc.id} actualizado con gastado: $totalGastado")
            }
        } catch (e: Exception) {
            println("❌ ERROR al recalcular presupuestos: ${e.message}")
            e.printStackTrace()
        }
    }

    private fun calcularRangoPeriodo(periodo: String, mesInicio: Int, anioInicio: Int): Pair<Long, Long> {
        val calendario = Calendar.getInstance()

        calendario.set(Calendar.YEAR, anioInicio)
        calendario.set(Calendar.MONTH, mesInicio - 1)
        calendario.set(Calendar.DAY_OF_MONTH, 1)
        calendario.set(Calendar.HOUR_OF_DAY, 0)
        calendario.set(Calendar.MINUTE, 0)
        calendario.set(Calendar.SECOND, 0)
        calendario.set(Calendar.MILLISECOND, 0)
        val fechaInicio = calendario.timeInMillis

        when (periodo.lowercase()) {
            "semanal" -> calendario.add(Calendar.WEEK_OF_YEAR, 1)
            "mensual" -> calendario.add(Calendar.MONTH, 1)
            "trimestral" -> calendario.add(Calendar.MONTH, 3)
            "anual" -> calendario.add(Calendar.YEAR, 1)
            else -> calendario.add(Calendar.MONTH, 1)
        }
        calendario.add(Calendar.MILLISECOND, -1)
        val fechaFin = calendario.timeInMillis

        return Pair(fechaInicio, fechaFin)
    }

    private fun escucharPresupuestos(userId: String) {
        presupuestoListener?.remove()
        _estado.value = _estado.value.copy(cargando = true)
        println("🔵 Iniciando listener de presupuestos")

        presupuestoListener = firestore
            .collection("usuarios")
            .document(userId)
            .collection("presupuestos")
            .whereEqualTo("activo", true)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    println("❌ ERROR al escuchar presupuestos: ${error.message}")
                    _estado.value = _estado.value.copy(
                        cargando = false,
                        error = "Error al cargar presupuestos: ${error.message}"
                    )
                    return@addSnapshotListener
                }

                viewModelScope.launch {
                    val presupuestosConInfo = mutableListOf<PresupuestoConInfo>()

                    snapshot?.documents?.forEach { doc ->
                        try {
                            val presupuesto = Presupuesto(
                                id = doc.id,
                                usuarioId = doc.getString("usuario_id") ?: userId,
                                categoriaId = doc.getString("categoria_id") ?: "",
                                monto = doc.getDouble("monto") ?: 0.0,
                                gastado = doc.getDouble("gastado") ?: 0.0,
                                periodo = doc.getString("periodo") ?: "mensual",
                                mesInicio = (doc.getLong("mes_inicio") ?: 1).toInt(),
                                anioInicio = (doc.getLong("anio_inicio") ?: Calendar.getInstance().get(Calendar.YEAR)).toInt(),
                                activo = doc.getBoolean("activo") ?: true,
                                alertaEn = (doc.getLong("alerta_en") ?: 80).toInt(),
                                fechaCreacion = doc.getLong("fecha_creacion") ?: System.currentTimeMillis()
                            )

                            val categoriaInfo = _estado.value.categorias.find { it.id == presupuesto.categoriaId }

                            presupuestosConInfo.add(
                                PresupuestoConInfo(
                                    presupuesto = presupuesto,
                                    categoriaNombre = categoriaInfo?.nombre ?: "Sin categoría",
                                    categoriaEmoji = categoriaInfo?.emoji ?: "📊"
                                )
                            )
                        } catch (e: Exception) {
                            println("❌ ERROR al parsear presupuesto: ${e.message}")
                        }
                    }

                    println("✅ Presupuestos con info: ${presupuestosConInfo.size}")

                    _estado.value = _estado.value.copy(
                        presupuestos = presupuestosConInfo,
                        cargando = false,
                        error = null
                    )
                }
            }
    }

    // ✅ NUEVA FUNCIÓN: Crear categoría desde el diálogo
    fun crearCategoria(
        nombre: String,
        emoji: String,
        tipo: String,
        onSuccess: (String) -> Unit
    ) {
        val userId = usuarioId ?: run {
            _estado.value = _estado.value.copy(error = "Usuario no autenticado")
            return
        }

        viewModelScope.launch {
            try {
                println("🔵 Creando categoría: $nombre ($emoji) - Tipo: $tipo")

                val categoriaData = hashMapOf(
                    "nombre" to nombre,
                    "emoji" to emoji,
                    "tipo" to tipo.uppercase(),
                    "fecha_creacion" to System.currentTimeMillis(),
                    "usuario_id" to userId
                )

                val docRef = firestore
                    .collection("usuarios")
                    .document(userId)
                    .collection("categorias")
                    .add(categoriaData)
                    .await()

                println("✅ Categoría creada con ID: ${docRef.id}")
                onSuccess(docRef.id)

            } catch (e: Exception) {
                println("❌ ERROR al crear categoría: ${e.message}")
                _estado.value = _estado.value.copy(
                    error = "Error al crear categoría: ${e.message}"
                )
            }
        }
    }

    fun crearPresupuesto(
        categoriaId: String,
        monto: Double,
        periodo: String,
        alertaEn: Int = 80
    ) {
        val userId = usuarioId ?: run {
            _estado.value = _estado.value.copy(error = "Usuario no autenticado")
            return
        }

        viewModelScope.launch {
            try {
                _estado.value = _estado.value.copy(cargando = true)
                println("🔵 Creando presupuesto para categoría: $categoriaId")

                val categoriaExiste = firestore
                    .collection("usuarios")
                    .document(userId)
                    .collection("categorias")
                    .document(categoriaId)
                    .get()
                    .await()
                    .exists()

                if (!categoriaExiste) {
                    _estado.value = _estado.value.copy(
                        cargando = false,
                        error = "La categoría no existe"
                    )
                    return@launch
                }

                val presupuestoExistente = firestore
                    .collection("usuarios")
                    .document(userId)
                    .collection("presupuestos")
                    .whereEqualTo("categoria_id", categoriaId)
                    .whereEqualTo("activo", true)
                    .get()
                    .await()

                if (!presupuestoExistente.isEmpty) {
                    _estado.value = _estado.value.copy(
                        cargando = false,
                        error = "Ya existe un presupuesto activo para esta categoría"
                    )
                    return@launch
                }

                val calendario = Calendar.getInstance()
                val mesActual = calendario.get(Calendar.MONTH) + 1
                val anioActual = calendario.get(Calendar.YEAR)

                val (fechaInicio, fechaFin) = calcularRangoPeriodo(periodo.lowercase(), mesActual, anioActual)

                val transaccionesExistentes = firestore
                    .collection("usuarios")
                    .document(userId)
                    .collection("transacciones")
                    .whereEqualTo("tipo", "GASTO")
                    .whereEqualTo("categoria_id", categoriaId)
                    .get()
                    .await()

                val gastoInicial = transaccionesExistentes.documents
                    .filter { doc ->
                        val fecha = doc.getLong("fecha") ?: 0L
                        fecha in fechaInicio..fechaFin
                    }
                    .sumOf { it.getDouble("monto") ?: 0.0 }

                println("🔵 Gasto inicial calculado: $gastoInicial")

                val presupuestoData = hashMapOf(
                    "usuario_id" to userId,
                    "categoria_id" to categoriaId,
                    "monto" to monto,
                    "gastado" to gastoInicial,
                    "periodo" to periodo.lowercase(),
                    "mes_inicio" to mesActual,
                    "anio_inicio" to anioActual,
                    "activo" to true,
                    "alerta_en" to alertaEn,
                    "fecha_creacion" to System.currentTimeMillis()
                )

                firestore
                    .collection("usuarios")
                    .document(userId)
                    .collection("presupuestos")
                    .add(presupuestoData)
                    .await()

                val mensajeGasto = if (gastoInicial > 0) {
                    " (Ya has gastado S/. ${String.format("%.2f", gastoInicial)})"
                } else ""

                _estado.value = _estado.value.copy(
                    cargando = false,
                    mensajeExito = "✅ Presupuesto creado$mensajeGasto",
                    error = null
                )

            } catch (e: Exception) {
                println("❌ ERROR al crear presupuesto: ${e.message}")
                _estado.value = _estado.value.copy(
                    cargando = false,
                    error = "❌ Error: ${e.message}"
                )
            }
        }
    }

    suspend fun actualizarGastoPresupuesto(
        categoriaId: String,
        monto: Double,
        esNuevaTransaccion: Boolean = true
    ) {
        val userId = usuarioId ?: return

        try {
            println("🔵 actualizarGasto: catId=$categoriaId, monto=$monto, esNueva=$esNuevaTransaccion")

            val presupuestosSnapshot = firestore
                .collection("usuarios")
                .document(userId)
                .collection("presupuestos")
                .whereEqualTo("categoria_id", categoriaId)
                .whereEqualTo("activo", true)
                .get()
                .await()

            if (presupuestosSnapshot.isEmpty) {
                println("🔵 No hay presupuesto activo para esta categoría")
                return
            }

            presupuestosSnapshot.documents.forEach { doc ->
                val gastoActual = doc.getDouble("gastado") ?: 0.0
                val nuevoGasto = if (esNuevaTransaccion) {
                    gastoActual + monto
                } else {
                    (gastoActual - monto).coerceAtLeast(0.0)
                }

                println("🔵 Gastado actual: $gastoActual, Nuevo gastado: $nuevoGasto")
                doc.reference.update("gastado", nuevoGasto).await()

                val montoPresupuesto = doc.getDouble("monto") ?: 0.0
                val alertaEn = (doc.getLong("alerta_en") ?: 80).toInt()

                if (montoPresupuesto > 0) {
                    val porcentaje = (nuevoGasto / montoPresupuesto) * 100
                    val categoriaNombre = _estado.value.categorias.find { it.id == categoriaId }?.nombre ?: "Categoría"

                    if (esNuevaTransaccion && porcentaje >= alertaEn) {
                        _estado.value = _estado.value.copy(
                            mensajeExito = "⚠️ Has usado el ${porcentaje.toInt()}% de tu presupuesto de $categoriaNombre"
                        )
                    }
                }
            }

        } catch (e: Exception) {
            println("❌ ERROR al actualizar gasto: ${e.message}")
            e.printStackTrace()
        }
    }

    fun eliminarPresupuesto(presupuestoId: String) {
        val userId = usuarioId ?: return

        viewModelScope.launch {
            try {
                _estado.value = _estado.value.copy(cargando = true)

                firestore
                    .collection("usuarios")
                    .document(userId)
                    .collection("presupuestos")
                    .document(presupuestoId)
                    .update("activo", false)
                    .await()

                _estado.value = _estado.value.copy(
                    cargando = false,
                    mensajeExito = "✅ Presupuesto eliminado",
                    error = null
                )

            } catch (e: Exception) {
                _estado.value = _estado.value.copy(
                    cargando = false,
                    error = "❌ Error: ${e.message}"
                )
            }
        }
    }

    fun obtenerCategoriasParaPresupuesto(): List<CategoriaInfo> {
        val categorias = _estado.value.categorias.filter {
            it.tipo == TipoCategoria.GASTO.name
        }
        println("🔵 Categorías de GASTO disponibles: ${categorias.size}")
        return categorias
    }

    fun limpiarMensajes() {
        _estado.value = _estado.value.copy(
            error = null,
            mensajeExito = null
        )
    }

    fun pausarListeners() {
        println("🔵 Pausando listeners")
        listenersActivos = false
        presupuestoListener?.remove()
        categoriasListener?.remove()
        transaccionesListener?.remove()
    }

    fun reanudarListeners() {
        val userId = usuarioId
        if (userId != null && !listenersActivos) {
            println("🔵 Reanudando listeners")
            iniciarListeners(userId)
        }
    }

    override fun onCleared() {
        super.onCleared()
        println("🔵 ViewModel limpiado, removiendo listeners")
        listenersActivos = false
        presupuestoListener?.remove()
        categoriasListener?.remove()
        transaccionesListener?.remove()
    }
}