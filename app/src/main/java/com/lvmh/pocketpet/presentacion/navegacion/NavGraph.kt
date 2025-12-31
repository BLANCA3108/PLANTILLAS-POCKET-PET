package com.lvmh.pocketpet.presentacion.navegacion

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.lvmh.pocketpet.pantallas.Categorias
import com.lvmh.pocketpet.pantallas.Configuracion
import com.lvmh.pocketpet.pantallas.Logo
import com.lvmh.pocketpet.pantallas.MiPerfil
import com.lvmh.pocketpet.pantallas.Slide1
import com.lvmh.pocketpet.pantallas.Slide2
import com.lvmh.pocketpet.pantallas.Slide3
import com.lvmh.pocketpet.pantallas.mascota.NavegacionMascota
import com.lvmh.pocketpet.presentacion.auth.LoginScreen
import com.lvmh.pocketpet.presentacion.auth.RegistroScreen
import com.lvmh.pocketpet.presentacion.pantallas.PantallaPrincipal
<<<<<<< HEAD
import com.lvmh.pocketpet.presentacion.pantallas.PantallaPresupuestos
=======
import com.lvmh.pocketpet.pantallas.PantallaPresupuestos
>>>>>>> 04fa32f97f35c788da9d88545e2c467a439f06f0
import com.lvmh.pocketpet.presentacion.pantallas.estadisticas.PantallaCalendario
import com.lvmh.pocketpet.presentacion.pantallas.estadisticas.PantallaComparativos
import com.lvmh.pocketpet.presentacion.pantallas.estadisticas.PantallaEstadisticas
import com.lvmh.pocketpet.presentacion.pantallas.estadisticas.PantallaEstadisticasCategorias
import com.lvmh.pocketpet.presentacion.pantallas.estadisticas.PantallaMetas
import com.lvmh.pocketpet.presentacion.pantallas.estadisticas.PantallaReportes
import com.lvmh.pocketpet.presentacion.pantallas.estadisticas.PantallaTendencias
import com.lvmh.pocketpet.presentacion.viewmodels.AuthViewModel
import com.lvmh.pocketpet.presentacion.viewmodels.EstadisticasViewModel
import com.lvmh.pocketpet.presentacion.viewmodels.PresupuestoViewModel
import com.lvmh.pocketpet.viewmodels.TransaccionViewModel

@Composable
fun PocketPetNavGraph() {
    val navController = rememberNavController()

    // ViewModel de autenticación
    val authViewModel: AuthViewModel = hiltViewModel()
    val isAuthenticated by authViewModel.isAuthenticated.collectAsState()

    // Determinar la pantalla inicial basada en la autenticación
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
            Slide1(
                onNext = {
                    navController.navigate(Routes.SLIDE2)
                }
            )
        }

        composable(Routes.SLIDE2) {
            Slide2(
                onNext = {
                    navController.navigate(Routes.SLIDE3)
                }
            )
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
        // 📱 APP PRINCIPAL
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
        // 👤 PERFIL - 🔥 ACTUALIZADO CON LOGOUT
        // ===============================

        composable(Routes.MI_PERFIL) {
            MiPerfil(
                onBack = {
                    navController.popBackStack()
                },
                onLogoutSuccess = {
                    // Navegar al logo/login y limpiar todo el stack
                    navController.navigate(Routes.LOGO) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                authViewModel = authViewModel
            )
        }

        // ===============================
        // 📊 ESTADÍSTICAS Y ANÁLISIS
        // ===============================

        composable(Routes.ESTADISTICAS) {
            val viewModel: EstadisticasViewModel = hiltViewModel()
            PantallaEstadisticas(
                viewModel = viewModel,
                alNavegar = { ruta ->
                    navController.navigate(ruta)
                }
            )
        }

        composable(Routes.ESTADISTICAS_CATEGORIAS) {
            val viewModel: EstadisticasViewModel = hiltViewModel()
            PantallaEstadisticasCategorias(
                viewModel = viewModel,
                alRegresar = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.COMPARATIVOS) {
            val viewModel: EstadisticasViewModel = hiltViewModel()
            PantallaComparativos(
                viewModel = viewModel,
                alRegresar = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.TENDENCIAS) {
            val viewModel: EstadisticasViewModel = hiltViewModel()
            PantallaTendencias(
                viewModel = viewModel,
                alRegresar = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.REPORTES) {
            val viewModel: EstadisticasViewModel = hiltViewModel()
            PantallaReportes(
                viewModel = viewModel,
                alRegresar = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.CALENDARIO) {
            val viewModel: EstadisticasViewModel = hiltViewModel()
            PantallaCalendario(
                viewModel = viewModel,
                usuarioId = "usuario_demo_001",
                alRegresar = {
                    navController.popBackStack()
                }
            )
        }

        // ===============================
        // 💰 PRESUPUESTOS Y METAS
        // ===============================

        composable(Routes.PRESUPUESTOS) {
            val viewModel: PresupuestoViewModel = hiltViewModel()
            PantallaPresupuestos(
<<<<<<< HEAD
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                alNavegar = { destino ->
                    navController.navigate(destino)
                }
=======
                onBackClick = {
                    navController.popBackStack()
                },
                viewModel = viewModel
>>>>>>> 04fa32f97f35c788da9d88545e2c467a439f06f0
            )
        }

        composable(Routes.METAS) {
            PantallaMetas(
                usuarioId = "usuario_demo_001",
                alRegresar = {
                    navController.popBackStack()
                }
            )
        }

        // ===============================
        // ⚙️ CONFIGURACIÓN
        // ===============================

        composable(Routes.CONFIGURACION) {
            Configuracion(
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.CATEGORIAS) {
            Categorias(
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        // ===============================
// 🐾 MASCOTA
// ===============================

        composable(Routes.MASCOTA) {
            NavegacionMascota(
                onVolverPrincipal = {
                    navController.navigate(Routes.PRINCIPAL) {
                        popUpTo(Routes.PRINCIPAL) { inclusive = false }
                    }
                }
            )
        }
    }
}