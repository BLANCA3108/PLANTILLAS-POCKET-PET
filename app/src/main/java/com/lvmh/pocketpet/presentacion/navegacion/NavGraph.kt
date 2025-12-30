package com.lvmh.pocketpet.presentacion.navegacion

import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.firestore.FirebaseFirestore
import com.lvmh.pocketpet.datos.local.AppDatabase
import com.lvmh.pocketpet.pantallas.*
import com.lvmh.pocketpet.pantallas.mascota.NavegacionMascota
import com.lvmh.pocketpet.presentacion.auth.LoginScreen
import com.lvmh.pocketpet.presentacion.auth.RegistroScreen
import com.lvmh.pocketpet.presentacion.pantallas.PantallaPrincipal
import com.lvmh.pocketpet.presentacion.pantallas.estadisticas.*
import com.lvmh.pocketpet.presentacion.viewmodels.*
import com.lvmh.pocketpet.viewmodels.TransaccionViewModel

@Composable
fun PocketPetNavGraph() {

    val navController = rememberNavController()

    // ===============================
    // 🔐 AUTH
    // ===============================
    val authViewModel: AuthViewModel = hiltViewModel()
    val isAuthenticated by authViewModel.isAuthenticated.collectAsState()

    // ===============================
    // 🗄️ DEPENDENCIAS GLOBALES (HILT)
    // ===============================
    val databaseViewModel: DatabaseViewModel = hiltViewModel()
    val firestoreViewModel: FirestoreViewModel = hiltViewModel()

    val database: AppDatabase = databaseViewModel.database
    val firestore: FirebaseFirestore = firestoreViewModel.firestore

    val startDestination = if (isAuthenticated) {
        Routes.PRINCIPAL
    } else {
        Routes.LOGO
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        // ===============================
        // 🔐 AUTENTICACIÓN
        // ===============================

        composable(Routes.LOGIN) {
            LoginScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }

        composable(Routes.REGISTRO) {
            RegistroScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }

        // ===============================
        // 🎯 ONBOARDING
        // ===============================

        composable(Routes.LOGO) {
            Logo {
                navController.navigate(Routes.SLIDE1) {
                    popUpTo(Routes.LOGO) { inclusive = true }
                }
            }
        }

        composable(Routes.SLIDE1) {
            Slide1 { navController.navigate(Routes.SLIDE2) }
        }

        composable(Routes.SLIDE2) {
            Slide2 { navController.navigate(Routes.SLIDE3) }
        }

        composable(Routes.SLIDE3) {
            Slide3 {
                navController.navigate(Routes.LOGIN) {
                    popUpTo(Routes.LOGO) { inclusive = true }
                }
            }
        }

        // ===============================
        // 📱 PRINCIPAL
        // ===============================

        composable(Routes.PRINCIPAL) {
            if (isAuthenticated) {
                val viewModel: TransaccionViewModel = hiltViewModel()
                PantallaPrincipal(
                    viewModel = viewModel,
                    alNavegar = { ruta ->
                        navController.navigate(ruta)
                    }
                )
            } else {
                LaunchedEffect(Unit) {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.PRINCIPAL) { inclusive = true }
                    }
                }
            }
        }

        // ===============================
        // 👤 PERFIL
        // ===============================

        composable(Routes.MI_PERFIL) {
            MiPerfil { navController.popBackStack() }
        }

        // ===============================
        // 📊 ESTADÍSTICAS
        // ===============================

        composable(Routes.ESTADISTICAS) {
            val viewModel: EstadisticasViewModel = hiltViewModel()
            PantallaEstadisticas(
                viewModel = viewModel,
                alNavegar = { navController.navigate(it) }
            )
        }

        composable(Routes.ESTADISTICAS_CATEGORIAS) {
            val viewModel: EstadisticasViewModel = hiltViewModel()
            PantallaEstadisticasCategorias(viewModel) {
                navController.popBackStack()
            }
        }

        composable(Routes.COMPARATIVOS) {
            val viewModel: EstadisticasViewModel = hiltViewModel()
            PantallaComparativos(viewModel) {
                navController.popBackStack()
            }
        }

        composable(Routes.TENDENCIAS) {
            val viewModel: EstadisticasViewModel = hiltViewModel()
            PantallaTendencias(viewModel) {
                navController.popBackStack()
            }
        }

        composable(Routes.REPORTES) {
            val viewModel: EstadisticasViewModel = hiltViewModel()
            PantallaReportes(viewModel) {
                navController.popBackStack()
            }
        }

        composable(Routes.CALENDARIO) {
            val viewModel: EstadisticasViewModel = hiltViewModel()
            PantallaCalendario(
                viewModel = viewModel,
                usuarioId = "usuario_demo_001"
            ) {
                navController.popBackStack()
            }
        }

        // ===============================
        // 💰 PRESUPUESTOS / METAS
        // ===============================

        composable(Routes.PRESUPUESTOS) {
            val viewModel: PresupuestoViewModel = hiltViewModel()
            PantallaPresupuestos(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Routes.METAS) {
            PantallaMetas(
                usuarioId = "usuario_demo_001",
                alRegresar = { navController.popBackStack() }
            )
        }

        // ===============================
        // ⚙️ CONFIG
        // ===============================

        composable(Routes.CONFIGURACION) {
            Configuracion { navController.popBackStack() }
        }

        composable(Routes.CATEGORIAS) {
            Categorias { navController.popBackStack() }
        }

        // ===============================
        // 🐾 MASCOTA (YA CORREGIDO)
        // ===============================

        composable(Routes.MASCOTA) {

            val userId by authViewModel.userId.collectAsState()
            val userIdActual = userId ?: "usuario_temp"

            NavegacionMascota(
                usuarioId = userIdActual,
                database = database,
                firestore = firestore,
                onVolverPrincipal = {
                    navController.navigate(Routes.PRINCIPAL) {
                        popUpTo(Routes.PRINCIPAL) { inclusive = false }
                    }
                }
            )
        }
    }
}
