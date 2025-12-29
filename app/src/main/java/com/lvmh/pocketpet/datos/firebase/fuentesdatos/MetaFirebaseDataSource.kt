package com.lvmh.pocketpet.datos.firebase.fuentesdatos

import com.google.firebase.firestore.FirebaseFirestore
import com.lvmh.pocketpet.datos.firebase.modelos.MetaFirebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MetaFirebaseDataSource @Inject constructor(  // ⬅️ CAMBIAR ESTO
    private val firestore: FirebaseFirestore
) {

    private val coleccion = firestore.collection("metas")

    fun obtenerPorUsuario(usuarioId: String): Flow<List<MetaFirebase>> = callbackFlow {
        val listener = coleccion
            .whereEqualTo("usuario_id", usuarioId)
            .orderBy("fecha_creacion", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val metas = snapshot?.documents?.mapNotNull {
                    it.toObject(MetaFirebase::class.java)
                } ?: emptyList()
                trySend(metas)
            }
        awaitClose { listener.remove() }
    }

    suspend fun obtenerPorId(id: String): MetaFirebase? {
        return coleccion.document(id).get().await().toObject(MetaFirebase::class.java)
    }

    suspend fun crear(meta: MetaFirebase) {
        coleccion.document(meta.id).set(meta).await()
    }

    suspend fun actualizar(meta: MetaFirebase) {
        coleccion.document(meta.id).set(meta).await()
    }

    suspend fun eliminar(id: String) {
        coleccion.document(id).delete().await()
    }
}