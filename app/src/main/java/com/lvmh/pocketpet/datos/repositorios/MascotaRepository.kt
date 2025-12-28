package com.lvmh.pocketpet.datos.repositorios

import com.lvmh.pocketpet.datos.firebase.fuentesdatos.MascotaFirebaseDataSource
import com.lvmh.pocketpet.datos.local.dao.MascotaDao
import com.lvmh.pocketpet.datos.mapeadores.MascotaMapper
import com.lvmh.pocketpet.dominio.modelos.Mascota
import com.lvmh.pocketpet.dominio.modelos.TipoMascota
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.catch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MascotaRepository @Inject constructor(
    private val firebaseDataSource: MascotaFirebaseDataSource,
    private val mascotaDao: MascotaDao
) {

    /**
     * Obtener mascota con sincronización Firebase → Room
     */
    fun obtenerMascota(usuarioId: String): Flow<Mascota?> {
        return firebaseDataSource.obtenerMascota(usuarioId)
            .map { firebase ->
                if (firebase != null) {
                    val mascota = MascotaMapper.fromFirebase(firebase)
                    // Sincronizar con Room
                    mascotaDao.insertar(MascotaMapper.toEntity(mascota))
                    mascota
                } else {
                    null
                }
            }
            .catch { error ->
                // Si falla Firebase, usar Room como fallback
                mascotaDao.obtenerMascota(usuarioId)
                    .map { it?.let { MascotaMapper.fromEntity(it) } }
                    .collect { emit(it) }
            }
    }

    /**
     * Obtener mascota solo desde Room
     */
    fun obtenerMascotaLocal(usuarioId: String): Flow<Mascota?> {
        return mascotaDao.obtenerMascota(usuarioId)
            .map { it?.let { MascotaMapper.fromEntity(it) } }
    }

    /**
     * Crear mascota
     */
    suspend fun crearMascota(mascota: Mascota): Result<String> {
        val result = firebaseDataSource.crearMascota(
            MascotaMapper.toFirebase(mascota)
        )

        return result.onSuccess { id ->
            val mascotaConId = mascota.copy(id = id)
            mascotaDao.insertar(MascotaMapper.toEntity(mascotaConId))
        }
    }

    /**
     * Crear mascota predeterminada para nuevo usuario
     */
    suspend fun crearMascotaDefault(usuarioId: String, tipo: TipoMascota = TipoMascota.PERRO): Result<String> {
        val mascota = Mascota(
            usuarioId = usuarioId,
            nombre = "Toby",
            emoji = tipo.emoji,
            tipo = tipo,
            nivel = 1,
            experiencia = 0,
            salud = 1.0f,
            felicidad = 1.0f,
            hambre = 0.5f,
            energia = 1.0f,
            monedas = 100
        )

        return crearMascota(mascota)
    }

    /**
     * Actualizar mascota
     */
    suspend fun actualizarMascota(mascota: Mascota): Result<Unit> {
        val result = firebaseDataSource.actualizarMascota(
            MascotaMapper.toFirebase(mascota)
        )

        return result.onSuccess {
            mascotaDao.actualizar(MascotaMapper.toEntity(mascota))
        }
    }

    /**
     * Actualizar estadísticas
     */
    suspend fun actualizarEstadisticas(
        mascotaId: String,
        salud: Float,
        felicidad: Float,
        hambre: Float
    ): Result<Unit> {
        val result = firebaseDataSource.actualizarEstadisticas(mascotaId, salud, felicidad, hambre)

        return result.onSuccess {
            mascotaDao.actualizarEstadisticas(mascotaId, salud, felicidad, hambre)
        }
    }

    /**
     * Alimentar mascota
     */
    suspend fun alimentarMascota(mascotaId: String): Result<Unit> {
        return firebaseDataSource.alimentarMascota(mascotaId).onSuccess {
            // Actualizar en Room
            mascotaDao.actualizarEstadisticas(mascotaId, 1.0f, 1.0f, 0.0f)
        }
    }

    /**
     * Ganar experiencia
     */
    suspend fun ganarExperiencia(mascotaId: String, cantidad: Int): Result<Unit> {
        return firebaseDataSource.ganarExperiencia(mascotaId, cantidad)
    }

    /**
     * Actualizar nivel
     */
    suspend fun actualizarNivel(mascotaId: String, nivel: Int, experiencia: Int): Result<Unit> {
        val result = firebaseDataSource.actualizarNivel(mascotaId, nivel, experiencia)

        return result.onSuccess {
            mascotaDao.actualizarNivel(mascotaId, nivel, experiencia)
        }
    }

    /**
     * Eliminar mascota
     */
    suspend fun eliminarMascota(mascota: Mascota): Result<Unit> {
        val result = firebaseDataSource.eliminarMascota(mascota.id)

        return result.onSuccess {
            mascotaDao.eliminar(MascotaMapper.toEntity(mascota))
        }
    }
}