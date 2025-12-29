package com.lvmh.pocketpet.datos.local.entidades

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "configuraciones")
data class ConfiguracionEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val notificacionesActivas: Boolean,
    val recordatoriosActivos: Boolean,
    val monedaPredeterminada: String,
    val recordatoriosCuidadoActivos: Boolean,
    val sonidosActivos: Boolean,
    val animacionesActivas: Boolean,
    val fechaCreacion: Long,
    val fechaActualizacion: Long
)