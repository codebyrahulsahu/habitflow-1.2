package com.habitflow.app

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.habitflow.app.data.HabitRepository
import com.habitflow.app.ui.navigation.HabitFlowNavHost
import com.habitflow.app.ui.navigation.Screen
import com.habitflow.app.ui.theme.HabitFlowTheme

class MainActivity : ComponentActivity() {

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { /* no-op either way */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }

        val app = application as HabitFlowApp

        setContent {
            HabitFlowTheme {
                HabitFlowRoot(repository = app.repository)
            }
        }
    }
}

private data class BottomNavItem(val screen: Screen, val label: String, val icon: ImageVector)

@Composable
fun HabitFlowRoot(repository: HabitRepository) {
    val navController = rememberNavController()

    val items = listOf(
        BottomNavItem(Screen.Home, "Home", Icons.Filled.Home),
        BottomNavItem(Screen.Calendar, "Calendar", Icons.Filled.CalendarMonth),
        BottomNavItem(Screen.Stats, "Stats", Icons.Filled.BarChart)
    )

    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            NavigationBar {
                items.forEach { item ->
                    NavigationBarItem(
                        selected = currentRoute == item.screen.route,
                        onClick = {
                            navController.navigate(item.screen.route) {
                                popUpTo(Screen.Home.route)
                                launchSingleTop = true
                            }
                        },
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) }
                    )
                }
            }
        }
    ) { padding ->
        HabitFlowNavHost(
            navController = navController,
            repository = repository,
            modifier = Modifier.padding(padding)
        )
    }
}
