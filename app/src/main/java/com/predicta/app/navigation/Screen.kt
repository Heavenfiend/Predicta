package com.predicta.app.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Sealed class defining all navigation routes in the app.
 * Each screen holds its route string and optional nav-bar metadata.
 */
sealed class Screen(
    val route: String,
    val label: String = "",
    val selectedIcon: ImageVector = Icons.Default.Dashboard,
    val unselectedIcon: ImageVector = Icons.Outlined.Dashboard,
) {

    data object Splash : Screen("splash")
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object ForgotPassword : Screen("forgot_password")

    data object Dashboard : Screen(
        route = "dashboard",
        label = "Проект",
        selectedIcon = Icons.Filled.Dashboard,
        unselectedIcon = Icons.Outlined.Dashboard,
    )

    data object TeamVelocity : Screen(
        route = "team_velocity",
        label = "Команда",
        selectedIcon = Icons.Filled.People,
        unselectedIcon = Icons.Outlined.People,
    )

    data object Settings : Screen(
        route = "settings",
        label = "Настройки",
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings,
    )

    data object EmployeeCard : Screen(
        route = "employee_card/{employeeId}",
    ) {
        fun createRoute(employeeId: String): String = "employee_card/$employeeId"
    }

    data object TaskReassignment : Screen(
        route = "task_reassignment/{taskId}",
    ) {
        fun createRoute(taskId: String): String = "task_reassignment/$taskId"
    }

    data object TaskCreate : Screen("task_create")

    companion object {
        /** Screens that appear in the bottom navigation bar. */
        val bottomNavItems = listOf(Dashboard, TeamVelocity, Settings)
    }
}
