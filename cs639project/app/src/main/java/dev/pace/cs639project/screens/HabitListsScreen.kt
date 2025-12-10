package dev.pace.cs639project.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.pace.cs639project.viewmodel.FirestoreViewModel
import dev.pace.cs639project.data.Habit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitListScreen(
    viewModel: FirestoreViewModel = viewModel(),
    onBack: () -> Unit,
    onAddHabit: () -> Unit
) {
    val habits by viewModel.habits.collectAsState()
    val userId by viewModel.userId.collectAsState()

    // Load habits when userId is ready
    LaunchedEffect(userId) {
        userId?.let { viewModel.loadHabits(it) }
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
                        HabitCard(habit)
                    }
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
        }
    }
}
