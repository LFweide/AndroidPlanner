package com.example.planner.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.planner.R

// Why: one place that lists every route, so the bar, the rail and the NavHost cannot disagree.
enum class Destination(
    val route: String,
    @param:StringRes val labelRes: Int,
    val icon: ImageVector
) {
    Home("home", R.string.nav_home, Icons.Filled.Home),
    Tasks("tasks", R.string.nav_tasks, Icons.AutoMirrored.Filled.List),
    Holidays("holidays", R.string.nav_holidays, Icons.Filled.DateRange),
    Settings("settings", R.string.settings_title, Icons.Filled.Settings);

    companion object {
        // The three destinations shown in the bottom bar / rail. Settings is opened from the top bar.
        val tabs = listOf(Home, Tasks, Holidays)

        fun fromRoute(route: String?): Destination? = entries.firstOrNull { it.route == route }
    }
}
