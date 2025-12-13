package dev.pace.cs639project.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalContext
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import dev.pace.cs639project.ui.components.DailyProgressPieChart
import dev.pace.cs639project.viewmodel.HomeViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    userId: String,
    viewModel: HomeViewModel = viewModel(),
    onOpenDrawer: () -> Unit,
    onOpenStreakTracker: (habitId: String) -> Unit,
    onOpenApi: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    // ✅ REQUIRED: load data for authenticated user
    LaunchedEffect(userId) {
        viewModel.loadDailyData(userId)
    }


    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Momentum", fontWeight = FontWeight.Bold, fontSize = 22.sp) },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                },
                actions = {
                    IconButton(onClick = onOpenSettings) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                    }
                }
            )
        }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                uiState.isLoading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                uiState.error != null -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            "Error loading data: ${uiState.error}",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFFF5F7FB))
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {

                        item {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Good Morning ${uiState.userName}",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Medium
                                )

                                Spacer(Modifier.height(8.dp))





                            }
                        }

                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                DailyProgressPieChart(
                                    completed = uiState.completedCount,
                                    total = uiState.totalHabitsCount
                                )
                            }
                        }

                        item {
                            Text(
                                text = "Today's Goals (${uiState.completedCount}/${uiState.totalHabitsCount})",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        items(uiState.allHabits) { habit ->
                            val isCompleted =
                                uiState.completedHabitIds.contains(habit.habitId)
                            val statusColor =
                                if (isCompleted) Color(0xFF22C55E) else Color(0xFF3B82F6)

                            GoalCard(
                                title = habit.name,
                                subtitle = if (isCompleted) "Completed Today" else "Ready to start",
                                statusLabel = if (isCompleted) "Done" else "Start",
                                statusColor = statusColor,
                                habitId = habit.habitId,
                                onCardClick = onOpenStreakTracker
                            )
                        }

                        item {
                            Spacer(Modifier.height(16.dp))
                            Button(onClick = onOpenApi, modifier = Modifier.fillMaxWidth()) {
                                Text("Explore New Habit Ideas (API)")
                            }
                        }
                    }
                }
            }
        }
    }
}

/* ---------------- Supporting composables ---------------- */

@Composable
fun PermissionPromptCard(onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                "Enable Step Tracking",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            Text(
                "Tap here to authorize Health Connect access for accurate step tracking.",
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            Spacer(Modifier.height(8.dp))
            Button(onClick = onClick) {
                Text("Grant Permissions")
            }
        }
    }
}

@Composable
fun GoalCard(
    title: String,
    subtitle: String,
    statusLabel: String,
    statusColor: Color,
    habitId: String,
    onCardClick: (habitId: String) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(30.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCardClick(habitId) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF4ADE80)),
                contentAlignment = Alignment.Center
            ) {
                Text("✔", fontSize = 14.sp)
            }

            Spacer(Modifier.width(12.dp))

            Column(Modifier.weight(1f)) {
                Text(title, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                Text(subtitle, fontSize = 12.sp, color = Color(0xFF6B7280))
            }

            Spacer(Modifier.width(8.dp))

            Surface(shape = RoundedCornerShape(20.dp), color = statusColor) {
                Box(Modifier.padding(horizontal = 12.dp, vertical = 4.dp)) {
                    Text(statusLabel, fontSize = 13.sp, color = Color.White)
                }
            }
        }
    }
}
