package com.lvmh.pocketpet.datos.local.dao

import androidx.room.*
import com.lvmh.pocketpet.datos.local.entidades.UsuarioEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {

    @Query("SELECT * FROM usuarios WHERE id = :id AND activo = 1")
    fun obtenerUsuario(id: String): Flow<UsuarioEntity?>

    @Query("SELECT * FROM usuarios WHERE id = :id")
    suspend fun obtenerUsuarioSync(id: String): UsuarioEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(usuario: UsuarioEntity)

    @Update
    suspend fun actualizar(usuario: UsuarioEntity)

    @Query("UPDATE usuarios SET nombre = :nombre WHERE id = :id")
    suspend fun actualizarNombre(id: String, nombre: String)

    @Query("UPDATE usuarios SET avatar_url = :avatarUrl WHERE id = :id")
    suspend fun actualizarAvatar(id: String, avatarUrl: String?)

    @Query("DELETE FROM usuarios WHERE id = :id")
    suspend fun eliminar(id: String)

    @Query("DELETE FROM usuarios")
    suspend fun eliminarTodos()
}