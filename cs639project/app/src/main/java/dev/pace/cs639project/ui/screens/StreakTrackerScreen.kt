package dev.pace.cs639project.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.pace.cs639project.ui.components.StreakBadge
import dev.pace.cs639project.ui.components.StreakCalendar
import dev.pace.cs639project.ui.components.WeeklyStreakTracker
import dev.pace.cs639project.viewmodel.StreakTrackerViewModel
import java.time.DayOfWeek
import java.time.LocalDate
@Composable fun StreakBadge(days: Int) { Text("Streak: $days Days") }
@Composable fun WeeklyStreakTracker(weeklyCompletedDays: Set<DayOfWeek>, currentDay: Int) { Text("Weekly Tracker: ${weeklyCompletedDays.size} days") }
@Composable fun StreakCalendar(completedDates: Set<LocalDate>) { Text("Calendar: ${completedDates.size} entries") }

// --------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StreakTrackerScreen(
    // The Habit ID must be passed from navigation (e.g., from HomeScreen)
    habitId: String,
    onNavigateBack: () -> Unit,
    // Initialize the ViewModel using the Factory pattern
    viewModel: StreakTrackerViewModel = viewModel(
        factory = StreakTrackerViewModel.Factory(habitId = habitId)
    )
) {
    // Collect the UI state stream from the ViewModel
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        // Display the name of the habit being tracked
                        text = uiState.habitName ?: "Streak Tracker",
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* Handle notification click */ }) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF5F7FB)),
            contentAlignment = Alignment.Center
        ) {

            // --- Error and Loading State Handling ---
            if (uiState.isLoading) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.height(8.dp))
                    Text("Loading streak data...")
                }
            } else if (uiState.error != null) {
                Text("Error: ${uiState.error}", color = MaterialTheme.colorScheme.error)
            } else {
                // --- Main Content (Data Loaded) ---
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(Modifier.height(32.dp))

                    // 2. Custom Badge - Uses the live calculated streak
                    StreakBadge(days = uiState.currentStreak)

                    Spacer(Modifier.height(32.dp))

                    // 3. Weekly Streak Tracker
                    WeeklyStreakTracker(
                        weeklyCompletedDays = uiState.weeklyCompletionDays,
                        currentDay = LocalDate.now().dayOfWeek.value // Pass current day index
                    )

                    Spacer(Modifier.height(32.dp))

                    // 4. Custom Calendar View
                    StreakCalendar(
                        completedDates = uiState.completedDates
                    )
                }
            }
        }
    }
}