package com.lvmh.pocketpet.datos.repositorios

import com.lvmh.pocketpet.datos.firebase.fuentesdatos.PresupuestoFirebaseDataSource
import com.lvmh.pocketpet.datos.local.dao.PresupuestoDao
import com.lvmh.pocketpet.datos.mapeadores.*
import com.lvmh.pocketpet.dominio.modelos.Presupuesto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

class PresupuestoRepository(
    private val firebaseDataSource: PresupuestoFirebaseDataSource,
    private val localDao: PresupuestoDao
) {

    fun obtenerPorUsuario(usuarioId: String): Flow<List<Presupuesto>> {
        return firebaseDataSource.obtenerPorUsuario(usuarioId)
            .onEach { presupuestos ->
                localDao.insertarVarios(presupuestos.map { it.aDominio().aEntity() })
            }
            .map { presupuestos -> presupuestos.map { it.aDominio() } }
    }

    fun obtenerDesdeCache(usuarioId: String): Flow<List<Presupuesto>> {
        return localDao.obtenerTodosPorUsuario(usuarioId).map { lista ->
            lista.map { it.aDominio() }
        }
    }

    suspend fun obtenerPorId(id: String): Presupuesto? {
        val local = localDao.obtenerPorId(id)
        if (local != null) return local.aDominio()

        val remoto = firebaseDataSource.obtenerPorId(id)
        remoto?.let { localDao.insertar(it.aDominio().aEntity()) }
        return remoto?.aDominio()
    }

    suspend fun crear(presupuesto: Presupuesto) {
        firebaseDataSource.crear(presupuesto.aFirebase())
        localDao.insertar(presupuesto.aEntity())
    }

    suspend fun actualizar(presupuesto: Presupuesto) {
        firebaseDataSource.actualizar(presupuesto.aFirebase())
        localDao.actualizar(presupuesto.aEntity())
    }

    suspend fun eliminar(presupuesto: Presupuesto) {
        firebaseDataSource.eliminar(presupuesto.id)
        localDao.eliminar(presupuesto.aEntity())
    }

    suspend fun actualizarGastado(presupuestoId: String, nuevoGastado: Double) {
        val presupuesto = obtenerPorId(presupuestoId) ?: return
        val actualizado = presupuesto.copy(gastado = nuevoGastado)
        actualizar(actualizado)
    }
}