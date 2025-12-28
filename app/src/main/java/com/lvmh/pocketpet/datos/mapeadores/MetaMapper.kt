package com.lvmh.pocketpetdemo.datos.mapeadores

import com.lvmh.pocketpet.datos.firebase.modelos.MetaFirebase
import com.lvmh.pocketpet.datos.local.entidades.MetaEntity
import com.lvmh.pocketpet.dominio.modelos.Meta

fun Meta.aFirebase() = MetaFirebase(
    id = id,
    usuarioId = usuarioId,
    nombre = nombre,
    descripcion = descripcion,
    montoObjetivo = montoObjetivo,
    montoActual = montoActual,
    fechaInicio = fechaInicio,
    fechaLimite = fechaLimite,
    categoriaId = categoriaId,
    iconoUrl = iconoUrl,
    completada = completada,
    fechaCompletada = fechaCompletada
)

fun MetaFirebase.aDominio() = Meta(
    id = id,
    usuarioId = usuarioId,
    nombre = nombre,
    descripcion = descripcion,
    montoObjetivo = montoObjetivo,
    montoActual = montoActual,
    fechaInicio = fechaInicio,
    fechaLimite = fechaLimite,
    categoriaId = categoriaId,
    iconoUrl = iconoUrl,
    completada = completada,
    fechaCompletada = fechaCompletada
)

fun Meta.aEntity() = MetaEntity(
    id = id,
    usuarioId = usuarioId,
    nombre = nombre,
    descripcion = descripcion,
    montoObjetivo = montoObjetivo,
    montoActual = montoActual,
    fechaInicio = fechaInicio,
    fechaLimite = fechaLimite,
    categoriaId = categoriaId,
    iconoUrl = iconoUrl,
    completada = completada,
    fechaCompletada = fechaCompletada
)

fun MetaEntity.aDominio() = Meta(
    id = id,
    usuarioId = usuarioId,
    nombre = nombre,
    descripcion = descripcion,
    montoObjetivo = montoObjetivo,
    montoActual = montoActual,
    fechaInicio = fechaInicio,
    fechaLimite = fechaLimite,
    categoriaId = categoriaId,
    iconoUrl = iconoUrl,
    completada = completada,
    fechaCompletada = fechaCompletada
)
