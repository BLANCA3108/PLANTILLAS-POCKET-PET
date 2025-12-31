package com.lvmh.pocketpet.dominio.modelos

data class Presupuesto(
    val id: String = "",
    val usuarioId: String = "",
    val categoriaId: String = "",
    val monto: Double = 0.0,
    val gastado: Double = 0.0,
    val periodo: String = "mensual",
    val mesInicio: Int = 1,
    val anioInicio: Int = 2025,
    val activo: Boolean = true,
    val alertaEn: Int = 80,
    val fechaCreacion: Long = System.currentTimeMillis()
) {
    val porcentajeGastado: Double
        get() = if (monto > 0) (gastado / monto) * 100 else 0.0

    val disponible: Double
        get() = monto - gastado

    val excedido: Boolean
        get() = gastado > monto
}