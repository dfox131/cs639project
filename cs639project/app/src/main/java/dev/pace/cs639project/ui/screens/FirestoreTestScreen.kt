package dev.pace.cs639project.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.pace.cs639project.data.Habit
import dev.pace.cs639project.viewmodel.FirestoreViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FirestoreTestScreen(
    viewModel: FirestoreViewModel = viewModel()
) {
    // Hardcoded test user until Firebase Auth is added
    val testUserId = "TEST_USER_123"

    var habitName by remember { mutableStateOf("") }

    // Observe state from ViewModel
    val habits by viewModel.habits.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Firestore Test Screen",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.height(16.dp))

        // -----------------------
        // INPUT FIELD
        // -----------------------
        OutlinedTextField(
            value = habitName,
            onValueChange = { habitName = it },
            label = { Text("Habit name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        // CREATE HABIT BUTTON
        Button(
            onClick = {
                if (habitName.isNotBlank()) {
                    viewModel.addHabit(
                        userId = testUserId,
                        habit = Habit(
                            userId = testUserId,
                            name = habitName,
                            type = "custom",
                            goal = null,
                            reminderTime = null
                        ),
                        onDone = {
                            habitName = ""   // clear field
                        }
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Habit to Firestore")
        }


        Spacer(Modifier.height(20.dp))

        // -----------------------
        // LOAD HABITS
        // -----------------------
        Button(
            onClick = { viewModel.loadHabits(testUserId) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Load Habits")
        }

        Spacer(Modifier.height(20.dp))

        // -----------------------
        // DISPLAY HABITS
        // -----------------------
        LazyColumn {
            items(habits) { habit ->
                Text(
                    text = "- ${habit.name}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}
