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
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.StepsRecord
import dev.pace.cs639project.ui.components.DailyProgressPieChart
import dev.pace.cs639project.viewmodel.HomeViewModel
import dev.pace.cs639project.viewmodel.HealthViewModel
import dev.pace.cs639project.ui.components.SavedConfirmationMessage
import dev.pace.cs639project.viewmodel.AuthViewModel
import kotlinx.coroutines.delay

val STEPS_READ_PERMISSIONS: Set<String> = setOf(
    HealthPermission.getReadPermission(StepsRecord::class)
)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    healthViewModel: HealthViewModel = viewModel(
        factory = HealthViewModel.Factory(LocalContext.current.applicationContext)
    ),
    onOpenDrawer: () -> Unit,
    onOpenStreakTracker: (habitId: String) -> Unit,
    onOpenApi: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val healthState by healthViewModel.uiState.collectAsState() 
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = {
            healthViewModel.permissionsRequestCompleted()
        }
    )

    val authViewModel: AuthViewModel = viewModel()
    val justSignedUp by authViewModel.justSignedUp.collectAsState()

    LaunchedEffect(healthState.permissionsRequired) {
        if (healthState.permissionsRequired) {
            permissionLauncher.launch(STEPS_READ_PERMISSIONS.toTypedArray())
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "Momentum", fontWeight = FontWeight.Bold, fontSize = 22.sp) },
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

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            // --- Handle Loading and Error States ---
            if (uiState.isLoading || healthState.isLoading) {
                Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (uiState.error != null) {
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
                    item {
                        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally ) {
                            Text(
                                text = "Good Morning ${uiState.userName}",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF111827)
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            if (healthState.permissionsGranted) {
                                Text(
                                    text = "Steps Today: ${healthState.stepsToday}",
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            } else if (healthState.permissionsRequired) {
                                PermissionPromptCard(onClick = {
                                    permissionLauncher.launch(STEPS_READ_PERMISSIONS.toTypedArray())
                                })
                            }
                        }
                    }

                    item {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
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
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF111827)
                        )
                    }

                    items(uiState.allHabits) { habit ->
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

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = onOpenApi, modifier = Modifier.fillMaxWidth()) {
                            Text("Explore New Habit Ideas (API)")
                        }
                    }
                }
            }
            if (justSignedUp) {
                SavedConfirmationMessage(
                    message = "Account created successfully 🎉"
                )

                LaunchedEffect(Unit) {
                    delay(2000)
                    authViewModel.clearSignupFlag()
                }
            }

        }
    }
}


@Composable
fun PermissionPromptCard(onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.padding(16.dp).fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Enable Step Tracking", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onErrorContainer)
            Text("Tap here to authorize Health Connect access for accurate step tracking.", color = MaterialTheme.colorScheme.onErrorContainer)
            Spacer(Modifier.height(8.dp))
            Button(onClick = onClick) {
                Text("Grant Permissions")
            }
        }
    }
}

// GoalCard definition (kept for reference, should be defined once in the file)
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