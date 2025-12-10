package dev.pace.cs639project.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DailyProgressPieChart(
    completed: Int,
    total: Int,
    size: Dp = 180.dp,
    strokeWidth: Dp = 35.dp
) {
    val progress = if (total > 0) completed.toFloat() / total.toFloat() else 0f

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 1000),
        label = "ProgressAnimation"
    )

    val backgroundArcColor = MaterialTheme.colorScheme.surfaceVariant
    val progressColor = MaterialTheme.colorScheme.primary

    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(size)) {
        Canvas(modifier = Modifier.size(size)) {
            val innerRadius = (size.toPx() - strokeWidth.toPx()) / 2
            val center = Offset(size.toPx() / 2f, size.toPx() / 2f)
            val drawSize = Size(innerRadius * 2, innerRadius * 2)

            drawArc(
                color = backgroundArcColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = center - Offset(innerRadius, innerRadius),
                size = drawSize,
                style = Stroke(width = strokeWidth.toPx())
            )

            drawArc(
                color = progressColor,
                startAngle = 270f, 
                sweepAngle = animatedProgress * 360f,
                useCenter = false,
                topLeft = center - Offset(innerRadius, innerRadius),
                size = drawSize,
                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "${(progress * 100).toInt()}%",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "$completed / $total Goals",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}