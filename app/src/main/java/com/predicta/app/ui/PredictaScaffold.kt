package com.predicta.app.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.predicta.app.R
import com.predicta.app.core.network.NetworkMonitor
import com.predicta.app.feature_auth.data.session.UserSessionManager
import com.predicta.app.feature_auth.presentation.ForgotPasswordScreen
import com.predicta.app.feature_auth.presentation.LoginScreen
import com.predicta.app.feature_auth.presentation.RegisterScreen
import com.predicta.app.feature_auth.presentation.SplashScreen
import com.predicta.app.feature_connectivity.presentation.NoInternetScreen
import com.predicta.app.feature_dashboard.presentation.DashboardScreen
import com.predicta.app.feature_employees.presentation.EmployeeCardScreen
import com.predicta.app.feature_employees.presentation.TeamVelocityScreen
import com.predicta.app.feature_settings.presentation.SettingsScreen
import com.predicta.app.feature_tasks.presentation.TaskReassignmentScreen
import com.predicta.app.navigation.Screen
import com.predicta.app.ui.modifier.liquidGlass
import com.predicta.app.ui.theme.SemanticSuccess
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PredictaScaffold(
    modifier: Modifier = Modifier,
    appViewModel: AppViewModel = koinViewModel(),
    fromIntro: Boolean = false
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val isOnline by appViewModel.isOnline.collectAsStateWithLifecycle()
    val session by appViewModel.session.collectAsStateWithLifecycle()

    if (!isOnline) {
        NoInternetScreen(
            onRetry = appViewModel::retryNetwork,
            modifier = modifier,
        )
        return
    }

    // Only show bottom bar on top-level screens
    val showBottomBar = currentRoute in Screen.bottomNavItems.map { it.route }
    val showTopBar = currentRoute !in listOf(
        Screen.Login.route,
        Screen.Register.route,
        Screen.ForgotPassword.route,
        Screen.Splash.route,
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            AnimatedVisibility(
                visible = showTopBar,
                enter = fadeIn(tween(180)) + slideInVertically { -it / 2 },
                exit = fadeOut(tween(120)) + slideOutVertically { -it / 2 },
            ) {
                PredictaTopBar()
            }
        },
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
                enter = fadeIn(tween(180)) + slideInVertically { it / 2 },
                exit = fadeOut(tween(120)) + slideOutVertically { it / 2 },
            ) {
                PredictaBottomBar(
                    navController = navController,
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.fillMaxSize(),
            enterTransition = {
                fadeIn(tween(220)) + slideInHorizontally { it / 6 }
            },
            exitTransition = {
                fadeOut(tween(140)) + slideOutHorizontally { -it / 8 }
            },
            popEnterTransition = {
                fadeIn(tween(220)) + slideInHorizontally { -it / 6 }
            },
            popExitTransition = {
                fadeOut(tween(140)) + slideOutHorizontally { it / 8 }
            },
        ) {
            composable(Screen.Splash.route) {
                SplashScreen(
                    skipDelay = fromIntro,
                    onSplashComplete = {
                        val destination = if (session.isLoggedIn) Screen.Dashboard.route else Screen.Login.route
                        navController.navigate(destination) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                )
            }

            // Auth Flow
            composable(Screen.Login.route) {
                LoginScreen(
                    onNavigateToDashboard = {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                    onNavigateToForgotPassword = { navController.navigate(Screen.ForgotPassword.route) }
                )
            }

            composable(Screen.Register.route) {
                RegisterScreen(
                    onNavigateBackToLogin = { navController.popBackStack() }
                )
            }

            composable(Screen.ForgotPassword.route) {
                ForgotPasswordScreen(
                    onNavigateBackToLogin = { navController.popBackStack() }
                )
            }

            // Экран 1: Дашборд (Здоровье проекта)
            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    onNavigateToTeamVelocity = {
                        navController.navigate(Screen.TeamVelocity.route) {
                            launchSingleTop = true
                        }
                    },
                    onResolveAlert = { alertId ->
                        navController.navigate(Screen.TeamVelocity.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    modifier = Modifier.padding(innerPadding)
                )
            }

            // Экран 2: Анализ темпа работы (Команда)
            composable(Screen.TeamVelocity.route) {
                TeamVelocityScreen(
                    onNavigateToEmployeeCard = { employeeId ->
                        navController.navigate(
                            Screen.EmployeeCard.createRoute(employeeId),
                        )
                    },
                    modifier = Modifier.padding(innerPadding)
                )
            }

            // Экран настроек
            composable(Screen.Settings.route) {
                SettingsScreen(
                    onLogout = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    },
                    modifier = Modifier.padding(innerPadding)
                )
            }

            // Экран 3: Карточка сотрудника & AI-инсайты
            composable(
                route = Screen.EmployeeCard.route,
                arguments = listOf(
                    navArgument("employeeId") { type = NavType.StringType },
                ),
            ) { backStackEntry ->
                val employeeId = backStackEntry.arguments?.getString("employeeId") ?: ""
                EmployeeCardScreen(
                    employeeId = employeeId,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToReassign = { taskId ->
                        navController.navigate(
                            Screen.TaskReassignment.createRoute(taskId),
                        )
                    },
                    modifier = Modifier.padding(innerPadding)
                )
            }

            // Экран 4: Перераспределение задачи
            composable(
                route = Screen.TaskReassignment.route,
                arguments = listOf(
                    navArgument("taskId") { type = NavType.StringType },
                ),
            ) { backStackEntry ->
                val taskId = backStackEntry.arguments?.getString("taskId") ?: ""
                TaskReassignmentScreen(
                    taskId = taskId,
                    onNavigateBack = { navController.popBackStack() },
                    onReassignmentComplete = {
                        // Navigate back to Dashboard, clearing the stack
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Dashboard.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}

// ──────────────────────────────────────────────────────────────────────────────
// Top App Bar
// ──────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PredictaTopBar() {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = "Predicta",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.5).sp,
                    ),
                    color = MaterialTheme.colorScheme.primary,
                )
                AiStatusIndicator()
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            scrolledContainerColor = MaterialTheme.colorScheme.surface,
        ),
        modifier = Modifier.shadow(
            elevation = 2.dp,
            ambientColor = Color.Black.copy(alpha = 0.06f),
            spotColor = Color.Black.copy(alpha = 0.06f),
        ).liquidGlass(
            shape = RoundedCornerShape(
                bottomStart = 20.dp,
                bottomEnd = 20.dp,
            ),
            blurRadius = 0.dp,
            liquidIntensity = 0.6f,
        ),
    )
}

/**
 * A small pulsing green dot indicating that the AI assistant is active.
 */
@Composable
private fun AiStatusIndicator() {
    val infiniteTransition = rememberInfiniteTransition(label = "ai_pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "ai_pulse_alpha",
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .alpha(alpha)
                .clip(CircleShape)
                .background(SemanticSuccess),
        )
        Text(
            text = "AI Active",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

// ──────────────────────────────────────────────────────────────────────────────
// Bottom Navigation Bar
// ──────────────────────────────────────────────────────────────────────────────

@Composable
private fun PredictaBottomBar(
    navController: NavHostController,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentExactRoute = currentDestination?.route

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp,
        modifier = Modifier.shadow(
            elevation = 8.dp,
            ambientColor = Color.Black.copy(alpha = 0.04f),
            spotColor = Color.Black.copy(alpha = 0.04f),
        ).liquidGlass(
            shape = RoundedCornerShape(
                topStart = 24.dp,
                topEnd = 24.dp,
            ),
            blurRadius = 0.dp,
            liquidIntensity = 0.6f,
        ),
    ) {
        Screen.bottomNavItems.forEach { item ->
            val isSelected = currentDestination?.hierarchy?.any { it.route == item.route } == true
            val isExactlyOnRootOfThisTab = currentExactRoute == item.route

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    // CRITICAL FIX: DO NOT use 'if (isSelected) return' or 'if (selected) return'.
                    // We ONLY skip navigation if the user is ALREADY exactly on the root destination of this tab.
                    if (!isExactlyOnRootOfThisTab) {
                        navController.navigate(item.route) {
                            // Pop up to the start destination of the graph to avoid building up a large stack
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            // Avoid multiple copies of the same destination when reselecting the same item
                            launchSingleTop = true
                            // Restore state when reselecting a previously selected item
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Icon(
                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.label,
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                ),
            )
        }
    }
}
