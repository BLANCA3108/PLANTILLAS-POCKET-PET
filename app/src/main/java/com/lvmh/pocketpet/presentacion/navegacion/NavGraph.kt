package com.lvmh.pocketpet.presentacion.navegacion

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.lvmh.pocketpet.presentacion.pantallas.PantallaPrincipal
import com.lvmh.pocketpet.presentacion.pantallas.estadisticas.PantallaEstadisticas
import com.lvmh.pocketpet.presentacion.pantallas.estadisticas.PantallaEstadisticasCategorias
import com.lvmh.pocketpet.presentacion.pantallas.estadisticas.PantallaTendencias
// ✅ CORREGIDO: Import correcto
import com.lvmh.pocketpet.pantallas.Presupuestos
import com.lvmh.pocketpet.viewmodels.TransaccionViewModel
import com.lvmh.pocketpet.presentacion.viewmodels.EstadisticasViewModel
import com.lvmh.pocketpet.presentacion.viewmodels.PresupuestoViewModel
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

        composable("estadisticas_categorias") {
            val viewModel: EstadisticasViewModel = hiltViewModel()
            PantallaEstadisticasCategorias(
                viewModel = viewModel,
                alRegresar = {
                    navController.popBackStack()
                }
            )
        }

        composable("tendencias") {
            val viewModel: EstadisticasViewModel = hiltViewModel()
            PantallaTendencias(
                viewModel = viewModel,
                alRegresar = {
                    navController.popBackStack()
                }
            )
        }

        // ✅ CORREGIDO: Sin parámetro usuarioId
        composable(Routes.PRESUPUESTOS) {
            val viewModel: PresupuestoViewModel = hiltViewModel()
            Presupuestos(
                onBackClick = {
                    navController.popBackStack()
                },
                viewModel = viewModel
            )
        }

        composable(Routes.MASCOTA) {
            NavegacionMascota()
        }
    }
}