package com.lvmh.pocketpet.datos.local.dao

import androidx.room.*
import com.lvmh.pocketpet.datos.local.entidades.MetaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MetaDao {

    @Query("SELECT * FROM metas WHERE usuario_id = :usuarioId ORDER BY fecha_limite ASC")
    fun obtenerTodosPorUsuario(usuarioId: String): Flow<List<MetaEntity>>

    @Query("SELECT * FROM metas WHERE id = :id")
    suspend fun obtenerPorId(id: String): MetaEntity?

    @Query("SELECT * FROM metas WHERE completada = 0 AND usuario_id = :usuarioId ORDER BY fecha_limite ASC")
    fun obtenerActivas(usuarioId: String): Flow<List<MetaEntity>>

    @Query("SELECT * FROM metas WHERE completada = 1 AND usuario_id = :usuarioId ORDER BY fecha_completada DESC")
    fun obtenerCompletadas(usuarioId: String): Flow<List<MetaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(meta: MetaEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarVarias(metas: List<MetaEntity>)

    @Update
    suspend fun actualizar(meta: MetaEntity)

    @Delete
    suspend fun eliminar(meta: MetaEntity)

    @Query("DELETE FROM metas WHERE usuario_id = :usuarioId")
    suspend fun eliminarTodosPorUsuario(usuarioId: String)

    @Query("DELETE FROM metas")
    suspend fun eliminarTodos()
}
