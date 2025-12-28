package com.lvmh.pocketpet.DateBase

data class Transaccion(
    val id: String,
    val tipo: String,
    val monto: String,
    val categoria: String,
    val fecha: String,
    val descripcion: String
)