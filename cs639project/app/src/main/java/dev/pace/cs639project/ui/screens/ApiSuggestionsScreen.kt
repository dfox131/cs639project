package dev.pace.cs639project.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.* // Import all Material3 components
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.pace.cs639project.ui.components.ExerciseSuggestion // Assumed import
import dev.pace.cs639project.viewmodel.ApiSuggestionsViewModel
import dev.pace.cs639project.viewmodel.AuthViewModel
import androidx.compose.material3.ExperimentalMaterial3Api // 🛠️ FIX 3: Required import

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApiSuggestionsScreen(
    onBack: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    viewModel: ApiSuggestionsViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel() // Use existing AuthViewModel
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val currentUserId by authViewModel.currentUserId.collectAsState() // Get the current user ID

    // ❌ REMOVED: Illegal Firestore initialization code here.

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "Habit Suggestions") },
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
                .background(Color(0xFFF5F7FB))
                .padding(innerPadding)
        ) {

            // --- LOADING AND ERROR STATES ---
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (uiState.error != null) {
                Text(
                    text = "Error: ${uiState.error}",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center).padding(16.dp)
                )
            } else {
                // --- SUCCESS STATE ---
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(uiState.suggestions) { item ->
                            ExerciseSuggestionCard(
                                suggestion = item,
                                onTryClick = {
                                    val userId = currentUserId
                                    if (userId != null) {
                                        // Call ViewModel to add habit
                                        viewModel.addHabitFromSuggestion(
                                            userId = userId,
                                            suggestion = item
                                        )
                                        Toast.makeText(
                                            context,
                                            "Habit added: ${item.name}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        onBack() // Navigate back after success
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Error: User not logged in.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------------------------
// ExerciseSuggestionCard Component Definition (Kept for completeness)
// ----------------------------------------------------------------------

@Composable
fun ExerciseSuggestionCard(
    suggestion: ExerciseSuggestion,
    onTryClick: () -> Unit = {}
) {
    Surface(
        shape = RoundedCornerShape(32.dp),
        color = Color(0xFFFBD7B3),
        tonalElevation = 2.dp,
        shadowElevation = 4.dp,
        modifier = Modifier.fillMaxWidth()
        // ✅ remove fixed height so the card can grow if text wraps
        // .height(150.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // icon
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF87CEEB)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = suggestion.icon, fontSize = 26.sp)
            }

            Spacer(modifier = Modifier.width(16.dp))

            // ✅ text takes remaining space, but never overlaps button
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 12.dp)
            ) {
                Text(
                    text = suggestion.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF111827)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = suggestion.description,
                    fontSize = 14.sp,
                    color = Color(0xFF4B5563)
                )
            }

            // ✅ button is its own column space
            Button(
                onClick = onTryClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFD6E4FF),
                    contentColor = Color(0xFF111827)
                ),
                shape = RoundedCornerShape(20.dp),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "Try",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
