package com.lvmh.pocketpet.datos.firebase.modelos

data class CategoriaFirebase(
    @PropertyName("id") val id: String = "",
    @PropertyName("usuario_id") val usuarioId: String = "",
    @PropertyName("nombre") val nombre: String = "",
    @PropertyName("emoji") val emoji: String = "🍔",
    @PropertyName("color") val color: String = "#FF6B6B",
    @PropertyName("tipo") val tipo: String = "GASTO",
    @PropertyName("presupuestado") val presupuestado: Double = 0.0,
    @PropertyName("gastado") val gastado: Double = 0.0,
    @PropertyName("activa") val activa: Boolean = true,
    @PropertyName("fecha_creacion") val fechaCreacion: Long = System.currentTimeMillis()
)
