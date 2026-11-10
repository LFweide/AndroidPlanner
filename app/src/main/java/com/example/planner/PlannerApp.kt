package com.example.planner

import android.content.res.Configuration
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.planner.navigation.Destination
import com.example.planner.ui.screens.HolidaysScreen
import com.example.planner.ui.screens.HomeScreen
import com.example.planner.ui.screens.SettingsScreen
import com.example.planner.ui.screens.TasksScreen

// Why: the app shell. It owns the NavController and picks bottom bar (portrait) or rail (landscape).
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlannerApp() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    // The route is null for a moment before the first screen is composed; Home is the start screen.
    val currentDestination = Destination.fromRoute(backStackEntry?.destination?.route) ?: Destination.Home
    val isTab = currentDestination in Destination.tabs
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(currentDestination.labelRes)) },
                navigationIcon = {
                    if (!isTab) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.action_back)
                            )
                        }
                    }
                },
                actions = {
                    if (isTab) {
                        IconButton(onClick = { navController.navigate(Destination.Settings.route) }) {
                            Icon(
                                Destination.Settings.icon,
                                contentDescription = stringResource(R.string.action_open_settings)
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            if (isTab && !isLandscape) {
                NavigationBar {
                    Destination.tabs.forEach { tab ->
                        NavigationBarItem(
                            selected = tab == currentDestination,
                            onClick = { navController.navigateToTab(tab) },
                            icon = { Icon(tab.icon, contentDescription = null) },
                            label = { Text(stringResource(tab.labelRes)) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Row(modifier = Modifier.padding(innerPadding)) {
            if (isTab && isLandscape) {
                NavigationRail {
                    Destination.tabs.forEach { tab ->
                        NavigationRailItem(
                            selected = tab == currentDestination,
                            onClick = { navController.navigateToTab(tab) },
                            icon = { Icon(tab.icon, contentDescription = null) },
                            label = { Text(stringResource(tab.labelRes)) }
                        )
                    }
                }
            }
            // weight(1f): the content takes all width left over after the rail.
            NavHost(
                navController = navController,
                startDestination = Destination.Home.route,
                modifier = Modifier.weight(1f)
            ) {
                composable(Destination.Home.route) { HomeScreen() }
                composable(Destination.Tasks.route) { TasksScreen() }
                composable(Destination.Holidays.route) { HolidaysScreen() }
                composable(Destination.Settings.route) { SettingsScreen() }
            }
        }
    }
}

// Why: standard tab behaviour: one copy of each tab on the back stack, and its state is restored.
private fun NavHostController.navigateToTab(tab: Destination) {
    navigate(tab.route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
