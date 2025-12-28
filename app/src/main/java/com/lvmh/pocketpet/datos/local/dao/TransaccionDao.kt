package com.lvmh.pocketpet.datos.local.dao

import androidx.room.*
import com.lvmh.pocketpet.datos.local.entidades.TransaccionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransaccionDao {

    @Query("SELECT * FROM transacciones WHERE usuario_id = :usuarioId ORDER BY fecha DESC")
    fun obtenerTransacciones(usuarioId: String): Flow<List<TransaccionEntity>>

    @Query("SELECT * FROM transacciones WHERE id = :id")
    suspend fun obtenerTransaccionPorId(id: String): TransaccionEntity?

    @Query("SELECT * FROM transacciones WHERE usuario_id = :usuarioId AND tipo = :tipo ORDER BY fecha DESC")
    fun obtenerTransaccionesPorTipo(usuarioId: String, tipo: String): Flow<List<TransaccionEntity>>

    @Query("SELECT * FROM transacciones WHERE usuario_id = :usuarioId AND categoria_id = :categoriaId ORDER BY fecha DESC")
    fun obtenerTransaccionesPorCategoria(usuarioId: String, categoriaId: String): Flow<List<TransaccionEntity>>

    @Query("SELECT * FROM transacciones WHERE usuario_id = :usuarioId AND fecha BETWEEN :fechaInicio AND :fechaFin ORDER BY fecha DESC")
    fun obtenerTransaccionesPorRangoFecha(
        usuarioId: String,
        fechaInicio: Long,
        fechaFin: Long
    ): Flow<List<TransaccionEntity>>

    @Query("SELECT * FROM transacciones WHERE usuario_id = :usuarioId AND monto BETWEEN :montoMin AND :montoMax ORDER BY fecha DESC")
    fun obtenerTransaccionesPorRangoMonto(
        usuarioId: String,
        montoMin: Double,
        montoMax: Double
    ): Flow<List<TransaccionEntity>>

    @Query("SELECT SUM(monto) FROM transacciones WHERE usuario_id = :usuarioId AND tipo = :tipo")
    suspend fun obtenerTotalPorTipo(usuarioId: String, tipo: String): Double?

    @Query("SELECT SUM(monto) FROM transacciones WHERE usuario_id = :usuarioId AND tipo = :tipo AND fecha BETWEEN :fechaInicio AND :fechaFin")
    suspend fun obtenerTotalPorTipoYFecha(
        usuarioId: String,
        tipo: String,
        fechaInicio: Long,
        fechaFin: Long
    ): Double?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(transaccion: TransaccionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarVarias(transacciones: List<TransaccionEntity>)

    @Update
    suspend fun actualizar(transaccion: TransaccionEntity)

    @Delete
    suspend fun eliminar(transaccion: TransaccionEntity)

    @Query("DELETE FROM transacciones WHERE id = :id")
    suspend fun eliminarPorId(id: String)

    @Query("DELETE FROM transacciones WHERE usuario_id = :usuarioId")
    suspend fun eliminarTodas(usuarioId: String)
}