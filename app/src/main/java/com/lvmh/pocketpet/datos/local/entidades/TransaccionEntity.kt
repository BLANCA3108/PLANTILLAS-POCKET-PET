package com.lvmh.pocketpet.datos.local.entidades

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transacciones")
data class TransaccionEntity(
    @PrimaryKey @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "usuario_id") val usuarioId: String,
    @ColumnInfo(name = "tipo") val tipo: String,
    @ColumnInfo(name = "monto") val monto: Double,
    @ColumnInfo(name = "categoria_id") val categoriaId: String,
    @ColumnInfo(name = "categoria_nombre") val categoriaNombre: String,
    @ColumnInfo(name = "categoria_emoji") val categoriaEmoji: String,
    @ColumnInfo(name = "fecha") val fecha: Long,
    @ColumnInfo(name = "descripcion") val descripcion: String,
    @ColumnInfo(name = "comprobante") val comprobante: String?,
    @ColumnInfo(name = "metodo_pago") val metodoPago: String,
    @ColumnInfo(name = "notas") val notas: String,
    @ColumnInfo(name = "fecha_creacion") val fechaCreacion: Long,
    @ColumnInfo(name = "fecha_modificacion") val fechaModificacion: Long
)