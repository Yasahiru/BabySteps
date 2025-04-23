package com.cmc.babysteps.ui.screens

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.*
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.cmc.babysteps.data.viewmodel.CalendarViewModel
import com.cmc.babysteps.ui.screens.home.HomeScreen
import com.cmc.babysteps.ui.screens.reminder.ReminderScreen
import java.time.LocalDate
import kotlin.collections.forEach
import kotlin.sequences.any

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("ContextCastToActivity")
@Composable
fun MainScreen(
    calendarViewModel: CalendarViewModel,
    onCalendarDatesRequested: () -> Pair<LocalDate, LocalDate>
) {
    val navController = rememberNavController()
    val activity = LocalContext.current as Activity
    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination

                val items = listOf(
                    BottomNavItem("Home", "home", Icons.Default.Home),
                    BottomNavItem("Calendar", "calendar", Icons.Default.DateRange),
                    BottomNavItem("Reminder", "reminder", Icons.Default.Notifications)
                )

                items.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                        selected = currentRoute?.hierarchy?.any { it.route == item.route } == true,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }

                        }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(padding)
        ) {
            composable("home") {
                HomeScreen()
            }
            composable("calendar") {
                val (start, end) = onCalendarDatesRequested()
                CalendarScreen(
                    navController = navController,
                    activity,
                    viewModel = calendarViewModel,
                    startDate = start,
                    endDate = end
                )
            }
            composable("reminder") {
                ReminderScreen()
            }
        }
    }

}

data class BottomNavItem(
    val label: String,
    val route: String,
    val icon: ImageVector
)
