package com.lvmh.pocketpet.DateBase

data class Presupuesto(
    val id: Int,
    val nombre: String,
    val monto: Double,
    var gastado: Double = 0.0
) {
    val quedan: Double
        get() = monto - gastado

    val porcentaje: Int
        get() = if (monto > 0) ((gastado / monto) * 100).toInt() else 0
}