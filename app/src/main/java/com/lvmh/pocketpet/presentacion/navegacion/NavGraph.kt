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
import com.lvmh.pocketpet.presentacion.pantallas.PantallaPrincipal
import com.lvmh.pocketpet.presentacion.pantallas.estadisticas.*
import com.lvmh.pocketpet.presentacion.viewmodels.*
import com.lvmh.pocketpet.viewmodels.TransaccionViewModel
import com.lvmh.pocketpet.presentacion.pantallas.PantallaPresupuestos
@Composable
fun PocketPetNavGraph() {

    val navController = rememberNavController()
    val context = LocalContext.current

    // 🔐 AuthViewModel
    val authViewModel: AuthViewModel = hiltViewModel()
    val isAuthenticated by authViewModel.isAuthenticated.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()

    // 🆔 Obtener userId de forma segura
    val userId = currentUser?.uid ?: ""

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
            Logo(
                onNext = {
                    navController.navigate(Routes.SLIDE1) {
                        popUpTo(Routes.LOGO) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.SLIDE1) {
            Slide1(onNext = { navController.navigate(Routes.SLIDE2) })
        }

        composable(Routes.SLIDE2) {
            Slide2(onNext = { navController.navigate(Routes.SLIDE3) })
        }

        composable(Routes.SLIDE3) {
            Slide3(
                onNext = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.LOGO) { inclusive = true }
                    }
                }
            )
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
                alNavegar = { ruta -> navController.navigate(ruta) }
            )
        }

        // ===============================
        // 👤 PERFIL
        // ===============================

        composable(Routes.MI_PERFIL) {
            MiPerfil(
                onBack = { navController.popBackStack() },
                onLogoutSuccess = {
                    navController.navigate(Routes.LOGO) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                authViewModel = authViewModel
            )
        }

        // ===============================
        // 📊 ESTADÍSTICAS
        // ===============================

        composable(Routes.ESTADISTICAS) {
            val vm: EstadisticasViewModel = hiltViewModel()
            PantallaEstadisticas(
                viewModel = vm,
                alNavegar = { ruta -> navController.navigate(ruta) }
            )
        }

        composable(Routes.ESTADISTICAS_CATEGORIAS) {
            val vm: EstadisticasViewModel = hiltViewModel()
            PantallaEstadisticasCategorias(
                viewModel = vm,
                alRegresar = { navController.popBackStack() }
            )
        }

        composable(Routes.COMPARATIVOS) {
            val vm: EstadisticasViewModel = hiltViewModel()
            PantallaComparativos(
                viewModel = vm,
                alRegresar = { navController.popBackStack() }
            )
        }

        composable(Routes.TENDENCIAS) {
            val vm: EstadisticasViewModel = hiltViewModel()
            PantallaTendencias(
                viewModel = vm,
                alRegresar = { navController.popBackStack() }
            )
        }

        composable(Routes.REPORTES) {
            val vm: EstadisticasViewModel = hiltViewModel()
            PantallaReportes(
                viewModel = vm,
                alRegresar = { navController.popBackStack() }
            )
        }

        composable(Routes.CALENDARIO) {
            val vm: EstadisticasViewModel = hiltViewModel()
            PantallaCalendario(
                viewModel = vm,
                alRegresar = { navController.popBackStack() }
            )
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
                usuarioId = userId.ifEmpty { "usuario_demo_001" },
                alRegresar = { navController.popBackStack() }
            )
        }

        // ===============================
        // ⚙️ CONFIGURACIÓN Y CATEGORÍAS
        // ===============================

        composable(Routes.CONFIGURACION) {
            Configuracion(onBack = { navController.popBackStack() })
        }

        composable(Routes.CATEGORIAS) {
            Categorias(onBack = { navController.popBackStack() })
        }

        // ===============================
        // 🐾 MASCOTA
        // ===============================

        composable(Routes.MASCOTA) {
            val database = AppDatabase.getInstance(context)
            val firestore = FirebaseFirestore.getInstance()
            val mascotaUserId = userId.ifEmpty { "usuario_temp" }

            NavegacionMascota(
                usuarioId = mascotaUserId,
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