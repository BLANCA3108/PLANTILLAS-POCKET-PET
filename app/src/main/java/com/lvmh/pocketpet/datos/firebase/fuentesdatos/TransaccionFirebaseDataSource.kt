package com.lvmh.pocketpet.datos.firebase.fuentesdatos

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.lvmh.pocketpet.datos.firebase.modelos.TransaccionFirebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransaccionFirebaseDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    private val collection = firestore.collection("transacciones")

    // Obtener todas las transacciones del usuario
    fun obtenerTransacciones(usuarioId: String): Flow<List<TransaccionFirebase>> = callbackFlow {
        val listener = collection
            .whereEqualTo("usuario_id", usuarioId)
            .orderBy("fecha", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val transacciones = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(TransaccionFirebase::class.java)?.copy(id = doc.id)
                } ?: emptyList()

                trySend(transacciones)
            }

        awaitClose { listener.remove() }
    }

    // Obtener transacción por ID
    suspend fun obtenerTransaccionPorId(id: String): TransaccionFirebase? {
        return try {
            val doc = collection.document(id).get().await()
            doc.toObject(TransaccionFirebase::class.java)?.copy(id = doc.id)
        } catch (e: Exception) {
            null
        }
    }

    // Crear transacción
    suspend fun crearTransaccion(transaccion: TransaccionFirebase): Result<String> {
        return try {
            val docRef = if (transaccion.id.isEmpty()) {
                collection.document()
            } else {
                collection.document(transaccion.id)
            }

            docRef.set(transaccion.copy(id = docRef.id)).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Actualizar transacción
    suspend fun actualizarTransaccion(transaccion: TransaccionFirebase): Result<Unit> {
        return try {
            collection.document(transaccion.id).set(transaccion).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Eliminar transacción
    suspend fun eliminarTransaccion(id: String): Result<Unit> {
        return try {
            collection.document(id).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Obtener transacciones por tipo
    fun obtenerTransaccionesPorTipo(usuarioId: String, tipo: String): Flow<List<TransaccionFirebase>> = callbackFlow {
        val listener = collection
            .whereEqualTo("usuario_id", usuarioId)
            .whereEqualTo("tipo", tipo)
            .orderBy("fecha", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val transacciones = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(TransaccionFirebase::class.java)?.copy(id = doc.id)
                } ?: emptyList()

                trySend(transacciones)
            }

        awaitClose { listener.remove() }
    }

    // Obtener transacciones por categoría
    fun obtenerTransaccionesPorCategoria(usuarioId: String, categoriaId: String): Flow<List<TransaccionFirebase>> = callbackFlow {
        val listener = collection
            .whereEqualTo("usuario_id", usuarioId)
            .whereEqualTo("categoria_id", categoriaId)
            .orderBy("fecha", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val transacciones = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(TransaccionFirebase::class.java)?.copy(id = doc.id)
                } ?: emptyList()

                trySend(transacciones)
            }

        awaitClose { listener.remove() }
    }

    // Obtener transacciones por rango de fecha
    fun obtenerTransaccionesPorRangoFecha(
        usuarioId: String,
        fechaInicio: Long,
        fechaFin: Long
    ): Flow<List<TransaccionFirebase>> = callbackFlow {
        val listener = collection
            .whereEqualTo("usuario_id", usuarioId)
            .whereGreaterThanOrEqualTo("fecha", fechaInicio)
            .whereLessThanOrEqualTo("fecha", fechaFin)
            .orderBy("fecha", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val transacciones = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(TransaccionFirebase::class.java)?.copy(id = doc.id)
                } ?: emptyList()

                trySend(transacciones)
            }

        awaitClose { listener.remove() }
    }
}