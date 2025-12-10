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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.pace.cs639project.ui.components.DailyProgressPieChart
import dev.pace.cs639project.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    // Inject the new ViewModel
    viewModel: HomeViewModel = viewModel(),
    onOpenDrawer: () -> Unit,
    onOpenStreakTracker: (habitId: String) -> Unit,
    onOpenApi: () -> Unit,
    onOpenSettings: () -> Unit
) {
    // Collect the data state
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = "Momentum", fontWeight = FontWeight.Bold, fontSize = 22.sp)
                },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu")
                    }
                },
                actions = {
                    IconButton(onClick = onOpenSettings) {
                        Icon(imageVector = Icons.Default.Notifications, contentDescription = "Notifications")
                    }
                }
            )
        }
    ) { innerPadding ->

        if (uiState.isLoading) {
            // Show loading indicator
            Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (uiState.error != null) {
            // Show error message
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                Text("Error loading data: ${uiState.error}", color = MaterialTheme.colorScheme.error)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF5F7FB))
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. Greeting and Streak (Using Data)
                item {
                    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally ) {
                        Text(
                            text = "Good Morning ${uiState.userName}",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF111827)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        // NOTE: Streak data is complex and is best left to the StreakTrackerScreen.
                        // For Home, we display Daily Completion.
                        Text(
                            text = "Daily Progress",
                            fontSize = 14.sp,
                            color = Color(0xFF6B7280)
                        )
                    }
                }

                // 2. PIE CHART VISUALIZATION
                item {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        // Use the new Pie Chart component
                        DailyProgressPieChart(
                            completed = uiState.completedCount,
                            total = uiState.totalHabitsCount
                        )

                    }
                }

                // 3. Today's Goals
                item {
                    Text(
                        text = "Today's Goals (${uiState.completedCount}/${uiState.totalHabitsCount})",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF111827)
                    )
                }

                // 4. List of Goals (Using Live Data)
                items(uiState.allHabits) { habit ->
                    // Determine status based on live progress data
                    val isCompleted = uiState.completedHabitIds.contains(habit.habitId)
                    val statusColor = if (isCompleted) Color(0xFF22C55E) else Color(0xFF3B82F6)

                    GoalCard(
                        title = habit.name,
                        subtitle = if (isCompleted) "Completed Today" else "Ready to start",
                        statusLabel = if (isCompleted) "Done" else "Start",
                        statusColor = statusColor,
                        habitId = habit.habitId,
                        onCardClick = onOpenStreakTracker
                    )
                }

                // 5. API Suggestions Button
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onOpenApi, modifier = Modifier.fillMaxWidth()) {
                        Text("Explore New Habit Ideas (API)")
                    }
                }
            }
        }
    }
}

// GoalCard is kept the same as it now correctly accepts habitId and onCardClick.
@Composable
fun GoalCard(
    title: String,
    subtitle: String,
    statusLabel: String,
    statusColor: Color,
    habitId: String,
    onCardClick: (habitId: String) -> Unit,
    extraRightText: String? = null
) {
    Surface(
        shape = RoundedCornerShape(30.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        tonalElevation = 0.dp,
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
            // √
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF4ADE80)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "✔", fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF111827)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = Color(0xFF6B7280)
                )
            }

            extraRightText?.let {
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = it,
                    fontSize = 12.sp,
                    color = Color(0xFF111827)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Status Badge
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = statusColor,
            ) {
                Box(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = statusLabel,
                        fontSize = 13.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}