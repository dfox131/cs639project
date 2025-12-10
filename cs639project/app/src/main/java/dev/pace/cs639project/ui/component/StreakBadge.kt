package dev.pace.cs639project.ui.component

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
           Text(
               text = "You're Doing Great!",
               style = MaterialTheme.typography.bodySmall,
               color = Color.Gray
           )
       }
   }
}