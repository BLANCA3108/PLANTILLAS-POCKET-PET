package com.lvmh.pocketpet.dominio.modelos

data class Usuario(
    val id: String = "",
    val nombre: String = "",
    val email: String = "",
    val avatarUrl: String? = null,
    val telefono: String = "",
    val fechaNacimiento: Long? = null,
    val monedaPrincipal: String = "PEN", // Soles peruanos
    val fechaRegistro: Long = System.currentTimeMillis(),
    val ultimaActualizacion: Long = System.currentTimeMillis(),
    val activo: Boolean = true
) {
    val nombreCompleto: String
        get() = nombre.ifEmpty { "Usuario PocketPet" }

    val iniciales: String
        get() {
            val palabras = nombre.trim().split(" ")
            return if (palabras.size >= 2) {
                "${palabras[0].first()}${palabras[1].first()}".uppercase()
            } else {
                nombre.take(2).uppercase()
            }
        }
}