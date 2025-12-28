package com.lvmh.pocketpet.datos.mapeadores

import com.lvmh.pocketpet.datos.firebase.modelos.TransaccionFirebase
import com.lvmh.pocketpet.datos.local.entidades.TransaccionEntity
import com.lvmh.pocketpet.dominio.modelos.Transaccion
import com.lvmh.pocketpet.dominio.modelos.TipoTransaccion

object TransaccionMapper {

    // Domain → Firebase
    fun toFirebase(transaccion: Transaccion): TransaccionFirebase {
        return TransaccionFirebase(
            id = transaccion.id,
            usuarioId = transaccion.usuarioId,
            tipo = transaccion.tipo.name,
            monto = transaccion.monto,
            categoriaId = transaccion.categoriaId,
            categoriaNombre = transaccion.categoriaNombre,
            categoriaEmoji = transaccion.categoriaEmoji,
            fecha = transaccion.fecha,
            descripcion = transaccion.descripcion,
            comprobante = transaccion.comprobante,
            metodoPago = transaccion.metodoPago,
            notas = transaccion.notas,
            fechaCreacion = transaccion.fechaCreacion,
            fechaModificacion = transaccion.fechaModificacion
        )
    }

    // Firebase → Domain
    fun fromFirebase(firebase: TransaccionFirebase): Transaccion {
        return Transaccion(
            id = firebase.id,
            usuarioId = firebase.usuarioId,
            tipo = try {
                TipoTransaccion.valueOf(firebase.tipo)
            } catch (e: Exception) {
                TipoTransaccion.GASTO
            },
            monto = firebase.monto,
            categoriaId = firebase.categoriaId,
            categoriaNombre = firebase.categoriaNombre,
            categoriaEmoji = firebase.categoriaEmoji,
            fecha = firebase.fecha,
            descripcion = firebase.descripcion,
            comprobante = firebase.comprobante,
            metodoPago = firebase.metodoPago,
            notas = firebase.notas,
            fechaCreacion = firebase.fechaCreacion,
            fechaModificacion = firebase.fechaModificacion
        )
    }

    // Domain → Entity (Room)
    fun toEntity(transaccion: Transaccion): TransaccionEntity {
        return TransaccionEntity(
            id = transaccion.id,
            usuarioId = transaccion.usuarioId,
            tipo = transaccion.tipo.name,
            monto = transaccion.monto,
            categoriaId = transaccion.categoriaId,
            categoriaNombre = transaccion.categoriaNombre,
            categoriaEmoji = transaccion.categoriaEmoji,
            fecha = transaccion.fecha,
            descripcion = transaccion.descripcion,
            comprobante = transaccion.comprobante,
            metodoPago = transaccion.metodoPago,
            notas = transaccion.notas,
            fechaCreacion = transaccion.fechaCreacion,
            fechaModificacion = transaccion.fechaModificacion
        )
    }

    // Entity (Room) → Domain
    fun fromEntity(entity: TransaccionEntity): Transaccion {
        return Transaccion(
            id = entity.id,
            usuarioId = entity.usuarioId,
            tipo = try {
                TipoTransaccion.valueOf(entity.tipo)
            } catch (e: Exception) {
                TipoTransaccion.GASTO
            },
            monto = entity.monto,
            categoriaId = entity.categoriaId,
            categoriaNombre = entity.categoriaNombre,
            categoriaEmoji = entity.categoriaEmoji,
            fecha = entity.fecha,
            descripcion = entity.descripcion,
            comprobante = entity.comprobante,
            metodoPago = entity.metodoPago,
            notas = entity.notas,
            fechaCreacion = entity.fechaCreacion,
            fechaModificacion = entity.fechaModificacion
        )
    }

    // Listas
    fun fromFirebaseList(list: List<TransaccionFirebase>): List<Transaccion> {
        return list.map { fromFirebase(it) }
    }

    fun fromEntityList(list: List<TransaccionEntity>): List<Transaccion> {
        return list.map { fromEntity(it) }
    }

    fun toEntityList(list: List<Transaccion>): List<TransaccionEntity> {
        return list.map { toEntity(it) }
    }
}