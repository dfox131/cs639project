package dev.pace.cs639project.ui.screens

import androidx.compose.foundation.clickable 
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.pace.cs639project.viewmodel.FirestoreViewModel
import dev.pace.cs639project.data.Habit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitListScreen(
    userId: String,
    viewModel: FirestoreViewModel = viewModel(),
    onBack: () -> Unit,
    onAddHabit: () -> Unit,
    onOpenStreakTracker: (habitId: String) -> Unit
) {
    // Fire whenever userId changes (first login, or switching accounts)
    LaunchedEffect(userId) {
        viewModel.initAuth()
        viewModel.loadHabits(userId)
    }

    val habits by viewModel.habits.collectAsState()

    LaunchedEffect(userId) {
        viewModel.loadHabits(userId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Habits") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddHabit,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Habit")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            if (habits.isEmpty()) {
                Text(
                    text = "No habits yet. Add your first habit!",
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(habits) { habit ->
                        // 2. PASSED: Pass the habit object and the new navigation callback
                        HabitCard(
                            habit = habit,
                            userId = userId,
                            firestoreVm = viewModel,
                            onCardClick = { onOpenStreakTracker(habit.habitId) }
                        )

                    }
                }
            }
        }
    }
}

@Composable
fun HabitCard(
    habit: Habit,
    userId: String,
    firestoreVm: FirestoreViewModel,
    onCardClick: (habitId: String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            // 4. ADDED: Make the whole card clickable and pass the habitId
            .clickable { onCardClick(habit.habitId) },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                text = habit.name,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = "Type: ${habit.type}",
                style = MaterialTheme.typography.bodyMedium
            )

            habit.goal?.let {
                Text(
                    text = "Goal: $it",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            habit.reminderTime?.let {
                Text(
                    text = "Reminder: $it",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {

                // VIEW STREAK BUTTON
                Button(
                    onClick = { onCardClick(habit.habitId) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(text = "View Streak")
                }

                // MARK COMPLETED BUTTON (GREEN)
                Button(
                    onClick = {
                        firestoreVm.markHabitCompleted(
                            userId = userId,
                            habitId = habit.habitId
                        )
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50),
                        contentColor = Color.White
                    )
                ) {
                    Text(text = "Mark Completed")
                }
            }

        }
    }
}