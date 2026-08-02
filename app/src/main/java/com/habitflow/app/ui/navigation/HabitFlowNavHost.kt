package com.habitflow.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.habitflow.app.data.HabitRepository
import com.habitflow.app.ui.addhabit.AddHabitScreen
import com.habitflow.app.ui.calendar.CalendarScreen
import com.habitflow.app.ui.home.HomeScreen
import com.habitflow.app.ui.stats.StatsScreen

@Composable
fun HabitFlowNavHost(
    navController: NavHostController,
    repository: HabitRepository,
    modifier: Modifier = Modifier
) {
    NavHost(navController = navController, startDestination = Screen.Home.route, modifier = modifier) {

        composable(Screen.Home.route) {
            HomeScreen(
                repository = repository,
                onAddHabit = { navController.navigate(Screen.AddHabit.createRoute()) },
                onEditHabit = { id -> navController.navigate(Screen.AddHabit.createRoute(id)) }
            )
        }

        composable(Screen.Calendar.route) {
            CalendarScreen(repository = repository)
        }

        composable(Screen.Stats.route) {
            StatsScreen(repository = repository)
        }

        composable(
            route = Screen.AddHabit.route,
            arguments = listOf(navArgument("habitId") {
                type = NavType.LongType
                defaultValue = -1L
            })
        ) { backStackEntry ->
            val habitId = backStackEntry.arguments?.getLong("habitId") ?: -1L
            AddHabitScreen(
                repository = repository,
                habitId = if (habitId == -1L) null else habitId,
                onDone = { navController.popBackStack() }
            )
        }
    }
}
