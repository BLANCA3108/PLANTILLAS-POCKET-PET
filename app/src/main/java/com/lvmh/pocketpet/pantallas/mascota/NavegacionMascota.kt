package com.lvmh.pocketpet.pantallas.mascota

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

// ================== RUTAS ==================
sealed class RutasMascota(val ruta: String) {
    object Principal : RutasMascota("mascota_principal")
    object Cuidar : RutasMascota("mascota_cuidar")
    object Logros : RutasMascota("mascota_logros")
    object Desafios : RutasMascota("mascota_desafios")
    object Evolucion : RutasMascota("mascota_evolucion")
    object Mensajes : RutasMascota("mascota_mensajes")
    object Diario : RutasMascota("mascota_diario")
    object Personalizar : RutasMascota("mascota_personalizar")
    object Habitacion : RutasMascota("mascota_habitacion")
    object Estadisticas : RutasMascota("mascota_estadisticas")
    object MenuJuegos : RutasMascota("mascota_juegos")
    object BuscaMinas : RutasMascota("juego_buscaminas")
    object AtrapaMonedas : RutasMascota("juego_atrapamonedas")
}

// ================== NAVEGACIÓN ==================
@Composable
fun NavegacionMascota(
    onVolverPrincipal: (() -> Unit)? = null  // ✅ Parámetro opcional
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = RutasMascota.Principal.ruta
    ) {
        composable(RutasMascota.Principal.ruta) {
            PantallaPrincipalMascota(
                onNavegar = { ruta ->
                    navController.navigate(ruta)
                }
            )
        }

        // ===== CUIDAR MASCOTA =====
        composable(RutasMascota.Cuidar.ruta) {
            PantallaCuidarMascota(
                onVolver = {
                    navController.popBackStack()
                }
            )
        }

        // ===== LOGROS =====
        composable(RutasMascota.Logros.ruta) {
            PantallaLogros(
                onVolver = { navController.popBackStack() }
            )
        }

        // ===== DESAFÍOS =====
        composable(RutasMascota.Desafios.ruta) {
            PantallaDesafios(
                onVolver = { navController.popBackStack() }
            )
        }

        // ===== EVOLUCIÓN =====
        composable(RutasMascota.Evolucion.ruta) {
            PantallaEvolucion(
                onVolver = { navController.popBackStack() }
            )
        }

        // ===== MENSAJES =====
        composable(RutasMascota.Mensajes.ruta) {
            PantallaMensajes(
                onVolver = { navController.popBackStack() }
            )
        }

        // ===== DIARIO (pendiente) =====
        composable(RutasMascota.Diario.ruta) {
            // PantallaDiario(
            //     onVolver = { navController.popBackStack() }
            // )
        }

        // ===== PERSONALIZAR (pendiente) =====
        composable(RutasMascota.Personalizar.ruta) {
            // PantallaPersonalizar(
            //     onVolver = { navController.popBackStack() }
            // )
        }

        // ===== HABITACIÓN (pendiente) =====
        composable(RutasMascota.Habitacion.ruta) {
            // PantallaHabitacion(
            //     onVolver = { navController.popBackStack() }
            // )
        }

        // ===== ESTADÍSTICAS (pendiente) =====
        composable(RutasMascota.Estadisticas.ruta) {
            // PantallaEstadisticas(
            //     onVolver = { navController.popBackStack() }
            // )
        }

        // ===== MENÚ DE JUEGOS (pendiente) =====
        composable(RutasMascota.MenuJuegos.ruta) {
            // PantallaMenuJuegos(
            //     onVolver = { navController.popBackStack() },
            //     onJuegoSeleccionado = { juego ->
            //         when (juego) {
            //             "buscaminas" -> navController.navigate(RutasMascota.BuscaMinas.ruta)
            //             "atrapamonedas" -> navController.navigate(RutasMascota.AtrapaMonedas.ruta)
            //         }
            //     }
            // )
        }

        // ===== JUEGO: BUSCA MINAS =====
        composable(RutasMascota.BuscaMinas.ruta) {
            // JuegoBuscaMinas(
            //     onVolver = { navController.popBackStack() }
            // )
        }

        // ===== JUEGO: ATRAPA MONEDAS =====
        composable(RutasMascota.AtrapaMonedas.ruta) {
            // JuegoAtrapaMonedas(
            //     onVolver = { navController.popBackStack() }
            // )
        }
    }
}
