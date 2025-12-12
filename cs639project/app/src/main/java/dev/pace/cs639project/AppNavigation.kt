package dev.pace.cs639project

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.pace.cs639project.ui.screens.AddHabitScreen
import dev.pace.cs639project.ui.screens.ApiSuggestionsScreen
import dev.pace.cs639project.ui.screens.HabitListScreen
import dev.pace.cs639project.ui.screens.HomeScreen
import dev.pace.cs639project.ui.screens.SettingsScreen
import dev.pace.cs639project.ui.screens.StreakTrackerScreen
import kotlinx.coroutines.launch
import dev.pace.cs639project.viewmodel.AuthViewModel
import dev.pace.cs639project.ui.screens.LoginScreen
import dev.pace.cs639project.ui.screens.SignupScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.pace.cs639project.ui.screens.ProfileScreen


sealed class AppScreen {
    object Home : AppScreen()
    data class StreakTracker(val habitId: String? = null) : AppScreen()
    object ApiSuggestions : AppScreen()
    object Settings : AppScreen()

    object Habits : AppScreen()
    object AddEditHabit : AppScreen()

    object Profile : AppScreen()
}

@Composable
fun AppNavigation(
    isDarkTheme: Boolean,
    onThemeChanged: (Boolean) -> Unit
) {
    val authViewModel: AuthViewModel = viewModel()
    val currentUserId by authViewModel.currentUserId.collectAsState()

    // Track whether we are on login or signup
    var showSignup by remember { mutableStateOf(false) }

    if (currentUserId == null) {

        if (showSignup) {

            SignupScreen(
                onSignupSuccess = { showSignup = false },
                onBackToLogin = { showSignup = false }
            )

        } else {

            LoginScreen(
                onLoginSuccess = { /* Firebase sets userId → recompose */ },
                onGoToSignup = { showSignup = true }
            )
        }
    }
    else {
        // 🚀 User LOGGED IN → show full app with drawer navigation
        MomentumApp(
            userId = currentUserId!!,
            isDarkTheme = isDarkTheme,
            onThemeChanged = onThemeChanged
        )
    }
}


@Composable
fun MomentumApp(
    userId: String,
    isDarkTheme: Boolean,
    onThemeChanged: (Boolean) -> Unit
) {
    // ⚠️ 之前这里有一行错误的 "fun MomentumApp(userId: String) {" 导致了语法错误，已删除

    var currentScreen by remember { mutableStateOf<AppScreen>(AppScreen.Home) }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val defaultStreakHabitId = "DEFAULT_HABIT_ID_FOR_DRAWER"

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    text = "Menu",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleMedium
                )

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
                        currentScreen = AppScreen.StreakTracker(habitId = defaultStreakHabitId)
                        scope.launch { drawerState.close() }
                    },
                    icon = { Icon(Icons.Default.Star, contentDescription = null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

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

                NavigationDrawerItem(
                    label = { Text("Add Habit") },
                    selected = currentScreen is AppScreen.AddEditHabit,
                    onClick = {
                        currentScreen = AppScreen.AddEditHabit
                        scope.launch { drawerState.close() }
                    },
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )


                NavigationDrawerItem(
                    label = { Text("My Habits") },
                    selected = currentScreen is AppScreen.Habits,
                    onClick = {
                        currentScreen = AppScreen.Habits
                        scope.launch { drawerState.close() }
                    },
                    icon = { Icon(Icons.Default.List, contentDescription = null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    label = { Text("Profile") },
                    selected = currentScreen is AppScreen.Profile,
                    onClick = {
                        currentScreen = AppScreen.Profile
                        scope.launch { drawerState.close() }
                    },
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

            }
        }
    ) {
        when (currentScreen) {
            is AppScreen.Home -> HomeScreen(
                onOpenDrawer = { scope.launch { drawerState.open() } },
                onOpenStreakTracker = { habitId ->
                    currentScreen = AppScreen.StreakTracker(habitId = habitId)
                },
                onOpenApi = { currentScreen = AppScreen.ApiSuggestions },
                onOpenSettings = { currentScreen = AppScreen.Settings }
            )

            is AppScreen.StreakTracker -> StreakTrackerScreen(
                habitId = (currentScreen as AppScreen.StreakTracker).habitId!!,
                userId = userId,
                onNavigateBack = { currentScreen = AppScreen.Home }
            )

            is AppScreen.Habits -> HabitListScreen(
                userId = userId,
                onBack = { currentScreen = AppScreen.Home },
                onAddHabit = { currentScreen = AppScreen.AddEditHabit },
                onOpenStreakTracker = { habitId -> currentScreen = AppScreen.StreakTracker(habitId = habitId) }
            )

            is AppScreen.AddEditHabit -> AddHabitScreen(
                userId = userId,
                onBack = { currentScreen = AppScreen.Home },
                onHabitSaved = { currentScreen = AppScreen.Habits }
            )

            is AppScreen.ApiSuggestions -> ApiSuggestionsScreen(
                onBack = { currentScreen = AppScreen.Home }
            )

            is AppScreen.Settings -> SettingsScreen(
                onBack = { currentScreen = AppScreen.Home },
                onProfileEdit = { currentScreen = AppScreen.Profile },
                isDarkTheme = isDarkTheme,
                onThemeChanged = onThemeChanged
            )

            // ⚠️ 修复了这里的参数传递：去掉了重复的 onBack，保留了 userId 和 isDarkTheme
            is AppScreen.Profile -> ProfileScreen(
                onBack = { currentScreen = AppScreen.Home },
                isDarkTheme = isDarkTheme,
                userId = userId
            )


            else -> {
                Text("Error: Unhandled Screen")
            }
        }
    }
}