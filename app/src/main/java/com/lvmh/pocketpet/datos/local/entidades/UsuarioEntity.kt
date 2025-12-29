package com.lvmh.pocketpet.datos.local.entidades

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class UsuarioEntity(
    @PrimaryKey @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "nombre") val nombre: String,
    @ColumnInfo(name = "email") val email: String,
    @ColumnInfo(name = "avatar_url") val avatarUrl: String?,
    @ColumnInfo(name = "telefono") val telefono: String,
    @ColumnInfo(name = "fecha_nacimiento") val fechaNacimiento: Long?,
    @ColumnInfo(name = "moneda_principal") val monedaPrincipal: String,
    @ColumnInfo(name = "fecha_registro") val fechaRegistro: Long,
    @ColumnInfo(name = "ultima_actualizacion") val ultimaActualizacion: Long,
    @ColumnInfo(name = "activo") val activo: Boolean
)