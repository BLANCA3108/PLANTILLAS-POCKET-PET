package com.lvmh.pocketpet.datos.firebase.fuentesdatos

import com.google.firebase.firestore.FirebaseFirestore
import com.lvmh.pocketpet.datos.firebase.modelos.ConfiguracionFirebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ConfiguracionFirebaseDataSource(
    private val firestore: FirebaseFirestore
) {
    private val collection = firestore.collection("configuraciones")

    fun obtenerConfiguracionPorUsuario(userId: String): Flow<ConfiguracionFirebase?> = callbackFlow {
        val listener = collection
            .whereEqualTo("userId", userId)
            .limit(1)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val configuracion = snapshot?.documents?.firstOrNull()?.let { doc ->
                    ConfiguracionFirebase.fromMap(doc.data ?: emptyMap()).copy(id = doc.id)
                }
                trySend(configuracion)
            }

        awaitClose { listener.remove() }
    }

    suspend fun obtenerConfiguracionPorUsuarioSuspend(userId: String): ConfiguracionFirebase? {
        return try {
            val snapshot = collection
                .whereEqualTo("userId", userId)
                .limit(1)
                .get()
                .await()

            snapshot.documents.firstOrNull()?.let { doc ->
                ConfiguracionFirebase.fromMap(doc.data ?: emptyMap()).copy(id = doc.id)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun obtenerConfiguracionPorId(id: String): ConfiguracionFirebase? {
        return try {
            val doc = collection.document(id).get().await()
            if (doc.exists()) {
                ConfiguracionFirebase.fromMap(doc.data ?: emptyMap()).copy(id = doc.id)
            } else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun guardarConfiguracion(configuracion: ConfiguracionFirebase): Result<String> {
        return try {
            val docRef = if (configuracion.id.isNotEmpty()) {
                collection.document(configuracion.id)
            } else {
                collection.document()
            }

            val configuracionConId = configuracion.copy(
                id = docRef.id,
                fechaActualizacion = System.currentTimeMillis()
            )

            docRef.set(configuracionConId.toMap()).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun actualizarConfiguracion(configuracion: ConfiguracionFirebase): Result<Unit> {
        return try {
            val updates = configuracion.copy(
                fechaActualizacion = System.currentTimeMillis()
            ).toMap()

            collection.document(configuracion.id).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun eliminarConfiguracion(id: String): Result<Unit> {
        return try {
            collection.document(id).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun actualizarNotificaciones(userId: String, activas: Boolean): Result<Unit> {
        return try {
            val snapshot = collection.whereEqualTo("userId", userId).limit(1).get().await()
            val docId = snapshot.documents.firstOrNull()?.id ?: return Result.failure(Exception("Configuración no encontrada"))

            collection.document(docId).update(
                mapOf(
                    "notificacionesActivas" to activas,
                    "fechaActualizacion" to System.currentTimeMillis()
                )
            ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun actualizarMoneda(userId: String, moneda: String): Result<Unit> {
        return try {
            val snapshot = collection.whereEqualTo("userId", userId).limit(1).get().await()
            val docId = snapshot.documents.firstOrNull()?.id ?: return Result.failure(Exception("Configuración no encontrada"))

            collection.document(docId).update(
                mapOf(
                    "monedaPredeterminada" to moneda,
                    "fechaActualizacion" to System.currentTimeMillis()
                )
            ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}