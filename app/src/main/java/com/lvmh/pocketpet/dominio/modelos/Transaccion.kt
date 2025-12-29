package com.lvmh.pocketpet.dominio.modelos

data class Transaccion(
    val id: String = "",
    val usuarioId: String = "",
    val tipo: TipoTransaccion = TipoTransaccion.GASTO,
    val monto: Double = 0.0,
    val categoriaId: String = "",
    val categoriaNombre: String = "",
    val categoriaEmoji: String = "🍔",
    val fecha: Long = System.currentTimeMillis(),
    val descripcion: String = "",
    val comprobante: String? = null, // URL de la imagen del comprobante
    val metodoPago: String = "Efectivo", // Efectivo, Tarjeta, Transferencia
    val notas: String = "",
    val fechaCreacion: Long = System.currentTimeMillis(),
    val fechaModificacion: Long = System.currentTimeMillis()
) {
    val montoFormateado: String
        get() = "S/. %.2f".format(monto)

    val fechaFormateada: String
        get() {
            val sdf = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
            return sdf.format(java.util.Date(fecha))
        }
}

enum class TipoTransaccion {
    GASTO,
    INGRESO
}