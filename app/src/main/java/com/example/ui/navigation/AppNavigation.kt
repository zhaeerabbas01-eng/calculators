package com.example.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.CalcXApplication
import com.example.ui.screens.calculator.CalculatorScreen
import com.example.ui.screens.calculator.CalculatorViewModel
import kotlinx.serialization.Serializable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.filled.ShowChart
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.aimath.AiMathScreen
import com.example.ui.screens.history.HistoryScreen
import com.example.ui.screens.settings.SettingsScreen
import com.example.ui.screens.tools.ToolsScreen
import com.example.ui.screens.market.MarketScreen

import com.example.ui.screens.tools.ToolFeatureScreen
import androidx.navigation.NavType
import androidx.navigation.navArgument

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Calculator : Screen("calculator", "Calculator", Icons.Default.Calculate)
    object Tools : Screen("tools", "Tools", Icons.Default.Widgets)
    object History : Screen("history", "History", Icons.Default.History)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
    object AiMath : Screen("aimath", "AI Math", Icons.Default.AutoAwesome)
    object Market : Screen("market", "Market", Icons.Default.ShowChart)
    object ToolFeature : Screen("tool/{toolType}", "Tool", Icons.Default.Widgets) {
        fun createRoute(toolType: String) = "tool/$toolType"
    }
}

val bottomNavItems = listOf(
    Screen.Home,
    Screen.Calculator,
    Screen.Tools,
    Screen.History,
    Screen.Settings
)

@Composable
fun AppNavigation(isDarkTheme: Boolean, onThemeToggle: () -> Unit) {
    val navController = rememberNavController()
    
    val context = LocalContext.current
    val app = context.applicationContext as CalcXApplication
    val calculatorViewModel: CalculatorViewModel = viewModel(
        factory = CalculatorViewModel.Factory(app.historyRepository)
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                bottomNavItems.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                // on the back stack as users select items
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination when
                                // reselecting the same item
                                launchSingleTop = true
                                // Restore state when reselecting a previously selected item
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Calculator.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onNavigateToCalculator = { navController.navigate(Screen.Calculator.route) },
                    onNavigateToTools = { navController.navigate(Screen.Tools.route) },
                    onNavigateToHistory = { navController.navigate(Screen.History.route) }
                )
            }
            composable(Screen.Calculator.route) {
                CalculatorScreen(
                    viewModel = calculatorViewModel,
                    isDarkTheme = isDarkTheme,
                    onThemeToggle = onThemeToggle
                )
            }
            composable(Screen.Tools.route) {
                ToolsScreen(
                    onNavigateToAiMath = { navController.navigate(Screen.AiMath.route) },
                    onNavigateToMarket = { navController.navigate(Screen.Market.route) },
                    onNavigateToTool = { toolType -> navController.navigate(Screen.ToolFeature.createRoute(toolType)) }
                )
            }
            composable(Screen.History.route) {
                HistoryScreen()
            }
            composable(Screen.Settings.route) {
                SettingsScreen()
            }
            composable(Screen.AiMath.route) {
                AiMathScreen()
            }
            composable(Screen.Market.route) {
                MarketScreen()
            }
            composable(
                route = Screen.ToolFeature.route,
                arguments = listOf(navArgument("toolType") { type = NavType.StringType })
            ) { backStackEntry ->
                val toolType = backStackEntry.arguments?.getString("toolType") ?: ""
                ToolFeatureScreen(
                    toolType = toolType,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
