package com.lvmh.pocketpet.presentacion.navegacion

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.lvmh.pocketpet.presentacion.pantallas.estadisticas.*
import com.lvmh.pocketpet.presentacion.viewmodels.EstadisticasViewModel
import com.lvmh.pocketpet.presentacion.viewmodels.PresupuestoViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    estadisticasViewModel: EstadisticasViewModel,
    presupuestoViewModel: PresupuestoViewModel,
    usuarioId: String,
    startDestination: String = Routes.Stats.ruta
) {
    // Establecer el usuarioId en los ViewModels al inicio
    LaunchedEffect(usuarioId) {
        estadisticasViewModel.establecerUsuario(usuarioId)
        presupuestoViewModel.establecerUsuario(usuarioId)
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        // ============================================
        // AUTENTICACIÓN (BLANCA)
        // ============================================
        composable(Routes.Login.ruta) {
            // LoginScreen(navController)
        }

        composable(Routes.Register.ruta) {
            // RegisterScreen(navController)
        }

        composable(Routes.Onboarding.ruta) {
            // OnboardingScreen(navController)
        }

        // ============================================
        // PRINCIPAL (BLANCA)
        // ============================================
        composable(Routes.Main.ruta) {
            // MainScreen(navController)
        }

        composable(Routes.Home.ruta) {
            // HomeScreen(navController)
        }

        // ============================================
        // TRANSACCIONES (BLANCA)
        // ============================================
        composable(Routes.TransactionList.ruta) {
            // TransactionListScreen(navController)
        }

        composable(Routes.AddTransaction.ruta) {
            // AddTransactionScreen(navController)
        }

        composable(Routes.TransactionDetail.ruta) { backStackEntry ->
            val transactionId = backStackEntry.arguments?.getString("transactionId")
            // TransactionDetailScreen(navController, transactionId)
        }

        composable(Routes.Categories.ruta) {
            // CategoriesScreen(navController)
        }

        composable(Routes.Accounts.ruta) {
            // AccountsScreen(navController)
        }

        // ============================================
        // ESTADÍSTICAS (GISELA) ✅
        // ============================================
        composable(Routes.Stats.ruta) {
            PantallaEstadisticas(
                viewModel = estadisticasViewModel,
                alNavegar = { ruta -> navController.navigate(ruta) }
            )
        }

        composable(Routes.CategoryStats.ruta) {
            PantallaEstadisticasCategorias(
                viewModel = estadisticasViewModel,
                alRegresar = { navController.popBackStack() }
            )
        }

        composable(Routes.Trends.ruta) {
            PantallaTendencias(
                viewModel = estadisticasViewModel,
                alRegresar = { navController.popBackStack() }
            )
        }

        composable(Routes.Budgets.ruta) {
            PantallaPresupuestos(
                viewModel = presupuestoViewModel,
                alRegresar = { navController.popBackStack() }
            )
        }

        composable(Routes.Goals.ruta) {
            PantallaMetas(
                viewModel = presupuestoViewModel,
                alRegresar = { navController.popBackStack() }
            )
        }

        composable(Routes.Calendar.ruta) {
            PantallaCalendario(
                viewModel = estadisticasViewModel,
                alRegresar = { navController.popBackStack() }
            )
        }

        composable(Routes.Reports.ruta) {
            PantallaReportes(
                viewModel = estadisticasViewModel,
                alRegresar = { navController.popBackStack() }
            )
        }

        composable(Routes.Comparatives.ruta) {
            PantallaComparativos(
                viewModel = estadisticasViewModel,
                alRegresar = { navController.popBackStack() }
            )
        }

        // ============================================
        // MASCOTA (MARICARMEN)
        // ============================================
        composable(Routes.PetHome.ruta) {
            // PetHomeScreen(navController)
        }

        composable(Routes.PetCare.ruta) {
            // PetCareScreen(navController)
        }

        composable(Routes.Challenges.ruta) {
            // ChallengesScreen(navController)
        }

        composable(Routes.Achievements.ruta) {
            // AchievementsScreen(navController)
        }

        composable(Routes.PetEvolution.ruta) {
            // PetEvolutionScreen(navController)
        }

        composable(Routes.PetMessages.ruta) {
            // PetMessagesScreen(navController)
        }

        composable(Routes.Diary.ruta) {
            // DiaryScreen(navController)
        }

        composable(Routes.Customize.ruta) {
            // CustomizeScreen(navController)
        }

        // ============================================
        // CONFIGURACIÓN
        // ============================================
        composable(Routes.Settings.ruta) {
            // SettingsScreen(navController)
        }

        composable(Routes.Profile.ruta) {
            // ProfileScreen(navController)
        }
    }
}