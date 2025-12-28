package com.lvmh.pocketpet.datos.firebase.fuentesdatos

import com.google.firebase.firestore.FirebaseFirestore
import com.lvmh.pocketpet.datos.firebase.modelos.UsuarioFirebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UsuarioFirebaseDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    private val collection = firestore.collection("usuarios")

    /**
     * Obtener usuario en tiempo real
     */
    fun obtenerUsuario(usuarioId: String): Flow<UsuarioFirebase?> = callbackFlow {
        val listener = collection.document(usuarioId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val usuario = snapshot?.toObject(UsuarioFirebase::class.java)?.copy(id = snapshot.id)
                trySend(usuario)
            }

        awaitClose { listener.remove() }
    }

    /**
     * Obtener usuario una sola vez
     */
    suspend fun obtenerUsuarioSync(usuarioId: String): UsuarioFirebase? {
        return try {
            val doc = collection.document(usuarioId).get().await()
            doc.toObject(UsuarioFirebase::class.java)?.copy(id = doc.id)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Crear usuario
     */
    suspend fun crearUsuario(usuario: UsuarioFirebase): Result<String> {
        return try {
            val docRef = if (usuario.id.isEmpty()) {
                collection.document()
            } else {
                collection.document(usuario.id)
            }

            docRef.set(usuario.copy(id = docRef.id)).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Actualizar usuario
     */
    suspend fun actualizarUsuario(usuario: UsuarioFirebase): Result<Unit> {
        return try {
            collection.document(usuario.id).set(usuario).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Actualizar solo el nombre
     */
    suspend fun actualizarNombre(usuarioId: String, nombre: String): Result<Unit> {
        return try {
            collection.document(usuarioId)
                .update("nombre", nombre)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Actualizar solo el avatar
     */
    suspend fun actualizarAvatar(usuarioId: String, avatarUrl: String?): Result<Unit> {
        return try {
            collection.document(usuarioId)
                .update("avatar_url", avatarUrl)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Eliminar usuario
     */
    suspend fun eliminarUsuario(usuarioId: String): Result<Unit> {
        return try {
            collection.document(usuarioId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}