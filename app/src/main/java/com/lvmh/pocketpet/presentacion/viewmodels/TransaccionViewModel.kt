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

        println("💰 Balance: S/.${_balance.value} (Ingresos: S/.$ingresos, Gastos: S/.$gastos)")
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

                println("✅ [TransaccionVM] Transacción creada")

                // 🔥 ACTUALIZAR PRESUPUESTO si es un gasto
                if (tipo == TipoTransaccion.GASTO) {
                    actualizarPresupuesto(userId, categoriaId, monto, esNueva = true)
                }

            } catch (e: Exception) {
                println("❌ [TransaccionVM] Error: ${e.message}")
                _error.value = "Error al crear transacción: ${e.message}"
            }
        }
    }

    // 🔥 NUEVA FUNCIÓN: Actualizar presupuesto automáticamente
    private suspend fun actualizarPresupuesto(
        userId: String,
        categoriaId: String,
        monto: Double,
        esNueva: Boolean
    ) {
        try {
            println("🔵 [TransaccionVM] Actualizando presupuesto para categoría: $categoriaId")

            val presupuestos = firestore
                .collection("usuarios")
                .document(userId)
                .collection("presupuestos")
                .whereEqualTo("categoria_id", categoriaId)
                .whereEqualTo("activo", true)
                .get()
                .await()

            if (presupuestos.isEmpty) {
                println("ℹ️ [TransaccionVM] No hay presupuesto activo para esta categoría")
                return
            }

            presupuestos.documents.forEach { doc ->
                val gastoActual = doc.getDouble("gastado") ?: 0.0
                val nuevoGasto = if (esNueva) {
                    gastoActual + monto
                } else {
                    (gastoActual - monto).coerceAtLeast(0.0)
                }

                doc.reference.update("gastado", nuevoGasto).await()
                println("✅ [TransaccionVM] Presupuesto actualizado: gastado = S/.$nuevoGasto")

                // Verificar alertas
                val montoPresupuesto = doc.getDouble("monto") ?: 0.0
                val alertaEn = (doc.getLong("alerta_en") ?: 80).toInt()

                if (montoPresupuesto > 0 && esNueva) {
                    val porcentaje = (nuevoGasto / montoPresupuesto) * 100
                    if (porcentaje >= alertaEn) {
                        println("⚠️ [TransaccionVM] Alerta: ${porcentaje.toInt()}% del presupuesto usado")
                    }
                }
            }

        } catch (e: Exception) {
            println("❌ [TransaccionVM] Error actualizando presupuesto: ${e.message}")
        }
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

                // 🔥 ACTUALIZAR PRESUPUESTO si era un gasto
                if (transaccion.tipo == TipoTransaccion.GASTO) {
                    actualizarPresupuesto(userId, transaccion.categoriaId, transaccion.monto, esNueva = false)
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