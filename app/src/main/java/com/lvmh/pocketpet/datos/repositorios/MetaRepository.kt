package com.lvmh.pocketpet.datos.repositorios

import com.lvmh.pocketpet.datos.firebase.fuentesdatos.MetaFirebaseDataSource
import com.lvmh.pocketpet.datos.local.dao.MetaDao
import com.lvmh.pocketpet.datos.mapeadores.*
import com.lvmh.pocketpet.dominio.modelos.Meta
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

class MetaRepository(
    private val firebaseDataSource: MetaFirebaseDataSource,
    private val localDao: MetaDao
) {

    fun obtenerPorUsuario(usuarioId: String): Flow<List<Meta>> {
        return firebaseDataSource.obtenerPorUsuario(usuarioId)
            .onEach { metas ->
                localDao.insertarVarias(metas.map { it.aDominio().aEntity() })
            }
            .map { metas -> metas.map { it.aDominio() } }
    }

    fun obtenerDesdeCache(usuarioId: String): Flow<List<Meta>> {
        return localDao.obtenerTodosPorUsuario(usuarioId).map { lista ->
            lista.map { it.aDominio() }
        }
    }

    fun obtenerActivas(usuarioId: String): Flow<List<Meta>> {
        return localDao.obtenerActivas(usuarioId).map { lista ->
            lista.map { it.aDominio() }
        }
    }

    fun obtenerCompletadas(usuarioId: String): Flow<List<Meta>> {
        return localDao.obtenerCompletadas(usuarioId).map { lista ->
            lista.map { it.aDominio() }
        }
    }

    suspend fun obtenerPorId(id: String): Meta? {
        val local = localDao.obtenerPorId(id)
        if (local != null) return local.aDominio()

        val remoto = firebaseDataSource.obtenerPorId(id)
        remoto?.let { localDao.insertar(it.aDominio().aEntity()) }
        return remoto?.aDominio()
    }

    suspend fun crear(meta: Meta) {
        firebaseDataSource.crear(meta.aFirebase())
        localDao.insertar(meta.aEntity())
    }

    suspend fun actualizar(meta: Meta) {
        firebaseDataSource.actualizar(meta.aFirebase())
        localDao.actualizar(meta.aEntity())
    }

    suspend fun eliminar(meta: Meta) {
        firebaseDataSource.eliminar(meta.id)
        localDao.eliminar(meta.aEntity())
    }

    suspend fun agregarMonto(metaId: String, monto: Double) {
        val meta = obtenerPorId(metaId) ?: return
        val nuevoMonto = meta.montoActual + monto
        val completada = nuevoMonto >= meta.montoObjetivo
        val actualizada = meta.copy(
            montoActual = nuevoMonto,
            completada = completada,
            fechaCompletada = if (completada && !meta.completada) System.currentTimeMillis() else meta.fechaCompletada
        )
        actualizar(actualizada)
    }
}
