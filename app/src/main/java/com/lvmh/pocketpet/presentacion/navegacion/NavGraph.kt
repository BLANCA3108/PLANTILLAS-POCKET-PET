package com.lvmh.pocketpet.presentacion.navegacion

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.lvmh.pocketpet.presentacion.pantallas.PantallaPrincipal
import com.lvmh.pocketpet.presentacion.pantallas.estadisticas.PantallaEstadisticas
import com.lvmh.pocketpet.presentacion.pantallas.estadisticas.PantallaPresupuestos
import com.lvmh.pocketpet.viewmodels.TransaccionViewModel
import com.lvmh.pocketpet.presentacion.viewmodels.EstadisticasViewModel
import com.lvmh.pocketpet.presentacion.viewmodels.PresupuestoViewModel
// ✅ Imports para Mascota
import com.lvmh.pocketpet.pantallas.mascota.NavegacionMascota
import com.lvmh.pocketpet.pantallas.Logo
import com.lvmh.pocketpet.pantallas.Slide1
import com.lvmh.pocketpet.pantallas.Slide2
import com.lvmh.pocketpet.pantallas.Slide3
import com.lvmh.pocketpet.presentacion.viewmodels.AuthViewModel
import com.lvmh.pocketpet.presentacion.auth.LoginScreen
import com.lvmh.pocketpet.presentacion.auth.RegistroScreen
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue

@Composable
fun PocketPetNavGraph() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel()

    // Estado de autenticación
    val isAuthenticated by authViewModel.isAuthenticated.collectAsState()

    // Determinar la pantalla inicial basada en la autenticación
    val startDestination = if (isAuthenticated) {
        Routes.PRINCIPAL
    } else {
        Routes.LOGO  // O Routes.LOGO si quieres mantener el onboarding primero
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        // ===============================
        // 🔐 FLUJO DE AUTENTICACIÓN
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
        // 🎯 ONBOARDING (Opcional - puedes decidir si mantenerlo)
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
                    navController.navigate(Routes.LOGIN) {  // Cambiado a LOGIN
                        popUpTo(Routes.LOGO) { inclusive = true }
                    }
                }
            )
        }

        // ===============================
        // 📱 APP PRINCIPAL (Solo para usuarios autenticados)
        // ===============================

        composable(Routes.PRINCIPAL) {
            if (isAuthenticated) {
                val viewModel: TransaccionViewModel = hiltViewModel()
                PantallaPrincipal(
                    viewModel = viewModel,
                    alNavegar = { ruta ->
                        navController.navigate(ruta)
                    },
                )
            } else {
                // Redirigir al login si no está autenticado
                LaunchedEffect(Unit) {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.PRINCIPAL) { inclusive = true }
                    }
                }
            }
        }

        composable(Routes.PRINCIPAL) {
            val viewModel: TransaccionViewModel = hiltViewModel()
            PantallaPrincipal(
                viewModel = viewModel,
                alNavegar = { ruta ->
                    navController.navigate(ruta)
                }
            )
        }

        composable(Routes.ESTADISTICAS) {
            val viewModel: EstadisticasViewModel = hiltViewModel()
            PantallaEstadisticas(
                viewModel = viewModel,
                alNavegar = { ruta ->
                    navController.navigate(ruta)
                }
            )
        }

        composable(Routes.PRESUPUESTOS) {
            val viewModel: PresupuestoViewModel = hiltViewModel()
            PantallaPresupuestos(
                viewModel = viewModel,
                alRegresar = {
                    navController.popBackStack()
                }
            )
        }

        // 🔹 Mascota (NavGraph interno)
        composable(Routes.MASCOTA) {
            NavegacionMascota()
        }
    }
}
