package com.lvmh.pocketpet.datos.local.entidades

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mascotas")
data class MascotaEntity(
    @PrimaryKey @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "usuario_id") val usuarioId: String,
    @ColumnInfo(name = "nombre") val nombre: String,
    @ColumnInfo(name = "emoji") val emoji: String,
    @ColumnInfo(name = "tipo") val tipo: String,
    @ColumnInfo(name = "nivel") val nivel: Int,
    @ColumnInfo(name = "experiencia") val experiencia: Int,
    @ColumnInfo(name = "salud") val salud: Float,
    @ColumnInfo(name = "felicidad") val felicidad: Float,
    @ColumnInfo(name = "hambre") val hambre: Float,
    @ColumnInfo(name = "energia") val energia: Float,
    @ColumnInfo(name = "monedas") val monedas: Int,
    @ColumnInfo(name = "ultima_alimentacion") val ultimaAlimentacion: Long,
    @ColumnInfo(name = "ultima_interaccion") val ultimaInteraccion: Long,
    @ColumnInfo(name = "fecha_creacion") val fechaCreacion: Long
)