package dev.pace.cs639project.screens

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.pace.cs639project.data.Habit
import dev.pace.cs639project.viewmodel.FirestoreViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHabitScreen(
    viewModel: FirestoreViewModel = viewModel(),
    onHabitSaved: () -> Unit = {}                  // callback for navigation later if we want
) {
    val realUserId by viewModel.userId.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.initAuth()
    }

    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("custom") }
    var goalText by remember { mutableStateOf("") }
    var reminderTime by remember { mutableStateOf("") }   // "HH:mm", e.g. "08:00"

    var isSaving by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Add New Habit",
            style = MaterialTheme.typography.headlineMedium
        )

        // NAME
        OutlinedTextField(
            value = name,
            onValueChange = {
                name = it
                errorMessage = null
                successMessage = null
            },
            label = { Text("Habit name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        // TYPE (simple text for now)
        OutlinedTextField(
            value = type,
            onValueChange = {
                type = it
                successMessage = null
            },
            label = { Text("Habit type (e.g. steps, stretch, custom)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        // GOAL (optional number)
        OutlinedTextField(
            value = goalText,
            onValueChange = {
                goalText = it
                successMessage = null
            },
            label = { Text("Goal (optional, e.g. 10000 steps)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            )
        )

        // REMINDER TIME (optional)
        OutlinedTextField(
            value = reminderTime,
            onValueChange = {
                reminderTime = it
                successMessage = null
            },
            label = { Text("Reminder time (optional, HH:mm)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = { Text("08:00") }
        )

        if (errorMessage != null) {
            Text(
                text = errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        if (successMessage != null) {
            Text(
                text = successMessage!!,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Button(
            onClick = {
                errorMessage = null
                successMessage = null

                if (name.isBlank()) {
                    errorMessage = "Habit name is required."
                    return@Button
                }

                isSaving = true

                val goal = goalText.toIntOrNull()
                val reminder = reminderTime.ifBlank { null }

                if (realUserId == null) {
                    errorMessage = "User not authenticated yet. Please wait..."
                    isSaving = false
                    return@Button
                }

                val habit = Habit(
                    userId = realUserId!!,
                    name = name.trim(),
                    type = type.ifBlank { "custom" }.trim(),
                    goal = goal,
                    reminderTime = reminder
                )

                viewModel.addHabit(
                    userId = realUserId!!,
                    habit = habit,
                    onDone = {
                        isSaving = false
                        successMessage = "Habit saved successfully!"
                        // Clear fields if you want:
                        // name = ""
                        // type = "custom"
                        // goalText = ""
                        // reminderTime = ""
                        onHabitSaved()
                    }
                )
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isSaving
        ) {
            Text(if (isSaving) "Saving..." else "Save Habit")
        }
    }
}
