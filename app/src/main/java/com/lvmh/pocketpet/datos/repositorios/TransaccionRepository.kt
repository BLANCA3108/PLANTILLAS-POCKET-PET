package com.lvmh.pocketpet.datos.repositorios

import com.lvmh.pocketpet.datos.firebase.fuentesdatos.TransaccionFirebaseDataSource
import com.lvmh.pocketpet.datos.local.dao.TransaccionDao
import com.lvmh.pocketpet.datos.mapeadores.TransaccionMapper
import com.lvmh.pocketpet.dominio.modelos.Transaccion
import com.lvmh.pocketpet.dominio.modelos.TipoTransaccion
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransaccionRepository @Inject constructor(
    private val firebaseDataSource: TransaccionFirebaseDataSource,
    private val transaccionDao: TransaccionDao
) {

    /**
     * Obtener transacciones con sincronización Firebase → Room
     */
    fun obtenerTransacciones(usuarioId: String): Flow<List<Transaccion>> {
        return firebaseDataSource.obtenerTransacciones(usuarioId)
            .map { firebaseList ->
                val transacciones = TransaccionMapper.fromFirebaseList(firebaseList)
                // Sincronizar con Room
                transaccionDao.insertarVarias(TransaccionMapper.toEntityList(transacciones))
                transacciones
            }
            .catch { error ->
                // Si falla Firebase, usar Room como fallback
                transaccionDao.obtenerTransacciones(usuarioId)
                    .map { TransaccionMapper.fromEntityList(it) }
                    .collect { emit(it) }
            }
    }

    /**
     * Obtener transacciones solo desde Room (offline-first)
     */
    fun obtenerTransaccionesLocal(usuarioId: String): Flow<List<Transaccion>> {
        return transaccionDao.obtenerTransacciones(usuarioId)
            .map { TransaccionMapper.fromEntityList(it) }
    }

    /**
     * Obtener transacción por ID
     */
    suspend fun obtenerTransaccionPorId(id: String): Transaccion? {
        val firebase = firebaseDataSource.obtenerTransaccionPorId(id)
        return if (firebase != null) {
            val transaccion = TransaccionMapper.fromFirebase(firebase)
            transaccionDao.insertar(TransaccionMapper.toEntity(transaccion))
            transaccion
        } else {
            transaccionDao.obtenerTransaccionPorId(id)?.let {
                TransaccionMapper.fromEntity(it)
            }
        }
    }

    /**
     * Crear nueva transacción
     */
    suspend fun crearTransaccion(transaccion: Transaccion): Result<String> {
        val result = firebaseDataSource.crearTransaccion(
            TransaccionMapper.toFirebase(transaccion)
        )

        return result.onSuccess { id ->
            val transaccionConId = transaccion.copy(id = id)
            transaccionDao.insertar(TransaccionMapper.toEntity(transaccionConId))
        }
    }

    /**
     * Actualizar transacción
     */
    suspend fun actualizarTransaccion(transaccion: Transaccion): Result<Unit> {
        val result = firebaseDataSource.actualizarTransaccion(
            TransaccionMapper.toFirebase(transaccion)
        )

        return result.onSuccess {
            transaccionDao.actualizar(TransaccionMapper.toEntity(transaccion))
        }
    }

    /**
     * Eliminar transacción
     */
    suspend fun eliminarTransaccion(transaccion: Transaccion): Result<Unit> {
        val result = firebaseDataSource.eliminarTransaccion(transaccion.id)

        return result.onSuccess {
            transaccionDao.eliminar(TransaccionMapper.toEntity(transaccion))
        }
    }

    /**
     * Obtener transacciones por tipo
     */
    fun obtenerTransaccionesPorTipo(usuarioId: String, tipo: TipoTransaccion): Flow<List<Transaccion>> {
        return firebaseDataSource.obtenerTransaccionesPorTipo(usuarioId, tipo.name)
            .map { TransaccionMapper.fromFirebaseList(it) }
            .onEach { transacciones ->
                transaccionDao.insertarVarias(TransaccionMapper.toEntityList(transacciones))
            }
            .catch { error ->
                transaccionDao.obtenerTransaccionesPorTipo(usuarioId, tipo.name)
                    .map { TransaccionMapper.fromEntityList(it) }
                    .collect { emit(it) }
            }
    }

    /**
     * Obtener transacciones por categoría
     */
    fun obtenerTransaccionesPorCategoria(usuarioId: String, categoriaId: String): Flow<List<Transaccion>> {
        return firebaseDataSource.obtenerTransaccionesPorCategoria(usuarioId, categoriaId)
            .map { TransaccionMapper.fromFirebaseList(it) }
            .onEach { transacciones ->
                transaccionDao.insertarVarias(TransaccionMapper.toEntityList(transacciones))
            }
            .catch { error ->
                transaccionDao.obtenerTransaccionesPorCategoria(usuarioId, categoriaId)
                    .map { TransaccionMapper.fromEntityList(it) }
                    .collect { emit(it) }
            }
    }

    /**
     * Obtener transacciones por rango de fecha
     */
    fun obtenerTransaccionesPorRangoFecha(
        usuarioId: String,
        fechaInicio: Long,
        fechaFin: Long
    ): Flow<List<Transaccion>> {
        return firebaseDataSource.obtenerTransaccionesPorRangoFecha(usuarioId, fechaInicio, fechaFin)
            .map { TransaccionMapper.fromFirebaseList(it) }
            .onEach { transacciones ->
                transaccionDao.insertarVarias(TransaccionMapper.toEntityList(transacciones))
            }
            .catch { error ->
                transaccionDao.obtenerTransaccionesPorRangoFecha(usuarioId, fechaInicio, fechaFin)
                    .map { TransaccionMapper.fromEntityList(it) }
                    .collect { emit(it) }
            }
    }

    /**
     * Obtener total por tipo (GASTO o INGRESO)
     */
    suspend fun obtenerTotalPorTipo(usuarioId: String, tipo: TipoTransaccion): Double {
        return transaccionDao.obtenerTotalPorTipo(usuarioId, tipo.name) ?: 0.0
    }

    /**
     * Obtener balance actual (Ingresos - Gastos)
     */
    suspend fun obtenerBalance(usuarioId: String): Double {
        val ingresos = obtenerTotalPorTipo(usuarioId, TipoTransaccion.INGRESO)
        val gastos = obtenerTotalPorTipo(usuarioId, TipoTransaccion.GASTO)
        return ingresos - gastos
    }
}