package dev.pace.cs639project.ui.screens

import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.lang.String.format
import java.util.Locale
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.pace.cs639project.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onProfileEdit: () -> Unit = {},
    isDarkTheme: Boolean,
    onThemeChanged: (Boolean) -> Unit
) {
    // local stat
    var dailyReminderOn by remember { mutableStateOf(true) }
    var notificationTime by remember { mutableStateOf("7:00 AM") }
    var showTimeDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val authViewModel: AuthViewModel = viewModel()

    // theme color
    val backgroundColor = if (isDarkTheme) Color(0xFF020617) else Color(0xFFF5F7FB)
    val cardColor = if (isDarkTheme) Color(0xFF111827) else Color.White
    val borderColor = if (isDarkTheme) Color(0xFF374151) else Color(0xFFEEEEEE)
    val primaryTextColor = if (isDarkTheme) Color.White else Color(0xFF111827)
    val secondaryTextColor = if (isDarkTheme) Color(0xFF9CA3AF) else Color(0xFF6B7280)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onNotificationClick) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // daily Reminder
                item {
                    SettingsCard(cardColor = cardColor, borderColor = borderColor) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Daily Reminder",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = primaryTextColor
                            )
                            Switch(
                                checked = dailyReminderOn,
                                onCheckedChange = { dailyReminderOn = it }
                            )
                        }
                    }
                }

                // notification Time
                item {
                    SettingsCard(cardColor = cardColor, borderColor = borderColor) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    val trimmed = notificationTime.trim()
                                    val isPm = trimmed.uppercase(Locale.getDefault()).contains("PM")
                                    val timePart = trimmed
                                        .replace("AM", "", ignoreCase = true)
                                        .replace("PM", "", ignoreCase = true)
                                        .trim()

                                    val parts = timePart.split(":")
                                    val hour12 = parts.getOrNull(0)?.toIntOrNull() ?: 7
                                    val minute = parts.getOrNull(1)?.toIntOrNull() ?: 0
                                    val hour24 = when {
                                        isPm && hour12 in 1..11 -> hour12 + 12
                                        !isPm && hour12 == 12 -> 0
                                        else -> hour12
                                    }

                                    // time selector
                                    TimePickerDialog(
                                        context,
                                        { _, selectedHour, selectedMinute ->
                                            val amPm = if (selectedHour >= 12) "PM" else "AM"
                                            val hourForDisplay = when {
                                                selectedHour == 0 -> 12
                                                selectedHour > 12 -> selectedHour - 12
                                                else -> selectedHour
                                            }
                                            notificationTime = format(
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
                                },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Notification Time",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = primaryTextColor
                            )
                            Text(
                                text = notificationTime,
                                fontSize = 16.sp,
                                color = primaryTextColor
                            )
                        }
                    }
                }


                //  profile
                item {
                    SettingsCard(cardColor = cardColor, borderColor = borderColor) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Profile",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = primaryTextColor
                            )
                            Text(
                                text = "Edit",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF2563EB),
                                modifier = Modifier.clickable {
                                    onProfileEdit()
                                }
                            )
                        }
                    }
                }

                //  theme ,version
                item {
                    SettingsCard(cardColor = cardColor, borderColor = borderColor) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "Theme",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = primaryTextColor
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (isDarkTheme) "Dark Mode" else "Light Mode",
                                    fontSize = 14.sp,
                                    color = secondaryTextColor
                                )
                            }
                            Switch(
                                checked = isDarkTheme,
                                onCheckedChange = { onThemeChanged(it) }
                            )
                        }
                    }
                }

                //  about
                item {
                    SettingsCard(cardColor = cardColor, borderColor = borderColor) {
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "About",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = primaryTextColor
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "App version 1.0.0",
                                fontSize = 14.sp,
                                color = secondaryTextColor
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show()
                            authViewModel.logout()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text(
                            text = "Log Out",
                            color = MaterialTheme.colorScheme.onError
                        )
                    }
                }
        }
        }
    }
}

// style
@Composable
fun SettingsCard(
    cardColor: Color,
    borderColor: Color,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = cardColor,
        tonalElevation = 0.dp,
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, borderColor),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 72.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            content = content
        )
    }
}