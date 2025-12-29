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

@Composable
fun PocketPetNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.LOGO   // 👈 CAMBIO CLAVE
    ) {

        // ===============================
        // 🔹 ONBOARDING
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
                    navController.navigate(Routes.PRINCIPAL) {
                        popUpTo(Routes.LOGO) { inclusive = true }
                    }
                }
            )
        }

        // ===============================
        // 🔹 APP PRINCIPAL (TU CÓDIGO ORIGINAL)
        // ===============================

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
