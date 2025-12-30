package com.lvmh.pocketpet.pantallas.mascota

sealed class RutasMascota(val ruta: String) {
    object Principal : RutasMascota("mascota_principal")
    object Cuidar : RutasMascota("mascota_cuidar")
    object Logros : RutasMascota("mascota_logros")
    object Desafios : RutasMascota("mascota_desafios")
    object Evolucion : RutasMascota("mascota_evolucion")
    object Mensajes : RutasMascota("mascota_mensajes")
    object Diario : RutasMascota("mascota_diario") // 👈 AGREGADO
    object Personalizar : RutasMascota("mascota_personalizar")
    object Habitacion : RutasMascota("mascota_habitacion") // 👈 AGREGADO
    object Estadisticas : RutasMascota("mascota_estadisticas")
    object MenuJuegos : RutasMascota("mascota_juegos")
    object BuscaMinas : RutasMascota("juego_buscaminas") // 👈 AGREGADO
    object AtrapaMonedas : RutasMascota("juego_atrapamonedas") // 👈 AGREGADO
}