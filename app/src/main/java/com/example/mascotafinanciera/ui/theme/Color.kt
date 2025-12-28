package com.example.mascotafinanciera.ui.theme

import androidx.compose.ui.graphics.Color

val MoradoPrincipal = Color(0xFF7B68EE)
val MoradoClaro = Color(0xFFB8A9F5)
val AzulHeader = Color(0xFF6B7FED)
val VerdeMenta = Color(0xFF7FE5B8)
val VerdeMentaClaro = Color(0xFFB2F5E0)
val VerdeMentaOscuro = Color(0xFF4ECDA0)
val RosaPastel = Color(0xFFFFB6D9)
val RosaPastelClaro = Color(0xFFFFD4E8)
val RosaPastelMedio = Color(0xFFFF9EC9)
val AzulPastel = Color(0xFF9DB4FF)
val AzulCielo = Color(0xFFC5D7FF)
val CoralPastel = Color(0xFFFFB8A5)
val DuraznoClaro = Color(0xFFFFD7C8)
val AmarilloSuave = Color(0xFFFFF4D6)
val AmarilloPastel = Color(0xFFFFE59E)
val TurquesaPastel = Color(0xFF7FE5E5)
val GrisTexto = Color(0xFF2D3436)
val GrisMedio = Color(0xFF636E72)
val GrisClaro = Color(0xFFDFE6E9)
val GrisBackground = Color(0xFFF5F6FA)
val FondoBlanco = Color(0xFFFFFFFF)
val FondoApp = Color(0xFFF8F9FA)
val NaranjaPastel = Color( color = 0xFFFFB347 )
object EstadoMascotaColores {
    val Critico = CoralPastel
    val Alerta = AmarilloPastel
    val Estable = AzulPastel
    val Saludable = VerdeMenta
    val Prospero = MoradoPrincipal
}
object CategoriasColores {
    val Alimentacion = CoralPastel
    val Transporte = AzulPastel
    val Entretenimiento = MoradoClaro
    val Salud = VerdeMenta
    val Educacion = TurquesaPastel
    val Vivienda = AmarilloPastel
    val Otros = GrisMedio
}

object AccionColores {
    val Verde = VerdeMenta
    val Rojo = CoralPastel
    val Morado = MoradoPrincipal
    val Azul = AzulHeader
}
fun obtenerColorSalud(salud: Int): Color {
    return when {
        salud >= 80 -> VerdeMenta
        salud >= 50 -> AmarilloPastel
        salud >= 30 -> NaranjaPastel
        else -> CoralPastel
    }
}

fun obtenerEmojiMascota(salud: Int): String {
    return when {
        salud >= 80 -> "😊"
        salud >= 50 -> "🙂"
        salud >= 30 -> "😐"
        else -> "😢"
    }
}