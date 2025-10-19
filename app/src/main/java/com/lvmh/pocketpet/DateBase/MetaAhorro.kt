package com.lvmh.pocketpet.DateBase

data class MetaAhorro(
    val id: Long,
    val titulo: String,
    val montoAhorrado: Float,
    val montoTotal: Float,
    val fechaInicio: String,
    val fechaFin: String,
    val emoji: String = " "
)