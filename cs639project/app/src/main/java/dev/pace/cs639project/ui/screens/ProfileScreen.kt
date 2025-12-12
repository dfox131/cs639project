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

// 1. 确保这个枚举只定义一次
private enum class UnitSystem { Metric, Imperial }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBack: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    isDarkTheme: Boolean,
    userId: String // 保留这个参数，方便后续扩展
) {
    // --- 这里是你之前报错冲突的地方，我现在只保留一套变量声明 ---

    // 这些是本地状态，如果你同学连了数据库，他应该会把这些换成 ViewModel 的各种 collectAsState
    // 但为了让你先跑通，我们先用这一套干净的声明，去掉重复的。
    var email by remember { mutableStateOf("alex@example.com") }
    var sex by remember { mutableStateOf("male") }
    var heightCm by remember { mutableStateOf(175.0) }
    var weightKg by remember { mutableStateOf(70.0) }
    var unitSystem by remember { mutableStateOf(UnitSystem.Metric) }
    var heightInput by remember { mutableStateOf("175") }
    var weightInput by remember { mutableStateOf("70") }

    val context = LocalContext.current

    // --- 主题颜色逻辑 ---
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
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onNotificationClick) {
                        Icon(imageVector = Icons.Default.Notifications, contentDescription = "Notifications")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(bgColor)
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Image Section
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
                            val initial = email.takeIf { it.isNotBlank() }?.firstOrNull()?.uppercaseChar()?.toString() ?: "U"
                            Text(text = initial, fontSize = 40.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = email, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = textColor)
                        Text(text = "Mapped to Firestore collection: users", fontSize = 12.sp, color = subTextColor, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                    }
                }

                // Account Section
                item {
                    ProfileSectionCard(title = "Account", cardBg = cardColor, titleColor = textColor) {
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = textColor,
                                unfocusedTextColor = textColor,
                                focusedLabelColor = textColor,
                                unfocusedLabelColor = subTextColor
                            )
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = "Gender", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = textColor)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            listOf("Male", "Female", "Other").forEach { option ->
                                SexOptionChip(
                                    label = option,
                                    value = option.lowercase(),
                                    selected = sex.equals(option, ignoreCase = true),
                                    onSelected = { sex = option.lowercase() },
                                    selectedColor = chipSelected,
                                    unselectedColor = chipUnselected,
                                    textColor = textColor
                                )
                            }
                        }
                    }
                }

                // Measurements Section
                item {
                    ProfileSectionCard(title = "Measurements", cardBg = cardColor, titleColor = textColor) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(text = "Unit system:", fontSize = 14.sp, color = textColor)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                AssistChip(
                                    onClick = {
                                        unitSystem = UnitSystem.Metric
                                        heightInput = formatNumber(heightCm)
                                        weightInput = formatNumber(weightKg)
                                    },
                                    label = { Text("Metric", color = textColor) },
                                    colors = AssistChipDefaults.assistChipColors(containerColor = if (unitSystem == UnitSystem.Metric) chipSelected else chipUnselected)
                                )
                                AssistChip(
                                    onClick = {
                                        unitSystem = UnitSystem.Imperial
                                        heightInput = formatNumber(heightCm / 2.54)
                                        weightInput = formatNumber(weightKg * 2.20462)
                                    },
                                    label = { Text("Imperial", color = textColor) },
                                    colors = AssistChipDefaults.assistChipColors(containerColor = if (unitSystem == UnitSystem.Imperial) chipSelected else chipUnselected)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(
                                value = heightInput,
                                onValueChange = {
                                    heightInput = it.filter { c -> c.isDigit() || c == '.' }
                                    // 可以在这里加转换逻辑更新 heightCm
                                },
                                label = { Text(if (unitSystem == UnitSystem.Metric) "Height (cm)" else "Height (in)") },
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = textColor, unfocusedTextColor = textColor)
                            )
                            OutlinedTextField(
                                value = weightInput,
                                onValueChange = {
                                    weightInput = it.filter { c -> c.isDigit() || c == '.' }
                                    // 可以在这里加转换逻辑更新 weightKg
                                },
                                label = { Text(if (unitSystem == UnitSystem.Metric) "Weight (kg)" else "Weight (lbs)") },
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = textColor, unfocusedTextColor = textColor)
                            )
                        }
                    }
                }

                // Save Button
                item {
                    Button(
                        onClick = { Toast.makeText(context, "Saved locally (Merge Fix)", Toast.LENGTH_SHORT).show() },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Save Changes", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}


@Composable
private fun SexOptionChip(label: String, value: String, selected: Boolean, onSelected: () -> Unit, selectedColor: Color, unselectedColor: Color, textColor: Color) {
    AssistChip(
        onClick = onSelected,
        label = { Text(label, color = textColor) },
        colors = AssistChipDefaults.assistChipColors(containerColor = if (selected) selectedColor else unselectedColor)
    )
}

@Composable
private fun ProfileSectionCard(title: String, cardBg: Color, titleColor: Color, content: @Composable ColumnScope.() -> Unit) {
    Surface(shape = RoundedCornerShape(24.dp), color = cardBg, tonalElevation = 1.dp, shadowElevation = 2.dp, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp)) {
            Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = titleColor)
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}

private fun formatNumber(value: Double): String {
    return if (value % 1.0 == 0.0) String.format(Locale.getDefault(), "%.0f", value) else String.format(Locale.getDefault(), "%.1f", value)
}