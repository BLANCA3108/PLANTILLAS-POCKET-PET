package com.lvmh.pocketpet.presentacion.navegacion

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
import com.lvmh.pocketpet.presentacion.pantallas.PantallaPresupuestos
import com.lvmh.pocketpet.presentacion.pantallas.estadisticas.PantallaCalendario
import com.lvmh.pocketpet.presentacion.pantallas.estadisticas.PantallaComparativos
import com.lvmh.pocketpet.presentacion.pantallas.estadisticas.PantallaEstadisticas
import com.lvmh.pocketpet.presentacion.pantallas.estadisticas.PantallaEstadisticasCategorias
import com.lvmh.pocketpet.presentacion.pantallas.estadisticas.PantallaMetas
import com.lvmh.pocketpet.presentacion.pantallas.estadisticas.PantallaReportes
import com.lvmh.pocketpet.presentacion.pantallas.estadisticas.PantallaTendencias
import com.lvmh.pocketpet.presentacion.viewmodels.AuthViewModel
import com.lvmh.pocketpet.presentacion.viewmodels.CategoriaViewModel
import com.lvmh.pocketpet.presentacion.viewmodels.EstadisticasViewModel
import com.lvmh.pocketpet.presentacion.viewmodels.PresupuestoViewModel
import com.lvmh.pocketpet.viewmodels.TransaccionViewModel

@Composable
fun PocketPetNavGraph() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel()
    val isAuthenticated by authViewModel.isAuthenticated.collectAsState()
    val userId by authViewModel.userId.collectAsState()

    val startDestination = if (isAuthenticated) Routes.PRINCIPAL else Routes.LOGO

    NavHost(navController = navController, startDestination = startDestination) {

        composable(Routes.LOGIN) {
            LoginScreen(navController = navController, authViewModel = authViewModel)
        }

        composable(Routes.REGISTRO) {
            RegistroScreen(navController = navController, authViewModel = authViewModel)
        }

        composable(Routes.LOGO) {
            Logo(onNext = {
                navController.navigate(Routes.SLIDE1) {
                    popUpTo(Routes.LOGO) { inclusive = true }
                }
            })
        }

        composable(Routes.SLIDE1) {
            Slide1(onNext = { navController.navigate(Routes.SLIDE2) })
        }

        composable(Routes.SLIDE2) {
            Slide2(onNext = { navController.navigate(Routes.SLIDE3) })
        }

        composable(Routes.SLIDE3) {
            Slide3(onNext = {
                navController.navigate(Routes.LOGIN) {
                    popUpTo(Routes.LOGO) { inclusive = true }
                }
            })
        }

        composable(Routes.PRINCIPAL) {
            if (isAuthenticated) {
                val transaccionViewModel: TransaccionViewModel = hiltViewModel()
                val categoriaViewModel: CategoriaViewModel = hiltViewModel()
                PantallaPrincipal(
                    viewModel = transaccionViewModel,
                    categoriaViewModel = categoriaViewModel,
                    alNavegar = { ruta -> navController.navigate(ruta) }
                )
            } else {
                LaunchedEffect(Unit) {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.PRINCIPAL) { inclusive = true }
                    }
                }
            }
        }

        composable(Routes.MI_PERFIL) {
            MiPerfil(onBack = { navController.popBackStack() })
        }

        composable(Routes.ESTADISTICAS) {
            val viewModel: EstadisticasViewModel = hiltViewModel()
            PantallaEstadisticas(
                viewModel = viewModel,
                alNavegar = { ruta -> navController.navigate(ruta) }
            )
        }

        composable(Routes.ESTADISTICAS_CATEGORIAS) {
            val viewModel: EstadisticasViewModel = hiltViewModel()
            PantallaEstadisticasCategorias(
                viewModel = viewModel,
                alRegresar = { navController.popBackStack() }
            )
        }

        composable(Routes.COMPARATIVOS) {
            val viewModel: EstadisticasViewModel = hiltViewModel()
            PantallaComparativos(
                viewModel = viewModel,
                alRegresar = { navController.popBackStack() }
            )
        }

        composable(Routes.TENDENCIAS) {
            val viewModel: EstadisticasViewModel = hiltViewModel()
            PantallaTendencias(
                viewModel = viewModel,
                alRegresar = { navController.popBackStack() }
            )
        }

        composable(Routes.REPORTES) {
            val viewModel: EstadisticasViewModel = hiltViewModel()
            PantallaReportes(
                viewModel = viewModel,
                alRegresar = { navController.popBackStack() }
            )
        }

        composable(Routes.CALENDARIO) {
            val viewModel: EstadisticasViewModel = hiltViewModel()
            PantallaCalendario(
                viewModel = viewModel,
                alRegresar = { navController.popBackStack() }
            )
        }

        // 🔥 PRESUPUESTOS - CORREGIDO
        composable(Routes.PRESUPUESTOS) {
            val presupuestoViewModel: PresupuestoViewModel = hiltViewModel()
            val categoriaViewModel: CategoriaViewModel = hiltViewModel()

            PantallaPresupuestos(
                viewModel = presupuestoViewModel,
                categoriaViewModel = categoriaViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        // 🔥 METAS - CORREGIDO
        composable(Routes.METAS) {
            PantallaMetas(
                usuarioId = userId ?: "",
                alRegresar = { navController.popBackStack() }
            )
        }

        composable(Routes.CONFIGURACION) {
            Configuracion(onBack = { navController.popBackStack() })
        }

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