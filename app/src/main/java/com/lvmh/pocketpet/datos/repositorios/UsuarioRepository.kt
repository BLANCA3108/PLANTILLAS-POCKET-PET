package com.lvmh.pocketpet.datos.repositorios

import com.lvmh.pocketpet.datos.firebase.fuentesdatos.UsuarioFirebaseDataSource
import com.lvmh.pocketpet.datos.local.dao.UsuarioDao
import com.lvmh.pocketpet.datos.mapeadores.UsuarioMapper
import com.lvmh.pocketpet.dominio.modelos.Usuario
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.catch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UsuarioRepository @Inject constructor(
    private val firebaseDataSource: UsuarioFirebaseDataSource,
    private val usuarioDao: UsuarioDao
) {

    /**
     * Obtener usuario con sincronización Firebase → Room
     */
    fun obtenerUsuario(usuarioId: String): Flow<Usuario?> {
        return firebaseDataSource.obtenerUsuario(usuarioId)
            .map { firebase ->
                if (firebase != null) {
                    val usuario = UsuarioMapper.fromFirebase(firebase)
                    // Sincronizar con Room
                    usuarioDao.insertar(UsuarioMapper.toEntity(usuario))
                    usuario
                } else {
                    null
                }
            }
            .catch { error ->
                // Si falla Firebase, usar Room como fallback
                usuarioDao.obtenerUsuario(usuarioId)
                    .map { it?.let { UsuarioMapper.fromEntity(it) } }
                    .collect { emit(it) }
            }
    }

    /**
     * Obtener usuario solo desde Room
     */
    fun obtenerUsuarioLocal(usuarioId: String): Flow<Usuario?> {
        return usuarioDao.obtenerUsuario(usuarioId)
            .map { it?.let { UsuarioMapper.fromEntity(it) } }
    }

    /**
     * Obtener usuario una sola vez (suspend)
     */
    suspend fun obtenerUsuarioSync(usuarioId: String): Usuario? {
        val firebase = firebaseDataSource.obtenerUsuarioSync(usuarioId)
        return if (firebase != null) {
            val usuario = UsuarioMapper.fromFirebase(firebase)
            usuarioDao.insertar(UsuarioMapper.toEntity(usuario))
            usuario
        } else {
            usuarioDao.obtenerUsuarioSync(usuarioId)?.let {
                UsuarioMapper.fromEntity(it)
            }
        }
    }

    /**
     * Crear usuario
     */
    suspend fun crearUsuario(usuario: Usuario): Result<String> {
        val result = firebaseDataSource.crearUsuario(
            UsuarioMapper.toFirebase(usuario)
        )

        return result.onSuccess { id ->
            val usuarioConId = usuario.copy(id = id)
            usuarioDao.insertar(UsuarioMapper.toEntity(usuarioConId))
        }
    }

    /**
     * Actualizar usuario
     */
    suspend fun actualizarUsuario(usuario: Usuario): Result<Unit> {
        val result = firebaseDataSource.actualizarUsuario(
            UsuarioMapper.toFirebase(usuario)
        )

        return result.onSuccess {
            usuarioDao.actualizar(UsuarioMapper.toEntity(usuario))
        }
    }

    /**
     * Actualizar nombre
     */
    suspend fun actualizarNombre(usuarioId: String, nombre: String): Result<Unit> {
        val result = firebaseDataSource.actualizarNombre(usuarioId, nombre)

        return result.onSuccess {
            usuarioDao.actualizarNombre(usuarioId, nombre)
        }
    }

    /**
     * Actualizar avatar
     */
    suspend fun actualizarAvatar(usuarioId: String, avatarUrl: String?): Result<Unit> {
        val result = firebaseDataSource.actualizarAvatar(usuarioId, avatarUrl)

        return result.onSuccess {
            usuarioDao.actualizarAvatar(usuarioId, avatarUrl)
        }
    }

    /**
     * Eliminar usuario
     */
    suspend fun eliminarUsuario(usuarioId: String): Result<Unit> {
        val result = firebaseDataSource.eliminarUsuario(usuarioId)

        return result.onSuccess {
            usuarioDao.eliminar(usuarioId)
        }
    }
}