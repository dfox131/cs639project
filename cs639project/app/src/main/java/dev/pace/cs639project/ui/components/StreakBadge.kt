package dev.pace.cs639project.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun StreakBadge(days: Int) {

    // ---------------------------------------------------------------------
    // 🔥 NEW: Dynamic Message Logic
    // ---------------------------------------------------------------------
    val motivationalComment = when (days) {
        0 -> "Start today, build momentum!"
        in 1..2 -> "You've begun the journey!"
        in 3..6 -> "Consistency is key! Keep going."
        in 7..13 -> "One week strong! You've formed a micro-habit."
        in 14..29 -> "Two weeks plus! This is becoming routine."
        in 30..89 -> "A full month! This habit is part of you."
        in 90..179 -> "Three months of dedication! True commitment."
        in 180..364 -> "Half a year! Unstoppable force."
        in 365..Int.MAX_VALUE -> "A full year! Legend status achieved! 🎉"
        else -> "You're Doing Great!"
    }
    // ---------------------------------------------------------------------

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(horizontal = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE6F3FF)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "$days",
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF007AFF)
                )
            )
            Text(
                text = "DAYS STREAK",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black.copy(alpha = 0.8f)
                )
            )
            // Use the dynamic comment here
            Text(
                text = motivationalComment,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}