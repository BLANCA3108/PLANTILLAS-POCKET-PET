package com.lvmh.pocketpet.datos.local.dao

import androidx.room.*
import com.lvmh.pocketpet.datos.local.entidades.CategoriaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoriaDao {

    @Query("SELECT * FROM categorias WHERE usuario_id = :usuarioId AND activa = 1 ORDER BY fecha_creacion DESC")
    fun obtenerCategorias(usuarioId: String): Flow<List<CategoriaEntity>>

    @Query("SELECT * FROM categorias WHERE id = :id")
    suspend fun obtenerCategoriaPorId(id: String): CategoriaEntity?

    @Query("SELECT * FROM categorias WHERE usuario_id = :usuarioId AND tipo = :tipo AND activa = 1")
    fun obtenerCategoriasPorTipo(usuarioId: String, tipo: String): Flow<List<CategoriaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(categoria: CategoriaEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarVarias(categorias: List<CategoriaEntity>)

    @Update
    suspend fun actualizar(categoria: CategoriaEntity)

    @Query("UPDATE categorias SET gastado = :gastado WHERE id = :id")
    suspend fun actualizarGastado(id: String, gastado: Double)

    @Query("UPDATE categorias SET activa = 0 WHERE id = :id")
    suspend fun desactivar(id: String)

    @Delete
    suspend fun eliminar(categoria: CategoriaEntity)

    @Query("DELETE FROM categorias WHERE usuario_id = :usuarioId")
    suspend fun eliminarTodas(usuarioId: String)
}