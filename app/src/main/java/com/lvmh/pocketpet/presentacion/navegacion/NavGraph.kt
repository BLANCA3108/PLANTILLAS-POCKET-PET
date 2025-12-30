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
import com.lvmh.pocketpet.presentacion.pantallas.estadisticas.PantallaComparativos
import com.lvmh.pocketpet.presentacion.pantallas.estadisticas.PantallaReportes
import com.lvmh.pocketpet.presentacion.pantallas.estadisticas.PantallaMetas
import com.lvmh.pocketpet.presentacion.pantallas.estadisticas.PantallaCalendario
import com.lvmh.pocketpet.pantallas.PantallaPresupuestos
import com.lvmh.pocketpet.pantallas.Configuracion
import com.lvmh.pocketpet.pantallas.Categorias
import com.lvmh.pocketpet.pantallas.MiPerfil
import com.lvmh.pocketpet.viewmodels.TransaccionViewModel
import com.lvmh.pocketpet.presentacion.viewmodels.EstadisticasViewModel
import com.lvmh.pocketpet.presentacion.viewmodels.PresupuestoViewModel
import com.lvmh.pocketpet.pantallas.mascota.NavegacionMascota
import com.lvmh.pocketpet.pantallas.Logo
import com.lvmh.pocketpet.pantallas.Slide1
import com.lvmh.pocketpet.pantallas.Slide2
import com.lvmh.pocketpet.pantallas.Slide3

@Composable
fun PocketPetNavGraph() {
    val navController = rememberNavController()

    // ID de usuario temporal - reemplaza con tu sistema de autenticación real
    val usuarioId = "usuario_demo_001"

    NavHost(
        navController = navController,
        startDestination = Routes.LOGO
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
        // 🔹 APP PRINCIPAL
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

        // ===============================
        // 🔹 PERFIL
        // ===============================

        composable(Routes.MI_PERFIL) {
            MiPerfil(
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        // ===============================
        // 🔹 SECCIÓN ANÁLISIS
        // ===============================

        composable("comparativos") {
            val viewModel: EstadisticasViewModel = hiltViewModel()
            PantallaComparativos(
                viewModel = viewModel,
                alRegresar = {
                    navController.popBackStack()
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

        composable(Routes.CALENDARIO) {
            val viewModel: EstadisticasViewModel = hiltViewModel()
            PantallaCalendario(
                viewModel = viewModel,
                usuarioId = usuarioId,
                alRegresar = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.PRESUPUESTOS) {
            val viewModel: PresupuestoViewModel = hiltViewModel()
            PantallaPresupuestos(
                onBackClick = {
                    navController.popBackStack()
                },
                viewModel = viewModel
            )
        }

        composable("reportes") {
            val viewModel: EstadisticasViewModel = hiltViewModel()
            PantallaReportes(
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

        composable("metas") {
            PantallaMetas(
                usuarioId = usuarioId,
                alRegresar = {
                    navController.popBackStack()
                }
            )
        }

        // ===============================
        // 🔹 SECCIÓN MÁS
        // ===============================

        composable("configuracion") {
            Configuracion(
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable("categorias") {
            Categorias(
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        // ===============================
        // 🔹 MASCOTA
        // ===============================

        composable(Routes.MASCOTA) {
            NavegacionMascota()
        }
    }
}