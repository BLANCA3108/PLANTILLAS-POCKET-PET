package com.lvmh.pocketpet.datos.local.entidades

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "presupuestos")
data class PresupuestoEntity(
    @PrimaryKey @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "usuario_id") val usuarioId: String,
    @ColumnInfo(name = "categoria_id") val categoriaId: String,
    @ColumnInfo(name = "monto") val monto: Double,
    @ColumnInfo(name = "gastado") val gastado: Double,
    @ColumnInfo(name = "periodo") val periodo: String,
    @ColumnInfo(name = "mes_inicio") val mesInicio: Int,
    @ColumnInfo(name = "anio_inicio") val anioInicio: Int,
    @ColumnInfo(name = "activo") val activo: Boolean,
    @ColumnInfo(name = "alerta_en") val alertaEn: Int,
    @ColumnInfo(name = "fecha_creacion") val fechaCreacion: Long
)
