package com.lvmh.pocketpet.presentacion.navegacion

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.*
import com.google.firebase.firestore.FirebaseFirestore
import com.lvmh.pocketpet.datos.local.AppDatabase
import com.lvmh.pocketpet.pantallas.*
import com.lvmh.pocketpet.pantallas.mascota.NavegacionMascota
import com.lvmh.pocketpet.presentacion.auth.LoginScreen
import com.lvmh.pocketpet.presentacion.auth.RegistroScreen
import com.lvmh.pocketpet.presentacion.pantallas.*
import com.lvmh.pocketpet.presentacion.pantallas.estadisticas.*
import com.lvmh.pocketpet.presentacion.viewmodels.*
import com.lvmh.pocketpet.viewmodels.TransaccionViewModel

@Composable
fun PocketPetNavGraph() {

    val navController = rememberNavController()
    val context = LocalContext.current

    // 🔐 Auth
    val authViewModel: AuthViewModel = hiltViewModel()
    val isAuthenticated by authViewModel.isAuthenticated.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()

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
            LoginScreen(navController, authViewModel)
        }

        composable(Routes.REGISTRO) {
            RegistroScreen(navController, authViewModel)
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

            if (!isAuthenticated) {
                LaunchedEffect(Unit) {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.PRINCIPAL) { inclusive = true }
                    }
                }
                return@composable
            }

            val transaccionVM: TransaccionViewModel = hiltViewModel()
            val categoriaVM: CategoriaViewModel = hiltViewModel()

            PantallaPrincipal(
                viewModel = transaccionVM,
                categoriaViewModel = categoriaVM,
                alNavegar = navController::navigate
            )
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
            val vm: EstadisticasViewModel = hiltViewModel()
            PantallaEstadisticas(vm, navController::navigate)
        }

        composable(Routes.ESTADISTICAS_CATEGORIAS) {
            val vm: EstadisticasViewModel = hiltViewModel()
            PantallaEstadisticasCategorias(vm) {
                navController.popBackStack()
            }
        }

        composable(Routes.COMPARATIVOS) {
            val vm: EstadisticasViewModel = hiltViewModel()
            PantallaComparativos(vm) {
                navController.popBackStack()
            }
        }

        composable(Routes.TENDENCIAS) {
            val vm: EstadisticasViewModel = hiltViewModel()
            PantallaTendencias(vm) {
                navController.popBackStack()
            }
        }

        composable(Routes.REPORTES) {
            val vm: EstadisticasViewModel = hiltViewModel()
            PantallaReportes(vm) {
                navController.popBackStack()
            }
        }

        composable(Routes.CALENDARIO) {
            val vm: EstadisticasViewModel = hiltViewModel()
            PantallaCalendario(
                viewModel = vm,
                usuarioId = currentUser?.uid ?: "usuario_demo_001"
            ) {
                navController.popBackStack()
            }
        }

        // ===============================
        // 💰 PRESUPUESTOS Y METAS
        // ===============================

        composable(Routes.PRESUPUESTOS) {
            val presupuestoVM: PresupuestoViewModel = hiltViewModel()
            val categoriaVM: CategoriaViewModel = hiltViewModel()

            PantallaPresupuestos(
                viewModel = presupuestoVM,
                categoriaViewModel = categoriaVM,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Routes.METAS) {
            PantallaMetas(
                usuarioId = currentUser?.uid ?: "usuario_demo_001"
            ) {
                navController.popBackStack()
            }
        }

        // ===============================
        // ⚙️ CONFIGURACIÓN Y CATEGORÍAS
        // ===============================

        composable(Routes.CONFIGURACION) {
            Configuracion { navController.popBackStack() }
        }

        composable(Routes.CATEGORIAS) {
            Categorias { navController.popBackStack() }
        }

        // ===============================
        // 🐾 MASCOTA
        // ===============================

        composable(Routes.MASCOTA) {

            val database = AppDatabase.getInstance(context)
            val firestore = FirebaseFirestore.getInstance()

            NavegacionMascota(
                usuarioId = currentUser?.uid ?: "usuario_temp",
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
