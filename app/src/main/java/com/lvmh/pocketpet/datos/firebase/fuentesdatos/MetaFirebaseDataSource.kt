package com.lvmh.pocketpet.datos.firebase.fuentesdatos

import com.lvmh.pocketpet.datos.firebase.modelos.MetaFirebase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class MetaFirebaseDataSource {

    fun obtenerPorUsuario(usuarioId: String): Flow<List<MetaFirebase>> {
        return flowOf(emptyList())
    }

    suspend fun obtenerPorId(id: String): MetaFirebase? {
        return null
    }

    suspend fun crear(meta: MetaFirebase) {}

    suspend fun actualizar(meta: MetaFirebase) {}

    suspend fun eliminar(id: String) {}
}
