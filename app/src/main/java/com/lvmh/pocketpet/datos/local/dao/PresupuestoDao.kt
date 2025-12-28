package com.lvmh.pocketpet.datos.local.dao

import androidx.room.*
import com.lvmh.pocketpet.datos.local.entidades.PresupuestoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PresupuestoDao {

    @Query("SELECT * FROM presupuestos WHERE usuario_id = :usuarioId ORDER BY fecha_creacion DESC")
    fun obtenerTodosPorUsuario(usuarioId: String): Flow<List<PresupuestoEntity>>

    @Query("SELECT * FROM presupuestos WHERE id = :id")
    suspend fun obtenerPorId(id: String): PresupuestoEntity?

    @Query("SELECT * FROM presupuestos WHERE categoria_id = :categoriaId AND activo = 1")
    suspend fun obtenerPorCategoria(categoriaId: String): PresupuestoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(presupuesto: PresupuestoEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarVarios(presupuestos: List<PresupuestoEntity>)

    @Update
    suspend fun actualizar(presupuesto: PresupuestoEntity)

    @Delete
    suspend fun eliminar(presupuesto: PresupuestoEntity)

    @Query("DELETE FROM presupuestos WHERE usuario_id = :usuarioId")
    suspend fun eliminarTodosPorUsuario(usuarioId: String)

    @Query("DELETE FROM presupuestos")
    suspend fun eliminarTodos()
}
