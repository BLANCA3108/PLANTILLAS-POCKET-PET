package com.lvmh.pocketpet.datos.firebase.modelos

import com.google.firebase.firestore.PropertyName

data class TransaccionFirebase(
    @PropertyName("id") val id: String = "",
    @PropertyName("usuario_id") val usuarioId: String = "",
    @PropertyName("tipo") val tipo: String = "GASTO",
    @PropertyName("monto") val monto: Double = 0.0,
    @PropertyName("categoria_id") val categoriaId: String = "",
    @PropertyName("categoria_nombre") val categoriaNombre: String = "",
    @PropertyName("categoria_emoji") val categoriaEmoji: String = "🍔",
    @PropertyName("fecha") val fecha: Long = System.currentTimeMillis(),
    @PropertyName("descripcion") val descripcion: String = "",
    @PropertyName("comprobante") val comprobante: String? = null,
    @PropertyName("metodo_pago") val metodoPago: String = "Efectivo",
    @PropertyName("notas") val notas: String = "",
    @PropertyName("fecha_creacion") val fechaCreacion: Long = System.currentTimeMillis(),
    @PropertyName("fecha_modificacion") val fechaModificacion: Long = System.currentTimeMillis()
)