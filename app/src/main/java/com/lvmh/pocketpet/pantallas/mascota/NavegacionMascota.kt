package com.lvmh.pocketpet.pantallas.mascota

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.firestore.FirebaseFirestore
import com.lvmh.pocketpet.datos.firebase.fuentesdatos.MascotaFirebaseDataSource
import com.lvmh.pocketpet.datos.local.AppDatabase
import com.lvmh.pocketpet.datos.repositorios.MascotaRepository
import com.example.mascotafinanciera.pantallas.juegos.*

@Composable
fun NavegacionMascota(
    usuarioId: String,
    database: AppDatabase,
    firestore: FirebaseFirestore,
    onVolverPrincipal: (() -> Unit)? = null
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = RutasMascota.Principal.ruta
    ) {

        composable(RutasMascota.Principal.ruta) {

            val repository = MascotaRepository(
                firebaseDataSource = MascotaFirebaseDataSource(firestore),
                mascotaDao = database.mascotaDao()
            )

            val viewModel: MascotaViewModel = viewModel(
                factory = MascotaViewModelFactory(
                    repository = repository,
                    usuarioId = usuarioId
                )
            )

            val debeMostrarPantallaPrincipal by viewModel.debeMostrarPantallaPrincipal.collectAsState()

            LaunchedEffect(debeMostrarPantallaPrincipal) {
                if (debeMostrarPantallaPrincipal) {
                    viewModel.resetearNavegacion()
                }
            }

            PantallaPrincipalMascota(
                viewModel = viewModel,
                onNavegar = { ruta ->
                    when (ruta) {
                        // 🔥 RUTAS QUE VUELVEN A LA APP PRINCIPAL
                        "inicio" -> onVolverPrincipal?.invoke()
                        "configuracion" -> onVolverPrincipal?.invoke()
                        "categorias" -> onVolverPrincipal?.invoke()
                        "perfil" -> onVolverPrincipal?.invoke()

                        // 🎮 RUTAS INTERNAS DE LA MASCOTA
                        else -> {
                            // Verificar si la ruta existe en RutasMascota
                            try {
                                navController.navigate(ruta)
                            } catch (e: Exception) {
                                // Si la ruta no existe, volver al principal
                                println("⚠️ Ruta no encontrada: $ruta")
                                onVolverPrincipal?.invoke()
                            }
                        }
                    }
                }
            )
        }

        composable(RutasMascota.Cuidar.ruta) {
            PantallaCuidarMascota { navController.popBackStack() }
        }

        composable(RutasMascota.Logros.ruta) {
            PantallaLogros { navController.popBackStack() }
        }

        composable(RutasMascota.Desafios.ruta) {
            PantallaDesafios { navController.popBackStack() }
        }

        composable(RutasMascota.Evolucion.ruta) {
            PantallaEvolucion { navController.popBackStack() }
        }

        composable(RutasMascota.Mensajes.ruta) {
            PantallaMensajes { navController.popBackStack() }
        }

        composable(RutasMascota.Diario.ruta) {
            PantallaDiarioFinanciero { navController.popBackStack() }
        }

        composable(RutasMascota.Personalizar.ruta) {
            PantallaPersonalizarMascota { navController.popBackStack() }
        }

        composable(RutasMascota.Habitacion.ruta) {
            PantallaHabitacionMascota { navController.popBackStack() }
        }

        composable(RutasMascota.Estadisticas.ruta) {
            PantallaEstadisticasMascota { navController.popBackStack() }
        }

        composable(RutasMascota.MenuJuegos.ruta) {
            PantallaMenuJuegos(
                onVolver = { navController.popBackStack() },
                onJuegoSeleccionado = { juego ->
                    when (juego) {
                        "buscaminas" ->
                            navController.navigate(RutasMascota.BuscaMinas.ruta)
                        "atrapamonedas" ->
                            navController.navigate(RutasMascota.AtrapaMonedas.ruta)
                    }
                }
            )
        }

        composable(RutasMascota.BuscaMinas.ruta) {
            PantallaBuscaMinasJuego { navController.popBackStack() }
        }

        composable(RutasMascota.AtrapaMonedas.ruta) {
            PantallaAtrapaMonedasJuego { navController.popBackStack() }
        }
    }
}