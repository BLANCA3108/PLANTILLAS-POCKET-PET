package com.lvmh.pocketpet.datos.repositorios

import com.lvmh.pocketpet.datos.firebase.fuentesdatos.CategoriaFirebaseDataSource
import com.lvmh.pocketpet.datos.local.dao.CategoriaDao
import com.lvmh.pocketpet.datos.mapeadores.CategoriaMapper
import com.lvmh.pocketpet.dominio.modelos.Categoria
import com.lvmh.pocketpet.dominio.modelos.TipoCategoria
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoriaRepository @Inject constructor(
    private val firebaseDataSource: CategoriaFirebaseDataSource,
    private val categoriaDao: CategoriaDao
) {

    /**
     * Obtener categorías con sincronización Firebase → Room
     */
    fun obtenerCategorias(usuarioId: String): Flow<List<Categoria>> {
        // Primero intenta desde Firebase y sincroniza con Room
        return firebaseDataSource.obtenerCategorias(usuarioId)
            .map { firebaseList ->
                val categorias = CategoriaMapper.fromFirebaseList(firebaseList)
                // Sincronizar con Room
                categoriaDao.insertarVarias(CategoriaMapper.toEntityList(categorias))
                categorias
            }
            .catch { error ->
                // Si falla Firebase, usar Room como fallback
                categoriaDao.obtenerCategorias(usuarioId)
                    .map { CategoriaMapper.fromEntityList(it) }
                    .collect { emit(it) }
            }
    }

    /**
     * Obtener categorías solo desde Room (offline-first)
     */
    fun obtenerCategoriasLocal(usuarioId: String): Flow<List<Categoria>> {
        return categoriaDao.obtenerCategorias(usuarioId)
            .map { CategoriaMapper.fromEntityList(it) }
    }

    /**
     * Obtener categoría por ID
     */
    suspend fun obtenerCategoriaPorId(id: String): Categoria? {
        // Intentar desde Firebase primero
        val firebase = firebaseDataSource.obtenerCategoriaPorId(id)
        return if (firebase != null) {
            val categoria = CategoriaMapper.fromFirebase(firebase)
            // Guardar en Room
            categoriaDao.insertar(CategoriaMapper.toEntity(categoria))
            categoria
        } else {
            // Fallback a Room
            categoriaDao.obtenerCategoriaPorId(id)?.let {
                CategoriaMapper.fromEntity(it)
            }
        }
    }

    /**
     * Crear nueva categoría
     */
    suspend fun crearCategoria(categoria: Categoria): Result<String> {
        // Guardar en Firebase
        val result = firebaseDataSource.crearCategoria(
            CategoriaMapper.toFirebase(categoria)
        )

        return result.onSuccess { id ->
            // Sincronizar con Room
            val categoriaConId = categoria.copy(id = id)
            categoriaDao.insertar(CategoriaMapper.toEntity(categoriaConId))
        }
    }

    /**
     * Actualizar categoría
     */
    suspend fun actualizarCategoria(categoria: Categoria): Result<Unit> {
        // Actualizar en Firebase
        val result = firebaseDataSource.actualizarCategoria(
            CategoriaMapper.toFirebase(categoria)
        )

        return result.onSuccess {
            // Sincronizar con Room
            categoriaDao.actualizar(CategoriaMapper.toEntity(categoria))
        }
    }

    /**
     * Actualizar solo el gasto de una categoría
     */
    suspend fun actualizarGastado(id: String, gastado: Double): Result<Unit> {
        // Actualizar en Firebase
        val result = firebaseDataSource.actualizarGastado(id, gastado)

        return result.onSuccess {
            // Sincronizar con Room
            categoriaDao.actualizarGastado(id, gastado)
        }
    }

    /**
     * Desactivar categoría (soft delete)
     */
    suspend fun desactivarCategoria(id: String): Result<Unit> {
        // Desactivar en Firebase
        val result = firebaseDataSource.desactivarCategoria(id)

        return result.onSuccess {
            // Sincronizar con Room
            categoriaDao.desactivar(id)
        }
    }

    /**
     * Eliminar categoría permanentemente
     */
    suspend fun eliminarCategoria(categoria: Categoria): Result<Unit> {
        // Eliminar de Firebase
        val result = firebaseDataSource.eliminarCategoria(categoria.id)

        return result.onSuccess {
            // Eliminar de Room
            categoriaDao.eliminar(CategoriaMapper.toEntity(categoria))
        }
    }

    /**
     * Obtener categorías por tipo (GASTO o INGRESO)
     */
    fun obtenerCategoriasPorTipo(usuarioId: String, tipo: TipoCategoria): Flow<List<Categoria>> {
        return firebaseDataSource.obtenerCategoriasPorTipo(usuarioId, tipo.name)
            .map { CategoriaMapper.fromFirebaseList(it) }
            .onEach { categorias ->
                // Sincronizar con Room
                categoriaDao.insertarVarias(CategoriaMapper.toEntityList(categorias))
            }
            .catch { error ->
                // Fallback a Room
                categoriaDao.obtenerCategoriasPorTipo(usuarioId, tipo.name)
                    .map { CategoriaMapper.fromEntityList(it) }
                    .collect { emit(it) }
            }
    }

    /**
     * Crear categorías predeterminadas para un nuevo usuario
     */
    suspend fun crearCategoriasDefault(usuarioId: String): Result<Unit> {
        return try {
            val categoriasDefault = listOf(
                Categoria(
                    usuarioId = usuarioId,
                    nombre = "Alimentación",
                    emoji = "🍔",
                    color = "#FF6B6B",
                    tipo = TipoCategoria.GASTO
                ),
                Categoria(
                    usuarioId = usuarioId,
                    nombre = "Transporte",
                    emoji = "🚗",
                    color = "#4ECDC4",
                    tipo = TipoCategoria.GASTO
                ),
                Categoria(
                    usuarioId = usuarioId,
                    nombre = "Entretenimiento",
                    emoji = "🎮",
                    color = "#95E1D3",
                    tipo = TipoCategoria.GASTO
                ),
                Categoria(
                    usuarioId = usuarioId,
                    nombre = "Salud",
                    emoji = "💊",
                    color = "#F38181",
                    tipo = TipoCategoria.GASTO
                ),
                Categoria(
                    usuarioId = usuarioId,
                    nombre = "Educación",
                    emoji = "📚",
                    color = "#AA96DA",
                    tipo = TipoCategoria.GASTO
                ),
                Categoria(
                    usuarioId = usuarioId,
                    nombre = "Salario",
                    emoji = "💰",
                    color = "#4CAF50",
                    tipo = TipoCategoria.INGRESO
                ),
                Categoria(
                    usuarioId = usuarioId,
                    nombre = "Freelance",
                    emoji = "💼",
                    color = "#8BC34A",
                    tipo = TipoCategoria.INGRESO
                )
            )

            categoriasDefault.forEach { categoria ->
                crearCategoria(categoria)
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}