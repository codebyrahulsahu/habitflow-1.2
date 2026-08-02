package com.habitflow.app.ui.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Calendar : Screen("calendar")
    data object Stats : Screen("stats")
    data object AddHabit : Screen("add_habit?habitId={habitId}") {
        fun createRoute(habitId: Long? = null) = "add_habit?habitId=${habitId ?: -1}"
    }
}
