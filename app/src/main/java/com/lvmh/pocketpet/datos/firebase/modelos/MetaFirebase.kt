package com.lvmh.pocketpet.datos.firebase.modelos

import com.google.firebase.firestore.PropertyName

data class MetaFirebase(
    @PropertyName("id") val id: String = "",
    @PropertyName("usuario_id") val usuarioId: String = "",
    @PropertyName("nombre") val nombre: String = "",
    @PropertyName("descripcion") val descripcion: String = "",
    @PropertyName("monto_objetivo") val montoObjetivo: Double = 0.0,
    @PropertyName("monto_actual") val montoActual: Double = 0.0,
    @PropertyName("fecha_inicio") val fechaInicio: Long = System.currentTimeMillis(),
    @PropertyName("fecha_limite") val fechaLimite: Long = System.currentTimeMillis(),
    @PropertyName("categoria_id") val categoriaId: String = "",
    @PropertyName("icono_url") val iconoUrl: String = "",
    @PropertyName("completada") val completada: Boolean = false,
    @PropertyName("fecha_completada") val fechaCompletada: Long? = null
)