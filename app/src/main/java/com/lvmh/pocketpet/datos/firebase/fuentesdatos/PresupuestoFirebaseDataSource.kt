package com.lvmh.pocketpet.datos.firebase.fuentesdatos

import com.google.firebase.firestore.FirebaseFirestore
import com.lvmh.pocketpet.datos.firebase.modelos.PresupuestoFirebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PresupuestoFirebaseDataSource @Inject constructor(  // ⬅️ CAMBIAR ESTO
    private val firestore: FirebaseFirestore
) {

    private val coleccion = firestore.collection("presupuestos")

    fun obtenerPorUsuario(usuarioId: String): Flow<List<PresupuestoFirebase>> = callbackFlow {
        val listener = coleccion
            .whereEqualTo("usuario_id", usuarioId)
            .orderBy("fecha_creacion", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val presupuestos = snapshot?.documents?.mapNotNull {
                    it.toObject(PresupuestoFirebase::class.java)
                } ?: emptyList()
                trySend(presupuestos)
            }
        awaitClose { listener.remove() }
    }

    suspend fun obtenerPorId(id: String): PresupuestoFirebase? {
        return coleccion.document(id).get().await().toObject(PresupuestoFirebase::class.java)
    }

    suspend fun crear(presupuesto: PresupuestoFirebase) {
        coleccion.document(presupuesto.id).set(presupuesto).await()
    }

    suspend fun actualizar(presupuesto: PresupuestoFirebase) {
        coleccion.document(presupuesto.id).set(presupuesto).await()
    }

    suspend fun eliminar(id: String) {
        coleccion.document(id).delete().await()
    }
}