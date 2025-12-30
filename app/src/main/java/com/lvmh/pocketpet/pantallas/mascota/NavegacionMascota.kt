package com.lvmh.pocketpet.pantallas.mascota

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
// ✅ IMPORTS DE LOS JUEGOS (paquete diferente)
import com.example.mascotafinanciera.pantallas.juegos.PantallaAtrapaMonedasJuego
import com.example.mascotafinanciera.pantallas.juegos.PantallaBuscaMinasJuego
import com.example.mascotafinanciera.pantallas.juegos.PantallaMenuJuegos

// ================== NAVEGACIÓN ==================
@Composable
fun NavegacionMascota(onVolverPrincipal: (() -> Unit)? = null) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = RutasMascota.Principal.ruta
    ) {
        // ===== PANTALLA PRINCIPAL =====
        composable(RutasMascota.Principal.ruta) {
            PantallaPrincipalMascota(
                onNavegar = { ruta ->
                    when (ruta) {
                        "inicio" -> {
                            // 👈 Volver a la pantalla principal de la app
                            onVolverPrincipal?.invoke()
                        }
                        else -> {
                            navController.navigate(ruta)
                        }
                    }
                }
            )
        }

        // ===== CUIDAR MASCOTA =====
        composable(RutasMascota.Cuidar.ruta) {
            PantallaCuidarMascota(
                onVolver = { navController.popBackStack() }
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

        // ===== DIARIO =====
        composable(RutasMascota.Diario.ruta) {
            PantallaDiarioFinanciero(
                onVolver = { navController.popBackStack() }
            )
        }

        // ===== PERSONALIZAR/TIENDA =====
        composable(RutasMascota.Personalizar.ruta) {
            PantallaPersonalizarMascota(
                onVolver = { navController.popBackStack() }
            )
        }

        // ===== HABITACIÓN =====
        composable(RutasMascota.Habitacion.ruta) {
            PantallaHabitacionMascota(
                onVolver = { navController.popBackStack() }
            )
        }

        // ===== ESTADÍSTICAS DETALLADAS =====
        composable(RutasMascota.Estadisticas.ruta) {
            PantallaEstadisticasMascota(
                onVolver = { navController.popBackStack() }
            )
        }

        // ===== MENÚ DE JUEGOS =====
        composable(RutasMascota.MenuJuegos.ruta) {
            PantallaMenuJuegos(
                onVolver = { navController.popBackStack() },
                onJuegoSeleccionado = { juego ->
                    when (juego) {
                        "buscaminas" -> navController.navigate(RutasMascota.BuscaMinas.ruta)
                        "atrapamonedas" -> navController.navigate(RutasMascota.AtrapaMonedas.ruta)
                    }
                }
            )
        }

        // ===== JUEGO: BUSCA MINAS =====
        composable(RutasMascota.BuscaMinas.ruta) {
            PantallaBuscaMinasJuego(
                onVolver = { navController.popBackStack() }
            )
        }

        // ===== JUEGO: ATRAPA MONEDAS =====
        composable(RutasMascota.AtrapaMonedas.ruta) {
            PantallaAtrapaMonedasJuego(
                onVolver = { navController.popBackStack() }
            )
        }
    }
}
