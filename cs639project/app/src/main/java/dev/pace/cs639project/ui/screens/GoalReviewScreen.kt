package dev.pace.cs639project.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.* // Import all runtime composables (including remember/LaunchedEffect)
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.pace.cs639project.data.Habit
import dev.pace.cs639project.viewmodel.HabitGoalsViewModel
import kotlinx.coroutines.launch // Required for coroutine scope inside LaunchedEffect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalReviewScreen(
    userId: String,
    onBack: () -> Unit
) {
    val viewModel: HabitGoalsViewModel = viewModel(
        factory = HabitGoalsViewModel.Factory(userId = userId)
    )
    val uiState by viewModel.uiState.collectAsState()

    // 🛠️ FIX 1: Define the state manager for the Snackbar
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope() // Required for launching the snackbar coroutine

    // 🛠️ FIX 2: Observe the ViewModel statusMessage and show the Snackbar
    LaunchedEffect(uiState.statusMessage) {
        val message = uiState.statusMessage
        if (message != null) {
            scope.launch {
                // Show the snackbar message
                snackbarHostState.showSnackbar(
                    message = message,
                    actionLabel = "DISMISS", // Optional dismiss button text
                    duration = SnackbarDuration.Short
                )
                // Clear the message in the ViewModel immediately after showing
                // (You'll need a clearStatusMessage() function in HabitGoalsViewModel)
                // viewModel.clearStatusMessage()
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Monthly Goal Review", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        // 🛠️ FIX 3: Pass the hostState to the SnackbarHost
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            if (uiState.isLoading) {
                CircularProgressIndicator(Modifier.padding(24.dp))
                Text("Loading habits...")
            } else if (uiState.error != null) {
                Text("Error: ${uiState.error}", color = MaterialTheme.colorScheme.error)
            } else if (uiState.habitsToReview.isEmpty()) {
                Text("No quantifiable habits require a monthly goal review.",
                    Modifier.padding(24.dp),
                    style = MaterialTheme.typography.titleMedium)
            } else {
                Text(
                    "Review ${uiState.habitsToReview.size} habits for incremental goal setting:",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.habitsToReview) { habit ->
                        ReviewHabitCard(habit = habit, onSetNewGoal = {
                            viewModel.calculateAndSetNextTarget(habit)
                        })
                    }
                }
            }
        }
    }
}

@Composable
fun ReviewHabitCard(
    habit: Habit,
    onSetNewGoal: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(habit.name, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(4.dp))
                Text(
                    "Current Target: ${habit.goal ?: 0} (${habit.type})",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Button(
                onClick = onSetNewGoal,
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "Set Next Goal", Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text("Analyze & Update")
            }
        }
    }
}