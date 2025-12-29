package com.lvmh.pocketpet.dominio.modelos

data class Configuracion(
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
)