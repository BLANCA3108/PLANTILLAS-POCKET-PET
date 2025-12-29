package com.lvmh.pocketpet.presentacion.navegacion

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.lvmh.pocketpet.presentacion.viewmodels.EstadisticasViewModel
import com.lvmh.pocketpet.presentacion.viewmodels.PresupuestoViewModel

@Composable
fun NavigationHost(
    estadisticasViewModel: EstadisticasViewModel,
    presupuestoViewModel: PresupuestoViewModel,
    usuarioId: String = "usuario_demo_123"
) {
    val navController = rememberNavController()

    NavGraph(
        navController = navController,
        estadisticasViewModel = estadisticasViewModel,
        presupuestoViewModel = presupuestoViewModel,
        usuarioId = usuarioId,
        startDestination = Routes.Stats.ruta
    )
}