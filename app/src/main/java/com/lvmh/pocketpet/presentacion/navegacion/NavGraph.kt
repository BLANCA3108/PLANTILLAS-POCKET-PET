package com.lvmh.pocketpet.presentacion.navegacion

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.firestore.FirebaseFirestore
import com.lvmh.pocketpet.datos.local.AppDatabase
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
// import com.lvmh.pocketpet.pantallas.PantallaPresupuestos  // ← Comentar si no existe
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
// Agregar imports necesarios si faltan
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
@Composable
fun PocketPetNavGraph() {
    val navController = rememberNavController()
    val context = LocalContext.current

    // ViewModel de autenticación
    val authViewModel: AuthViewModel = hiltViewModel()
    val isAuthenticated by authViewModel.isAuthenticated.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()

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
                // Redirigir al login si no está autenticado
                LaunchedEffect(Unit) {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.PRINCIPAL) { inclusive = true }
                    }
                }
            }
        }

        // ===============================
        // 👤 PERFIL
        // ===============================

        composable(Routes.MI_PERFIL) {
            MiPerfil(
                onBack = {
                    navController.popBackStack()
                }
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
            val userId = currentUser?.uid ?: "usuario_demo_001"
            PantallaCalendario(
                viewModel = viewModel,
                usuarioId = userId,
                alRegresar = {
                    navController.popBackStack()
                }
            )
        }

        // ===============================
        // 💰 PRESUPUESTOS Y METAS
        // ===============================

        composable(Routes.PRESUPUESTOS) {
            // OPCIÓN 1: Si PantallaPresupuestos NO existe, usar pantalla temporal
            androidx.compose.foundation.layout.Box(
                modifier = androidx.compose.ui.Modifier.fillMaxSize(),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                androidx.compose.foundation.layout.Column(
                    horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                ) {
                    androidx.compose.material3.Text(
                        "💰 Presupuestos",
                        fontSize = 24.sp,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    )
                    androidx.compose.foundation.layout.Spacer(
                        modifier = androidx.compose.ui.Modifier.height(16.dp)
                    )
                    androidx.compose.material3.Text("Pantalla en desarrollo")
                    androidx.compose.foundation.layout.Spacer(
                        modifier = androidx.compose.ui.Modifier.height(24.dp)
                    )
                    androidx.compose.material3.Button(
                        onClick = { navController.popBackStack() }
                    ) {
                        androidx.compose.material3.Text("Volver")
                    }
                }
            }

            // OPCIÓN 2: Si PantallaPresupuestos SÍ existe, descomenta esto:
            /*
            val viewModel: PresupuestoViewModel = hiltViewModel()
            PantallaPresupuestos(
                onBackClick = {
                    navController.popBackStack()
                },
                viewModel = viewModel
            )
            */
        }

        composable(Routes.METAS) {
            val userId = currentUser?.uid ?: "usuario_demo_001"
            PantallaMetas(
                usuarioId = userId,
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
            val userId = currentUser?.uid ?: "usuario_temp"
            val database = AppDatabase.getInstance(context)
            val firestore = FirebaseFirestore.getInstance()

            NavegacionMascota(
                usuarioId = userId,
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

