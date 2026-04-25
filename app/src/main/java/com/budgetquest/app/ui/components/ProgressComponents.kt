package com.budgetquest.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.budgetquest.app.ui.theme.Gold
import com.budgetquest.app.ui.theme.GoldOrange
import com.budgetquest.app.ui.theme.OrangeWarning
import com.budgetquest.app.ui.theme.RedDanger
import com.budgetquest.app.ui.theme.GreenSafe

@Composable
fun ProgressRing(
    progress: Float,
    label: String,
    modifier: Modifier = Modifier,
    size: Dp = 100.dp,
    strokeWidth: Dp = 8.dp
) {
    val animatedProgress by animateFloatAsState(targetValue = progress.coerceIn(0f, 1f), label = "progress")
    
    val color = when {
        progress >= 1f -> RedDanger
        progress >= 0.8f -> OrangeWarning
        else -> GreenSafe
    }

    Box(contentAlignment = Alignment.Center, modifier = modifier.size(size)) {
        Canvas(modifier = Modifier.size(size)) {
            drawCircle(
                color = color.copy(alpha = 0.1f),
                style = Stroke(width = strokeWidth.toPx())
            )
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = 360 * animatedProgress,
                useCenter = false,
                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "${(progress * 100).toInt()}%",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun XPProgressBar(
    currentXP: Int,
    maxXP: Int,
    modifier: Modifier = Modifier
) {
    val progress = (currentXP.toFloat() / maxXP).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(targetValue = progress, label = "xp_progress")

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "XP Progress", style = MaterialTheme.typography.labelMedium)
            Text(text = "$currentXP / $maxXP XP", style = MaterialTheme.typography.labelMedium)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Canvas(modifier = Modifier.fillMaxWidth().height(12.dp)) {
            val width = size.width
            
            // Background
            drawRoundRect(
                color = Color.Gray.copy(alpha = 0.2f),
                size = size
            )
            
            // Progress with Gradient
            drawRoundRect(
                brush = Brush.horizontalGradient(listOf(Gold, GoldOrange)),
                size = size.copy(width = width * animatedProgress)
            )
        }
    }
}
