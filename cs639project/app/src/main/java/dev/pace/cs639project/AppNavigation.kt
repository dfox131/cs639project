package dev.pace.cs639project

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
<<<<<<< Updated upstream
=======
import dev.pace.cs639project.viewmodel.AuthViewModel
import dev.pace.cs639project.ui.screens.LoginScreen
import dev.pace.cs639project.ui.screens.SignupScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.pace.cs639project.ui.screens.ProfileScreen
>>>>>>> Stashed changes


sealed class AppScreen {
    object Home : AppScreen()
    object ApiSuggestions : AppScreen()
    object Settings : AppScreen()
<<<<<<< Updated upstream
=======

    object Profile : AppScreen()
    object Habits : AppScreen()
    object AddEditHabit : AppScreen()
}

@Composable
fun AppNavigation() {
    val authViewModel: AuthViewModel = viewModel()
    val currentUserId by authViewModel.currentUserId.collectAsState()

    // Track whether we are on login or signup
    var showSignup by remember { mutableStateOf(false) }

    if (currentUserId == null) {
        // 🔐 User NOT logged in → show Login or Signup
        if (showSignup) {
            SignupScreen(
                onSignupSuccess = { showSignup = false }, // return to login
                onGoToLogin = { showSignup = false }
            )
        } else {
            LoginScreen(
                onLoginSuccess = { /* Firebase updates userId → recomposes to MomentumApp */ },
                onGoToSignup = { showSignup = true }
            )
        }
    } else {
        // 🚀 User LOGGED IN → show full app with drawer navigation
        MomentumApp(userId = currentUserId!!)
    }
>>>>>>> Stashed changes
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
<<<<<<< Updated upstream
=======

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

>>>>>>> Stashed changes
            }
        }
    ) {
        when (currentScreen) {
            is AppScreen.Home -> HomeScreen(
                onOpenDrawer = { scope.launch { drawerState.open() } },
                onOpenApi = { currentScreen = AppScreen.ApiSuggestions },
                onOpenSettings = { currentScreen = AppScreen.Settings }
            )

            is AppScreen.ApiSuggestions -> ApiSuggestionsScreen(
                onBack = { currentScreen = AppScreen.Home }
            )

            is AppScreen.Settings -> SettingsScreen(
                onBack = { currentScreen = AppScreen.Home },
                onProfileEdit = { currentScreen = AppScreen.Profile }
            )
<<<<<<< Updated upstream
=======

            is AppScreen.Profile -> {                                     // ⬅️ 新增分支
                ProfileScreen(
                    onBack = { currentScreen = AppScreen.Home }
                )
            }

            else -> {
                Text("Error: Unhandled Screen")
            }
>>>>>>> Stashed changes
        }
    }
}
