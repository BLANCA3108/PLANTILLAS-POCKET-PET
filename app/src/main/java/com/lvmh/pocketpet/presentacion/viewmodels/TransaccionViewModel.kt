package com.lvmh.pocketpet.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.lvmh.pocketpet.dominio.modelos.Transaccion
import com.lvmh.pocketpet.dominio.modelos.TipoTransaccion
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class TransaccionViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _transaccionesFiltradas = MutableStateFlow<List<Transaccion>>(emptyList())
    val transaccionesFiltradas: StateFlow<List<Transaccion>> = _transaccionesFiltradas.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _balance = MutableStateFlow(0.0)
    val balance: StateFlow<Double> = _balance.asStateFlow()

    private val _totalIngresos = MutableStateFlow(0.0)
    val totalIngresos: StateFlow<Double> = _totalIngresos.asStateFlow()

    private val _totalGastos = MutableStateFlow(0.0)
    val totalGastos: StateFlow<Double> = _totalGastos.asStateFlow()

    private var transaccionesListener: ListenerRegistration? = null
    private var usuarioId: String? = null

    fun inicializar() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            usuarioId = userId
            escucharTransacciones(userId)
        } else {
            _error.value = "Usuario no autenticado"
        }
    }

    private fun escucharTransacciones(userId: String) {
        transaccionesListener?.remove()
        _isLoading.value = true

        println("🔵 [TransaccionVM] Escuchando transacciones del usuario: $userId")

        transaccionesListener = firestore
            .collection("usuarios")
            .document(userId)
            .collection("transacciones")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    println("❌ [TransaccionVM] Error: ${error.message}")
                    _error.value = "Error al cargar transacciones: ${error.message}"
                    _isLoading.value = false
                    return@addSnapshotListener
                }

                val transacciones = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        Transaccion(
                            id = doc.id,
                            usuarioId = doc.getString("usuario_id") ?: userId,
                            tipo = TipoTransaccion.valueOf(doc.getString("tipo") ?: "GASTO"),
                            monto = doc.getDouble("monto") ?: 0.0,
                            categoriaId = doc.getString("categoria_id") ?: "",
                            categoriaNombre = doc.getString("categoria_nombre") ?: "",
                            categoriaEmoji = doc.getString("categoria_emoji") ?: "💰",
                            descripcion = doc.getString("descripcion") ?: "",
                            fecha = doc.getLong("fecha") ?: System.currentTimeMillis()
                        )
                    } catch (e: Exception) {
                        println("❌ [TransaccionVM] Error parseando: ${e.message}")
                        null
                    }
                } ?: emptyList()

                println("✅ [TransaccionVM] Transacciones cargadas: ${transacciones.size}")

                _transaccionesFiltradas.value = transacciones.sortedByDescending { it.fecha }
                calcularTotales(transacciones)
                _isLoading.value = false
            }
    }

    private fun calcularTotales(transacciones: List<Transaccion>) {
        val ingresos = transacciones
            .filter { it.tipo == TipoTransaccion.INGRESO }
            .sumOf { it.monto }

        val gastos = transacciones
            .filter { it.tipo == TipoTransaccion.GASTO }
            .sumOf { it.monto }

        _totalIngresos.value = ingresos
        _totalGastos.value = gastos
        _balance.value = ingresos - gastos

        println("💰 [TransaccionVM] Balance: S/.${_balance.value} (Ingresos: S/.$ingresos, Gastos: S/.$gastos)")
    }

    fun crearTransaccion(
        tipo: TipoTransaccion,
        monto: Double,
        categoriaId: String,
        categoriaNombre: String,
        categoriaEmoji: String,
        descripcion: String
    ) {
        val userId = usuarioId ?: return

        viewModelScope.launch {
            try {
                println("🔵 [TransaccionVM] Creando transacción: $tipo - S/.$monto - $categoriaNombre")

                val transaccionData = hashMapOf(
                    "usuario_id" to userId,
                    "tipo" to tipo.name,
                    "monto" to monto,
                    "categoria_id" to categoriaId,
                    "categoria_nombre" to categoriaNombre,
                    "categoria_emoji" to categoriaEmoji,
                    "descripcion" to descripcion,
                    "fecha" to System.currentTimeMillis()
                )

                firestore
                    .collection("usuarios")
                    .document(userId)
                    .collection("transacciones")
                    .add(transaccionData)
                    .await()

                println("✅ [TransaccionVM] Transacción creada exitosamente")

                // 🔥 SI ES GASTO, ACTUALIZAR PRESUPUESTOS INMEDIATAMENTE
                if (tipo == TipoTransaccion.GASTO) {
                    println("🔥 [TransaccionVM] Es un GASTO, actualizando presupuestos...")
                    actualizarPresupuestosDeLaCategoria(userId, categoriaId)
                }

            } catch (e: Exception) {
                println("❌ [TransaccionVM] Error: ${e.message}")
                _error.value = "Error al crear transacción: ${e.message}"
            }
        }
    }

    // 🔥 FUNCIÓN SIN ÍNDICES COMPUESTOS - Filtra en memoria
    private suspend fun actualizarPresupuestosDeLaCategoria(userId: String, categoriaId: String) {
        try {
            println("🔥 [TransaccionVM] Buscando presupuestos activos para categoría: $categoriaId")

            // Buscar TODOS los presupuestos activos de esta categoría
            val presupuestosSnapshot = firestore
                .collection("usuarios")
                .document(userId)
                .collection("presupuestos")
                .whereEqualTo("categoria_id", categoriaId)
                .whereEqualTo("activo", true)
                .get()
                .await()

            println("🔥 [TransaccionVM] Presupuestos encontrados: ${presupuestosSnapshot.size()}")

            if (presupuestosSnapshot.isEmpty) {
                println("ℹ️ [TransaccionVM] No hay presupuestos activos para esta categoría")
                return
            }

            // Para cada presupuesto encontrado
            presupuestosSnapshot.documents.forEach { presupuestoDoc ->
                val presupuestoId = presupuestoDoc.id
                val periodo = presupuestoDoc.getString("periodo") ?: "mensual"
                val mesInicio = (presupuestoDoc.getLong("mes_inicio") ?: 1).toInt()
                val anioInicio = (presupuestoDoc.getLong("anio_inicio") ?: Calendar.getInstance().get(Calendar.YEAR)).toInt()

                println("   📊 Procesando presupuesto ID: $presupuestoId - Período: $periodo")

                // Calcular el rango de fechas del período
                val (fechaInicio, fechaFin) = calcularRangoPeriodo(periodo, mesInicio, anioInicio)
                println("   📅 Rango: $fechaInicio a $fechaFin")

                // 🔥 OBTENER TODAS LAS TRANSACCIONES DE GASTO DE ESTA CATEGORÍA (sin filtro de fecha)
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

                println("   ✅ Presupuesto ID $presupuestoId actualizado con gastado = S/.$totalGastado")
            }

            println("✅ [TransaccionVM] 🎉 TODOS los presupuestos de la categoría actualizados")

        } catch (e: Exception) {
            println("❌ [TransaccionVM] Error actualizando presupuestos: ${e.message}")
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

    fun eliminarTransaccion(transaccion: Transaccion) {
        val userId = usuarioId ?: return

        viewModelScope.launch {
            try {
                println("🔵 [TransaccionVM] Eliminando transacción: ${transaccion.id}")

                firestore
                    .collection("usuarios")
                    .document(userId)
                    .collection("transacciones")
                    .document(transaccion.id)
                    .delete()
                    .await()

                println("✅ [TransaccionVM] Transacción eliminada")

                // 🔥 SI ERA UN GASTO, ACTUALIZAR PRESUPUESTOS
                if (transaccion.tipo == TipoTransaccion.GASTO) {
                    println("🔥 [TransaccionVM] Era un GASTO, actualizando presupuestos...")
                    actualizarPresupuestosDeLaCategoria(userId, transaccion.categoriaId)
                }

            } catch (e: Exception) {
                println("❌ [TransaccionVM] Error: ${e.message}")
                _error.value = "Error al eliminar transacción: ${e.message}"
            }
        }
    }

    fun limpiarError() {
        _error.value = null
    }

    override fun onCleared() {
        super.onCleared()
        transaccionesListener?.remove()
    }
}