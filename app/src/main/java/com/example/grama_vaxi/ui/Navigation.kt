package com.example.grama_vaxi.ui

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Farmer : Screen("farmer")
    object RegisterAnimal : Screen("register_animal")
    object ReportDisease : Screen("report_disease")
    object VetDashboard : Screen("vet_dashboard")
    object AdminDashboard : Screen("admin_dashboard")
}
