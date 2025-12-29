package com.lvmh.pocketpet.presentacion.navegacion

sealed class Routes(val ruta: String) {

    // Autenticación
    object Login : Routes("login")
    object Register : Routes("register")
    object Onboarding : Routes("onboarding")

    // Principal
    object Main : Routes("main")
    object Home : Routes("home")

    // Transacciones
    object TransactionList : Routes("transactions")
    object AddTransaction : Routes("add_transaction")
    object TransactionDetail : Routes("transaction_detail/{transactionId}") {
        fun crearRuta(transactionId: String) = "transaction_detail/$transactionId"
    }
    object Categories : Routes("categories")
    object Accounts : Routes("accounts")

    // Estadísticas (GISELA)
    object Stats : Routes("stats")
    object CategoryStats : Routes("category_stats")
    object Trends : Routes("trends")
    object Budgets : Routes("budgets")
    object Goals : Routes("goals")
    object Calendar : Routes("calendar")
    object Reports : Routes("reports")
    object Comparatives : Routes("comparatives")

    // Mascota (MARICARMEN)
    object PetHome : Routes("pet_home")
    object PetCare : Routes("pet_care")
    object Challenges : Routes("challenges")
    object Achievements : Routes("achievements")
    object PetEvolution : Routes("pet_evolution")
    object PetMessages : Routes("pet_messages")
    object Diary : Routes("diary")
    object Customize : Routes("customize")

    // Configuración
    object Settings : Routes("settings")
    object Profile : Routes("profile")
}