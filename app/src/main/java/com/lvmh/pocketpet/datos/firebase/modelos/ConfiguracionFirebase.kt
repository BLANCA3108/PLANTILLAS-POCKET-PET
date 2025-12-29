package com.lvmh.pocketpet.datos.firebase.modelos

data class ConfiguracionFirebase(
    val id: String = "",
    val userId: String = "",
    val notificacionesActivas: Boolean = true,
    val recordatoriosActivos: Boolean = true,
    val monedaPredeterminada: String = "PEN - Sol Peruano",
    val recordatoriosCuidadoActivos: Boolean = true,
    val sonidosActivos: Boolean = true,
    val animacionesActivas: Boolean = true,
    val fechaCreacion: Long = System.currentTimeMillis(),
    val fechaActualizacion: Long = System.currentTimeMillis()
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "userId" to userId,
        "notificacionesActivas" to notificacionesActivas,
        "recordatoriosActivos" to recordatoriosActivos,
        "monedaPredeterminada" to monedaPredeterminada,
        "recordatoriosCuidadoActivos" to recordatoriosCuidadoActivos,
        "sonidosActivos" to sonidosActivos,
        "animacionesActivas" to animacionesActivas,
        "fechaCreacion" to fechaCreacion,
        "fechaActualizacion" to fechaActualizacion
    )

    companion object {
        fun fromMap(map: Map<String, Any?>): ConfiguracionFirebase {
            return ConfiguracionFirebase(
                id = map["id"] as? String ?: "",
                userId = map["userId"] as? String ?: "",
                notificacionesActivas = map["notificacionesActivas"] as? Boolean ?: true,
                recordatoriosActivos = map["recordatoriosActivos"] as? Boolean ?: true,
                monedaPredeterminada = map["monedaPredeterminada"] as? String ?: "PEN - Sol Peruano",
                recordatoriosCuidadoActivos = map["recordatoriosCuidadoActivos"] as? Boolean ?: true,
                sonidosActivos = map["sonidosActivos"] as? Boolean ?: true,
                animacionesActivas = map["animacionesActivas"] as? Boolean ?: true,
                fechaCreacion = map["fechaCreacion"] as? Long ?: System.currentTimeMillis(),
                fechaActualizacion = map["fechaActualizacion"] as? Long ?: System.currentTimeMillis()
            )
        }
    }
}