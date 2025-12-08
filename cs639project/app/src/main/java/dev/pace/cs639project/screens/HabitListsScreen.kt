package dev.pace.cs639project.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.pace.cs639project.viewmodel.FirestoreViewModel
import dev.pace.cs639project.data.Habit

@Composable
fun HabitListScreen(
    viewModel: FirestoreViewModel = viewModel()
) {
    val habits by viewModel.habits.collectAsState()
    val userId by viewModel.userId.collectAsState()

    // Load habits once userId is ready
    LaunchedEffect(userId) {
        userId?.let {
            viewModel.loadHabits(it)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "My Habits",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

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
                    HabitCard(habit)
                }
            }
        }
    }
}

@Composable
fun HabitCard(habit: Habit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
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
        }
    }
}
