package dev.pace.cs639project.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun StreakCalendar(
    completedDates: Set<LocalDate>
) {
    val today = LocalDate.now()
    val firstDayOfMonth = today.withDayOfMonth(1)

    val displayRange = 35 

    val startDayOffset = firstDayOfMonth.dayOfWeek.value % 7
    val monthDays = (0 until displayRange).mapNotNull { offset ->
        if (offset < startDayOffset) null
        else firstDayOfMonth.plusDays((offset - startDayOffset).toLong())
    }

    Card(
        modifier = Modifier.fillMaxWidth().heightIn(min = 350.dp, max = 400.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "${today.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${today.year}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                DayOfWeek.values().forEach { day ->
                    Text(
                        text = day.getDisplayName(TextStyle.NARROW, Locale.getDefault()),
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(Modifier.height(8.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                contentPadding = PaddingValues(1.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(monthDays.size) { index ->
                    val date = monthDays[index]

                    if (date != null) {
                        val isCompleted = completedDates.contains(date)
                        val isToday = date.isEqual(today)

                        val cellColor = when {
                            isCompleted -> MaterialTheme.colorScheme.primary.copy(alpha = 0.9f)
                            isToday -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.3f)
                            else -> Color.Transparent
                        }
                        val borderColor = if (isToday) MaterialTheme.colorScheme.primary else Color.Transparent

                        Card(
                            modifier = Modifier.aspectRatio(1f),
                            shape = MaterialTheme.shapes.small,
                            colors = CardDefaults.cardColors(containerColor = cellColor),
                            border = BorderStroke(1.dp, borderColor)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = date.dayOfMonth.toString(),
                                    color = if (isCompleted) Color.White else MaterialTheme.colorScheme.onSurface,
                                    fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    } else {
                        Spacer(Modifier.aspectRatio(1f))
                    }
                }
            }
        }
    }
}