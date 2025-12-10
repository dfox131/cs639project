package dev.pace.cs639project.screens

import androidx.compose.foundation.clickable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.pace.cs639project.data.Habit
import dev.pace.cs639project.viewmodel.FirestoreViewModel
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.material3.TimePicker
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.Alignment
import android.app.TimePickerDialog
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.platform.LocalContext
import java.util.Locale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHabitScreen(
    viewModel: FirestoreViewModel = viewModel(),
    onBack: () -> Unit,
    onHabitSaved: () -> Unit = {}
) {
    val realUserId by viewModel.userId.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.initAuth()
    }

    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("custom") }
    var goalText by remember { mutableStateOf("") }
    var reminderTime by remember { mutableStateOf("") }   // "HH:mm", e.g. "08:00"
    var showTimePicker by remember { mutableStateOf(false) }

    var isSaving by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add/Edit Habit") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* future notification logic */ }) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
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

            // TYPE
            Text(
                text = "Habit Type",
                style = MaterialTheme.typography.bodyMedium
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                HabitTypeChip(
                    label = "Steps",
                    selected = type == "steps",
                    onSelect = {
                        type = "steps"
                        successMessage = null
                    }
                )

                HabitTypeChip(
                    label = "Stretch & Mobility",
                    selected = type == "stretch",
                    onSelect = {
                        type = "stretch"
                        successMessage = null
                    }
                )

                HabitTypeChip(
                    label = "Custom Habit",
                    selected = type == "custom",
                    onSelect = {
                        type = "custom"
                        successMessage = null
                    }
                )
            }


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

            // REMINDER TIME (Native Picker - FIXED)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        val trimmed = reminderTime.trim()
                        val isPm = trimmed.uppercase(Locale.getDefault()).contains("PM")
                        val timePart = trimmed
                            .replace("AM", "", ignoreCase = true)
                            .replace("PM", "", ignoreCase = true)
                            .trim()

                        val parts = timePart.split(":")
                        val hour12 = parts.getOrNull(0)?.toIntOrNull() ?: 8
                        val minute = parts.getOrNull(1)?.toIntOrNull() ?: 0

                        val hour24 = when {
                            isPm && hour12 in 1..11 -> hour12 + 12
                            !isPm && hour12 == 12 -> 0
                            else -> hour12
                        }

                        TimePickerDialog(
                            context,
                            { _, selectedHour, selectedMinute ->
                                val amPm = if (selectedHour >= 12) "PM" else "AM"
                                val hourForDisplay = when {
                                    selectedHour == 0 -> 12
                                    selectedHour > 12 -> selectedHour - 12
                                    else -> selectedHour
                                }

                                reminderTime = String.format(
                                    Locale.getDefault(),
                                    "%d:%02d %s",
                                    hourForDisplay,
                                    selectedMinute,
                                    amPm
                                )
                            },
                            hour24,
                            minute,
                            false
                        ).show()
                    }
            ) {
                OutlinedTextField(
                    value = reminderTime,
                    onValueChange = {},
                    label = { Text("Reminder time") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    enabled = false, // still disabled for click handling
                    trailingIcon = {
                        Icon(Icons.Default.Notifications, contentDescription = null)
                    },
                    placeholder = { Text("10:00 AM") },
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledBorderColor = MaterialTheme.colorScheme.outline
                    )
                )
            }

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
                            onHabitSaved()
                        }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !isSaving,
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50),
                    contentColor = Color.White,
                    disabledContainerColor = Color(0xFFBDBDBD)
                )
            ) {
                Text(
                    text = if (isSaving) "Saving..." else "Save Habit",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun HabitTypeChip(
    label: String,
    selected: Boolean,
    onSelect: () -> Unit
) {
    AssistChip(
        onClick = onSelect,
        label = { Text(label) },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = if (selected)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.surfaceVariant,
            labelColor = if (selected)
                MaterialTheme.colorScheme.onPrimary
            else
                MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
}