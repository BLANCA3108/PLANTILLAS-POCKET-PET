package com.lvmh.pocketpet.datos.firebase.fuentesdatos

import com.google.firebase.firestore.FirebaseFirestore
import com.lvmh.pocketpet.datos.firebase.modelos.MascotaFirebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MascotaFirebaseDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    private val collection = firestore.collection("mascotas")

    /**
     * Obtener mascota del usuario en tiempo real
     */
    fun obtenerMascota(usuarioId: String): Flow<MascotaFirebase?> = callbackFlow {
        val listener = collection
            .whereEqualTo("usuario_id", usuarioId)
            .limit(1)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val mascota = snapshot?.documents?.firstOrNull()?.let { doc ->
                    doc.toObject(MascotaFirebase::class.java)?.copy(id = doc.id)
                }

                trySend(mascota)
            }

        awaitClose { listener.remove() }
    }

    /**
     * Obtener mascota una sola vez
     */
    suspend fun obtenerMascotaSync(usuarioId: String): MascotaFirebase? {
        return try {
            val snapshot = collection
                .whereEqualTo("usuario_id", usuarioId)
                .limit(1)
                .get()
                .await()

            snapshot.documents.firstOrNull()?.let { doc ->
                doc.toObject(MascotaFirebase::class.java)?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Crear mascota
     */
    suspend fun crearMascota(mascota: MascotaFirebase): Result<String> {
        return try {
            val docRef = if (mascota.id.isEmpty()) {
                collection.document()
            } else {
                collection.document(mascota.id)
            }

            docRef.set(mascota.copy(id = docRef.id)).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Actualizar mascota
     */
    suspend fun actualizarMascota(mascota: MascotaFirebase): Result<Unit> {
        return try {
            collection.document(mascota.id).set(mascota).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Actualizar estadísticas de la mascota
     */
    suspend fun actualizarEstadisticas(
        mascotaId: String,
        salud: Float,
        felicidad: Float,
        hambre: Float
    ): Result<Unit> {
        return try {
            collection.document(mascotaId)
                .update(
                    mapOf(
                        "salud" to salud,
                        "felicidad" to felicidad,
                        "hambre" to hambre,
                        "ultima_interaccion" to System.currentTimeMillis()
                    )
                )
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Actualizar nivel y experiencia
     */
    suspend fun actualizarNivel(mascotaId: String, nivel: Int, experiencia: Int): Result<Unit> {
        return try {
            collection.document(mascotaId)
                .update(
                    mapOf(
                        "nivel" to nivel,
                        "experiencia" to experiencia
                    )
                )
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Alimentar mascota
     */
    suspend fun alimentarMascota(mascotaId: String): Result<Unit> {
        return try {
            collection.document(mascotaId)
                .update(
                    mapOf(
                        "hambre" to 0.0f,
                        "salud" to 1.0f,
                        "felicidad" to 1.0f,
                        "ultima_alimentacion" to System.currentTimeMillis(),
                        "ultima_interaccion" to System.currentTimeMillis()
                    )
                )
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Ganar experiencia
     */
    suspend fun ganarExperiencia(mascotaId: String, cantidad: Int): Result<Unit> {
        return try {
            val mascota = collection.document(mascotaId).get().await()
                .toObject(MascotaFirebase::class.java) ?: return Result.failure(Exception("Mascota no encontrada"))

            var nuevaExp = mascota.experiencia + cantidad
            var nuevoNivel = mascota.nivel
            val expRequerida = nuevoNivel * 100

            // Subir de nivel si es necesario
            if (nuevaExp >= expRequerida) {
                nuevoNivel++
                nuevaExp -= expRequerida
            }

            actualizarNivel(mascotaId, nuevoNivel, nuevaExp)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Eliminar mascota
     */
    suspend fun eliminarMascota(mascotaId: String): Result<Unit> {
        return try {
            collection.document(mascotaId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}