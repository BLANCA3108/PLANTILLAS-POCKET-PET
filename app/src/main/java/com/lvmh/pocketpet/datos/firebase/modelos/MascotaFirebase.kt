package com.lvmh.pocketpet.datos.firebase.modelos

import com.google.firebase.firestore.PropertyName

data class MascotaFirebase(
    @PropertyName("id") val id: String = "",
    @PropertyName("usuario_id") val usuarioId: String = "",
    @PropertyName("nombre") val nombre: String = "Toby",
    @PropertyName("emoji") val emoji: String = "🐕",
    @PropertyName("tipo") val tipo: String = "PERRO",
    @PropertyName("nivel") val nivel: Int = 1,
    @PropertyName("experiencia") val experiencia: Int = 0,
    @PropertyName("salud") val salud: Float = 1.0f,
    @PropertyName("felicidad") val felicidad: Float = 1.0f,
    @PropertyName("hambre") val hambre: Float = 0.5f,
    @PropertyName("energia") val energia: Float = 1.0f,
    @PropertyName("monedas") val monedas: Int = 0,
    @PropertyName("ultima_alimentacion") val ultimaAlimentacion: Long = System.currentTimeMillis(),
    @PropertyName("ultima_interaccion") val ultimaInteraccion: Long = System.currentTimeMillis(),
    @PropertyName("fecha_creacion") val fechaCreacion: Long = System.currentTimeMillis()
)