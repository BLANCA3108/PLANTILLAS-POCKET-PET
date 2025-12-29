package com.lvmh.pocketpet.datos.firebase.fuentesdatos

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.lvmh.pocketpet.datos.firebase.modelos.CategoriaFirebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoriaFirebaseDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    private val collection = firestore.collection("categorias")

    // Obtener categorías en tiempo real
    fun obtenerCategorias(usuarioId: String): Flow<List<CategoriaFirebase>> = callbackFlow {
        val listener = collection
            .whereEqualTo("usuario_id", usuarioId)
            .whereEqualTo("activa", true)
            .orderBy("fecha_creacion", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val categorias = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(CategoriaFirebase::class.java)?.copy(id = doc.id)
                } ?: emptyList()

                trySend(categorias)
            }

        awaitClose { listener.remove() }
    }

    // Obtener categoría por ID
    suspend fun obtenerCategoriaPorId(id: String): CategoriaFirebase? {
        return try {
            val doc = collection.document(id).get().await()
            doc.toObject(CategoriaFirebase::class.java)?.copy(id = doc.id)
        } catch (e: Exception) {
            null
        }
    }

    // Crear categoría
    suspend fun crearCategoria(categoria: CategoriaFirebase): Result<String> {
        return try {
            val docRef = if (categoria.id.isEmpty()) {
                collection.document()
            } else {
                collection.document(categoria.id)
            }

            docRef.set(categoria.copy(id = docRef.id)).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Actualizar categoría
    suspend fun actualizarCategoria(categoria: CategoriaFirebase): Result<Unit> {
        return try {
            collection.document(categoria.id).set(categoria).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Actualizar solo el gasto
    suspend fun actualizarGastado(id: String, gastado: Double): Result<Unit> {
        return try {
            collection.document(id)
                .update("gastado", gastado)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Desactivar categoría (soft delete)
    suspend fun desactivarCategoria(id: String): Result<Unit> {
        return try {
            collection.document(id)
                .update("activa", false)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Eliminar categoría (hard delete)
    suspend fun eliminarCategoria(id: String): Result<Unit> {
        return try {
            collection.document(id).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Obtener categorías por tipo
    fun obtenerCategoriasPorTipo(usuarioId: String, tipo: String): Flow<List<CategoriaFirebase>> = callbackFlow {
        val listener = collection
            .whereEqualTo("usuario_id", usuarioId)
            .whereEqualTo("tipo", tipo)
            .whereEqualTo("activa", true)
            .orderBy("fecha_creacion", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val categorias = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(CategoriaFirebase::class.java)?.copy(id = doc.id)
                } ?: emptyList()

                trySend(categorias)
            }

        awaitClose { listener.remove() }
    }
}