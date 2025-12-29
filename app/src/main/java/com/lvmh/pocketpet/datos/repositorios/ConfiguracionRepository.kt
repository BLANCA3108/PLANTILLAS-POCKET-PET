package com.lvmh.pocketpet.datos.repositorios

import com.lvmh.pocketpet.datos.firebase.fuentesdatos.ConfiguracionFirebaseDataSource
import com.lvmh.pocketpet.datos.local.dao.ConfiguracionDao
import com.lvmh.pocketpet.datos.mapeadores.toDomain
import com.lvmh.pocketpet.datos.mapeadores.toEntity
import com.lvmh.pocketpet.datos.mapeadores.toFirebase
import com.lvmh.pocketpet.dominio.modelos.Configuracion
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ConfiguracionRepository(
    private val firebaseDataSource: ConfiguracionFirebaseDataSource,
    private val localDao: ConfiguracionDao
) {

    fun obtenerConfiguracionPorUsuario(userId: String): Flow<Configuracion?> {
        return firebaseDataSource.obtenerConfiguracionPorUsuario(userId)
            .map { firebaseConfig ->
                if (firebaseConfig != null) {
                    val entity = firebaseConfig.toDomain().toEntity()
                    localDao.insertarConfiguracion(entity)
                    firebaseConfig.toDomain()
                } else {
                    localDao.obtenerConfiguracionPorUsuarioSuspend(userId)?.toDomain()
                }
            }
    }

    suspend fun obtenerConfiguracionPorId(id: String): Configuracion? {
        val firebaseConfig = firebaseDataSource.obtenerConfiguracionPorId(id)
        return if (firebaseConfig != null) {
            localDao.insertarConfiguracion(firebaseConfig.toDomain().toEntity())
            firebaseConfig.toDomain()
        } else {
            localDao.obtenerConfiguracionPorId(id)?.toDomain()
        }
    }

    fun obtenerConfiguracionLocal(userId: String): Flow<Configuracion?> {
        return localDao.obtenerConfiguracionPorUsuario(userId).map { it?.toDomain() }
    }

    suspend fun guardarConfiguracion(configuracion: Configuracion): Result<String> {
        return try {
            val result = firebaseDataSource.guardarConfiguracion(configuracion.toFirebase())

            if (result.isSuccess) {
                val id = result.getOrNull() ?: configuracion.id
                val configuracionConId = configuracion.copy(id = id)
                localDao.insertarConfiguracion(configuracionConId.toEntity())
                Result.success(id)
            } else {
                result
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun actualizarConfiguracion(configuracion: Configuracion): Result<Unit> {
        return try {
            val result = firebaseDataSource.actualizarConfiguracion(configuracion.toFirebase())

            if (result.isSuccess) {
                localDao.actualizarConfiguracion(configuracion.toEntity())
            }
            result
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun eliminarConfiguracion(configuracion: Configuracion): Result<Unit> {
        return try {
            val result = firebaseDataSource.eliminarConfiguracion(configuracion.id)

            if (result.isSuccess) {
                localDao.eliminarConfiguracion(configuracion.toEntity())
            }
            result
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun actualizarNotificaciones(userId: String, activas: Boolean): Result<Unit> {
        return try {
            val result = firebaseDataSource.actualizarNotificaciones(userId, activas)

            if (result.isSuccess) {
                localDao.actualizarNotificaciones(userId, activas, System.currentTimeMillis())
            }
            result
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun actualizarMoneda(userId: String, moneda: String): Result<Unit> {
        return try {
            val result = firebaseDataSource.actualizarMoneda(userId, moneda)

            if (result.isSuccess) {
                localDao.actualizarMoneda(userId, moneda, System.currentTimeMillis())
            }
            result
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun crearConfiguracionPorDefecto(userId: String): Result<String> {
        val configuracionPorDefecto = Configuracion(
            userId = userId,
            notificacionesActivas = true,
            recordatoriosActivos = true,
            monedaPredeterminada = "PEN - Sol Peruano",
            recordatoriosCuidadoActivos = true,
            sonidosActivos = true,
            animacionesActivas = true,
            fechaCreacion = System.currentTimeMillis(),
            fechaActualizacion = System.currentTimeMillis()
        )

        return guardarConfiguracion(configuracionPorDefecto)
    }

    suspend fun limpiarCacheLocal() {
        localDao.eliminarTodasLasConfiguraciones()
    }
}