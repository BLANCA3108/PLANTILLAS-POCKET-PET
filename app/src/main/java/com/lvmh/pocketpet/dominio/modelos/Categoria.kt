package com.lvmh.pocketpet.dominio.modelos

data class Categoria(
    val id: String = "",
    val usuarioId: String = "",
    val nombre: String = "",
    val emoji: String = "🍔",
    val color: String = "#FF6B6B",
    val tipo: TipoCategoria = TipoCategoria.GASTO,
    val presupuestado: Double = 0.0,
    val gastado: Double = 0.0,
    val activa: Boolean = true,
    val fechaCreacion: Long = System.currentTimeMillis()
) {
    val progreso: Float
        get() = if (presupuestado > 0) (gastado / presupuestado * 100).toFloat() else 0f

    val disponible: Double
        get() = presupuestado - gastado
}

enum class TipoCategoria {
    GASTO,
    INGRESO
}

// Función de extensión para convertir TipoCategoria a TipoTransaccion
fun TipoCategoria.toTipoTransaccion(): TipoTransaccion {
    return when (this) {
        TipoCategoria.GASTO -> TipoTransaccion.GASTO
        TipoCategoria.INGRESO -> TipoTransaccion.INGRESO
    }
}

// Función de extensión para convertir TipoTransaccion a TipoCategoria
fun TipoTransaccion.toTipoCategoria(): TipoCategoria {
    return when (this) {
        TipoTransaccion.GASTO -> TipoCategoria.GASTO
        TipoTransaccion.INGRESO -> TipoCategoria.INGRESO
    }
}