package com.lvmh.pocketpet.datos.mapeadores

import com.lvmh.pocketpet.datos.firebase.modelos.PresupuestoFirebase
import com.lvmh.pocketpet.datos.local.entidades.PresupuestoEntity
import com.lvmh.pocketpet.dominio.modelos.Presupuesto

fun Presupuesto.aFirebase(): PresupuestoFirebase = PresupuestoFirebase(
    id = id,
    usuarioId = usuarioId,
    categoriaId = categoriaId,
    monto = monto,
    gastado = gastado,
    periodo = periodo,
    mesInicio = mesInicio,
    anioInicio = anioInicio,
    activo = activo,
    alertaEn = alertaEn,
    fechaCreacion = fechaCreacion
)

fun PresupuestoFirebase.aDominio(): Presupuesto = Presupuesto(
    id = id,
    usuarioId = usuarioId,
    categoriaId = categoriaId,
    monto = monto,
    gastado = gastado,
    periodo = periodo,
    mesInicio = mesInicio,
    anioInicio = anioInicio,
    activo = activo,
    alertaEn = alertaEn,
    fechaCreacion = fechaCreacion
)

fun Presupuesto.aEntity(): PresupuestoEntity = PresupuestoEntity(
    id = id,
    usuarioId = usuarioId,
    categoriaId = categoriaId,
    monto = monto,
    gastado = gastado,
    periodo = periodo,
    mesInicio = mesInicio,
    anioInicio = anioInicio,
    activo = activo,
    alertaEn = alertaEn,
    fechaCreacion = fechaCreacion
)

fun PresupuestoEntity.aDominio(): Presupuesto = Presupuesto(
    id = id,
    usuarioId = usuarioId,
    categoriaId = categoriaId,
    monto = monto,
    gastado = gastado,
    periodo = periodo,
    mesInicio = mesInicio,
    anioInicio = anioInicio,
    activo = activo,
    alertaEn = alertaEn,
    fechaCreacion = fechaCreacion
)