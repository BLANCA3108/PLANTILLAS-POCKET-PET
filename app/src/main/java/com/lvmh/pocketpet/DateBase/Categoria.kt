package com.lvmh.pocketpet.DateBase

data class Categoria(
    val nombre: String,
    val presupuestado: Float = 0f,
    val gastado: Float = 0f,
    val emoji: String = "🍔"
)