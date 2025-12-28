package com.lvmh.pocketpet.datos.firebase.modelos

import com.google.firebase.firestore.PropertyName

data class UsuarioFirebase(
    @PropertyName("id") val id: String = "",
    @PropertyName("nombre") val nombre: String = "",
    @PropertyName("email") val email: String = "",
    @PropertyName("avatar_url") val avatarUrl: String? = null,
    @PropertyName("telefono") val telefono: String = "",
    @PropertyName("fecha_nacimiento") val fechaNacimiento: Long? = null,
    @PropertyName("moneda_principal") val monedaPrincipal: String = "PEN",
    @PropertyName("fecha_registro") val fechaRegistro: Long = System.currentTimeMillis(),
    @PropertyName("ultima_actualizacion") val ultimaActualizacion: Long = System.currentTimeMillis(),
    @PropertyName("activo") val activo: Boolean = true
)