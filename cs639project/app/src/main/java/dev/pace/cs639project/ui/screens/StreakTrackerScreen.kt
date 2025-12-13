package dev.pace.cs639project.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import dev.pace.cs639project.viewmodel.FirestoreViewModel
import dev.pace.cs639project.viewmodel.StreakTrackerViewModel
import java.time.DayOfWeek
import java.time.LocalDate

@Composable fun StreakBadge(days: Int) { Text("Streak: $days Days") }
@Composable fun WeeklyStreakTracker(weeklyCompletedDays: Set<DayOfWeek>, currentDay: Int) { Text("Weekly Tracker: ${weeklyCompletedDays.size} days") }
@Composable fun StreakCalendar(completedDates: Set<LocalDate>) { Text("Calendar: ${completedDates.size} entries") }


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StreakTrackerScreen(
    habitId: String,
    userId: String,
    onNavigateBack: () -> Unit,
) {

    val viewModel: StreakTrackerViewModel = viewModel(
        key = habitId,
        factory = StreakTrackerViewModel.Factory(
            habitId = habitId,
            userId = userId
        )
    )

    val firestoreVm: FirestoreViewModel = viewModel()

    LaunchedEffect(firestoreVm) {
        firestoreVm.habitCompleted.collect { completedHabitId ->
            if (completedHabitId == habitId) {
                viewModel.reload()
            }
        }
    }


    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
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
                    IconButton(onClick = { }) {
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

            when {
                uiState.isLoading -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.height(8.dp))
                        Text("Loading streak data...")
                    }
                }
                uiState.error != null -> {
                    Text("Error: ${uiState.error}", color = MaterialTheme.colorScheme.error)
                }
                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(Modifier.height(32.dp))
                        StreakBadge(days = uiState.currentStreak)
                        Spacer(Modifier.height(32.dp))
                        WeeklyStreakTracker(
                            weeklyCompletedDays = uiState.weeklyCompletionDays,
                            currentDay = LocalDate.now().dayOfWeek.value
                        )
                        Spacer(Modifier.height(32.dp))
                        StreakCalendar(completedDates = uiState.completedDates)
                    }
                }
            }
        }
    }
}