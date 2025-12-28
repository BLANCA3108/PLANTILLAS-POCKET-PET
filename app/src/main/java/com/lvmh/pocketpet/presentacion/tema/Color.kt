package com.lvmh.pocketpet.presentacion.tema

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

val NaranjaPastel = Color(0xFFFFB347)

val FondoBlanco = Color(0xFFFFFFFF)
val FondoApp = Color(0xFFF8F9FA)
val FondoClaro = Color(0xFFF5F6FA)
val GrisBackground = Color(0xFFF5F6FA)

val GrisTexto = Color(0xFF2D3436)
val GrisMedio = Color(0xFF636E72)
val GrisClaro = Color(0xFFDFE6E9)
val GrisOscuro = Color(0xFF1A1A1A)

val FondoOscuro = Color(0xFF121212)
val SuperficieOscura = Color(0xFF1E1E1E)
val SuperficieClara = Color(0xFFFFFFFF)

val AzulPrimario = Color(0xFF6366F1)
val AzulPrimarioVariante = Color(0xFF4F46E5)
val AzulSecundario = Color(0xFF8B5CF6)

val VerdePrimario = Color(0xFF10B981)
val VerdeSecundario = Color(0xFF34D399)

val RojoPrimario = Color(0xFFEF4444)
val RojoSecundario = Color(0xFFF87171)

val AmarilloPrimario = Color(0xFFF59E0B)
val AmarilloSecundario = Color(0xFFFBBF24)

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

fun obtenerColorNivel(nivel: Int): Color {
    return when {
        nivel >= 20 -> MoradoPrincipal
        nivel >= 15 -> AzulPastel
        nivel >= 10 -> VerdeMenta
        nivel >= 5 -> AmarilloPastel
        else -> RosaPastel
    }
}

fun obtenerColorCategoria(categoria: String): Color {
    return when (categoria.lowercase()) {
        "alimentacion", "comida" -> CategoriasColores.Alimentacion
        "transporte" -> CategoriasColores.Transporte
        "entretenimiento", "ocio" -> CategoriasColores.Entretenimiento
        "salud", "medicina" -> CategoriasColores.Salud
        "educacion", "estudio" -> CategoriasColores.Educacion
        "vivienda", "hogar" -> CategoriasColores.Vivienda
        else -> CategoriasColores.Otros
    }
}