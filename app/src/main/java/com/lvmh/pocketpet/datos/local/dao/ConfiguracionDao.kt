package com.lvmh.pocketpet.datos.local.dao

import androidx.room.*
import com.lvmh.pocketpet.datos.local.entidades.ConfiguracionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ConfiguracionDao {

    @Query("SELECT * FROM configuraciones WHERE userId = :userId LIMIT 1")
    fun obtenerConfiguracionPorUsuario(userId: String): Flow<ConfiguracionEntity?>

    @Query("SELECT * FROM configuraciones WHERE userId = :userId LIMIT 1")
    suspend fun obtenerConfiguracionPorUsuarioSuspend(userId: String): ConfiguracionEntity?

    @Query("SELECT * FROM configuraciones WHERE id = :id")
    suspend fun obtenerConfiguracionPorId(id: String): ConfiguracionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarConfiguracion(configuracion: ConfiguracionEntity)

    @Update
    suspend fun actualizarConfiguracion(configuracion: ConfiguracionEntity)

    @Delete
    suspend fun eliminarConfiguracion(configuracion: ConfiguracionEntity)

    @Query("DELETE FROM configuraciones")
    suspend fun eliminarTodasLasConfiguraciones()

    @Query("UPDATE configuraciones SET notificacionesActivas = :activas, fechaActualizacion = :fecha WHERE userId = :userId")
    suspend fun actualizarNotificaciones(userId: String, activas: Boolean, fecha: Long)

    @Query("UPDATE configuraciones SET monedaPredeterminada = :moneda, fechaActualizacion = :fecha WHERE userId = :userId")
    suspend fun actualizarMoneda(userId: String, moneda: String, fecha: Long)
}