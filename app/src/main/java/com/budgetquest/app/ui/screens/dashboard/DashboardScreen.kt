package com.budgetquest.app.ui.screens.dashboard

import com.budgetquest.app.data.local.entity.CategoryEntity
import com.budgetquest.app.data.local.entity.ExpenseEntity
import com.budgetquest.app.data.local.entity.QuestEntity
import com.budgetquest.app.data.local.entity.UserEntity

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.budgetquest.app.ui.components.*

import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.budgetquest.app.ui.viewmodel.MainViewModel

@Composable
fun DashboardScreen(
    onViewFeatures: () -> Unit,
    onLogout: () -> Unit,
    viewModel: MainViewModel = hiltViewModel()
) {
    val user by viewModel.currentUser.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val categorySpending by viewModel.categorySpending.collectAsState()
    val chartData by viewModel.chartData.collectAsState()
    val quests by viewModel.allQuests.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        contentPadding = PaddingValues(top = 24.dp, bottom = 32.dp)
    ) {
        item {
            LevelHeader(
                levelName = "Level ${user?.level ?: 1}",
                currentXP = user?.xp ?: 0,
                maxXP = (user?.level ?: 1) * 1000,
                streakDays = user?.streakCount ?: 0
            )
        }

        item {
            AnalyticsArena(spendingData = chartData)
        }

        item {
            CategoryOrbGrid(categories = categories, spending = categorySpending)
        }

        item {
            AgeStatCard(user = user) 
        }
        
        item {
            Text(
                text = "ACTIVE QUESTS",
                style = MaterialTheme.typography.labelMedium,
                color = com.budgetquest.app.ui.theme.CyberGold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        items(quests) { quest ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                colors = CardDefaults.cardColors(containerColor = com.budgetquest.app.ui.theme.CyberSurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(quest.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(quest.description, style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.6f))
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    val questColor = when(quest.color) {
                        "NeonPurple" -> com.budgetquest.app.ui.theme.NeonPurple
                        "NeonCyan" -> com.budgetquest.app.ui.theme.NeonCyan
                        "NeonGreen" -> com.budgetquest.app.ui.theme.NeonGreen
                        else -> com.budgetquest.app.ui.theme.NeonPink
                    }
                    
                    LinearProgressIndicator(
                        progress = { quest.progress / 100f },
                        modifier = Modifier.fillMaxWidth().height(4.dp),
                        color = questColor,
                        trackColor = Color.White.copy(alpha = 0.05f),
                        strokeCap = StrokeCap.Round
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Progress: ${quest.progress}%", style = MaterialTheme.typography.labelSmall, color = questColor)
                }
            }
        }

        item {
            Column(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)) {
                TextButton(
                    onClick = onViewFeatures,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "VIEW ALL FEATURES", 
                        color = com.budgetquest.app.ui.theme.NeonGreen,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                TextButton(
                    onClick = {
                        viewModel.logout()
                        onLogout()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "SYSTEM LOGOUT", 
                        color = com.budgetquest.app.ui.theme.NeonPink,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}
