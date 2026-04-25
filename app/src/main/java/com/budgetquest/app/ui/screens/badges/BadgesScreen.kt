package com.budgetquest.app.ui.screens.badges

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.budgetquest.app.ui.theme.*
import com.budgetquest.app.ui.viewmodel.MainViewModel
import java.util.Calendar

data class BadgeModel(
    val name: String,
    val description: String,
    val icon: String,
    val isLocked: Boolean
)

@Composable
fun BadgesScreen(
    viewModel: MainViewModel = hiltViewModel()
) {
    val user by viewModel.currentUser.collectAsState()
    val allExpenses by viewModel.allExpenses.collectAsState()
    
    // Stats calculation
    val xp = user?.xp ?: 0
    val level = user?.level ?: 1
    val xpProgress = (xp % 1000).toFloat() / 1000f
    
    val streak = user?.streakCount ?: 0
    
    val totalExpenses = remember(allExpenses) { allExpenses.size }
    
    val budgetDays = remember(allExpenses) {
        allExpenses.map { 
            val cal = Calendar.getInstance().apply { timeInMillis = it.date }
            "${cal.get(Calendar.YEAR)}-${cal.get(Calendar.DAY_OF_YEAR)}"
        }.distinct().size
    }
    
    // Badge logic
    val hasGoals = true // Placeholder or check items from DB
    val hasReceipts = remember(allExpenses) { allExpenses.any { it.imagePath != null } }
    
    val earnedBadges = remember(hasReceipts) {
        listOf(
            BadgeModel("Goal Setter", "Set first budget goals", "🎯", !hasGoals),
            BadgeModel("Evidence Keeper", "Attached a receipt photo", "📷", !hasReceipts)
        ).filter { !it.isLocked }
    }

    val lockedBadges = listOf(
        BadgeModel("Monthly Master", "30-day streak needed", "🏆", true),
        BadgeModel("Budget Hero", "Stay under monthly budget", "💚", true),
        BadgeModel("Financial Expert", "Reach Level 5", "⭐", true),
        BadgeModel("Organiser", "Create 5 categories", "📁", true)
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
            .background(CyberBackground)
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 24.dp, bottom = 120.dp)
    ) {
        // Profile Header
        item(span = { GridItemSpan(2) }) {
            ProfileHeaderCard(
                levelName = "Budget Novice",
                level = level,
                xpText = "${xp % 1000} / 1000 XP to next level",
                xpProgress = xpProgress,
                streak = streak,
                expenses = totalExpenses,
                days = budgetDays
            )
        }

        // Earned Section
        item(span = { GridItemSpan(2) }) {
            SectionTitle("Earned Badges")
        }
        items(earnedBadges) { badge ->
            BadgeCard(badge)
        }

        // Locked Section
        item(span = { GridItemSpan(2) }) {
            SectionTitle("Locked Badges")
        }
        items(lockedBadges) { badge ->
            BadgeCard(badge)
        }
    }
}

@Composable
fun ProfileHeaderCard(
    levelName: String,
    level: Int,
    xpText: String,
    xpProgress: Float,
    streak: Int,
    expenses: Int,
    days: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CyberSurface),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Medal Icon
                Surface(
                    modifier = Modifier.size(64.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White.copy(alpha = 0.05f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("🥇", fontSize = 32.sp)
                    }
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(xpText, color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { xpProgress },
                        modifier = Modifier.fillMaxWidth().height(8.dp),
                        color = CyberGold,
                        trackColor = Color.White.copy(alpha = 0.1f),
                        strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(levelName, fontWeight = FontWeight.Bold, color = CyberGold, fontSize = 18.sp)
                    Text("Level $level", color = Color.White.copy(alpha = 0.5f), fontSize = 14.sp)
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                StatItem("🔥", "$streak", "day streak")
                StatItem("📊", "$expenses", "expenses")
                StatItem("📅", "$days", "budget days")
            }
        }
    }
}

@Composable
fun StatItem(icon: String, value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(icon, fontSize = 20.sp)
        Text(value, fontWeight = FontWeight.Black, color = Color.White, fontSize = 16.sp)
        Text(label, color = Color.White.copy(alpha = 0.5f), fontSize = 10.sp)
    }
}

@Composable
fun BadgeCard(badge: BadgeModel) {
    val alpha = if (badge.isLocked) 0.3f else 1f
    Card(
        modifier = Modifier.fillMaxWidth().height(140.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (badge.isLocked) Color.Transparent else CyberSurface
        ),
        shape = RoundedCornerShape(20.dp),
        border = if (badge.isLocked) androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)) else null
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(badge.icon, fontSize = 32.sp, modifier = Modifier.alpha(alpha))
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = badge.name,
                fontWeight = FontWeight.Bold,
                color = if (badge.isLocked) Color.Gray else CyberGold,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
            Text(
                text = badge.description,
                color = Color.Gray,
                fontSize = 10.sp,
                textAlign = TextAlign.Center,
                lineHeight = 12.sp
            )
        }
    }
}

@Composable
fun SectionTitle(text: String) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelMedium,
        color = CyberGold,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 8.dp)
    )
}
