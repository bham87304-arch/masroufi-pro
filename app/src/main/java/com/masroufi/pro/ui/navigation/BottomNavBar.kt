package com.masroufi.pro.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

import androidx.compose.ui.res.stringResource
import com.masroufi.pro.R

@Composable
fun MasroufiBottomNavBar(navController: NavController) {
    val items = listOf(
        BottomNavItem(Screen.Dashboard.route, R.string.dashboard, Icons.Default.Home),
        BottomNavItem(Screen.History.route, R.string.history, Icons.Default.FormatListBulleted),
        BottomNavItem(Screen.Stats.route, R.string.statistics, Icons.Default.BarChart),
        BottomNavItem(Screen.Settings.route, R.string.settings, Icons.Default.Settings)
    )
    
    val backStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry.value?.destination?.route
    
    // Only show bottom bar on main screens
    if (items.any { it.route == currentRoute }) {
        NavigationBar {
            items.forEach { item ->
                NavigationBarItem(
                    icon = { Icon(item.icon, contentDescription = stringResource(item.labelResId)) },
                    label = { Text(stringResource(item.labelResId)) },
                    selected = currentRoute == item.route,
                    onClick = {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    }
}

data class BottomNavItem(val route: String, val labelResId: Int, val icon: ImageVector)
