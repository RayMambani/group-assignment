package com.budgetquest.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.budgetquest.app.data.local.entity.UserEntity
import com.budgetquest.app.ui.theme.*
import java.util.*

@Composable
fun LevelHeader(
    levelName: String = "Novice",
    currentXP: Int = 0,
    maxXP: Int = 1000,
    streakDays: Int = 0
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CyberSurface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = levelName.uppercase(),
                    style = MaterialTheme.typography.titleLarge,
                    color = CyberGold,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp
                )
                
                Surface(
                    color = NeonPink.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Streak",
                            tint = NeonPink,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${streakDays}D STREAK",
                            color = NeonPink,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            val progress = (currentXP.toFloat() / maxXP).coerceIn(0f, 1f)
            val animatedProgress by animateFloatAsState(targetValue = progress, label = "xp")

            Column {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "XP PROGRESS", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    Text(text = "$currentXP / $maxXP XP", style = MaterialTheme.typography.labelSmall, color = CyberGold)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Canvas(modifier = Modifier.fillMaxWidth().height(10.dp)) {
                    val w = size.width
                    val h = size.height
                    
                    drawRoundRect(
                        color = Color.White.copy(alpha = 0.05f),
                        size = size,
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(h/2, h/2)
                    )
                    
                    drawRoundRect(
                        brush = Brush.horizontalGradient(listOf(NeonGreen, NeonCyan)),
                        size = Size(w * animatedProgress, h),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(h/2, h/2)
                    )
                }
            }
        }
    }
}

@Composable
fun AnalyticsArena(
    spendingData: List<com.budgetquest.app.ui.viewmodel.MainViewModel.ChartBarData> = emptyList(),
    maxThreshold: Float = 0.75f,
    minThreshold: Float = 0.25f
) {
    Card(
        modifier = Modifier.fillMaxWidth().height(280.dp),
        colors = CardDefaults.cardColors(containerColor = CyberSurface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "EXPENDITURE ANALYSIS",
                style = MaterialTheme.typography.titleSmall,
                color = CyberGold,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(24.dp))
            
            Box(modifier = Modifier.fillMaxSize()) {
                Canvas(modifier = Modifier.fillMaxSize().padding(bottom = 32.dp)) {
                    val canvasWidth = size.width
                    val canvasHeight = size.height
                    val barSpacing = if (spendingData.isNotEmpty()) canvasWidth / spendingData.size else canvasWidth
                    val barWidth = barSpacing * 0.5f
                    
                    val maxY = canvasHeight * (1f - maxThreshold)
                    drawLine(
                        color = ThresholdMax,
                        start = Offset(0f, maxY),
                        end = Offset(canvasWidth, maxY),
                        strokeWidth = 1.dp.toPx(),
                        pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
                    )
                    
                    spendingData.forEachIndexed { index, data ->
                        val barHeight = canvasHeight * data.value
                        val x = index * barSpacing + (barSpacing - barWidth) / 2
                        
                        val barColor = when(data.color) {
                            "NeonCyan" -> NeonCyan
                            "NeonPurple" -> NeonPurple
                            "NeonPink" -> NeonPink
                            "NeonGreen" -> NeonGreen
                            else -> CyberGold
                        }
                        
                        val gradient = Brush.verticalGradient(
                            colors = listOf(barColor, barColor.copy(alpha = 0.3f))
                        )
                        
                        drawRoundRect(
                            brush = gradient,
                            topLeft = Offset(x, canvasHeight - barHeight),
                            size = Size(barWidth, barHeight),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx(), 4.dp.toPx())
                        )
                    }
                }
                
                // Labels layer
                Row(
                    modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter).padding(bottom = 4.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    spendingData.forEach { data ->
                        Text(
                            text = if (data.name.length > 5) data.name.take(4) + "." else data.name,
                            color = Color.Gray,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.width(40.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryOrb(
    name: String,
    percentage: Int,
    neonColor: Color,
    size: Dp = 70.dp
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(size)) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val stroke = 6.dp.toPx()
                drawCircle(
                    color = neonColor.copy(alpha = 0.1f),
                    style = Stroke(width = stroke)
                )
                drawArc(
                    color = neonColor,
                    startAngle = -90f,
                    sweepAngle = 360f * (percentage / 100f),
                    useCenter = false,
                    style = Stroke(width = stroke, cap = StrokeCap.Round)
                )
            }
            Text(
                text = "${percentage}%",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = name.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun CategoryOrbGrid(
    categories: List<com.budgetquest.app.data.local.entity.CategoryEntity>,
    spending: List<com.budgetquest.app.data.local.dao.CategorySpending>
) {
    val colors = listOf(NeonGreen, NeonCyan, NeonPurple, NeonPink)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        categories.take(4).forEachIndexed { index, category ->
            val spent = spending.find { it.categoryId == category.id }?.total ?: 0.0
            val percentage = ((spent / category.monthlyLimit) * 100).toInt().coerceIn(0, 100)
            CategoryOrb(category.name, percentage, colors[index % colors.size])
        }
    }
}

@Composable
fun AgeStatCard(user: UserEntity? = null) {
    val calendar = Calendar.getInstance()
    val createdAt = user?.createdAt ?: System.currentTimeMillis()
    calendar.timeInMillis = createdAt
    
    val month = calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault())?.uppercase() ?: "???"
    val day = calendar.get(Calendar.DAY_OF_MONTH).toString()
    
    val diffMs = System.currentTimeMillis() - createdAt
    val daysCount = (diffMs / (1000 * 60 * 60 * 24)).toInt().coerceAtLeast(1)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CyberSurface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                 modifier = Modifier.size(60.dp).background(NeonCyan.copy(alpha = 0.1f), CircleShape),
                 contentAlignment = Alignment.Center
            ) {
                 Column(horizontalAlignment = Alignment.CenterHorizontally) {
                     Text(month, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                     Text(day, fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color.White)
                 }
            }
            
            Spacer(modifier = Modifier.width(24.dp))
            
            Column {
                Text(
                    text = "AGE OF BUDGET SCORE",
                    style = MaterialTheme.typography.labelMedium,
                    color = CyberGold,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$daysCount DAYS",
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "WITHIN BUDGET LIMITS",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }
        }
    }
}
