package com.lvmh.pocketpet.dominio.modelos

data class Meta(
    val id: String = "",
    val usuarioId: String = "",
    val nombre: String = "",
    val descripcion: String = "",
    val montoObjetivo: Double = 0.0,
    val montoActual: Double = 0.0,
    val fechaInicio: Long = System.currentTimeMillis(),
    val fechaLimite: Long = System.currentTimeMillis(),
    val categoriaId: String = "",
    val iconoUrl: String = "",
    val completada: Boolean = false,
    val fechaCompletada: Long? = null
) {
    val porcentajeCompletado: Double
        get() = if (montoObjetivo > 0) (montoActual / montoObjetivo) * 100 else 0.0

    val faltante: Double
        get() = montoObjetivo - montoActual

    val diasRestantes: Long
        get() {
            val diff = fechaLimite - System.currentTimeMillis()
            return diff / (1000 * 60 * 60 * 24)
        }
}