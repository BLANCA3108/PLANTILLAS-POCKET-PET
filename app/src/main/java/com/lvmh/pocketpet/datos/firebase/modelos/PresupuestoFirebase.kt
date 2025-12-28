package com.lvmh.pocketpet.datos.firebase.modelos

import com.google.firebase.firestore.PropertyName

data class PresupuestoFirebase(
    @PropertyName("id") val id: String = "",
    @PropertyName("usuario_id") val usuarioId: String = "",
    @PropertyName("categoria_id") val categoriaId: String = "",
    @PropertyName("monto") val monto: Double = 0.0,
    @PropertyName("gastado") val gastado: Double = 0.0,
    @PropertyName("periodo") val periodo: String = "mensual",
    @PropertyName("mes_inicio") val mesInicio: Int = 1,
    @PropertyName("anio_inicio") val anioInicio: Int = 2025,
    @PropertyName("activo") val activo: Boolean = true,
    @PropertyName("alerta_en") val alertaEn: Int = 80,
    @PropertyName("fecha_creacion") val fechaCreacion: Long = System.currentTimeMillis()
)