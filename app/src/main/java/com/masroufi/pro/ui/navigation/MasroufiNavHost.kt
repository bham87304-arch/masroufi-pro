package com.masroufi.pro.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.masroufi.pro.data.model.TransactionType
import com.masroufi.pro.ui.screen.accounts.AccountsScreen
import com.masroufi.pro.ui.screen.auth.AuthScreen
import com.masroufi.pro.ui.screen.categories.CategoriesScreen
import com.masroufi.pro.ui.screen.dashboard.DashboardScreen
import com.masroufi.pro.ui.screen.history.HistoryScreen
import com.masroufi.pro.ui.screen.reminders.RemindersScreen
import com.masroufi.pro.ui.screen.settings.SettingsScreen
import com.masroufi.pro.ui.screen.stats.StatsScreen
import com.masroufi.pro.ui.screen.transaction.AddTransactionScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MasroufiNavHost() {
    val navController = rememberNavController()
    var isFabExpanded by remember { mutableStateOf(false) }

    // Track current route for conditional UI
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Only show bottom bar and FAB on main tabs
    val mainRoutes = listOf(Screen.Dashboard.route, Screen.History.route, Screen.Stats.route, Screen.Settings.route)
    val showBottomBar = currentRoute in mainRoutes

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                MasroufiBottomNavBar(navController = navController)
            }
        },
        floatingActionButton = {
            if (showBottomBar) {
                Column(horizontalAlignment = Alignment.End) {
                    AnimatedVisibility(
                        visible = isFabExpanded,
                        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
                    ) {
                        Column(horizontalAlignment = Alignment.End) {
                            // Add Income mini FAB
                            SmallFloatingActionButton(
                                onClick = {
                                    isFabExpanded = false
                                    navController.navigate(Screen.AddTransaction.createRoute(TransactionType.INCOME))
                                },
                                containerColor = Color(0xFF2E7D32)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Add Income", tint = Color.White)
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            // Add Expense mini FAB
                            SmallFloatingActionButton(
                                onClick = {
                                    isFabExpanded = false
                                    navController.navigate(Screen.AddTransaction.createRoute(TransactionType.EXPENSE))
                                },
                                containerColor = Color(0xFFC62828)
                            ) {
                                Icon(Icons.Default.Remove, contentDescription = "Add Expense", tint = Color.White)
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                    FloatingActionButton(
                        onClick = { isFabExpanded = !isFabExpanded },
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Transaction")
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Auth.route,
            modifier = Modifier.padding(padding)
        ) {
            // Main tab destinations
            composable(Screen.Dashboard.route) { DashboardScreen(navController) }
            composable(Screen.History.route) { HistoryScreen(navController) }
            composable(Screen.Stats.route) { StatsScreen() }
            composable(Screen.Settings.route) { SettingsScreen(navController = navController) }

            // Add/Edit Transaction
            composable(
                route = Screen.AddTransaction.route,
                arguments = listOf(navArgument("type") { type = NavType.StringType })
            ) {
                val typeStr = it.arguments?.getString("type") ?: TransactionType.EXPENSE.name
                AddTransactionScreen(
                    navController = navController,
                    transactionType = TransactionType.valueOf(typeStr)
                )
            }
            composable(
                route = Screen.EditTransaction.route,
                arguments = listOf(navArgument("transactionId") { type = NavType.StringType })
            ) {
                AddTransactionScreen(
                    navController = navController,
                    transactionType = TransactionType.EXPENSE // Will be overridden by loaded data
                )
            }

            // Secondary screens
            composable(Screen.Categories.route) { CategoriesScreen() }
            composable(Screen.Accounts.route) { AccountsScreen() }
            composable(Screen.Reminders.route) { RemindersScreen(navController = navController) }
            composable(Screen.Auth.route) {
                AuthScreen(
                    onAuthSuccess = {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Auth.route) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}
