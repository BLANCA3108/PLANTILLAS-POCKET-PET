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

@Composable
fun PocketPetNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.PRINCIPAL
    ) {
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

        // ✅ NUEVA RUTA: Sección Mascota con su propia navegación interna
        composable(Routes.MASCOTA) {
            NavegacionMascota()
        }
    }
}