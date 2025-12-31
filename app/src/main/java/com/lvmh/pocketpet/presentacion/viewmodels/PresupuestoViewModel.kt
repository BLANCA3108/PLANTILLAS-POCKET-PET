package com.lvmh.pocketpet.presentacion.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.lvmh.pocketpet.dominio.modelos.Presupuesto
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
    val cargando: Boolean = false,
    val error: String? = null,
    val mensajeExito: String? = null
)

@HiltViewModel
class PresupuestoViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _estado = MutableStateFlow(EstadoPresupuesto())
    val estado: StateFlow<EstadoPresupuesto> = _estado.asStateFlow()

    private var presupuestoListener: ListenerRegistration? = null
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
        println("🔵 [PresupuestoVM] Iniciando listeners para usuario: $userId")
        listenersActivos = true
        escucharPresupuestos(userId)
        escucharTransacciones(userId)
    }

    private fun escucharTransacciones(userId: String) {
        transaccionesListener?.remove()
        println("🔵 [PresupuestoVM] Iniciando listener de transacciones")

        transaccionesListener = firestore
            .collection("usuarios")
            .document(userId)
            .collection("transacciones")
            .whereEqualTo("tipo", "GASTO")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    println("❌ [PresupuestoVM] ERROR al escuchar transacciones: ${error.message}")
                    return@addSnapshotListener
                }

                println("🔵 [PresupuestoVM] Transacciones actualizadas (${snapshot?.size() ?: 0}), recalculando presupuestos...")
                snapshot?.documents?.forEach {
                    println("   📝 Transacción: ${it.getString("categoria_nombre")} - S/.${it.getDouble("monto")}")
                }

                viewModelScope.launch {
                    recalcularTodosLosPresupuestos(userId)
                }
            }
    }

    // 🔥 FUNCIÓN CORREGIDA: Sin índices compuestos - Filtra en memoria
    private suspend fun recalcularTodosLosPresupuestos(userId: String) {
        try {
            println("🔵 [PresupuestoVM] 🔥 RECALCULANDO TODOS LOS PRESUPUESTOS")

            val presupuestosSnapshot = firestore
                .collection("usuarios")
                .document(userId)
                .collection("presupuestos")
                .whereEqualTo("activo", true)
                .get()
                .await()

            println("🔵 [PresupuestoVM] Presupuestos activos encontrados: ${presupuestosSnapshot.size()}")

            presupuestosSnapshot.documents.forEach { presupuestoDoc ->
                val categoriaId = presupuestoDoc.getString("categoria_id") ?: return@forEach
                val categoriaNombre = presupuestoDoc.getString("categoria_nombre") ?: "Sin nombre"
                val periodo = presupuestoDoc.getString("periodo") ?: "mensual"
                val mesInicio = (presupuestoDoc.getLong("mes_inicio") ?: 1).toInt()
                val anioInicio = (presupuestoDoc.getLong("anio_inicio") ?: Calendar.getInstance().get(Calendar.YEAR)).toInt()

                println("   🔍 Procesando presupuesto: $categoriaNombre (ID: $categoriaId)")

                val (fechaInicio, fechaFin) = calcularRangoPeriodo(periodo, mesInicio, anioInicio)
                println("   📅 Rango: $fechaInicio a $fechaFin")

                // 🔥 OBTENER TODAS LAS TRANSACCIONES DE ESTA CATEGORÍA (sin filtro de fecha)
                val transaccionesSnapshot = firestore
                    .collection("usuarios")
                    .document(userId)
                    .collection("transacciones")
                    .whereEqualTo("tipo", "GASTO")
                    .whereEqualTo("categoria_id", categoriaId)
                    .get()
                    .await()

                println("   📦 Transacciones totales obtenidas: ${transaccionesSnapshot.size()}")

                // 🔥 FILTRAR EN MEMORIA las que están en el período
                val totalGastado = transaccionesSnapshot.documents
                    .mapNotNull { doc ->
                        val fecha = doc.getLong("fecha") ?: 0L
                        val monto = doc.getDouble("monto") ?: 0.0
                        if (fecha in fechaInicio..fechaFin) {
                            println("      ✅ Transacción incluida: fecha=$fecha, monto=S/.$monto")
                            monto
                        } else {
                            println("      ❌ Transacción excluida: fecha=$fecha (fuera de rango)")
                            null
                        }
                    }
                    .sum()

                println("   💰 Total gastado calculado: S/.$totalGastado")

                // 🔥 ACTUALIZAR EL PRESUPUESTO EN FIRESTORE
                presupuestoDoc.reference.update(
                    mapOf(
                        "gastado" to totalGastado,
                        "ultima_actualizacion" to System.currentTimeMillis()
                    )
                ).await()

                println("   ✅ Presupuesto actualizado: $categoriaNombre - Gastado: S/.$totalGastado")
            }

            println("✅ [PresupuestoVM] 🎉 TODOS LOS PRESUPUESTOS RECALCULADOS")

        } catch (e: Exception) {
            println("❌ [PresupuestoVM] ERROR al recalcular presupuestos: ${e.message}")
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
        println("🔵 [PresupuestoVM] Iniciando listener de presupuestos")

        presupuestoListener = firestore
            .collection("usuarios")
            .document(userId)
            .collection("presupuestos")
            .whereEqualTo("activo", true)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    println("❌ [PresupuestoVM] ERROR al escuchar presupuestos: ${error.message}")
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

                            val categoriaDoc = firestore
                                .collection("usuarios")
                                .document(userId)
                                .collection("categorias")
                                .document(presupuesto.categoriaId)
                                .get()
                                .await()

                            val categoriaNombre = categoriaDoc.getString("nombre") ?: "Sin categoría"
                            val categoriaEmoji = categoriaDoc.getString("emoji") ?: "📊"

                            println("   📊 Presupuesto cargado: $categoriaEmoji $categoriaNombre - Gastado: S/.${presupuesto.gastado}/${presupuesto.monto}")

                            presupuestosConInfo.add(
                                PresupuestoConInfo(
                                    presupuesto = presupuesto,
                                    categoriaNombre = categoriaNombre,
                                    categoriaEmoji = categoriaEmoji
                                )
                            )
                        } catch (e: Exception) {
                            println("❌ [PresupuestoVM] ERROR al parsear presupuesto: ${e.message}")
                        }
                    }

                    println("✅ [PresupuestoVM] Presupuestos con info actualizados: ${presupuestosConInfo.size}")

                    _estado.value = _estado.value.copy(
                        presupuestos = presupuestosConInfo,
                        cargando = false,
                        error = null
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
                println("🔵 [PresupuestoVM] Creando presupuesto para categoría: $categoriaId")

                val categoriaDoc = firestore
                    .collection("usuarios")
                    .document(userId)
                    .collection("categorias")
                    .document(categoriaId)
                    .get()
                    .await()

                if (!categoriaDoc.exists()) {
                    _estado.value = _estado.value.copy(
                        cargando = false,
                        error = "La categoría no existe"
                    )
                    return@launch
                }

                val categoriaNombre = categoriaDoc.getString("nombre") ?: "Sin nombre"

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

                // 🔥 OBTENER TODAS las transacciones de esta categoría (sin filtro de fecha)
                val transaccionesExistentes = firestore
                    .collection("usuarios")
                    .document(userId)
                    .collection("transacciones")
                    .whereEqualTo("tipo", "GASTO")
                    .whereEqualTo("categoria_id", categoriaId)
                    .get()
                    .await()

                // 🔥 FILTRAR EN MEMORIA las que están en el período actual
                val gastoInicial = transaccionesExistentes.documents
                    .mapNotNull { doc ->
                        val fecha = doc.getLong("fecha") ?: 0L
                        val monto = doc.getDouble("monto") ?: 0.0
                        if (fecha in fechaInicio..fechaFin) monto else null
                    }
                    .sum()

                println("🔵 [PresupuestoVM] Gasto inicial calculado: S/.$gastoInicial (${transaccionesExistentes.size()} transacciones)")

                val presupuestoData = hashMapOf(
                    "usuario_id" to userId,
                    "categoria_id" to categoriaId,
                    "categoria_nombre" to categoriaNombre,
                    "monto" to monto,
                    "gastado" to gastoInicial,
                    "periodo" to periodo.lowercase(),
                    "mes_inicio" to mesActual,
                    "anio_inicio" to anioActual,
                    "activo" to true,
                    "alerta_en" to alertaEn,
                    "fecha_creacion" to System.currentTimeMillis(),
                    "ultima_actualizacion" to System.currentTimeMillis()
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
                println("❌ [PresupuestoVM] ERROR al crear presupuesto: ${e.message}")
                _estado.value = _estado.value.copy(
                    cargando = false,
                    error = "❌ Error: ${e.message}"
                )
            }
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

    fun limpiarMensajes() {
        _estado.value = _estado.value.copy(
            error = null,
            mensajeExito = null
        )
    }

    fun pausarListeners() {
        println("🔵 [PresupuestoVM] Pausando listeners")
        listenersActivos = false
        presupuestoListener?.remove()
        transaccionesListener?.remove()
    }

    fun reanudarListeners() {
        val userId = usuarioId
        if (userId != null && !listenersActivos) {
            println("🔵 [PresupuestoVM] Reanudando listeners")
            iniciarListeners(userId)
        }
    }

    override fun onCleared() {
        super.onCleared()
        println("🔵 [PresupuestoVM] ViewModel limpiado, removiendo listeners")
        listenersActivos = false
        presupuestoListener?.remove()
        transaccionesListener?.remove()
    }
}