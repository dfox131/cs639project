package dev.pace.cs639project

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch


sealed class AppScreen {
    object Home : AppScreen()
    object StreakTracker : AppScreen()
    object ApiSuggestions : AppScreen()
    object Settings : AppScreen()
}


@Composable
fun MomentumApp() {
    var currentScreen by remember { mutableStateOf<AppScreen>(AppScreen.Home) }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    text = "Menu",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleMedium
                )

                // Home
                NavigationDrawerItem(
                    label = { Text("Home") },
                    selected = currentScreen is AppScreen.Home,
                    onClick = {
                        currentScreen = AppScreen.Home
                        scope.launch { drawerState.close() }
                    },
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    label = { Text("Streak Tracker") },
                    selected = currentScreen is AppScreen.StreakTracker,
                    onClick = {
                        currentScreen = AppScreen.StreakTracker
                        scope.launch { drawerState.close() }
                    },
                    icon = { Icon(Icons.Default.Star, contentDescription = null) }, // Using Star icon
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                // API Suggestions
                NavigationDrawerItem(
                    label = { Text("API Suggestions") },
                    selected = currentScreen is AppScreen.ApiSuggestions,
                    onClick = {
                        currentScreen = AppScreen.ApiSuggestions
                        scope.launch { drawerState.close() }
                    },
                    icon = { Icon(Icons.Default.List, contentDescription = null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                // Settings
                NavigationDrawerItem(
                    label = { Text("Settings") },
                    selected = currentScreen is AppScreen.Settings,
                    onClick = {
                        currentScreen = AppScreen.Settings
                        scope.launch { drawerState.close() }
                    },
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }
        }
    ) {
        when (currentScreen) {
            is AppScreen.Home -> HomeScreen(
                onOpenDrawer = { scope.launch { drawerState.open() } },
                onOpenStreakTracker = { currentScreen = AppScreen.StreakTracker },
                onOpenApi = { currentScreen = AppScreen.ApiSuggestions },
                onOpenSettings = { currentScreen = AppScreen.Settings }
            )

            is AppScreen.StreakTracker -> StreakTrackerScreen(
                onNavigateBack = { currentScreen = AppScreen.Home }
            )

            is AppScreen.ApiSuggestions -> ApiSuggestionsScreen(
                onBack = { currentScreen = AppScreen.Home }
            )

            is AppScreen.Settings -> SettingsScreen(
                onBack = { currentScreen = AppScreen.Home }
            )
        }
    }
}