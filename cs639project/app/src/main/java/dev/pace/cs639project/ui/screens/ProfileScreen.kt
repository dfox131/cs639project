package dev.pace.cs639project.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

private enum class UnitSystem { Metric, Imperial }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBack: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    isDarkTheme: Boolean
) {
    // local stat
    var email by remember { mutableStateOf("alex@example.com") }   // string
    var sex by remember { mutableStateOf("male") }                 // "male" / "female" / "other"（可选）
    var heightCm by remember { mutableStateOf(175.0) }             // number
    var weightKg by remember { mutableStateOf(70.0) }              // number
    var unitSystem by remember { mutableStateOf(UnitSystem.Metric) }
    var heightInput by remember { mutableStateOf("175") }
    var weightInput by remember { mutableStateOf("70") }

    val context = LocalContext.current

    val screenBackgroundColor = if (isDarkTheme) Color(0xFF020617) else Color(0xFFF5F7FB)
    val cardBackgroundColor = if (isDarkTheme) Color(0xFF111827) else Color.White
    val primaryTextColor = if (isDarkTheme) Color.White else Color(0xFF111827)
    val secondaryTextColor = if (isDarkTheme) Color(0xFF9CA3AF) else Color(0xFF6B7280)

    val chipSelectedColor = if (isDarkTheme) Color(0xFF1E3A8A) else Color(0xFFDBEAFE)
    val chipUnselectedColor = if (isDarkTheme) Color(0xFF374151) else Color(0xFFE5E7EB)

    val editLinkColor = if (isDarkTheme) Color(0xFF60A5FA) else Color(0xFF2563EB)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Profile") },
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
                .background(screenBackgroundColor)
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // img
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(96.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFFC4D6)),
                            contentAlignment = Alignment.Center
                        ) {
                            val initial = email
                                .takeIf { it.isNotBlank() }
                                ?.firstOrNull()
                                ?.uppercaseChar()
                                ?.toString()
                                ?: "U"

                            Text(
                                text = initial,
                                fontSize = 40.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = email,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = primaryTextColor
                        )

                        Text(
                            text = "Mapped to Firestore collection: users",
                            fontSize = 12.sp,
                            color = secondaryTextColor,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // email + gender
                item {
                    ProfileSectionCard(title = "Account", backgroundColor = cardBackgroundColor, titleColor = primaryTextColor) {
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email (matches Firebase Auth)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = primaryTextColor,
                                unfocusedTextColor = primaryTextColor,
                                focusedLabelColor = primaryTextColor,
                                unfocusedLabelColor = secondaryTextColor
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Gender",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = primaryTextColor
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        // male / female / other
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            SexOptionChip(
                                label = "Male",
                                value = "male",
                                selected = sex == "male",
                                onSelected = { sex = "male" },
                                selectedColor = chipSelectedColor,
                                unselectedColor = chipUnselectedColor,
                                textColor = primaryTextColor
                            )
                            SexOptionChip(
                                label = "Female",
                                value = "female",
                                selected = sex == "female",
                                onSelected = { sex = "female" },
                                selectedColor = chipSelectedColor,
                                unselectedColor = chipUnselectedColor,
                                textColor = primaryTextColor
                            )
                            SexOptionChip(
                                label = "Other",
                                value = "other",
                                selected = sex == "other",
                                onSelected = { sex = "other" },
                                selectedColor = chipSelectedColor,
                                unselectedColor = chipUnselectedColor,
                                textColor = primaryTextColor
                            )
                        }
                    }
                }

                // measurements
                item {
                    ProfileSectionCard(title = "Measurements", backgroundColor = cardBackgroundColor, titleColor = primaryTextColor) {

                        // switch unit (label + chips 分两行，防止挤）
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Unit system:",
                                fontSize = 14.sp,
                                color = primaryTextColor
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                AssistChip(
                                    onClick = {
                                        unitSystem = UnitSystem.Metric
                                        heightInput = formatNumber(heightCm)
                                        weightInput = formatNumber(weightKg)
                                    },
                                    label = { Text("Metric", color = primaryTextColor) },
                                    colors = AssistChipDefaults.assistChipColors(
                                        containerColor = if (unitSystem == UnitSystem.Metric)
                                            chipSelectedColor else chipUnselectedColor
                                    )
                                )

                                AssistChip(
                                    onClick = {
                                        unitSystem = UnitSystem.Imperial
                                        heightInput = formatNumber(heightCm / 2.54)
                                        weightInput = formatNumber(weightKg * 2.20462)
                                    },
                                    label = { Text("Imperial", color = primaryTextColor) },
                                    colors = AssistChipDefaults.assistChipColors(
                                        containerColor = if (unitSystem == UnitSystem.Imperial)
                                            chipSelectedColor else chipUnselectedColor
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // input area
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            val heightLabel = if (unitSystem == UnitSystem.Metric)
                                "Height (cm)" else "Height (inches)"
                            val weightLabel = if (unitSystem == UnitSystem.Metric)
                                "Weight (kg)" else "Weight (lbs)"

                            OutlinedTextField(
                                value = heightInput,
                                onValueChange = { new ->
                                    val filtered = new.filter { it.isDigit() || it == '.' }
                                    heightInput = filtered
                                    val v = filtered.toDoubleOrNull()
                                    if (v != null) {
                                        heightCm = if (unitSystem == UnitSystem.Metric) {
                                            v
                                        } else {
                                            v * 2.54        // in → cm
                                        }
                                    }
                                },
                                label = { Text(heightLabel) },
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = primaryTextColor,
                                    unfocusedTextColor = primaryTextColor,
                                    focusedLabelColor = primaryTextColor,
                                    unfocusedLabelColor = secondaryTextColor
                                )
                            )

                            OutlinedTextField(
                                value = weightInput,
                                onValueChange = { new ->
                                    val filtered = new.filter { it.isDigit() || it == '.' }
                                    weightInput = filtered
                                    val v = filtered.toDoubleOrNull()
                                    if (v != null) {
                                        weightKg = if (unitSystem == UnitSystem.Metric) {
                                            v
                                        } else {
                                            v / 2.20462      // lbs → kg
                                        }
                                    }
                                },
                                label = { Text(weightLabel) },
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = primaryTextColor,
                                    unfocusedTextColor = primaryTextColor,
                                    focusedLabelColor = primaryTextColor,
                                    unfocusedLabelColor = secondaryTextColor
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Height: cm or inches.  Weight: kg or lbs.",
                            fontSize = 12.sp,
                            color = secondaryTextColor
                        )
                    }
                }

                // save
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(
                            onClick = {
                                // local fake data
                                Toast.makeText(
                                    context,
                                    "Profile updated locally (email, sex, height, weight)",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                text = "Save Changes",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun SexOptionChip(
    label: String,
    value: String,
    selected: Boolean,
    onSelected: () -> Unit,
    selectedColor: Color,
    unselectedColor: Color,
    textColor: Color
) {
    AssistChip(
        onClick = onSelected,
        label = { Text(label, color = textColor) },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = if (selected) selectedColor else unselectedColor
        )
    )
}

@Composable
private fun ProfileSectionCard(
    title: String,
    backgroundColor: Color,
    titleColor: Color,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = backgroundColor,
        tonalElevation = 1.dp,
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = titleColor
            )
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}


private fun formatNumber(value: Double): String {
    return if (value % 1.0 == 0.0) {
        String.format(Locale.getDefault(), "%.0f", value)
    } else {
        String.format(Locale.getDefault(), "%.1f", value)
    }
}