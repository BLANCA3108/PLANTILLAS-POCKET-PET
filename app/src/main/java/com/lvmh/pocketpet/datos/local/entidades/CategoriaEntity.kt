package com.lvmh.pocketpet.datos.local.entidades

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categorias")
data class CategoriaEntity(
    @PrimaryKey @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "usuario_id") val usuarioId: String,
    @ColumnInfo(name = "nombre") val nombre: String,
    @ColumnInfo(name = "emoji") val emoji: String,
    @ColumnInfo(name = "color") val color: String,
    @ColumnInfo(name = "tipo") val tipo: String,
    @ColumnInfo(name = "presupuestado") val presupuestado: Double,
    @ColumnInfo(name = "gastado") val gastado: Double,
    @ColumnInfo(name = "activa") val activa: Boolean,
    @ColumnInfo(name = "fecha_creacion") val fechaCreacion: Long
)