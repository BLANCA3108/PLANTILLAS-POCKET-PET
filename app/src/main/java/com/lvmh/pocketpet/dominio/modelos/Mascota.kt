package com.lvmh.pocketpet.dominio.modelos

data class Mascota(
    val id: String = "",
    val usuarioId: String = "",
    val nombre: String = "Toby",
    val emoji: String = "🐕",
    val tipo: TipoMascota = TipoMascota.PERRO,
    val nivel: Int = 1,
    val experiencia: Int = 0,
    val salud: Float = 1.0f, // 0.0 a 1.0
    val felicidad: Float = 1.0f, // 0.0 a 1.0
    val hambre: Float = 0.5f, // 0.0 (lleno) a 1.0 (hambriento)
    val energia: Float = 1.0f, // 0.0 a 1.0
    val monedas: Int = 0,
    val ultimaAlimentacion: Long = System.currentTimeMillis(),
    val ultimaInteraccion: Long = System.currentTimeMillis(),
    val fechaCreacion: Long = System.currentTimeMillis()
) {
    val experienciaSiguienteNivel: Int
        get() = nivel * 100

    val progresoNivel: Float
        get() = if (experienciaSiguienteNivel > 0) {
            (experiencia.toFloat() / experienciaSiguienteNivel).coerceIn(0f, 1f)
        } else 0f

    val estadoGeneral: EstadoMascota
        get() = when {
            salud < 0.3f || felicidad < 0.3f || hambre > 0.8f -> EstadoMascota.CRITICO
            salud < 0.5f || felicidad < 0.5f || hambre > 0.6f -> EstadoMascota.NECESITA_ATENCION
            salud > 0.8f && felicidad > 0.8f && hambre < 0.3f -> EstadoMascota.EXCELENTE
            else -> EstadoMascota.BIEN
        }

    val emojiEstado: String
        get() = when (estadoGeneral) {
            EstadoMascota.EXCELENTE -> "😄"
            EstadoMascota.BIEN -> "😊"
            EstadoMascota.NECESITA_ATENCION -> "😐"
            EstadoMascota.CRITICO -> "😢"
        }
}

enum class TipoMascota(val emoji: String) {
    PERRO("🐕"),
    GATO("🐱"),
    CONEJO("🐰"),
    HAMSTER("🐹"),
    PANDA("🐼"),
    KOALA("🐨")
}

enum class EstadoMascota {
    EXCELENTE,
    BIEN,
    NECESITA_ATENCION,
    CRITICO
}