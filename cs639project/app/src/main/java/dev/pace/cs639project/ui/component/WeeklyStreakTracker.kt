package dev.pace.cs639project.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.DayOfWeek
import java.time.LocalDate

@Composable
fun WeeklyStreakTracker(
    // The set of DayOfWeek that were completed in the last 7 days
    weeklyCompletedDays: Set<DayOfWeek>,
    // The current day of the week, used to highlight 'Today'
    currentDay: Int = LocalDate.now().dayOfWeek.value
) {
    // Weekdays in order (Monday=1 to Sunday=7)
    val days = DayOfWeek.values()

    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)) {
        Text(
            text = "Weekly Consistency",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 12.dp, start = 4.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            days.forEach { dayOfWeek ->
                val isToday = dayOfWeek.value == currentDay
                val isCompleted = weeklyCompletedDays.contains(dayOfWeek)

                // Colors based on status
                val circleColor = when {
                    isCompleted -> MaterialTheme.colorScheme.primary // Completed
                    isToday -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.6f) // Today
                    else -> Color.LightGray.copy(alpha = 0.3f) // Missed/Incomplete
                }

                val textColor = if (isCompleted || isToday) Color.White else Color.Black

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f).padding(4.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(40.dp)
                            .background(circleColor, CircleShape)
                    ) {
                        Text(
                            text = dayOfWeek.name.first().toString(), // M, T, W, etc.
                            color = textColor,
                            fontSize = 16.sp,
                            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    if (isToday) {
                        Text(
                            text = "Today",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }
            }
        }
    }
}