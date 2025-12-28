package com.lvmh.pocketpet.datos.local.entidades

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "metas")
data class MetaEntity(
    @PrimaryKey @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "usuario_id") val usuarioId: String,
    @ColumnInfo(name = "nombre") val nombre: String,
    @ColumnInfo(name = "descripcion") val descripcion: String,
    @ColumnInfo(name = "monto_objetivo") val montoObjetivo: Double,
    @ColumnInfo(name = "monto_actual") val montoActual: Double,
    @ColumnInfo(name = "fecha_inicio") val fechaInicio: Long,
    @ColumnInfo(name = "fecha_limite") val fechaLimite: Long,
    @ColumnInfo(name = "categoria_id") val categoriaId: String,
    @ColumnInfo(name = "icono_url") val iconoUrl: String,
    @ColumnInfo(name = "completada") val completada: Boolean,
    @ColumnInfo(name = "fecha_completada") val fechaCompletada: Long?
)