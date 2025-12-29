package com.lvmh.pocketpet.datos.local.dao

import androidx.room.*
import com.lvmh.pocketpet.datos.local.entidades.MascotaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MascotaDao {

    @Query("SELECT * FROM mascotas WHERE usuario_id = :usuarioId")
    fun obtenerMascota(usuarioId: String): Flow<MascotaEntity?>

    @Query("SELECT * FROM mascotas WHERE usuario_id = :usuarioId")
    suspend fun obtenerMascotaSync(usuarioId: String): MascotaEntity?

    @Query("SELECT * FROM mascotas WHERE id = :id")
    suspend fun obtenerMascotaPorId(id: String): MascotaEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(mascota: MascotaEntity)

    @Update
    suspend fun actualizar(mascota: MascotaEntity)

    @Query("UPDATE mascotas SET salud = :salud, felicidad = :felicidad, hambre = :hambre WHERE id = :id")
    suspend fun actualizarEstadisticas(id: String, salud: Float, felicidad: Float, hambre: Float)

    @Query("UPDATE mascotas SET nivel = :nivel, experiencia = :experiencia WHERE id = :id")
    suspend fun actualizarNivel(id: String, nivel: Int, experiencia: Int)

    @Query("UPDATE mascotas SET monedas = :monedas WHERE id = :id")
    suspend fun actualizarMonedas(id: String, monedas: Int)

    @Delete
    suspend fun eliminar(mascota: MascotaEntity)

    @Query("DELETE FROM mascotas WHERE usuario_id = :usuarioId")
    suspend fun eliminarPorUsuario(usuarioId: String)
}