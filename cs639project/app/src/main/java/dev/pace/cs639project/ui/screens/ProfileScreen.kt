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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.pace.cs639project.viewmodel.ProfileViewModel
import java.util.Locale

private enum class UnitSystem { Metric, Imperial }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userId: String,
    isDarkTheme: Boolean,
    onBack: () -> Unit = {},
    onNotificationClick: () -> Unit = {}
) {
    // ------------------------------
    // ViewModel (RESTORED)
    // ------------------------------
    val viewModel: ProfileViewModel = viewModel(
        key = "profile-$userId",
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ProfileViewModel(userId = userId) as T
            }
        }
    )

    val uiState by viewModel.uiState.collectAsState()

    // ------------------------------
    // UI State mapped to ViewModel values
    // ------------------------------
    var email by remember(uiState.email) { mutableStateOf(uiState.email) }
    var sex by remember(uiState.sex) { mutableStateOf(uiState.sex ?: "") }
    var heightInput by remember(uiState.height) { mutableStateOf(uiState.height?.toString() ?: "") }
    var weightInput by remember(uiState.weight) { mutableStateOf(uiState.weight?.toString() ?: "") }

    // For unit conversion
    var unitSystem by remember { mutableStateOf(UnitSystem.Metric) }
    var heightCm by remember(uiState.height) { mutableStateOf((uiState.height ?: 0).toDouble()) }
    var weightKg by remember(uiState.weight) { mutableStateOf((uiState.weight ?: 0).toDouble()) }

    val context = LocalContext.current

    // ------------------------------
    // DARK MODE COLORS (preserved from teammate)
    // ------------------------------
    val bgColor = if (isDarkTheme) Color(0xFF020617) else Color(0xFFF5F7FB)
    val cardColor = if (isDarkTheme) Color(0xFF111827) else Color.White
    val textColor = if (isDarkTheme) Color.White else Color(0xFF111827)
    val subTextColor = if (isDarkTheme) Color(0xFF9CA3AF) else Color(0xFF6B7280)
    val chipSelected = if (isDarkTheme) Color(0xFF1E3A8A) else Color(0xFFDBEAFE)
    val chipUnselected = if (isDarkTheme) Color(0xFF374151) else Color(0xFFE5E7EB)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Profile") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onNotificationClick) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                    }
                }
            )
        }
    ) { innerPadding ->

        // Loading State
        if (uiState.isLoading) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(bgColor),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(bgColor)
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ------------------------------
            // Image + Email Display
            // ------------------------------
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
                        val initial = email.firstOrNull()?.uppercaseChar()?.toString() ?: "U"

                        Text(
                            text = initial,
                            fontSize = 40.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    Text(email, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = textColor)

                    Text(
                        "Mapped to Firestore collection: users",
                        fontSize = 12.sp,
                        color = subTextColor,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // ------------------------------
            // ACCOUNT SECTION
            // ------------------------------
            item {
                ProfileSectionCard("Account", cardColor, textColor) {

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(12.dp))

                    Text("Gender", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = textColor)

                    Spacer(Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Male", "Female", "Other").forEach { option ->
                            SexOptionChip(
                                label = option,
                                value = option.lowercase(),
                                selected = sex.equals(option, true),
                                onSelected = { sex = option.lowercase() },
                                selectedColor = chipSelected,
                                unselectedColor = chipUnselected,
                                textColor = textColor
                            )
                        }
                    }
                }
            }

            // ------------------------------
            // MEASUREMENTS SECTION
            // ------------------------------
            item {
                ProfileSectionCard("Measurements", cardColor, textColor) {

                    Text("Unit System", color = textColor)

                    Spacer(Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

                        AssistChip(
                            onClick = {
                                unitSystem = UnitSystem.Metric
                                heightInput = formatNumber(heightCm)
                                weightInput = formatNumber(weightKg)
                            },
                            label = { Text("Metric", color = textColor) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor =
                                    if (unitSystem == UnitSystem.Metric) chipSelected else chipUnselected
                            )
                        )

                        AssistChip(
                            onClick = {
                                unitSystem = UnitSystem.Imperial
                                heightInput = formatNumber(heightCm / 2.54)
                                weightInput = formatNumber(weightKg * 2.20462)
                            },
                            label = { Text("Imperial", color = textColor) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor =
                                    if (unitSystem == UnitSystem.Imperial) chipSelected else chipUnselected
                            )
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    // Height + Weight Fields
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {

                        OutlinedTextField(
                            value = heightInput,
                            onValueChange = {
                                val filtered = it.filter { c -> c.isDigit() || c == '.' }
                                heightInput = filtered
                                filtered.toDoubleOrNull()?.let { v ->
                                    heightCm =
                                        if (unitSystem == UnitSystem.Metric) v else v * 2.54
                                }
                            },
                            label = {
                                Text(
                                    if (unitSystem == UnitSystem.Metric) "Height (cm)" else "Height (in)"
                                )
                            },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = weightInput,
                            onValueChange = {
                                val filtered = it.filter { c -> c.isDigit() || c == '.' }
                                weightInput = filtered
                                filtered.toDoubleOrNull()?.let { v ->
                                    weightKg =
                                        if (unitSystem == UnitSystem.Metric) v else v / 2.20462
                                }
                            },
                            label = {
                                Text(
                                    if (unitSystem == UnitSystem.Metric) "Weight (kg)" else "Weight (lbs)"
                                )
                            },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // ------------------------------
            // SAVE BUTTON
            // ------------------------------
            item {
                Button(
                    onClick = {
                        viewModel.saveProfile(
                            email = email,
                            sex = sex,
                            height = heightCm.toInt(),
                            weight = weightKg.toInt()
                        )

                        Toast.makeText(context, "Saved!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Save Changes", fontSize = 16.sp, fontWeight = FontWeight.Medium)
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
    cardBg: Color,
    titleColor: Color,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = cardBg,
        tonalElevation = 1.dp,
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(title, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = titleColor)
            Spacer(Modifier.height(8.dp))
            content()
        }
    }
}

private fun formatNumber(value: Double): String {
    return if (value % 1.0 == 0.0)
        String.format(Locale.getDefault(), "%.0f", value)
    else
        String.format(Locale.getDefault(), "%.1f", value)
}
