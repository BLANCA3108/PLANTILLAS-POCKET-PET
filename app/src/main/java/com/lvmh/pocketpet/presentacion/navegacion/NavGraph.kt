package com.lvmh.pocketpet.presentacion.navegacion

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.lvmh.pocketpet.pantallas.PantallaPrincipal
import com.lvmh.pocketpet.presentacion.pantallas.PantallaPrincipal
import com.lvmh.pocketpet.presentacion.pantallas.estadisticas.PantallaEstadisticas
import com.lvmh.pocketpet.presentacion.pantallas.estadisticas.PantallaPresupuestos

@Composable
fun PocketPetNavGraph() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.PRINCIPAL
    ) {

        composable(Routes.PRINCIPAL) {
            PantallaPrincipal(
                alNavegar = { ruta ->
                    navController.navigate(ruta)
                }
            )
        }

        composable(Routes.ESTADISTICAS) {
            PantallaEstadisticas(
                alRegresar = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.PRESUPUESTOS) {
            PantallaPresupuestos(
                alRegresar = {
                    navController.popBackStack()
                }
            )
        }
    }
}
